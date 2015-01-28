package nihao.util.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import nihao.NiHaoException;

public class NetConnection extends HttpClient {
	private DataOutputStream output = null;
	private boolean hasPost = false;
	private boolean sended = false;

	/**
	 * Abre una conexi�n para la url indicada
	 * 
	 * @param url
	 *            <code>Url</code> a la que conectar
	 */
	public NetConnection(Url url, NetMethod method) {
		super(url);
		try {
			connection.setRequestMethod(method.name());
			connection.setDoInput(true);
			if (method == NetMethod.POST) {
				connection.setDoOutput(true);
				output = new DataOutputStream(connection.getOutputStream());
			}
			connection.setUseCaches(false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * A�ade un campo post
	 * 
	 * @param name
	 *            nombre del campo
	 * @param value
	 *            valor del campo
	 */
	public void post(String name, String value) {
		if (sended)
			throw new NiHaoException("Can't post if sended");
		StringBuilder query = new StringBuilder();
		if (hasPost)
			query.append('&');
		query.append(name);
		query.append('=');
		try {
			query.append(URLEncoder.encode(value, "UTF-8"));
			output.writeBytes(query.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		hasPost = true;
	}

	/**
	 * A�ade un conjunto de valores post desde un <code>HashMap</code>
	 * 
	 * @param parameters
	 *            <code>HashMap</code> con los vlaores
	 */
	public void post(HashMap<String, String> parameters) {
		for (String s : parameters.keySet()) {
			post(s, parameters.get(s));
		}
	}

	/**
	 * Envia un array de bytes al servidor
	 * 
	 * @param data
	 *            bytes a enviar
	 */
	public void write(byte[] data) {
		try {
			output.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna el objeto de conexi�n
	 * 
	 * @return <code>HttpURLConnection</code>
	 * @deprecated use <code>HttpClient</code> or extend
	 *             <code>NetConection</code>
	 */
	public HttpURLConnection getConnectionObject() {
		return connection;
	}

	/**
	 * Termina el envio y retorna un <code>InputStream</code> con la respuesta
	 * del servidor
	 * 
	 * @return <code>InputStream</code> con la respuesta del servidor
	 */
	public InputStream send() {
		if (sended)
			throw new NiHaoException("alreadey sended");
		try {
			if (output != null)
				output.close();
			sended = true;
			return connection.getInputStream();
		} catch (IOException e) {
			throw new NiHaoNetException(getHttpCode(), e.getMessage(), connection.getErrorStream());
		}
	}

	/**
	 * Establece un valor para la cabecera.
	 * 
	 * @param key
	 * @param value
	 */
	public void setHeader(String key, String value) {
		connection.setRequestProperty(key, value);
	}

	/**
	 * Retorna el c�digo HTTP (100,404...)
	 * 
	 * @return
	 */
	public int getHttpCode() {
		if (!sended)
			throw new NiHaoException("send first");
		try {
			return connection.getResponseCode();
		} catch (IOException e1) {
			return -1;
		}
	}

	/**
	 * Indica si ha de usar o no cache
	 * 
	 * @param cache
	 */
	public void setCache(boolean cache) {
		if (sended)
			throw new NiHaoException("set cache before send");
		connection.setUseCaches(false);
	}

	/**
	 * Retorna el Encoding del retorno
	 * 
	 * @return String
	 */
	public String getContentEncoding() {
		if (!sended)
			throw new NiHaoException("send first");
		return connection.getContentEncoding();
	}

	/**
	 * Retorna el tipo mime (ContetType) del retorno
	 * 
	 * @return String
	 */
	public String getContentType() {
		if (!sended)
			throw new NiHaoException("send first");
		return connection.getContentType();
	}

	/**
	 * Retorna el stream de error
	 * 
	 * @return InputStream
	 */
	public InputStream getErrorStream() {
		if (!sended)
			throw new NiHaoException("send first");
		return connection.getErrorStream();
	}

	/**
	 * Retorna una linea del header de respuesta, la linea 0 es tratada de
	 * manera especial
	 * 
	 * @param index
	 *            indice de lines
	 * @return String
	 */
	public String getHeaderField(int index) {
		if (!sended)
			throw new NiHaoException("send first");
		return connection.getHeaderField(index);
	}
}
