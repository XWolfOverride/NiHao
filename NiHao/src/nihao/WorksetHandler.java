package nihao;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nihao.util.reflection.Reflector;

public class WorksetHandler implements Serializable {
	private static final long serialVersionUID = 1L;
	transient private HashMap<String, Method> get = new HashMap<String, Method>();
	transient private HashMap<String, Method> put = new HashMap<String, Method>();
	transient private HashMap<String, Method> run = new HashMap<String, Method>();

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		get = new HashMap<String, Method>();
		put = new HashMap<String, Method>();
		run = new HashMap<String, Method>();
	}

	private String wname;
	private Workset w;
	private Class<Workset> wc;

	@SuppressWarnings("unchecked")
	public WorksetHandler(String name) {
		wname = name;
		Object o = NiHao.get(name);
		if (o == null)
			throw new NiHaoException("Workset '" + name + "' dont exist!");
		wc = (Class<Workset>) o.getClass();
		if (!(o instanceof Workset))
			throw new NiHaoException("Workset '" + name + "' class '" + wc.getCanonicalName() + "' must extend Workset");
		w = (Workset) o;
	}

	/**
	 * Invoca el getter del workset, el getter puede tener definido
	 * adicionalmente un parametro de tipo WebCall, para pasarle la llamada a la
	 * hora de realizar el getter.
	 * 
	 * @param name
	 *            String. Nombre de la propiedad a leer
	 * @param cw
	 *            WebCall actual (sera pasado en la invocación, si esta lo
	 *            define)
	 * @return Object, Valor retornado por el getter.
	 */
	public Object get(String name, WebCall cw) {
		Method m;
		if (get.containsKey(name))
			m = get.get(name);
		else {
			m = Reflector.getGetter(wc, name);
			if (m == null)
				throw new NiHaoException("Workset '" + wname + "' dont have getter for property '" + name + "'!");
			get.put(name, m);
		}
		try {
			Class<?>[] pars = m.getParameterTypes();
			if (pars.length == 0)
				return m.invoke(w);
			if (pars.length == 1)
				if (pars[0].isAssignableFrom(WebCall.class))
					return m.invoke(w, wc);
			throw new NiHaoException("Unknown parameters in workset getter, only none or WebCall supported");
		} catch (IllegalArgumentException e) {
			throw new NiHaoException(e);
		} catch (IllegalAccessException e) {
			throw new NiHaoException(e);
		} catch (InvocationTargetException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Invoca el setter del workset, es obligación que los setters se definan en
	 * el workset de la página.<br>
	 * Adicionalmente se puede definir un parametro WebCall, en el que se pasara
	 * el WebCall de la página.
	 * 
	 * @param name
	 *            nombre de propiedad
	 * @param value
	 *            valor a poner
	 */
	public void put(String name, Object value) {
		Method m;
		if (put.containsKey(name))
			m = put.get(name);
		else {
			m = Reflector.getSetter(wc, name);
			if (m == null)
				throw new NiHaoException("Workset '" + wname + "' dont have setter for property '" + name + "'!");
			put.put(name, m);
		}
		try {
			Class<?>[] pars = m.getParameterTypes();
			if (pars.length == 1) {
				m.invoke(w, value);
				return;
			}
			if (pars.length == 2)
				if (pars[0].isAssignableFrom(WebCall.class)) {
					m.invoke(w, wc, value);
					return;
				} else if (pars[1].isAssignableFrom(WebCall.class)) {
					m.invoke(w, value, wc);
					return;
				}
			throw new NiHaoException("Unknown parameters in workset setter. Only (<type>), (<type>,WebCall) or (WebCall,<type>) supported");
		} catch (IllegalArgumentException e) {
			throw new NiHaoException(e);
		} catch (IllegalAccessException e) {
			throw new NiHaoException(e);
		} catch (InvocationTargetException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Ejecuta un método del workset
	 * 
	 * @param name
	 *            Nombre del método
	 * @param call
	 *            WebCall de la llamada, que se pasará al método
	 */
	public void run(String name, WebCall call) {
		Method m;
		if (run.containsKey(name))
			m = run.get(name);
		else {
			m = Reflector.getCompatibleMethod(wc, name, WebCall.class);
			if (m == null)
				throw new NiHaoException("Workset '" + wname + "' dont have callable method '" + name + "'!");
			run.put(name, m);
		}
		try {
			m.invoke(w, call);
		} catch (IllegalArgumentException e) {
			throw new NiHaoException(e);
		} catch (IllegalAccessException e) {
			throw new NiHaoException(e);
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t == null)
				throw new NiHaoException(e);
			if (t instanceof NiHaoException)
				throw (NiHaoException) t;
			throw new NiHaoException(t);
		}
	}

	/**
	 * Ejecuta el workset (realiza los setters y los invoke especificados en los
	 * parametros).<br>
	 * Los parametros han de empezar por "::" para que los identifique el
	 * workset como propios. el resto de parametros no serán procesados por el
	 * setter del workset.<br>
	 * Los parametros que empiezen por "@" notifican que se invocarán esos
	 * métodos del workset despues de terminar con los setters. Se ejecutarán en
	 * el orden definido.
	 * 
	 * @param map
	 *            Map, Mapa con los parametros en forma de String, String[]
	 * @param call
	 *            WebCall de la página
	 */
	public void execute(Map<String, String[]> map, WebCall call) {
		ArrayList<String> toRun = new ArrayList<String>(5);
		// Setters (::<name>)
		for (String k : map.keySet())
			if (k.startsWith("::")) {
				String[] v = map.get(k);
				if (v.length == 1)
					put(k.substring(2), v[0]);
				else
					put(k.substring(2), v);
			} else if (k.startsWith("@"))
				toRun.add(k.substring(1));
		// Invokes (@<name>)
		for (String k : toRun)
			run(k, call);
	}
}
