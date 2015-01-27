package nihao.context;

import nihao.NiHaoException;
import nihao.Workset;
import nihao.util.reflection.Reflector;

public class StructureItemWorkset extends StructureItem {
	private String className;
	private Class<?> cls;

	public StructureItemWorkset(Context owner, String id) {
		super(owner, id);
	}

	@Override
	public Object get() {
		try {
			return cls.newInstance();
		} catch (IllegalArgumentException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		} catch (InstantiationException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		} catch (IllegalAccessException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		}
	}

	@Override
	public Class<?> getStructureClass() {
		return cls;
	}

	@Override
	public boolean canPreinstantiate() {
		return false; // never for worksets
	}

	@Override
	public void commit() {
		cls = Reflector.getClass(className);
		if (!Workset.class.isAssignableFrom(cls))
			throw new NiHaoException("Workset '" + id + "' class '" + className + "' must extend Workset");
		className = null;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return "workset " + id + " class " + className + ";";
	}
}
