package nihao.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Herramientas con datos
 * 
 * @author ivan.dominguez
 * 
 */
public class Hasher {
	/**
	 * Genera el SHA-1 de un array de bytes
	 * 
	 * @param data
	 *            Array de bytes
	 * @return Array de bytes con el digest
	 */
	public static byte[] SHA1(byte[] data) {
		return generateHash(data, HashType.SHA1);
	}

	/**
	 * Genera el SHA-1 de un array de un inputStream (el input stream se lee
	 * hasta el final)
	 * 
	 * @param is
	 *            InputStream con los bytes
	 * @return byte[] con el SHA1
	 */
	public static byte[] SHA1(InputStream is) {
		return generateHash(is, HashType.SHA1);
	}

	/**
	 * Genera un Hash de un array de bytes
	 * 
	 * @param data
	 *            Array de bytes
	 * @param hash
	 *            HashType con el tipo de Hash a aplicar
	 * @return digest del hashing
	 */
	public static byte[] generateHash(byte[] data, HashType hash) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance(hashTypeToString(hash));
			md.update(data);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Genera el hash de un array de un inputStream (el input stream se lee
	 * hasta el final)
	 * 
	 * @param is
	 *            InputStream con los bytes
	 * @param hash
	 *            HashType con el tipo de hash a realizar
	 * @return byte[] con el hash
	 */
	public static byte[] generateHash(InputStream is, HashType hash) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance(hashTypeToString(hash));
			byte buf[] = new byte[1024];
			int n;
			while ((n = is.read(buf)) > 0)
				md.update(buf, 0, n);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Genera un Hash de un array de bytes
	 * 
	 * @param data
	 *            Array de bytes
	 * @param algorithm
	 *            Sting con el nombre MessageDigest del algoritmo
	 * @return Digest
	 * @throws NoSuchAlgorithmException
	 *             el string del nombre de hash es incorrecto
	 */
	public static byte[] generateHash(byte[] data, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance(algorithm);
		md.update(data);
		return md.digest();
	}

	/**
	 * Retorna el nombre de algoritmo MessageDigest a partir de un HashType
	 * 
	 * @param hash
	 *            tipo de hash
	 * @return Nombre del algoritmo.
	 */
	public static String hashTypeToString(HashType hash) {
		switch (hash) {
		case SHA1:
			return "SHA-1";
		case MD5:
			return "MD5";
		case MD2:
			return "MD2";
		case SHA224:
			return "SHA-224";
		case SHA256:
			return "SHA-256";
		case SHA384:
			return "SHA-384";
		case SHA512:
			return "SHA-512";
		default:
			throw new RuntimeException("Unimplemented hash ''" + hash.name() + "'' @hashTypeToString");
		}
	}

	/**
	 * Encripta datos con una clave publica en RSA con BouncyCastle
	 * 
	 * @param inpBytes
	 *            bytes a cifrar
	 * @param key
	 *            clave publica
	 * @return bytes encriptados
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] encrypt(byte[] inpBytes, PublicKey key) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		try {
			Cipher cipher = Cipher.getInstance("RSA", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(inpBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		}
	}
}
