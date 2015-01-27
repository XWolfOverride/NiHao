package nihao.context;

import java.lang.reflect.Array;

import nihao.util.reflection.Reflector;

public class StructureItemArray extends StructureItem {
	private String typeName;
	private Class<?> type;
	private StructureItem[] items;

	public StructureItemArray(Context owner, String id, String typeName, StructureItem[] items) {
		super(owner, id);
		this.typeName = typeName;
		this.type = null;
		this.items = items;
	}

	@Override
	public Object get() {
		Object result = Array.newInstance(type, items.length);
		for (int i = 0; i < items.length; i++)
			Array.set(result, i, items[i].get());
		return result;
	}

	@Override
	public Class<?> getStructureClass() {
		return Array.newInstance(type, 0).getClass();
	}

	@Override
	public boolean canPreinstantiate() {
		for (StructureItem i : items)
			if (!i.canPreinstantiate())
				return false;
		return true;
	}

	@Override
	public void commit() {
		type = Reflector.getClass(typeName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!singleton)
			sb.append("multi ");
		sb.append("array ");
		if (id != null) {
			sb.append(id);
			sb.append(' ');
		}
		sb.append("{\n");
		for (StructureItem i : items) {
			sb.append(' ');
			sb.append(i);
			sb.append('\n');
		}
		sb.append("}");
		return sb.toString();
	}
}
