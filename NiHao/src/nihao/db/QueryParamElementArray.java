package nihao.db;

import java.lang.reflect.Array;
import java.util.List;

public class QueryParamElementArray implements IQueryParamElement {
	private int index;

	public QueryParamElementArray(int index) {
		this.index = index;
	}

	@Override
	public QueryParamValue evaluate(Object o) {
		if (o == null)
			return null;
		if (o instanceof List)
			return new QueryParamValue(null, ((List<?>) o).get(index));
		else
			return new QueryParamValue(o.getClass().getComponentType(), Array.get(o, index));
	}

	@Override
	public String toString() {
		return "[" + index + "]";
	}
}
