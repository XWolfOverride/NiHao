package nihao.context;

import nihao.ProviderInfo;

public class StructureItemProvider extends StructureItem {
	private String def;
	private String[] aux;

	public StructureItemProvider(Context owner, String id) {
		super(owner, id);
	}

	public void setDefault(String def) {
		this.def = def;
	}

	public void setAuxiliar(String[] aux) {
		this.aux = aux;
	}

	@Override
	public Object get() {
		return new ProviderInfo(id, def, aux);
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
		return "provider "+id+" default "+def+"{\n"+"}";
	}
}
