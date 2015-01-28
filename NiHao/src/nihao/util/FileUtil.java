package nihao.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

	private static String tempDirPropertyName = "java.io.tmpdir";
	private static String NOT_FOUND = "NOT_FOUND";

	// private static final String LINE_SEPARATOR =
	// System.getProperty("line.separator");

	/**
	 * Escribe en un fichero un array de bytes
	 * 
	 * @param fname
	 *            Ruta dónde guardar los bytes
	 * @param data
	 *            Bytes a guardar
	 */
	public static void bytesToFile(String fname, byte[] data) {
		try {
			FileOutputStream fos = new FileOutputStream(fname);
			fos.write(data);
			fos.close();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Vuelca los datos de un archivo en un array de bytes
	 * 
	 * @param fname
	 *            Ruta del archivo a leer
	 * @return byte[] Los bytes del fichero
	 */
	public static byte[] fileToBytes(String fname) {
		try {
			FileInputStream fis = new FileInputStream(fname);
			byte[] result = new byte[fis.available()];
			fis.read(result);
			fis.close();
			return result;
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Lee un archivo de la ruta temporal del S.O.
	 * 
	 * @param filename
	 *            nombre del archivo en la ruta temporal
	 * @return FileInputStream del archivo
	 */
	public static InputStream readTempFile(String filename) throws IOException, FileNotFoundException {
		String tempPath = System.getProperty(tempDirPropertyName, NOT_FOUND);
		if (tempPath.equals(NOT_FOUND))
			throw new RuntimeException("Temporary File not found");
		InputStream tmpFile = new FileInputStream(tempPath + System.getProperty("file.separator") + filename + ".crl");
		return tmpFile;
	}

	/**
	 * 
	 * @param src
	 * @param dest
	 * @param bufferSize
	 * @throws IOException
	 */
	public static void copyToTempPath(byte[] src, String filename, int bufferSize) throws IOException {
		if (bufferSize <= 0)
			bufferSize = 2000;
		String tempPath = System.getProperty(tempDirPropertyName, NOT_FOUND);
		if (tempPath.equals(NOT_FOUND))
			throw new RuntimeException("Temporary File not found");
		InputStream is = new ByteArrayInputStream(src);
		OutputStream os = new BufferedOutputStream(new FileOutputStream(tempPath + System.getProperty("file.separator") + filename + ".crl"));
		byte[] buffer = new byte[bufferSize];
		int c;
		while ((c = is.read(buffer)) != -1)
			os.write(buffer, 0, c);
		is.close();
		os.close();
		return;
	}

	/**
	 * Crea una estructura de directorios con la ruta que le entra
	 * 
	 * @param dirpath
	 * @return true si ha conseguido crear los directorios/false si no ha podido
	 *         crearlo
	 */
	public static boolean createDir(String dirpath) {
		File file = new File(dirpath);
		if (file.exists())
			return true;
		return file.mkdirs();
	}
}
