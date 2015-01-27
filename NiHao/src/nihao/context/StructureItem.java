package nihao.context;

public abstract class StructureItem {
	protected String id;
	boolean singleton = true;
	Context owner;

	public StructureItem(Context owner, String id) {
		this.owner = owner;
		if (id != null)
			id = id.toLowerCase();
		this.id = id;
		owner.fullstructure.add(this);
	}

	/**
	 * Retorna el ID de la estructura
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * True si el elemento es un singleton
	 * 
	 * @return
	 */
	public boolean isSingleton() {
		return singleton;
	}

	/**
	 * Retorna la instancia del objeto reflejado en la estructura
	 * 
	 * @return
	 */
	public abstract Object get();

	/**
	 * Retorna true si el objeto es un singleton por completo (incluidas sus
	 * dependencias)
	 * 
	 * @return
	 */
	public abstract boolean canPreinstantiate();

	/**
	 * Retorna la clase de la instancia reflejada por la estructura
	 * 
	 * @return
	 */
	public abstract Class<?> getStructureClass();

	/**
	 * Realiza ajustes de finalizaci�n, en las estructuras
	 */
	public abstract void commit();
}
