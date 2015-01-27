package nihao.context;

import java.util.ArrayList;

public class StructureItemList extends StructureItem {
	private ArrayList<StructureItem> items = new ArrayList<StructureItem>();

	public StructureItemList(Context owner, String id) {
		super(owner, id);
	}

	public void add(StructureItem i) {
		items.add(i);
	}

	@Override
	public Object get() {
		ArrayList<Object> result = new ArrayList<Object>();
		for (StructureItem i : items)
			result.add(i.get());
		return result;
	}

	@Override
	public Class<?> getStructureClass() {
		return ArrayList.class;
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
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!singleton)
			sb.append("multi ");
		sb.append("list ");
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
