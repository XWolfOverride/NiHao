package nihao.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import nihao.NiHaoException;

public class IoUtil {
	/**
	 * Lee un stream hasta el final y lo retorna en forma de byte[]
	 * 
	 * @param inputStream
	 *            InputStream de donde leer
	 * @return byte[]
	 */
	public static byte[] inputStreamToByteArray(InputStream inputStream) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l;
			byte[] tmp = new byte[8192];
			while ((l = inputStream.read(tmp)) != -1)
				baos.write(tmp, 0, l);
			baos.close();
			return baos.toByteArray();
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Escribe un byte[] en bloques de hasta 255 bytes, cada uno con su marca de
	 * tama�o.<br>
	 * Este m�todo no es aconsejable para archivos grandes, ya que genera un
	 * byte por cada 255 bytes (4Kb por cada Mb).<br>
	 * Pero se recomienda para archivos de entre 0 y 5 Mb, y en escrituras de
	 * streams de red (omitiendo falsos finales)<br>
	 * Para archivos mayores usar <code>writeLongBlock</code>
	 * 
	 * @param data
	 *            byte[] con los datos
	 * @param os
	 *            OutputStream destino
	 */
	public static void writeChunkedBlockToStream(byte[] data, OutputStream os) {
		int len = data.length;
		int pos = 0;
		try {
			while (pos < len) {
				int chunklen = Math.min(len - pos, 255);
				os.write(chunklen);
				os.write(data, pos, chunklen);
				pos += chunklen;
			}
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Lee un byte[] desde un InputStream, usando el m�todo "ChunckedBlock"
	 * definido en <code>writeChunkedBlockToStream</code><br>
	 * 
	 * @param is
	 *            InputStream de origen
	 * @return byte[] con el bloque.
	 */
	public static byte[] readChunkedBlockFromStream(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[255];
		try {
			int chunklen;
			do {
				chunklen = is.read();
				if (chunklen != is.read(buffer, 0, chunklen))
					throw new NiHaoException("Unexpected stream EOF");
				baos.write(buffer, 0, chunklen);
			} while (chunklen < 255);
			baos.close();
			return baos.toByteArray();
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Copia un numero determinado de bytes de un Stream a otro
	 * 
	 * @param from
	 *            InputStream origen
	 * @param to
	 *            OutputStream destino
	 * @param length
	 *            cantidad a copiar
	 */
	public static int copyStream(InputStream from, OutputStream to, int length) {
		try {
			int result = 0;
			byte[] tmp = new byte[8192];
			while (result < length) {
				int blockLength = Math.min(tmp.length, length - result);
				int readed = from.read(tmp);
				to.write(tmp, 0, readed);
				result += readed;
				if (readed != blockLength)
					break;
			}
			return result;
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Copia los datos de un stream a otro hasta el final del origen
	 * 
	 * @param from
	 *            InputStream origen
	 * @param to
	 *            OutputStream destino
	 */
	public static void copyStream(InputStream from, OutputStream to) {
		try {
			int l;
			byte[] tmp = new byte[8192];
			while ((l = from.read(tmp)) != -1)
				to.write(tmp, 0, l);
		} catch (NiHaoException e) {
			throw e;
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

	/**
	 * Lee un stream hasta que termina y lo retorna como array de bytes
	 * 
	 * @param is
	 *            InputStream
	 * @return byte[]
	 */
	public static byte[] readToEnd(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l;
			byte[] tmp = new byte[2048];
			while ((l = is.read(tmp)) != -1)
				baos.write(tmp, 0, l);
			return baos.toByteArray();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads an InputStream and returns its contents as String
	 * 
	 * @param is
	 *            InputStream
	 * @return String
	 */
	public static String readStringToEnd(InputStream is) {
		return readStringToEnd(new BufferedReader(new InputStreamReader(is)));
	}

	/**
	 * Reads a BufferedReader and returns its contents as String
	 * 
	 * @param br
	 *            BufferedReader
	 * @return String
	 */
	public static String readStringToEnd(BufferedReader br) {
		try {
			char[] charBuffer = new char[512];
			int readed = -1;
			StringBuilder sb = new StringBuilder();
			while ((readed = br.read(charBuffer)) > 0)
				sb.append(charBuffer, 0, readed);
			return sb.toString();
		} catch (IOException e) {
			throw new NiHaoException(e);
		}
	}

}
