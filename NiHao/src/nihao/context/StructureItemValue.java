package nihao.context;

public class StructureItemValue extends StructureItem {
	public enum Type {
		STRING, NUMBER, BOOL
	}

	private Object value;

	public StructureItemValue(Context owner, String id, String value, Type type) {
		super(owner, id);
		switch (type) {
		case STRING:
			this.value = value;
			break;
		case NUMBER:
			if (value.contains("."))
				this.value = Double.parseDouble(value);
			else
				this.value = Integer.parseInt(value);
			break;
		case BOOL:
			if ("true".equalsIgnoreCase(value))
				this.value = true;
			else if ("false".equalsIgnoreCase(value))
				this.value = false;
			else
				throw new ContextParseException("unknown boolean ''" + value + "''");
		}
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public Class<?> getStructureClass() {
		if (value == null)
			return null;
		return value.getClass();
	}

	@Override
	public boolean canPreinstantiate() {
		return true;
	}

	@Override
	public void commit() {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (id != null) {
			sb.append("val ");
			sb.append(id);
			sb.append(' ');
		}
		if (value instanceof String)
			sb.append('"');
		sb.append(value.toString());
		if (value instanceof String)
			sb.append('"');
		return sb.toString();
	}
}
