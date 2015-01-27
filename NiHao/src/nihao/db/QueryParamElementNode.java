package nihao.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import nihao.NiHaoException;
import nihao.util.reflection.Reflector;

public class QueryParamElementNode implements IQueryParamElement {
	private String name;

	public QueryParamElementNode(String name) {
		this.name = name;
	}

	@Override
	public QueryParamValue evaluate(Object o) {
		if (o == null)
			return null;
		if (o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) o;
			if (m.containsKey(name))
				return new QueryParamValue(null, m.get(name));
			throw new NiHaoException("Query map don't contain element '" + name + "'");
		} else {
			Class<?> cls = o.getClass();
			String methodName = Reflector.getGetterName(name, cls == boolean.class);
			Method m = Reflector.getCompatibleMethod(cls, methodName);
			try {
				return new QueryParamValue(m.getReturnType(), m.invoke(o));
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
