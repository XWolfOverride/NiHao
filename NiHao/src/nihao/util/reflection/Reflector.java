package nihao.util.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import nihao.NiHaoException;
import nihao.util.Conversor;

public class Reflector {
	private static Map<Class<?>, Class<?>> primitiveMap = new HashMap<Class<?>, Class<?>>();
	private static final HashMap<String, Class<?>> CLASSALIAS = new HashMap<String, Class<?>>();
	static {
		primitiveMap.put(boolean.class, Boolean.class);
		primitiveMap.put(byte.class, Byte.class);
		primitiveMap.put(char.class, Character.class);
		primitiveMap.put(short.class, Short.class);
		primitiveMap.put(int.class, Integer.class);
		primitiveMap.put(long.class, Long.class);
		primitiveMap.put(float.class, Float.class);
		primitiveMap.put(double.class, Double.class);
		CLASSALIAS.put("boolean", boolean.class);
		CLASSALIAS.put("byte", byte.class);
		CLASSALIAS.put("char", char.class);
		CLASSALIAS.put("short", short.class);
		CLASSALIAS.put("int", int.class);
		CLASSALIAS.put("long", long.class);
		CLASSALIAS.put("float", float.class);
		CLASSALIAS.put("double", double.class);
		CLASSALIAS.put("boolean", boolean.class);
		CLASSALIAS.put("Byte", Byte.class);
		CLASSALIAS.put("Character", Character.class);
		CLASSALIAS.put("Short", Short.class);
		CLASSALIAS.put("Integer", Integer.class);
		CLASSALIAS.put("Long", Long.class);
		CLASSALIAS.put("Float", Float.class);
		CLASSALIAS.put("Double", Double.class);
		CLASSALIAS.put("Boolean", Boolean.class);
		CLASSALIAS.put("String", String.class);
		CLASSALIAS.put("HashMap", HashMap.class);
		CLASSALIAS.put("LiknedHashMap", LinkedHashMap.class);
	}

