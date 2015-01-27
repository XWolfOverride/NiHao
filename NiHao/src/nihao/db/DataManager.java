package nihao.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import nihao.NiHaoException;

/***
 * Clase de acceso a datos
 * 
 * @author ivan.dominguez
 * 
 */
public class DataManager {
	private DataSourceProvider data;
	Connection con;

	/***
	 * Instancia de acceso a BBDD
	 */
	public DataManager() {
		data = DataSourceProvider.getProvider();
		init();
	}

	public DataManager(String connection) {
		if (connection == null)
			data = DataSourceProvider.getProvider();
		else
			data = DataSourceProvider.getProvider(connection);
		init();
	}

	public DataManager(DataSourceProvider provider) {
		data = provider;
		if (data == null)
			data = DataSourceProvider.getProvider();
		init();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			con.close();
		} catch (Throwable t) {
		}
	}

	private void init() {
		con = data.getConnection();
		try {
			con.setAutoCommit(false);
		} catch (SQLException e) {
			throw new NiHaoException(e);
		}
	}

	protected DataTransaction beginTransaction() {
		return new DataTransaction(con, data.engine);
	}

	protected void rollbackTransaction(DataTransaction trans) {
		try {
			trans.rollback();
		} catch (SQLException | NullPointerException e) {
		}
	}

	protected void commitTransaction(DataTransaction trans) {
		try {
			trans.commit();
		} catch (SQLException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Ejecuta la Query y castea el resultado, si la clase de retorno no es un
	 * array se retorna solo el primer elemento, si lo es se retorna el array
	 * del tipo correspondiente.
	 * 
	 * @param <T>
	 *            Tipo retornado
	 * @param queryname
	 *            Nombre de query
	 * @param param
	 *            Parametros de entrada, objeto, hashmap o valor.
	 * @param resultClass
	 *            Tipo (usar "[].class" para retornar arrays)
	 * @return
	 */
	public <T> T selectAs(String queryname, Object param, Class<T> resultClass) {
		DataTransaction trans = beginTransaction();
		try {
			T result = trans.selectAs(queryname, param, resultClass);
			commitTransaction(trans);
			return result;
		} catch (Throwable t) {
			rollbackTransaction(trans);
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new NiHaoException(t);
		}
	}

	/**
	 * Ejecuta la query y castea, Los datos siempre se retornan en forma de
	 * Array
	 * 
	 * @param <T>
	 * @param queryname
	 *            Nombre de query
	 * @param param
	 *            Parametros de entrada, objeto, hashmap o valor.
	 * @param limit
	 *            Limite máximo de elementos de retorno
	 * @return
	 */
	public <T> T[] selectAs(String queryname, Object param, int limit) {
		DataTransaction trans = beginTransaction();
		try {
			T[] result = trans.selectAs(queryname, param, limit);
			commitTransaction(trans);
			return result;
		} catch (Throwable t) {
			rollbackTransaction(trans);
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new NiHaoException(t);
		}
	}

	/**
	 * Executes the query isolated in one transaction
	 * 
	 * @param queryname
	 *            String name of registered query
	 * @param param
	 *            Object any opbject for params
	 * @return int number of changes on database
	 */
	public int execute(String queryname, Object param) {
		DataTransaction trans = beginTransaction();
		try {
			int result = trans.execute(queryname, param);
			commitTransaction(trans);
			return result;
		} catch (Throwable t) {
			rollbackTransaction(trans);
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new NiHaoException(t);
		}
	}

	/**
	 * Copies the object getters values to a HasMap
	 * 
	 * @param o
	 *            Object source
	 * @param hm
	 *            HashMap destination
	 * @param fld
	 *            Strings. if any defined, copies only the defined getter names,
	 *            if not, get all data
	 */
	public static void joinObjectToMap(Object o, HashMap<String, Object> hm, String... fld) {
		Class<? extends Object> cls = o.getClass();
		if (fld == null || fld.length == 0) {
			Method[] allm = cls.getMethods();
			for (Method m : allm) {
				String mname = m.getName();
				if (mname.startsWith("get") && (!"getClass".equals(mname))) {
					mname = mname.substring(3, 4).toLowerCase().concat(mname.substring(4));
					try {
						hm.put(mname, m.invoke(o));
					} catch (Exception e) {
					}
				}
			}
		} else
			for (String s : fld)
				try {
					Method m = cls.getMethod(s);
					hm.put(s, m.invoke(o));
				} catch (Exception e) {
				}
	}

	/**
	 * Return the engine associated to the DataManager
	 * 
	 * @return
	 */
	public String getEngine() {
		return data.engine;
	}
}
