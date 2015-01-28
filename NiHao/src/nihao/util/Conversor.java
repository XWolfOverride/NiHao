package nihao.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.DatatypeConverter;

import nihao.NiHaoException;
import nihao.types.TextEncoding;

public class Conversor {
	static final String ASCIICharset = "ISO-8859-1";
	static final String UTF8Charset = "UTF-8";
	static final char[] HEX_CHAR_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Genera un String con el hexadecimal de los datos
	 * 
	 * @param data
	 *            Array de bytes
	 * @return string en hexadecimal
	 */
	public static String bytesToHex(byte[] data) {
		char[] result = new char[data.length * 2];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			result[j++] = HEX_CHAR_TABLE[(b & 0xF0) >> 4];
			result[j++] = HEX_CHAR_TABLE[b & 0x0F];
		}
		return new String(result);
	}

	/**
	 * Pasa un string en hexadecimal a datos binarios
	 * 
	 * @param hex
	 *            String en hexadecimal
	 * @return Array de bytes con los datos
	 */
	public static byte[] hexToBytes(String hex) {
		byte[] result = new byte[hex.length() / 2];
		char[] hexc = hex.toCharArray();
		int j = 0;
		for (int i = 0; i < result.length; i++) {
			int b = charHexNoob(hexc[j++]) << 4;
			b = b | charHexNoob(hexc[j++]);
			result[i] = (byte) b;
		}
		return result;
	}

	private static int charHexNoob(char c) {
		for (int i = 0; i < HEX_CHAR_TABLE.length; i++)
			if (HEX_CHAR_TABLE[i] == c)
				return i;
		return -1;
	}

	/**
	 * Genera un string ISO-8859-1 a partir de bytes
	 * 
	 * @param data
	 *            Array de bytes
	 * @return String
	 */
	public static String bytesToASCII(byte[] data) {
		try {
			return new String(data, ASCIICharset);
		} catch (UnsupportedEncodingException e) {
			return new String(data);
		}
	}

	/**
	 * Genera un Array de bytes de un string en ISO-8859-1
	 * 
	 * @param ascii
	 *            String
	 * @return Array de bytes
	 */
	public static byte[] asciiToBytes(String ascii) {
		try {
			return ascii.getBytes(ASCIICharset);
		} catch (UnsupportedEncodingException e) {
			return ascii.getBytes();
		}
	}

	/**
	 * Genera un string a partir de unos bytes en UTF-8
	 * 
	 * @param data
	 *            Array de bytes
	 * @return String
	 */
	public static String bytesToUTF8(byte[] data) {
		try {
			return new String(data, UTF8Charset);
		} catch (UnsupportedEncodingException e) {
			return new String(data);
		}
	}

	/**
	 * Genera un array de bytes a partir de una string en UTF-8
	 * 
	 * @param ascii
	 *            String
	 * @return Array de bytes
	 */
	public static byte[] utf8ToBytes(String ascii) {
		try {
			return ascii.getBytes(UTF8Charset);
		} catch (UnsupportedEncodingException e) {
			return ascii.getBytes();
		}
	}

	/**
	 * Genera una string con los daots en base64
	 * 
	 * @param data
	 *            Array de bytes
	 * @return String
	 */
	public static String bytesToBase64(byte[] data) {
		return DatatypeConverter.printBase64Binary(data);
	}

	/**
	 * genera un Array de bytes a partir de su Base64
	 * 
	 * @param data64
	 *            String con el Base64
	 * @return Array de bytes
	 */
	public static byte[] base64ToBytes(String data64) {
		return DatatypeConverter.parseBase64Binary(data64);
	}

	/**
	 * genera un Array de bytes a partir de su Base64
	 * 
	 * @param data64
	 *            String con el Base64
	 * @return Array de bytes
	 */
	public static byte[] base64ToBytes(byte[] data64) {
		return DatatypeConverter.parseBase64Binary(bytesToASCII(data64));
	}

	/**
	 * Array de bytes a binario
	 * 
	 * @param data
	 * @return
	 */
	public static String bytesToBin(byte[] data) {
		char[] result = new char[data.length * 9];
		int j = 0;
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			result[j++] = ((b & 0x80) > 0) ? '1' : '0';
			result[j++] = ((b & 0x40) > 0) ? '1' : '0';
			result[j++] = ((b & 0x20) > 0) ? '1' : '0';
			result[j++] = ((b & 0x10) > 0) ? '1' : '0';

			result[j++] = ((b & 0x08) > 0) ? '1' : '0';
			result[j++] = ((b & 0x04) > 0) ? '1' : '0';
			result[j++] = ((b & 0x02) > 0) ? '1' : '0';
			result[j++] = ((b & 0x01) > 0) ? '1' : '0';
			result[j++] = '-';
		}
		return new String(result);
	}

	/**
	 * Pasa un array de bytes a un string intentando detectar su encoding,
	 * admite BOM
	 * 
	 * @param data
	 * @return
	 */
	public static String bytesToString(byte[] data) {
		TextEncoding enc = getStringEncoding(data);
		switch (enc) {
		case ASCII:
			return bytesToASCII(data);
		case UTF8:
			return bytesToUTF8(data);
		case UTF16LE:
			return silentEncode(data, "UTF-16LE");
		case UTF16BE:
			return silentEncode(data, "UTF-16BE");
		default:
			throw new RuntimeException("32Bits Encoding not suported");
		}
	}

	/**
	 * Pasa un array de bytes a un string intentando detectar su encoding,
	 * admite BOM
	 * 
	 * @param data
	 *            byte[]
	 * @param maxlen
	 *            int maximo de la cadena a retornar
	 * @return String
	 */
	public static String bytesToString(byte[] data, int maxlen) {
		TextEncoding enc = getStringEncoding(data);
		switch (enc) {
		case ASCII:
			return silentEncode(data, "ISO-8859-1", maxlen);
		case UTF8:
			return silentEncode(data, "UTF-8", maxlen);
		case UTF16LE:
			return silentEncode(data, "UTF-16LE", maxlen);
		case UTF16BE:
			return silentEncode(data, "UTF-16BE", maxlen);
		default:
			throw new RuntimeException("32Bits Encoding not suported");
		}
	}

	/**
	 * Controla la excepcion del econding
	 * 
	 * @param data
	 *            byte[] de datos
	 * @param encode
	 *            String con la descripcion del encoding
	 * @return
	 */
	private static String silentEncode(byte[] data, String encode) {
		try {
			return new String(data, encode);
		} catch (Exception e) {
			return bytesToASCII(data);
		}
	}

	/**
	 * Controla la excepcion del econding
	 * 
	 * @param data
	 *            byte[] de datos
	 * @param encode
	 *            String con la descripcion del encoding
	 * @param maxlen
	 *            int maximo de la cadena a retornar
	 * @return
	 */
	private static String silentEncode(byte[] data, String encode, int maxlen) {
		try {
			if (maxlen > data.length)
				maxlen = data.length;
			return new String(data, 0, maxlen, encode);
		} catch (Exception e) {
			return bytesToASCII(data);
		}
	}

	/**
	 * Detectamos el encoding de un string con su BOM o su inicio
	 * 
	 * @param data
	 * @return encoding
	 */
	public static TextEncoding getStringEncoding(byte[] data) {
		int lng = data.length;
		if (lng == 0)
			return TextEncoding.ASCII;
		// BOM
		if (lng >= 3 && data[0] == 0xEF && data[1] == 0xBB && data[2] == 0xBF)
			return TextEncoding.UTF8;
		if (lng >= 2 && data[0] == 0xFF && data[1] == 0xFE)
			return TextEncoding.UTF16LE;
		if (lng >= 2 && data[0] == 0xFE && data[1] == 0xFF)
			return TextEncoding.UTF16BE;
		if (lng >= 4 && data[0] == 0xFF && data[1] == 0xFE && data[2] == 0x00 && data[3] == 0x00)
			return TextEncoding.UTF32LE;
		if (lng >= 4 && data[0] == 0x00 && data[1] == 0x00 && data[2] == 0xFE && data[3] == 0xFF)
			return TextEncoding.UTF32BE;
		// Inicio del texto (autodeteccion)
		if (lng >= 4 && data[0] != 0x00 && data[1] == 0x00 && data[2] != 0x00 && data[3] == 0x00)
			return TextEncoding.UTF16LE;
		if (lng >= 4 && data[0] == 0x00 && data[1] != 0x00 && data[2] == 0x00 && data[3] != 0x00)
			return TextEncoding.UTF16BE;
		if (lng >= 2 && data[0] != 0x00 && data[1] == 0x00)
			return TextEncoding.UTF16LE;
		if (lng >= 2 && data[0] == 0x00 && data[1] != 0x00)
			return TextEncoding.UTF16BE;
		// Se asume ASCII
		return TextEncoding.ASCII;
	}

	/**
	 * Pasa una coleccion a un array del tipo indicato
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<?> c, Class<T> rtype) {
		T[] result = (T[]) Array.newInstance(rtype, 0);
		return c.toArray(result);
	}

	/**
	 * Pasa un array a un ArrayList que lo contenga
	 */
	public static <T> ArrayList<T> toArrayList(T[] array) {
		ArrayList<T> result = new ArrayList<T>(array.length);
		for (T t : array)
			result.add(t);
		return result;
	}

	/**
	 * Serializa un objeto en Base64
	 * 
	 * @param myObject
	 *            Object objeto a serializar
	 * @return String con el Base64 de la serializaci�n del objeto
	 */
	public static String objectToBase64(Object myObject) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(myObject);
			oos.flush();
			oos.close();
			bos.close();
			byte[] data = bos.toByteArray();
			return bytesToBase64(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deserializa un Base64 a objeto
	 * 
	 * @param myObject
	 *            base64 del objeto serializado
	 * @return Objeto en cuenstion
	 */
	public static Object base64ToObject(String myObject) {
		try {
			byte[] data = base64ToBytes(myObject);
			ObjectInputStream objectIn = null;
			if (data == null)
				return null;
			objectIn = new ObjectInputStream(new ByteArrayInputStream(data));
			return objectIn.readObject();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * Deserializa un Base64 a objeto
	 * 
	 * @param <T>
	 *            Tipo de objeto de vuelta
	 * @param b64data
	 *            base64 del objeto serializado
	 * @param resultType
	 *            Clase de objeto
	 * @return Objeto en cuensti�n
	 */
	public static <T> Object base64ToObject(String b64data, Class<T> resultType) {
		byte[] data = base64ToBytes(b64data);
		return Serializer.deserialize(data, resultType);
	}

	/**
	 * Intenta un cast, si no se consigue retorna null
	 * 
	 * @param <T>
	 *            Tipo de vuelta
	 * @param o
	 *            Objeto a realizar cast
	 * @param c
	 *            Clase destino
	 * @return (T) o;
	 */
	public static <T> T as(Object o, Class<T> c) {
		if (o == null)
			return null;
		if (c == String.class)
			return c.cast(o.toString());
		if (c.isInstance(o))
			return c.cast(o);
		else
			return null;
	}

	/**
	 * Intenta un cast, si no se consigue retorna null, metodo de rerror
	 * 
	 * @param <T>
	 *            Tipo de vuelta
	 * @param o
	 *            Objeto a realizar cast
	 * @return (T) o;
	 */
	@SuppressWarnings("unchecked")
	public static <T> T as(Object o) {
		try {
			return (T) o;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * toString de cualquier object (incluso nulos)
	 * 
	 * @param o
	 *            Object
	 * @return String
	 */
	public static String toString(Object o) {
		if (o == null)
			return null;
		String result = o instanceof String ? (String) o : null;
		if (result == null)
			result = o.toString();
		return result;
	}

	/**
	 * Quita el signo de un array de bytes
	 * 
	 * @param data
	 * @param start
	 * @param length
	 * @return
	 */
	public static short[] unsignedBytes(byte[] data, int start, int length) {
		short[] result = new short[length];
		for (int i = 0; i < length; i++) {
			int j = i + start;
			result[i] = (short) (0x000000FF & data[j]);
		}
		return result;
	}

	/**
	 * Retorna true si yesornot es afirmativo
	 * 
	 * @param yesornot
	 *            String
	 * @return boolean
	 */
	public static boolean isYes(String yesornot) {
		if (yesornot == null)
			return false;
		String[] yeses = { "y", "yes", "j", "ja", "s", "si", "1", "o", "oui", "x", "true" };
		for (String yes : yeses)
			if (yes.equalsIgnoreCase(yesornot))
				return true;
		return false;
	}

	/**
	 * Lee el stream y genera un String con la entrada en UTF-8
	 * 
	 * @param is
	 *            InputStream
	 * @return String
	 */
	public static String readToString(InputStream is) {
		try {
			if (is != null) {
				Writer writer = new StringWriter();

				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is, UTF8Charset));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					is.close();
				}
				return writer.toString();
			} else {
				return "";
			}
		} catch (Throwable t) {
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new RuntimeException(t);
		}
	}

	/**
	 * Realiza el tostring con cotnrol de nulos
	 * 
	 * @param o
	 *            Object
	 * @param nval
	 *            String del valor nulo
	 * @return String
	 */
	public static String nvl(Object o, String nval) {
		return o == null ? nval : o.toString();
	}

	/**
	 * Returns the string to the better number representative object(Double,
	 * Integer or Long) based on string value.
	 * 
	 * For more controlled option use the StringToNumber with class parameter
	 * 
	 * @param snum
	 * @return
	 */
	public static Object StringToNumber(String snum) {
		return StringToNumber(snum, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T StringToNumber(String snum, Class<T> as) {
		if (as == null) {
			if (snum.contains("."))
				return (T) new Double(Double.parseDouble(snum));
			else
				try {
					return (T) new Integer(Integer.parseInt(snum));
				} catch (NumberFormatException nfe) {
					return (T) new Long(Long.parseLong(snum));
				}
		} else {
			if (as == BigDecimal.class)
				return as.cast(new BigDecimal(snum));
			else if (as == BigInteger.class)
				return as.cast(new BigInteger(snum));
			else if (as == int.class || as == Integer.class)
				return (T) new Integer(Integer.parseInt(snum));
			else if (as == long.class || as == Long.class)
				return (T) new Long(Long.parseLong(snum));
			else if (as == float.class || as == Float.class)
				return (T) new Float(Float.parseFloat(snum));
			else if (as == double.class || as == Double.class)
				return (T) new Double(Double.parseDouble(snum));
			else
				throw new NiHaoException("Casting type " + as.getName() + " not supported.");
		}
	}
}
