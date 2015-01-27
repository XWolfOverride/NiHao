package nihao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Resources {
	public static ArrayList<ClassLoader> clsloaders = new ArrayList<ClassLoader>();
	public static ArrayList<String> resourcespath = new ArrayList<String>();

	static {
		addClassLoader(Resources.class.getClassLoader());
		addClassLoader(InputStream.class.getClassLoader());
		addClassLoader(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Accede a recursos locales en el siguiente orden: Recursos del
	 * SystemClassLoader, Recursos del ClassLoader actual, Recursos del sistema
	 * de archivos, uno a uno los distintos ClassLoaders almacenados, uno a uno
	 * las distintas rutas de recursos.
	 */
	public static InputStream getResourceAsStream(String path) {
		try {
			return new FileInputStream(path);
		} catch (Exception e) {
		}
		;
		InputStream result;
		result = ClassLoader.getSystemResourceAsStream(path);
		if (result != null)
			return result;
		result = getResourceAsStreamInResourcesClassLoaders(path);
		if (result != null)
			return result;
		result = getResourceAsStreamInResourcesPaths(path);
		return result;
	}

	/**
	 * Retorna un recurso buscandolo en los RsourceClassLoaders
	 * 
	 * @param path
	 *            ruta del archivo
	 * @return InputStream con el archivo, o null si no se encuentra
	 */
	public static InputStream getResourceAsStreamInResourcesClassLoaders(String path) {
		for (ClassLoader cls : clsloaders) {
			InputStream result = cls.getResourceAsStream(path);
			if (result != null)
				return result;
		}
		return null;
	}

	/**
	 * Busca el recurso entre las rutas de recursos definidas y lo retorna
	 * 
	 * @param path
	 *            ruta relativa
	 * @return InputStream del archivo, o null si no se encontro
	 */
	public static InputStream getResourceAsStreamInResourcesPaths(String path) {
		InputStream result;
		for (String p : resourcespath) {
			try {
				result = new FileInputStream(p + path);
				return result;
			} catch (Exception e) {
			}
			;
		}
		return null;
	}

	/**
	 * Busca el recurso en un ClassLoader especifico, si no lo encuentra, lo
	 * busca usando el orden de getResourceAsStream
	 * 
	 * @param cl
	 * @param path
	 * @return
	 */
	public static InputStream getResourceAsStreamFromClassLoader(ClassLoader cl, String path) {
		InputStream result;
		result = cl.getResourceAsStream(path);
		if (result != null)
			return result;
		return getResourceAsStream(path);
	}

	public static String getResourcePath(String resourceName) {
		URL path = ClassLoader.getSystemResource(resourceName);
		if (path == null) {
			File f = new File(resourceName);
			if (f.exists())
				return f.getAbsolutePath();
			path = getResourcePathInResourcesClassLoaders(resourceName);
		}
		return UrlToPath(path);
	}

	private static String UrlToPath(URL url) {
		String result = url.getPath();
		return result.substring(1).replaceAll("%20", " ");
	}

	private static URL getResourcePathInResourcesClassLoaders(String path) {
		for (ClassLoader cls : clsloaders) {
			URL result = cls.getResource(path);
			if (result != null)
				return result;
		}
		return null;
	}

	/**
	 * A�ade un classLoader a la lista de b�squeda
	 * 
	 * @param c
	 *            ClassLoader
	 * @return true si se a�adio, no se a�aden duplicados
	 */
	public static boolean addClassLoader(ClassLoader c) {
		if (c == null)
			return false;
		if (!clsloaders.contains(c)) {
			clsloaders.add(c);
			return true;
		}
		return false;
	}

	/**
	 * Añade una ruta de recursos si no se a�adio anteriormente
	 * 
	 * @param path
	 *            ruta local
	 * @return true se se añadió
	 */
	public static boolean addResourcePath(String path) {
		if (!path.endsWith("\\"))
			path += '\\';
		for (String s : resourcespath) {
			if (s.equals(path))
				return false;
		}
		resourcespath.add(path);
		return true;
	}

	/**
	 * Return all the registered (and known) class loaders
	 * @return
	 */
	public static ClassLoader[] getClassLoaders() {
		return clsloaders.toArray(new ClassLoader[clsloaders.size()]);
	}

}
