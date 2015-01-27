package nihao.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	private static String tempDirPropertyName = "java.io.tmpdir";
	private static String NOT_FOUND = "NOT_FOUND";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

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

	// Writers
	// ------------------------------------------------------------------------------------

	/**
	 * Write byte array to file. If file already exists, it will be overwritten.
	 * 
	 * @param file
	 *            The file where the given byte array have to be written to.
	 * @param bytes
	 *            The byte array which have to be written to the given file.
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, byte[] bytes) throws IOException {
		write(file, new ByteArrayInputStream(bytes), false);
	}

	/**
	 * Write byte array to file with option to append to file or not. If not,
	 * then any existing file will be overwritten.
	 * 
	 * @param file
	 *            The file where the given byte array have to be written to.
	 * @param bytes
	 *            The byte array which have to be written to the given file.
	 * @param append
	 *            Append to file?
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, byte[] bytes, boolean append) throws IOException {
		write(file, new ByteArrayInputStream(bytes), append);
	}

	/**
	 * Write byte inputstream to file. If file already exists, it will be
	 * overwritten.It's highly recommended to feed the inputstream as
	 * BufferedInputStream or ByteArrayInputStream as those are been
	 * automatically buffered.
	 * 
	 * @param file
	 *            The file where the given byte inputstream have to be written
	 *            to.
	 * @param input
	 *            The byte inputstream which have to be written to the given
	 *            file.
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, InputStream input) throws IOException {
		write(file, input, false);
	}

	/**
	 * Write byte inputstream to file with option to append to file or not. If
	 * not, then any existing file will be overwritten. It's highly recommended
	 * to feed the inputstream as BufferedInputStream or ByteArrayInputStream as
	 * those are been automatically buffered.
	 * 
	 * @param file
	 *            The file where the given byte inputstream have to be written
	 *            to.
	 * @param input
	 *            The byte inputstream which have to be written to the given
	 *            file.
	 * @param append
	 *            Append to file?
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, InputStream input, boolean append) throws IOException {
		mkdirs(file);
		BufferedOutputStream output = null;
		try {
			output = new BufferedOutputStream(new FileOutputStream(file, append));
			int data = -1;
			while ((data = input.read()) != -1) {
				output.write(data);
			}
		} finally {
			close(input, file);
			close(output, file);
		}
	}

	/**
	 * Write character array to file. If file already exists, it will be
	 * overwritten.
	 * 
	 * @param file
	 *            The file where the given character array have to be written
	 *            to.
	 * @param chars
	 *            The character array which have to be written to the given
	 *            file.
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, char[] chars) throws IOException {
		write(file, new CharArrayReader(chars), false);
	}

	/**
	 * Write character array to file with option to append to file or not. If
	 * not, then any existing file will be overwritten.
	 * 
	 * @param file
	 *            The file where the given character array have to be written
	 *            to.
	 * @param chars
	 *            The character array which have to be written to the given
	 *            file.
	 * @param append
	 *            Append to file?
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, char[] chars, boolean append) throws IOException {
		write(file, new CharArrayReader(chars), append);
	}

	/**
	 * Write string value to file. If file already exists, it will be
	 * overwritten.
	 * 
	 * @param file
	 *            The file where the given string value have to be written to.
	 * @param string
	 *            The string value which have to be written to the given file.
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, String string) throws IOException {
		write(file, new CharArrayReader(string.toCharArray()), false);
	}

	/**
	 * Write string value to file with option to append to file or not. If not,
	 * then any existing file will be overwritten.
	 * 
	 * @param file
	 *            The file where the given string value have to be written to.
	 * @param string
	 *            The string value which have to be written to the given file.
	 * @param append
	 *            Append to file?
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, String string, boolean append) throws IOException {
		write(file, new CharArrayReader(string.toCharArray()), append);
	}

	/**
	 * Write character reader to file. If file already exists, it will be
	 * overwritten. It's highly recommended to feed the reader as BufferedReader
	 * or CharArrayReader as those are been automatically buffered.
	 * 
	 * @param file
	 *            The file where the given character reader have to be written
	 *            to.
	 * @param reader
	 *            The character reader which have to be written to the given
	 *            file.
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, Reader reader) throws IOException {
		write(file, reader, false);
	}

	/**
	 * Write character reader to file with option to append to file or not. If
	 * not, then any existing file will be overwritten. It's highly recommended
	 * to feed the reader as BufferedReader or CharArrayReader as those are been
	 * automatically buffered.
	 * 
	 * @param file
	 *            The file where the given character reader have to be written
	 *            to.
	 * @param reader
	 *            The character reader which have to be written to the given
	 *            file.
	 * @param append
	 *            Append to file?
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, Reader reader, boolean append) throws IOException {
		mkdirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			int data = -1;
			while ((data = reader.read()) != -1) {
				writer.write(data);
			}
		} finally {
			close(reader, file);
			close(writer, file);
		}
	}

	/**
	 * Write list of String records to file. If file already exists, it will be
	 * overwritten.
	 * 
	 * @param file
	 *            The file where the given character reader have to be written
	 *            to.
	 * @param records
	 *            The list of String records which have to be written to the
	 *            given file.
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, List<String> records) throws IOException {
		write(file, records, false);
	}

	/**
	 * Write list of String records to file with option to append to file or
	 * not. If not, then any existing file will be overwritten.
	 * 
	 * @param file
	 *            The file where the given character reader have to be written
	 *            to.
	 * @param records
	 *            The list of String records which have to be written to the
	 *            given file.
	 * @param append
	 *            Append to file?
	 * @throws IOException
	 *             If writing file fails.
	 */
	public static void write(java.io.File file, List<String> records, boolean append) throws IOException {
		mkdirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			for (String record : records) {
				writer.write(record);
				writer.write(LINE_SEPARATOR);
			}
		} finally {
			close(writer, file);
		}
	}

	// Readers
	// ------------------------------------------------------------------------------------

	/**
	 * Read byte array from file. Take care with big files, this would be memory
	 * hogging, rather use readStream() instead.
	 * 
	 * @param file
	 *            The file to read the byte array from.
	 * @return The byte array with the file contents.
	 * @throws IOException
	 *             If reading file fails.
	 */
	public static byte[] readBytes(java.io.File file) throws IOException {
		BufferedInputStream stream = (BufferedInputStream) readStream(file);
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		return bytes;
	}

	/**
	 * Read byte stream from file.
	 * 
	 * @param file
	 *            The file to read the byte stream from.
	 * @return The byte stream with the file contents (actually:
	 *         BufferedInputStream).
	 * @throws IOException
	 *             If reading file fails.
	 */
	public static InputStream readStream(java.io.File file) throws IOException {
		return new BufferedInputStream(new FileInputStream(file));
	}

	/**
	 * Read character array from file. Take care with big files, this would be
	 * memory hogging, rather use readReader() instead.
	 * 
	 * @param file
	 *            The file to read the character array from.
	 * @return The character array with the file contents.
	 * @throws IOException
	 *             If reading file fails.
	 */
	public static char[] readChars(java.io.File file) throws IOException {
		BufferedReader reader = (BufferedReader) readReader(file);
		char[] chars = new char[(int) file.length()];
		reader.read(chars);
		return chars;
	}

	/**
	 * Read string value from file. Take care with big files, this would be
	 * memory hogging, rather use readReader() instead.
	 * 
	 * @param file
	 *            The file to read the string value from.
	 * @return The string value with the file contents.
	 * @throws IOException
	 *             If reading file fails.
	 */
	public static String readString(java.io.File file) throws IOException {
		return new String(readChars(file));
	}

	/**
	 * Read character reader from file.
	 * 
	 * @param file
	 *            The file to read the character reader from.
	 * @return The character reader with the file contents (actually:
	 *         BufferedReader).
	 * @throws IOException
	 *             If reading file fails.
	 */
	public static Reader readReader(java.io.File file) throws IOException {
		return new BufferedReader(new FileReader(file));
	}

	/**
	 * Read list of String records from file.
	 * 
	 * @param file
	 *            The file to read the character writer from.
	 * @return A list of String records which represents lines of the file
	 *         contents.
	 * @throws IOException
	 *             If reading file fails.
	 */
	public static List<String> readRecords(java.io.File file) throws IOException {
		BufferedReader reader = (BufferedReader) readReader(file);
		List<String> records = new ArrayList<String>();
		String record = null;
		try {
			while ((record = reader.readLine()) != null)
				records.add(record);
		} finally {
			close(reader, file);
		}
		return records;
	}

	// Copiers
	// ------------------------------------------------------------------------------------

	/**
	 * Copy file. Any existing file at the destination will be overwritten.
	 * 
	 * @param source
	 *            The file to read the contents from.
	 * @param destination
	 *            The file to write the contents to.
	 * @throws IOException
	 *             If copying file fails.
	 */
	public static void copy(java.io.File source, java.io.File destination) throws IOException {
		copy(source, destination, true);
	}

	/**
	 * Copy file with the option to overwrite any existing file at the
	 * destination.
	 * 
	 * @param source
	 *            The file to read the contents from.
	 * @param destination
	 *            The file to write the contents to.
	 * @param overwrite
	 *            Set whether to overwrite any existing file at the destination.
	 * @throws IOException
	 *             If the destination file already exists while
	 *             <tt>overwrite</tt> is set to false, or if copying file fails.
	 */
	public static void copy(java.io.File source, java.io.File destination, boolean overwrite) {
		if (destination.exists() && !overwrite)
			throw new RuntimeException("Copying file " + source.getPath() + " to " + destination.getPath() + " failed." + " The destination file already exists.");
		try {
			mkdirs(destination);
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			try {
				input = new BufferedInputStream(new FileInputStream(source));
				output = new BufferedOutputStream(new FileOutputStream(destination));
				int data = -1;
				while ((data = input.read()) != -1) {
					output.write(data);
				}
			} finally {
				close(input, source);
				close(output, destination);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	// Movers
	// -------------------------------------------------------------------------------------

	/**
	 * Move (rename) file. Any existing file at the destination will be
	 * overwritten.
	 * 
	 * @param source
	 *            The file to be moved.
	 * @param destination
	 *            The new destination of the file.
	 * @throws IOException
	 *             If moving file fails.
	 */
	public static void move(java.io.File source, java.io.File destination) throws IOException {
		move(source, destination, true);
	}

	/**
	 * Move (rename) file with the option to overwrite any existing file at the
	 * destination.
	 * 
	 * @param source
	 *            The file to be moved.
	 * @param destination
	 *            The new destination of the file.
	 * @param overwrite
	 *            Set whether to overwrite any existing file at the destination.
	 * @throws IOException
	 *             If the destination file already exists while
	 *             <tt>overwrite</tt> is set to false, or if moving file fails.
	 */
	public static void move(java.io.File source, java.io.File destination, boolean overwrite) {
		if (destination.exists()) {
			if (overwrite) {
				destination.delete();
			} else {
				throw new RuntimeException("Moving file " + source.getPath() + " to " + destination.getPath() + " failed." + " The destination file already exists.");
			}
		}
		try {
			mkdirs(destination);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		if (!source.renameTo(destination)) {
			throw new RuntimeException("Moving file " + source.getPath() + " to " + destination.getPath() + " failed.");
		}
	}

	// Filenames
	// ----------------------------------------------------------------------------------

	/**
	 * Trim the eventual file path from the given file name. Anything before the
	 * last occurred "/" and "\" will be trimmed, including the slash.
	 * 
	 * @param fileName
	 *            The file name to trim the file path from.
	 * @return The file name with the file path trimmed.
	 */
	public static String trimFilePath(String fileName) {
		return fileName.substring(fileName.lastIndexOf("/") + 1).substring(fileName.lastIndexOf("\\") + 1);
	}

	/**
	 * Generate unique file based on the given path and name. If the file
	 * exists, then it will add "[i]" to the file name as long as the file
	 * exists. The value of i can be between 0 and 2147483647 (the value of
	 * Integer.MAX_VALUE).
	 * 
	 * @param filePath
	 *            The path of the unique file.
	 * @param fileName
	 *            The name of the unique file.
	 * @return The unique file.
	 * @throws IOException
	 *             If unique file cannot be generated, this can be caused if all
	 *             file names are already in use. You may consider another
	 *             filename instead.
	 */
	public static java.io.File uniqueFile(java.io.File filePath, String fileName) throws IOException {
		java.io.File file = new java.io.File(filePath, fileName);

		if (file.exists()) {

			// Split filename and add braces, e.g. "name.ext" --> "name[",
			// "].ext".
			String prefix;
			String suffix;
			int dotIndex = fileName.lastIndexOf(".");

			if (dotIndex > -1) {
				prefix = fileName.substring(0, dotIndex) + "[";
				suffix = "]" + fileName.substring(dotIndex);
			} else {
				prefix = fileName + "[";
				suffix = "]";
			}

			int count = 0;

			// Add counter to filename as long as file exists.
			while (file.exists()) {
				if (count < 0) { // int++ restarts at -2147483648 after
									// 2147483647.
					throw new IOException("No unique filename available for " + fileName + " in path " + filePath.getPath() + ".");
				}

				// Glue counter between prefix and suffix, e.g. "name[" + count
				// + "].ext".
				file = new java.io.File(filePath, prefix + (count++) + suffix);
			}
		}

		return file;
	}

	// Helpers
	// ------------------------------------------------------------------------------------

	/**
	 * Check and create missing parent directories for the given file.
	 * 
	 * @param file
	 *            The file to check and create the missing parent directories
	 *            for.
	 * @throws IOException
	 *             If the given file is actually not a file or if creating
	 *             parent directories fails.
	 */
	private static void mkdirs(java.io.File file) throws IOException {
		if (file.exists() && !file.isFile()) {
			throw new IOException("File " + file.getPath() + " is actually not a file.");
		}
		java.io.File parentFile = file.getParentFile();
		if (!parentFile.exists() && !parentFile.mkdirs()) {
			throw new IOException("Creating directories " + parentFile.getPath() + " failed.");
		}
	}

	/**
	 * Close the given I/O resource of the given file.
	 * 
	 * @param resource
	 *            The I/O resource to be closed.
	 * @param file
	 *            The I/O resource's subject.
	 */
	private static void close(Closeable resource, java.io.File file) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				String message = "Closing file " + file.getPath() + " failed.";
				// Do your thing with the exception and the message. Print it,
				// log it or mail it.
				System.err.println(message);
				e.printStackTrace();
			}
		}
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