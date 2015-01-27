package nihao.context;

public class StructureItemReference extends StructureItem {
	private String linkto;
	private StructureItem link;

	public StructureItemReference(Context owner, String id, String linkto) {
		super(owner, id);
		this.linkto = linkto.toLowerCase();
	}

	public StructureItem getLink() {
		if (link == null) {
			link = owner.getStructure(linkto);
			if (link == null)
				throw new ContextParseException("Cant find reference to " + linkto);
		}
		return link;
	}

	@Override
	public Object get() {
		return getLink().get();
	}

	@Override
	public Class<?> getStructureClass() {
		return getLink().getStructureClass();
	}

	@Override
	public boolean canPreinstantiate() {
		return getLink().canPreinstantiate();
	}
	
	@Override
	public void commit() {
	}

	@Override
	public String toString() {
		return "@" + linkto;
	}
}
