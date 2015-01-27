package nihao;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import nihao.context.Context;
import nihao.context.FileContext;
import nihao.db.ChangesetDataManager;
import nihao.db.Query;
import nihao.util.Conversor;
import nihao.util.Resources;

public class NiHao {
	public static final String Version = "0.9";
	static Context ctx;
	static NiHaoConf conf;
	static Map<String, String> initParameters;
	static Map<Class<?>, Object> wired = new HashMap<Class<?>, Object>();
	static ServletContext servletContext;

	static {
		Resources.addClassLoader(NiHao.class.getClassLoader());
		Resources.addClassLoader(Thread.currentThread().getContextClassLoader());
		init();
	}

	/**
	 * Inicia el core, este paso se realiza sólo una vez
	 */
	public static void init() {
		if (ctx != null)
			return;
		Resources.addClassLoader(Thread.currentThread().getContextClassLoader());
		ctx = new FileContext("META-INF/nihao.ctx");
		ctx.commit();
		// Changesets
		if (ctx.haveChangesets()) {
			ChangesetDataManager cdm = new ChangesetDataManager();
			cdm.runChagesets(ctx);
		}
	}

	/**
	 * Obtiene un objeto desde el contexto
	 * 
	 * @param name
	 *            String
	 * @return Object
	 */
	public static Object get(String name) {
		return ctx.get(name);
	}

	/**
	 * Obtiene un objeto desde el contexto, con el tipo especifico, y con el
	 * nombre del tipo
	 * 
	 * @param <T>
	 *            Tipo de retorno
	 * @param cls
	 *            Clase del tipo de retonro
	 * @return Instncia del tipo del parametro cls
	 */
	public static <T> T get(Class<T> cls) {
		return getAs(cls.getSimpleName(), cls);
	}

	/**
	 * Obtiene un string desde el contexto
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public static String getString(String name) {
		Object result = ctx.get(name);
		if (result == null)
			return null;
		return result.toString();
	}

	/**
	 * Obtiene un entero desde el contexto
	 * 
	 * @param name
	 *            String
	 * @return int
	 */
	public static int getInt(String name) {
		return Integer.parseInt(getString(name));
	}

	/**
	 * Obtiene un entero largo desde el contexto
	 * 
	 * @param name
	 *            String
	 * @return long
	 */
	public static long getLong(String name) {
		return Long.parseLong(getString(name));
	}

	/**
	 * Obtiene un doble desde el contexto
	 * 
	 * @param name
	 *            String
	 * @return double
	 */
	public static double getDouble(String name) {
		return Double.parseDouble(getString(name));
	}

	/**
	 * Obtiene un objeto desde el contexto con el tipo especifico
	 * 
	 * @param <T>
	 *            tipo de retorno
	 * @param name
	 *            String
	 * @param cls
	 *            Class clase re retorno
	 * @return
	 */
	public static <T> T getAs(String name, Class<T> cls) {
		Object result = ctx.get(name);
		if (result == null)
			return null;
		if (cls.isInstance(result))
			return cls.cast(result);
		return null;
	}

	/**
	 * Retona una query desde el contexto
	 * 
	 * @param name
	 *            String nombre de la query
	 * @return Query
	 */
	public static Query getQuery(String name, String engine) {
		return ctx.getQuery(name, engine);
	}

	/**
	 * Retorna la página diseñada para una URL determinada
	 * 
	 * @param url
	 *            String
	 * @return Page
	 */
	public static Page getPage(String url) {
		if (!url.startsWith("/"))
			url = "/" + url;
		return ctx.getPage(url);
	}

	/**
	 * Retorna true en caso de que el valor del bean sea positivo
	 * 
	 * @param beanName
	 *            String
	 * @return boolean
	 */
	public static boolean isYes(String beanName) {
		return Conversor.isYes(getString(beanName));
	}

	/**
	 * Comprueba que a y b son iguales, usando su .equals, y controlando nulos
	 * 
	 * @param a
	 *            Object
	 * @param b
	 *            Object
	 * @return boolean
	 */
	public static boolean equals(Object a, Object b) {
		if (a == b)
			return true;
		if (a == null)
			return false;
		if (b == null)
			return false;
		return a.equals(b);
	}

	public static NiHaoConf getConf() {
		if (conf == null)
			conf = get(NiHaoConf.class);
		return conf;
	}

	/**
	 * Retorna un EnvEntry
	 * 
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getEnvEntry(String key) {
		try {
			javax.naming.Context miCtx = (javax.naming.Context) (new InitialContext()).lookup("java:comp/env");
			return (String) miCtx.lookup(key);
		} catch (NamingException e) {
			return null;
		}
	}

	/**
	 * Retorna un InitParameter
	 * 
	 * @param key
	 *            String
	 * @return String
	 */
	public static String getInitParameter(String key) {
		if (initParameters.containsKey(key))
			return initParameters.get(key);
		return null;
	}

	/**
	 * Realiza el enlace de los Autowired de una instancia.<br>
	 * Este método se usa tanto internamente para cada clase instanciada desde
	 * el contexto, como para iniciar los Autowired de un objeto ya instanciado.
	 * 
	 * @param o
	 *            Object objeto a autoenlazar
	 */
	public static void autoWire(Object o) {
		if (o == null)
			return;
	}

	/**
	 * Return the application servlet context
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}
}