	public static HashMap<String, Object> getHashMapFromObject(Object o) {
		if (o == null)
			return null;
		try {
			HashMap<String, Object> result = new HashMap<String, Object>();
			Class<?> t = o.getClass();
			for (Method m : t.getMethods()) {
				if ((!m.getName().startsWith("get") && !m.getName().startsWith("is")) || m.getParameterTypes().length > 0)
					continue;
				String key = m.getName();
				if (key.equals("getClass"))
					continue;
				if (key.startsWith("is"))
					key = key.substring(2);
				else
					key = key.substring(3);
				key = key.substring(0, 1).toLowerCase() + key.substring(1);
				Object val = m.invoke(o);
				result.put(key, val);
			}
			return result;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ObjectType getObjectType(Object o) {
		if (o == null)
			return ObjectType.Unknown;
		Class<?> t = o.getClass();
		if (o instanceof String)
			return ObjectType.String;
		if (o instanceof byte[])
			return ObjectType.ByteArray;
		if (t.isArray())
			return ObjectType.Array;
		if (t.isPrimitive())
			return ObjectType.Primitive;
		return ObjectType.Object;
	}

	/**
	 * Coje la versi�n primitiva de una implementaci�n de primitiva, si la clase
	 * no es una implementaci�n de primitiva retorna null
	 * 
	 * @param cls
	 *            Class
	 * @return Class
	 */
	public static Class<?> getPrimitiveClass(Class<?> cls) {
		for (Class<?> primitive : primitiveMap.keySet())
			if (primitiveMap.get(primitive) == cls)
				return primitive;
		return null;
	}

	/**
	 * Retorna la implementacion de primitiva de una primitiva, si la clase no
	 * es primitiva retorna null
	 * 
	 * @param cls
	 *            Class
	 * @return Class
	 */
	public static Class<?> getPrimitiveImplementationClass(Class<?> cls) {
		if (primitiveMap.containsKey(cls))
			return primitiveMap.get(cls);
		return null;
	}

	/**
	 * Especifica si hay alg�n modo de conversion entre una clase y otra
	 * 
	 * @param a
	 *            Class
	 * @param b
	 *            Class
	 * @return boolean
	 */
	public static boolean isCompatible(Class<?> a, Class<?> b) {
		if (a == String.class)
			return true;
		if (a.isAssignableFrom(b))
			return true;
		if (Reflector.getPrimitiveImplementationClass(a) == b)
			return true;
		if (Reflector.getPrimitiveImplementationClass(b) == a)
			return true;
		return false;
	}

	/**
	 * Busca un metodo con tipos compatibles
	 * 
	 * @param c
	 *            Class
	 * @param methodName
	 *            String
	 * @param paramTypes
	 *            Class[]
	 * @return Method
	 */
	public static Method getCompatibleMethod(Class<?> c, String methodName, Class<?>... paramTypes) {
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (!m.getName().equals(methodName))
				continue;
			Class<?>[] actualTypes = m.getParameterTypes();
			if (actualTypes.length != paramTypes.length)
				continue;
			boolean found = true;
			for (int j = 0; j < actualTypes.length; j++) {
				if (paramTypes[j] == null)
					found = !actualTypes[j].isPrimitive();
				else if (!actualTypes[j].isAssignableFrom(paramTypes[j]))
					if (actualTypes[j].isPrimitive())
						found = primitiveMap.get(actualTypes[j]).equals(paramTypes[j]);
					else if (paramTypes[j].isPrimitive())
						found = primitiveMap.get(paramTypes[j]).equals(actualTypes[j]);
				if (!found)
					break;
			}
			if (found)
				return m;
		}
		return null;
	}

	/**
	 * Busca un constructor con tipos compatibles
	 * 
	 * @param c
	 *            Class
	 * @param paramTypes
	 *            Class[]
	 * @return Constructor
	 */
	public static Constructor<?> getCompatibleConstructor(Class<?> c, Class<?>... paramTypes) {
		Constructor<?>[] methods = c.getConstructors();
		for (int i = 0; i < methods.length; i++) {
			Constructor<?> m = methods[i];
			Class<?>[] actualTypes = m.getParameterTypes();
			if (actualTypes.length != paramTypes.length)
				continue;
			boolean found = true;
			for (int j = 0; j < actualTypes.length; j++) {
				if (paramTypes[j] == null)
					found = !actualTypes[j].isPrimitive();
				else if (!actualTypes[j].isAssignableFrom(paramTypes[j]))
					if (actualTypes[j].isPrimitive())
						found = primitiveMap.get(actualTypes[j]).equals(paramTypes[j]);
					else if (paramTypes[j].isPrimitive())
						found = primitiveMap.get(paramTypes[j]).equals(actualTypes[j]);
				if (!found)
					break;
			}
			if (found)
				return m;
		}
		return null;
	}

	/**
	 * Retorna el nombre de un getter a traves de el nombre de la propiedad
	 * 
	 * @param fieldName
	 *            String
	 * @return String true si el getter es de una propiedad booleana
	 */
	public static String getGetterName(String fieldName, boolean forBoolean) {
		fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return (forBoolean ? "is" : "get") + fieldName;
	}

	/**
	 * Retorna el nombre de un setter a traves de el nombre de la propiedad
	 * 
	 * @param fieldName
	 *            String
	 * @return String
	 */
	public static String getSetterName(String fieldName) {
		fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		return "set" + fieldName;
	}

	/**
	 * Retorna el método getter de una clase para un nombre de propiedad, se
	 * asume el primer método con el nombre del getter
	 * 
	 * @param cls
	 *            Class, dónde buscar el getter
	 * @param name
	 *            String, nombre de la propiedad a la que pertenece el setter
	 * @return Method
	 */
	public static Method getGetter(Class<?> cls, String name) {
		name = getGetterName(name, false);
		for (Method m : cls.getMethods())
			if (m.getName().equals(name))
				return m;
		name = getGetterName(name, true);
		for (Method m : cls.getMethods())
			if (m.getName().equals(name))
				return m;
		return null;
	}

	/**
	 * Retorna el método setter de una clase, para un nombre de propiedad, se
	 * asume el primero con el nombre de la propiedad, ya que un setter solo
	 * tiene un tipo
	 * 
	 * @param cls
	 *            Class, dónde buscar el setter
	 * @param name
	 *            String, nombre de la propiedad a la que pertenece el setter
	 * @return Method
	 */
	public static Method getSetter(Class<?> cls, String name) {
		name = getSetterName(name);
		for (Method m : cls.getMethods())
			if (m.getName().equals(name) && m.getParameterCount() == 1)
				return m;
		return null;
	}

	/**
	 * Retorna la clase desde un nombre dado, es como Class.forName, pero
	 * extendido
	 * 
	 * @param name
	 *            String
	 * @return Class
	 */
	public static Class<?> getClass(String name) {
		if (CLASSALIAS.containsKey(name))
			return CLASSALIAS.get(name);
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new NiHaoException("Unknown class " + name, e);
		}
	}

	/**
	 * Retorna true si la clase es primitiva o se puede cambiar a primitiva
	 * 
	 * @param cls
	 * @return
	 */
	public static boolean canBePrimitive(Class<?> cls) {
		if (cls.isPrimitive())
			return true;
		for (Class<?> p : primitiveMap.values())
			if (p == cls)
				return true;
		return false;
	}

