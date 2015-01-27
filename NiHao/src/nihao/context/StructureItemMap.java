package nihao.context;

import java.util.HashMap;
import java.util.Map.Entry;

public class StructureItemMap extends StructureItem {
	private HashMap<StructureItem, StructureItem> map = new HashMap<StructureItem, StructureItem>();

	public StructureItemMap(Context owner, String id) {
		super(owner, id);
	}

	@Override
	public HashMap<?, ?> get() {
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		for (Entry<StructureItem, StructureItem> e : map.entrySet())
			result.put(e.getKey().get(), e.getValue().get());
		return result;
	}

	public void putMap(StructureItem k, StructureItem v) {
		map.put(k, v);
	}

	@Override
	public Class<?> getStructureClass() {
		return HashMap.class;
	}

	@Override
	public boolean canPreinstantiate() {
		for (StructureItem m : map.keySet())
			if (!m.canPreinstantiate())
				return false;
			else if (!map.get(m).canPreinstantiate())
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
		sb.append("map ");
		if (id != null) {
			sb.append(id);
			sb.append(' ');
		}
		sb.append("{\n");
		for (StructureItem k : map.keySet()) {
			sb.append(" {");
			sb.append(k);
			sb.append(',');
			sb.append(map.get(k));
			sb.append("}\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
