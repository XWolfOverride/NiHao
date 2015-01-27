package nihao.context;

public class StructureItemNull extends StructureItem {
	public StructureItemNull(Context owner) {
		super(owner, null);
	}

	@Override
	public Object get() {
		return null;
	}

	@Override
	public Class<?> getStructureClass() {
		return null;
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
		return "null";
	}
}
