package nihao.util.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import nihao.util.Conversor;
import nihao.util.IoUtil;

/**
 * Utilidades de red
 * 
 * @author ivan.dominguez
 * 
 */
public class NetTools {

	/**
	 * Conecta con la url y retorna un array de bytes con la respuesta del servidor
	 * @param url <code>Url</code> con  informaci�n de la conexi�n
	 * @return array de bytes con la respuesta del servidor
	 * @hint Memory consume option, for MemoryFriendly use <code>httpGet</code>
	 */
	public static byte[] httpGetBytes(Url url) {
		InputStream result = httpGet(url);
		if (result == null)
			return null;
		return IoUtil.inputStreamToByteArray(result);
	}
	
	public static String readToEndString(InputStream result){
		if (result == null)
			return null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l;
			byte[] tmp = new byte[2048];
			while ((l = result.read(tmp)) != -1)
				baos.write(tmp, 0, l);
			return Conversor.bytesToUTF8(baos.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	

	/**
	 * Conecta con la url y retorna el <code>InputStream</code> con la respuesta del servidor
	 * @param url <code>Url</code> con  informaci�n de la conexi�n
	 * @return <code>InputStream</code> con la respuesta del servidor
	 */
	public static InputStream httpGet(Url url) {
		NetConnection con=url.getConnection(NetMethod.GET);
		return con.send();
	}

	/**
	 * Conecta con la url por post y retorna el <code>InputStream</code> con la respuesta del servidor
	 * @param url <code>Url</code> con  informaci�n de la conexi�n
	 * @return <code>InputStream</code> con la respuesta del servidor
	 */
	public static InputStream httpPost(Url url,HashMap<String,String> parameters) {
		NetConnection con=url.getConnection(NetMethod.POST);
		con.post(parameters);
		return con.send();
	}
}
