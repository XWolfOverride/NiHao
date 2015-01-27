package nihao.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import nihao.NiHaoException;
import nihao.tokenizer.QToken;
import nihao.tokenizer.QTokenType;
import nihao.tokenizer.QTokenizer;
import nihao.util.reflection.Reflector;

/**
 * Serializa objetos
 * 
 * @author ivan.dominguez
 * 
 */
public class Serializer {
	final static String TextEncoding = "UTF-8";

	/**
	 * Serializa un objeto a un Stream
	 * 
	 * @param o
	 *            Objeto serializable
	 * @param os
	 *            OutputStream destino
	 */
	public static void serialize(Serializable o, OutputStream os) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(o);
			oos.flush();
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Serializa un objeto a un array de bytes
	 * 
	 * @param o
	 *            Objecto serializable
	 * @return Array de bytes con el contenido
	 */

	public static byte[] serialize(Serializable o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serialize(o, baos);
			baos.close();
			return baos.toByteArray();
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Serializa un objeto a un stream, en fomrato "ChunkedBlocks" definido por
	 * <code>core.io.IoTools.writeChunkedBlockToStream</code><br>
	 * Este m�todo usa un paso por RAM del objeto serializado, pudiendo causar
	 * un alto uso de esta en serialiaciones grandes.
	 * 
	 * @param o
	 *            Objeto serializable
	 * @param os
	 *            OutputStream
	 */
	public static void serializeChunckedBlock(Serializable o, OutputStream os) {
		IoUtil.writeChunkedBlockToStream(serialize(o), os);
	}

	/**
	 * Serializa un array de objetos a un array de bytes
	 * 
	 * @param a
	 *            Array de objetos
	 * @return Array de bytes
	 * @throws IOException
	 */
	public static byte[] serializeArray(Object[] a) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(a.getClass().getComponentType());
			oos.writeInt(a.length);
			for (Object o : a) {
				oos.writeObject(o);
			}
			oos.close();
			byte[] result = baos.toByteArray();
			baos.close();
			return result;
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Deserializa a partir de un stream
	 * 
	 * @param inputStream
	 *            InputStream con el serializado
	 * @return Serializable
	 */
	public static Serializable deserialize(InputStream inputStream) {
		try {
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			return (Serializable) ois.readObject();
		} catch (IOException e) {
			throw new NiHaoException(e);
		} catch (ClassNotFoundException e) {
			throw new NiHaoException(e);
		}
	}

	public static <T> T deserialize(InputStream inputStream, Class<T> resultType) {
		Serializable o = deserialize(inputStream);
		if (o == null)
			return null;
		if (resultType.isAssignableFrom(o.getClass()))
			return null;
		return resultType.cast(o);
	}

	/**
	 * Deserializa a partir de un array de bytes
	 * 
	 * @param data
	 *            Array de bytes
	 * @return Objet
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Serializable deserialize(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			Serializable result = deserialize(bais);
			bais.close();
			return result;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	public static <T> T deserialize(byte[] data, Class<T> resultType) {
		Serializable o = deserialize(data);
		if (o == null)
			return null;
		if (resultType.isAssignableFrom(o.getClass()))
			return null;
		return resultType.cast(o);
	}

	/**
	 * Deserializa un array desde un array de bytes
	 * 
	 * @param data
	 *            Array de bytes
	 * @return Array de objetos
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] deserializeArray(byte[] data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Class<T> aType = (Class<T>) ois.readObject();
			int aLength = ois.readInt();
			T[] result = (T[]) Array.newInstance(aType, aLength);
			for (int i = 0; i < aLength; i++) {
				result[i] = (T) ois.readObject();
			}
			ois.close();
			bais.close();
			return result;
		} catch (IOException e) {
			throw new NiHaoException(e);
		} catch (ClassNotFoundException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Serializa un objeto a un XML
	 * 
	 * @param o
	 *            objeto
	 * @return String con el XML
	 */
	public static String serializeToXML(Object o) {
		return serializeToXML(o, TextEncoding);
	}

	/**
	 * Serializa un objeto a un XML con un encoding
	 * 
	 * @param o
	 *            Objeto
	 * @param encoding
	 *            Texto del encoding
	 * @return String con el XML
	 */
	public static String serializeToXML(Object o, String encoding) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmle = new XMLEncoder(baos);
		xmle.writeObject(o);
		xmle.close();
		try {
			return new String(baos.toByteArray(), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported charset: " + encoding, e);
		}
	}

	/**
	 * Deserializa desde un XML
	 * 
	 * @param xml
	 *            XML con el objeto
	 * @return Objeto
	 */
	public static Object deserializeFromXML(String xml) {
		return deserializeFromXML(xml, TextEncoding);
	}

	/**
	 * Deserializa desde un XML con un enconding especifico
	 * 
	 * @param xml
	 *            XML
	 * @param encoding
	 *            Código de encoding
	 * @return Objeto
	 */
	public static Object deserializeFromXML(String xml, String encoding) {
		ByteArrayInputStream bais;
		try {
			bais = new ByteArrayInputStream(xml.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported charset: " + encoding, e);
		}
		XMLDecoder xmld = new XMLDecoder(bais);
		Object result = xmld.readObject();
		xmld.close();
		return result;
	}

	/**
	 * Deserializes from any object to a JSON string. Only public geters and
	 * seters are used
	 * 
	 * @param data
	 *            Object bean.
	 * @return String JSON
	 */
	public static String serializeToJSON(Object data) {
		StringBuilder sb = new StringBuilder();
		serializeToJSON(data, sb);
		return sb.toString();
	}

	private static void serializeToJSON(Object data, StringBuilder dest) {
		if (data == null) {
			dest.append("");
		} else if (data instanceof String) {
			dest.append("\"");
			dest.append(jsStringEncode((String) data));
			dest.append("\"");
		} else {
			Class<?> c = data.getClass();
			if (c.isPrimitive() || data instanceof BigInteger || data instanceof BigDecimal || data instanceof Integer || data instanceof Long || data instanceof Float || data instanceof Double) {
				dest.append(data);
			} else if (c.isArray() || data instanceof Collection<?>) {
				dest.append('[');
				boolean first = true;
				if (c.isArray()) {
					int len = Array.getLength(data);
					ArrayList<Object> list = new ArrayList<Object>(len);
					for (int i = 0; i < len; i++)
						list.add(Array.get(data, i));
					data = list;
				}
				for (Object o : (Collection<?>) data) {
					if (first)
						first = false;
					else
						dest.append(", ");
					serializeToJSON(o, dest);
				}
				dest.append(']');
			} else {
				dest.append('{');
				HashMap<?, ?> hm = data instanceof HashMap ? (HashMap<?, ?>) data : Reflector.getHashMapFromObject(data);
				boolean first = true;
				for (Entry<?, ?> e : hm.entrySet()) {
					String key = e.getKey().toString();
					Object val = e.getValue();
					if (val == null)
						continue;
					if (first)
						first = false;
					else
						dest.append(", ");
					dest.append("\"");
					dest.append(jsStringEncode(key));
					dest.append("\":");
					serializeToJSON(val, dest);
				}
				dest.append('}');
			}
		}
	}

	private static String jsStringEncode(String s) {
		return s.replace("\"", "\\\"");
	}

	/**
	 * Deserializes from JSON to a:<br/>
	 * · String if the JSON contains single String.<br/>
	 * · Integer or Double if the JSON contains single number.<br/>
	 * · Object[] if the JSON contains an array.<br/>
	 * · HashMap for complex objects.<br>
	 * For typed conversions use deserializeFromJSON(String,type)
	 * 
	 * @param json
	 *            String JSON
	 * @return Object
	 */
	public static Object deserializeFromJSON(String json) {
		return deserializeFromJSON(new QTokenizer(json), null, null);
	}

	/**
	 * Deserializes from JSON to a desired type if compatible
	 * 
	 * @param json
	 *            String JSON
	 * @param as
	 *            Class of object
	 * @param typeMap
	 *            HashMap of classes or other typeMap to inform of the type tree
	 * @return Object
	 */
	public static <T> T deserializeFromJSON(String json, Class<T> as, HashMap<String, Object> typeMap) {
		if (json.length() == 0)
			return null;
		return deserializeFromJSON(new QTokenizer(json), as, typeMap);
	}

	@SuppressWarnings({ "unchecked" })
	private static <T> T deserializeFromJSON(QTokenizer tkz, Class<T> as, HashMap<String, Object> typeMap) {
		QToken t = tkz.next();
		switch (t.getType()) {
		case LITERAL:
			return as == null ? (T) t.getValue() : as.cast(t.getValue());
		case NUMBER:
			return Conversor.StringToNumber(t.getValue(), as);
		case SYMBOL:
			if ("{".equals(t.getValue())) {
				T o = null;
				if (as == null)
					o = (T) new HashMap<String, Object>();
				else
					try {
						o = as.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new NiHaoException(e);
					}
				t = tkz.next();
				while (!"}".equals(t.getValue())) {
					if (t.getType() != QTokenType.LITERAL && t.getType() != QTokenType.WORD)
						throw new NiHaoException("JSON type error: " + t.getValue());
					String key = t.getValue();
					Class<?> vcls;
					Method m = null;
					if (typeMap != null && typeMap.get(key) instanceof Class)
						vcls = (Class<?>) typeMap.get(key);
					else if (as == null)
						vcls = null;
					else {
						m = Reflector.getSetter(as, key);
						vcls = m == null ? null : m.getParameterTypes()[0];
					}
					if (!":".equals(tkz.next().getValue()))
						throw new NiHaoException("JSON separator error: " + t.getValue());
					HashMap<String, Object> subTypeMap = typeMap != null && typeMap.get(key) instanceof HashMap ? (HashMap<String, Object>) typeMap.get(key) : null;
					Object val = deserializeFromJSON(tkz, vcls, subTypeMap);
					if (as == null || o instanceof HashMap)
						((HashMap<Object, Object>) o).put(key, val);
					else
						try {
							m.invoke(o, val);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new NiHaoException(e);
						}
					t = tkz.next();
					if (",".equals(t.getValue()))
						t = tkz.next();
				}
				return o;
			} else if ("[".equals(t.getValue())) {
				ArrayList<Object> buff = new ArrayList<Object>();
				Class<?> vcls = as == null ? null : as.getComponentType();
				t = tkz.patrol();
				while (!"]".equals(t.getValue())) {
					buff.add(deserializeFromJSON(tkz, vcls, typeMap));
					t = tkz.next();
					if (",".equals(t.getValue()))
						t = tkz.patrol();
				}
				t = tkz.next();
				if (as == null)
					return (T) Conversor.toArray(buff, Object.class);
				else if (as.isArray())
					return (T) Conversor.toArray(buff, vcls);
				else
					return (T) buff;
			}
		default:
			throw new NiHaoException("JSON error: " + t.getValue());
		}
	}
}