	// @SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	public static <T> T compatibleCast(Object value, Class<T> cls) {
		if (value == null)
			return null;
		if (cls.isInstance(value))
			return cls.cast(value);
		if (cls == String.class)
			return (T) value.toString();
		Class<?> vc = value.getClass();
		if (Number.class.isInstance(value)) {
			Class<?> c = Reflector.getPrimitiveImplementationClass(cls);
			if (c == null)
				c = cls;
			Object result;
			Number n = (Number) value;
			if (c == Byte.class)
				result = n.byteValue();
			else if (c == Short.class)
				result = n.shortValue();
			else if (c == Integer.class)
				result = n.intValue();
			else if (c == Long.class)
				result = n.longValue();
			else if (c == Float.class)
				result = n.floatValue();
			else if (c == Double.class)
				result = n.doubleValue();
			else
				throw new NiHaoException("Can't cast from " + vc.getName() + " to " + cls.getName());
			return (T) result;
		}
		if (Character.class.equals(value)) {
			char ch = ((Character) value).charValue();
			if (cls == String.class)
				return (T) ((Object) ("" + ch));
			throw new NiHaoException("Can't cast from " + vc.getName() + " to " + cls.getName());
		}
		throw new NiHaoException("Can't cast from " + vc.getName() + " to " + cls.getName());
	}

	/**
	 * Retorna el tipo de array desde el tipo base
	 * 
	 * @param componentClass
	 * @return
	 */
	public Class<?> getArrayClass(Class<?> componentClass) {
		return Array.newInstance(componentClass, 0).getClass();
	}

	/**
	 * Coje el valor de un campo de un objecto, primero buscando su getter, y
	 * luego su field, si encuentra alguno retorna el valor
	 * 
	 * @param <T>
	 *            Tipo de retorno
	 * @param o
	 *            Objeto que contiene el campo
	 * @param fieldName
	 *            String nombre del campo
	 * @param cls
	 *            Clase del tipo de retorno
	 * @return T retorno
	 */
	public static <T> T getFromObject(Object o, String fieldName, Class<T> cls) {
		Class<?> ocls = o.getClass();
		Method m;
		try {
			m = getGetter(ocls, fieldName);
		} catch (Throwable t) {
			m = null;
		}
		if (m != null) {
			try {
				return Conversor.as(m.invoke(o), cls);
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable t) {
				throw new NiHaoException(t);
			}
		}
		Field f;
		try {
			f = ocls.getField(fieldName);
			if (f == null)
				throw new NiHaoException("Reflector field or getter missing");
			return Conversor.as(f.get(o), cls);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			throw new NiHaoException(t);
		}
	}

	/**
	 * Navega a través de la ruta de un objeto y retorna el valor, usa los
	 * getters del objeto, o el campo interno.<br>
	 * En caso de que algún campo sea null, la navegación se detiene y se
	 * retorna null
	 * 
	 * @param <T>
	 *            tipo de retorno
	 * @param o
	 *            Objeto por el que navegar
	 * @param path
	 *            Ruta de navegación
	 * @param cls
	 *            Clase de retorno
	 * @return Objeto del valor
	 */
	public static <T> T getPathValue(Object o, String path, Class<T> cls) {
		if (path == null)
			return null;
		if (!path.contains("."))
			return getFromObject(o, path, cls);
		String[] p = path.split("\\.");
		return getPathValue(o, p, 0, cls);
	}

	/**
	 * Navega a través de la ruta de un objeto y retorna el valor, usa los
	 * getters del objeto, o el campo interno.<br>
	 * En caso de que algún campo sea null, la navegación se detiene y se
	 * retorna null
	 * 
	 * @param <T>
	 *            tipo de retorno
	 * @param o
	 *            Objeto por el que navegar
	 * @param path
	 *            Ruta de navegación
	 * @param index
	 *            Indice por dónde comenzar la navegación (usualmente 0)
	 * @param cls
	 *            Clase de retorno
	 * @return Objeto del valor
	 */
	public static <T> T getPathValue(Object o, String[] path, int index, Class<T> cls) {
		if (path == null)
			return null;
		for (int i = index; i < path.length; i++) {
			o = getFromObject(o, path[i], Object.class);
			if (o == null)
				return null;
		}
		return Conversor.as(o, cls);
	}

	/**
	 * Return true if the Method is a setter method
	 * 
	 * @param m
	 *            Method method
	 * @return boolean
	 */
	public static boolean isSetter(Method m) {
		String name = m.getName();
		return name.startsWith("get") && name.length() > 3 && m.getParameterCount() == 1;
	}
}