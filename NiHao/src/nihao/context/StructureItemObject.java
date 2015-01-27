package nihao.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import nihao.util.reflection.Reflector;

public class StructureItemObject extends StructureItem {
	private ArrayList<StructureItem> constructor = new ArrayList<StructureItem>();
	private String clsName;
	private Class<?> cls;
	private HashMap<String, StructureItem> flds = new HashMap<String, StructureItem>();

	public StructureItemObject(Context owner, String id) {
		super(owner, id);
	}

	public void addConstructor(StructureItem item) {
		constructor.add(item);
	}

	public void setObjectClass(String clsName) {
		this.clsName = clsName;
	}

	public void setParam(String name, StructureItem item) {
		flds.put(name, item);
	}

	@Override
	public Object get() {
		Class<?>[] ctorpt = new Class<?>[constructor.size()];
		for (int i = 0; i < ctorpt.length; i++)
			ctorpt[i] = constructor.get(i).getStructureClass();
		Constructor<?> ctor = Reflector.getCompatibleConstructor(cls, ctorpt);
		if (ctor == null)
			throw new ContextParseException("Can't find constructor for class " + cls.getName() + " defined in " + id);
		Object[] ctorpv = new Object[constructor.size()];
		for (int i = 0; i < ctorpv.length; i++)
			ctorpv[i] = constructor.get(i).get();
		Object result;
		try {
			result = ctor.newInstance(ctorpv);
		} catch (IllegalArgumentException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		} catch (InstantiationException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		} catch (IllegalAccessException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		} catch (InvocationTargetException e) {
			throw new ContextParseException("Error instantiating class " + cls.getName() + " defined in " + id, e);
		}
		for (String name : flds.keySet()) {
			StructureItem item = flds.get(name);
			Method m = Reflector.getSetter(cls, name);
			boolean setted = m != null;
			Object v = item.get();
			if (setted)
				try {
					m.invoke(result, v);
				} catch (IllegalArgumentException e) {
					setted = false;
				} catch (IllegalAccessException e) {
					setted = false;
				} catch (InvocationTargetException e) {
					setted = false;
				}
			Class<?> c = cls;
			while (!setted && c != null) {
				for (Field f : c.getDeclaredFields())
					if (f.getName().equals(name))
						try {
							f.setAccessible(true);
							f.set(result, v);
							setted = true;
							break;
						} catch (IllegalAccessException e) {
							throw new ContextParseException("Error setting property '" + name + "' of class " + cls.getName() + " defined in " + id, e);
						}
				c = c.getSuperclass();
			}
			if (!setted)
				throw new ContextParseException("Can't find property '" + name + "' or setter in class " + cls.getName() + " defined in " + id);
		}
		return result;
	}

	@Override
	public Class<?> getStructureClass() {
		return cls;
	}

	@Override
	public boolean canPreinstantiate() {
		if (!singleton)
			return false;
		for (StructureItem i : constructor)
			if (!i.canPreinstantiate())
				return false;
		for (String m : flds.keySet())
			if (!flds.get(m).canPreinstantiate())
				return false;
		return true;
	}

	@Override
	public void commit() {
		cls = Reflector.getClass(clsName);
		clsName = null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!singleton)
			sb.append("multi ");
		sb.append("object ");
		if (id != null) {
			sb.append(id);
			sb.append(' ');
		}
		sb.append("class ");
		if (cls == null)
			sb.append("<null>");
		else
			sb.append(cls.getName());
		sb.append('{');
		if (constructor.size() > 0) {
			sb.append('(');
			for (int i = 0; i < constructor.size(); i++) {
				if (i != 0)
					sb.append(',');
				sb.append(constructor.get(i).toString());
			}
			sb.append(")\n");
		} else
			sb.append('\n');
		for (String m : flds.keySet()) {
			sb.append(' ');
			sb.append(m);
			sb.append('=');
			sb.append(flds.get(m).toString());
			sb.append('\n');
		}
		sb.append("}");
		return sb.toString();
	}
}
