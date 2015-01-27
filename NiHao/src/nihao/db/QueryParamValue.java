package nihao.db;

import java.sql.Types;
import java.util.Date;

import nihao.NiHaoException;

public class QueryParamValue {
	Class<?> type;
	Object value;
	int sqlType=-1;

	public QueryParamValue(Class<?> c, Object o) {
		if (c == null)
			if (o != null)
				c = o.getClass();
		type = c;
		value = o;
		if (c == null)
			sqlType=Types.NULL;
		else
			if (c.isAssignableFrom(byte[].class))
				sqlType = Types.BLOB;
			else if (c.isArray())
				sqlType = Types.ARRAY;
			else if (c == long.class || c == Long.class)
				sqlType = Types.BIGINT;
			else if (c == boolean.class || c == Boolean.class)
				sqlType = Types.BOOLEAN;
			else if (c == Date.class)
				sqlType = Types.DATE;
			else if (c == double.class || c == Double.class)
				sqlType = Types.DECIMAL;
			else if (c == String.class)
				sqlType = Types.VARCHAR;
			else
				throw new NiHaoException("Query unreconozable object type");
	}
}
