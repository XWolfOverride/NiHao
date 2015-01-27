package nihao.context;

import nihao.db.Query;
import nihao.util.reflection.Reflector;

public class StructureItemQuery extends StructureItem {
	String engine;
	String returnTypeName;
	private Class<?> returnType;
	String query;
	String[] names;

	public StructureItemQuery(Context owner, String id) {
		super(owner, id);
	}

	@Override
	public Object get() {
		return new Query(id, engine, returnType, query, names);
	}

	@Override
	public Class<?> getStructureClass() {
		return Query.class;
	}

	@Override
	public boolean canPreinstantiate() {
		return true;
	}

	@Override
	public void commit() {
		if (returnTypeName != null)
			returnType = Reflector.getClass(returnTypeName);
		else
			returnType = null;
	}

	@Override
	public String toString() {
		return "query";
	}
}
