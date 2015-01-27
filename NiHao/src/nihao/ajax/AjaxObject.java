package nihao.ajax;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Set;

import nihao.NiHaoException;
import nihao.util.Serializer;
import nihao.util.reflection.Classes;

public class AjaxObject {
	private static HashMap<String, AjaxObject> objects = new HashMap<String, AjaxObject>();
	private Class<?> c;
	private Object o;
	private HashMap<String, Method> methods;
	static {
		for (Class<?> c : Classes.getLocalClassesWithAnnotation(AjaxPublished.class))
			Register(c);
	}

	public static void Register(Class<?> c) {
		String name = c.getSimpleName();
		AjaxObject obj = objects.get(name);
		if (obj != null)
			throw new NiHaoException("Duplicated object. " + c.getName() + " was also registered with class " + obj.c.getName());
		objects.put(name, new AjaxObject(c));
	}

	static AjaxObject get(String name) {
		return objects.get(name);
	}

	private AjaxObject(Class<?> c) {
		this.c = c;
		try {
			o = c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new NiHaoException("Can't create instance of ajax object " + getName(), e);
		}
		if (methods == null) {
			methods = new HashMap<String, Method>();
			for (Method m : c.getMethods())
				if (m.getAnnotationsByType(AjaxPublished.class).length > 0)
					methods.put(m.getName(), m);
		}
	}

	public String getName() {
		return c.getSimpleName();
	}

	/**
	 * Return the ajax published methods
	 * 
	 * @return
	 */
	public String[] getPublicMethods() {
		Set<String> keys = methods.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * Return the parameter names of a method
	 * 
	 * @param key
	 *            String method name
	 * @return
	 */
	public String[] getPublicMethodParams(String key) {
		Method m = methods.get(key);
		if (m == null)
			return null;
		Parameter[] pars = m.getParameters();
		String[] result = new String[pars.length];
		for (int i = 0; i < pars.length; i++)
			result[i] = pars[i].getName();
		return result;
	}

	/**
	 * Invokes a object method
	 * 
	 * @param name
	 *            String method name
	 * @param request
	 *            String arguments in JSON object
	 * @return
	 */
	public Object invoke(String name, String request) {
		Method m = methods.get(name);
		if (m == null)
			return new NiHaoException("Ajax method not exists");
		HashMap<String, Object> typeMap = new HashMap<String, Object>();
		Parameter[] pars = m.getParameters();
		for (Parameter p : pars)
			typeMap.put(p.getName(), p.getParameterizedType());
		HashMap<String, Object> req = Serializer.deserializeFromJSON(request, null, typeMap);
		Object[] args = new Object[pars.length];
		for (int i = 0; i < pars.length; i++)
			args[i] = req.get(pars[i].getName());
		try {
			return m.invoke(o, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new NiHaoException(e);
		}
	}
}
