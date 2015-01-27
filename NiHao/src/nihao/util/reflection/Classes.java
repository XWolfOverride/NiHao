package nihao.util.reflection;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;

import nihao.NiHao;
import nihao.util.Resources;

public class Classes {
	private static String[] localClasses;

	public static void listLoadedClasses(ClassLoader byClassLoader) {
		Class<?> clKlass = byClassLoader.getClass();
		System.out.println("Classloader: " + clKlass.getCanonicalName());
		while (clKlass != java.lang.ClassLoader.class) {
			clKlass = clKlass.getSuperclass();
		}
		try {
			java.lang.reflect.Field fldClasses = clKlass.getDeclaredField("classes");
			fldClasses.setAccessible(true);
			Vector<?> classes = (Vector<?>) fldClasses.get(byClassLoader);
			for (Iterator<?> iter = classes.iterator(); iter.hasNext();) {
				System.out.println("   Loaded " + iter.next());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void listLoadedClasses() {
		for (ClassLoader cl : Resources.getClassLoaders())
			listLoadedClasses(cl);
	}

	/**
	 * Returns all the jars contained inside this WebApplication
	 * 
	 * @return
	 */
	public static ArrayList<String> listWebAppJars() {
		ServletContext sctx = NiHao.getServletContext();
		ArrayList<String> jars = new ArrayList<String>();
		for (String jar : sctx.getResourcePaths("/WEB-INF/lib"))
			jars.add(jar);
		return jars;
	}

	/**
	 * Returns a list of all classes inside the WebApp
	 */
	private static void readLocalDirectory(String path, ArrayList<String> dest) {
		ServletContext sctx = NiHao.getServletContext();
		for (String p : sctx.getResourcePaths(path)) {
			if (p.charAt(p.length() - 1) == '/')
				readLocalDirectory(p, dest);
			else
				dest.add(p);
		}
	}

	private static String[] listWebAppClasses() {
		if (localClasses == null) {
			ArrayList<String> classes = new ArrayList<String>();
			readLocalDirectory("/WEB-INF/classes", classes);
			int i = 0;
			while (i < classes.size()) {
				String cls = classes.get(i);
				if (cls.endsWith(".class")) {
					cls = cls.substring(17, cls.length() - 6).replace('/', '.');
					classes.set(i, cls);
					i++;
				} else
					classes.remove(i);
			}
			localClasses = classes.toArray(new String[classes.size()]);
		}
		return localClasses;
	}

	public static ArrayList<Class<?>> getLocalClassesWithAnnotation(Class<? extends Annotation> annotation) {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		for (String clsPath : listWebAppClasses()) {
			try {
				Class<?> c = Class.forName(clsPath);
				if (c.getAnnotationsByType(annotation).length > 0)
					result.add(c);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
