package nihao.db;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import nihao.NiHaoException;
import nihao.util.net.Url;
import nihao.util.reflection.Reflector;

public class Query {
	private String engine;
	private String id;
	private Class<?> returnType;
	private String query;
	private QueryParam[] params;

	public Query(String id, String engine, Class<?> returnType, String query, String[] params) {
		if (returnType !=null && returnType.isPrimitive())
			returnType = Reflector.getPrimitiveImplementationClass(returnType);
		this.id = id;
		this.engine = engine;
		this.returnType = returnType;
		this.query = query;
		this.params = QueryParam.parse(params);
	}

	public PreparedStatement buildStatement(Connection con, Object param) throws SQLException {
		PreparedStatement q = con.prepareStatement(query);
		if (params.length > 0) {
			if (param == null)
				throw new NiHaoException("Can't use null param on parametrized query.");
			for (int i = 0; i < params.length; i++)
				setParam(i + 1, q, params[i].evaluate(param));
		}
		return q;
	}

	public void setParam(int index, PreparedStatement q, QueryParamValue v) throws SQLException {
		if (v == null)
			q.setObject(index, null, Types.NULL);
		q.setObject(index, v.value, v.sqlType);
	}

	public Object select(Connection con, Object param) throws SQLException {
		return select(con, param, -1);
	}

	@SuppressWarnings("unchecked")
	public Object select(Connection con, Object param, int limit) throws SQLException {
		PreparedStatement s = buildStatement(con, param);
		ResultSet rs = s.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		ArrayList<Object> l = new ArrayList<Object>();
		if (Reflector.canBePrimitive(returnType) || returnType == String.class) {
			if (numberOfColumns != 1)
				throw new NiHaoException("Query too many columns for basic result type");
			while (rs.next()) {
				l.add(returnAS(rs, 1, returnType));
				if ((limit != -1) && (--limit) == 0)
					break;
			}
		} else if (HashMap.class.isAssignableFrom(returnType)) {
			while (rs.next()) {
				HashMap<String, Object> hm;
				try {
					hm = (HashMap<String, Object>) returnType.newInstance();
				} catch (RuntimeException e) {
					throw e;
				} catch (Throwable e) {
					throw new NiHaoException("Can't create HashMap " + returnType.getName(), e);
				}
				for (int col = 1; col <= numberOfColumns; col++)
					hm.put(rsmd.getColumnName(col), rs.getObject(col));
				l.add(hm);
				if ((limit != -1) && (--limit) == 0)
					break;
			}
		} else {
			Method[] setters = new Method[numberOfColumns];
			for (int col = 1; col <= numberOfColumns; col++) {
				Method m = Reflector.getSetter(returnType, rsmd.getColumnName(col));
				if (m == null)
					throw new NiHaoException("Can't find setter for column '" + rsmd.getColumnName(col) + "' in " + returnType.getName());
				setters[col - 1] = m;
			}
			while (rs.next()) {
				Object row;
				try {
					row = returnType.newInstance();
				} catch (InstantiationException e) {
					throw new NiHaoException("Can't instantiate query result object type " + returnType.getName());
				} catch (IllegalAccessException e) {
					throw new NiHaoException("Can't instantiate query result object type " + returnType.getName());
				}
				for (int col = 0; col < numberOfColumns; col++) {
					Object o = returnAS(rs, col + 1, setters[col].getParameterTypes()[0]);
					try {
						setters[col].invoke(row, o);
					} catch (Throwable t) {
						throw new NiHaoException("Cant set value for field " + setters[col].getName(), t);
					}
				}
				l.add(row);
				if ((limit != -1) && (--limit) == 0)
					break;
			}
		}
		s.close();
		return typeIt(l);
	}

	/**
	 * Executes the query into database without retrieving result.
	 * 
	 * @param con
	 *            Connection
	 * @param param
	 *            Object any object param
	 * @return int number of affected rows
	 * @throws SQLException
	 */
	public int execute(Connection con, Object param) throws SQLException {
		PreparedStatement s = buildStatement(con, param);
		int result = s.executeUpdate();
		s.close();
		return result;
	}

	private Object typeIt(ArrayList<Object> l) {
		int length = l.size();
		Object result = Array.newInstance(returnType, length);
		for (int i = 0; i < length; i++) {
			Object o = l.get(i);
			Array.set(result, i, Reflector.compatibleCast(o, returnType));
			// Array.set(result, i, returnType.cast(o));
			// else
			// throw new QCoreException("Can't cast from " +
			// o.getClass().getName() + " to " + returnType.getName());
		}
		return result;
	}

	private static Object returnAS(ResultSet rs, int columnIndex, Class<?> t) {
		try {
			if (t == String.class)
				return rs.getString(columnIndex);
			if (t == boolean.class || t == Boolean.class)
				return rs.getBoolean(columnIndex);
			if (t == byte.class || t == Byte.class)
				return rs.getByte(columnIndex);
			if (t == short.class || t == Short.class)
				return rs.getShort(columnIndex);
			if (t == int.class || t == Integer.class)
				return rs.getInt(columnIndex);
			if (t == long.class || t == Long.class)
				return rs.getLong(columnIndex);
			if (t == float.class || t == Float.class)
				return rs.getFloat(columnIndex);
			if (t == double.class || t == Double.class)
				return rs.getDouble(columnIndex);
			if (t == Date.class)
				return rs.getDate(columnIndex);
			if (t == Time.class)
				return rs.getTime(columnIndex);
			if (t == Timestamp.class)
				return rs.getTimestamp(columnIndex);
			if (t == URL.class)
				return rs.getURL(columnIndex);
			if (t == Url.class)
				return new Url(rs.getString(columnIndex));
			if (t == byte[].class) {
				Blob b = rs.getBlob(columnIndex);
				return b.getBytes(0l, (int) b.length());
			}
			if (t == InputStream.class)
				return rs.getBlob(columnIndex).getBinaryStream();
			throw new NiHaoException("Unimplemented for type " + t.getName());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna el identificador de la query
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retorna el engine de la query
	 * 
	 * @return
	 */
	public String getEngine() {
		return engine;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(query);
		sb.append('{');
		for (int i = 0; i < params.length; i++) {
			if (i != 0)
				sb.append(',');
			sb.append(params[i].toString());
		}
		sb.append('}');
		return sb.toString();
	}
}
