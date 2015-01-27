package nihao.util.net;

import java.io.IOException;
import java.net.HttpURLConnection;

import nihao.util.Conversor;

public class HttpClient {
	protected HttpURLConnection connection = null;
	private String userAgent = "core.io.net.http";

	public HttpClient(Url url) {
		try {
			if (url.getProxy() == null)
				connection = (HttpURLConnection) url.getJavaUrl()
						.openConnection();
			else {
				Proxy proxy = url.getProxy();
				connection = (HttpURLConnection) url.getJavaUrl()
						.openConnection(proxy.getJavaProxy());
				if (proxy.isAuth()) {
					String encodedAuth = getBasicAuthBase64(proxy.getUser(),
							proxy.getPass());
					setHeader("Proxy-Authorization", "Basic " + encodedAuth);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Pone una propiedad a la conexi�n
	 * 
	 * @param property
	 *            key
	 * @param value
	 *            valor
	 */
	public void setHeader(String property, String value) {
		connection.setRequestProperty(property, value);
	}

	/**
	 * Ejecuta una conexi�n
	 * 
	 * @param call
	 *            objeto de llamada
	 */
	public void doCall(HttpClientCall call) {

	}

	// ------------------------------------------------- STATIC
	/**
	 * Retorna el auth basico de usuario y contrase�a
	 * 
	 * @param user
	 * @param pass
	 * @return
	 */
	public static String getBasicAuthBase64(String user, String pass) {
		String password = user + ":" + pass;
		return Conversor.bytesToBase64(Conversor.asciiToBytes(password));
	}

	// ------------------------------------------------- PROPERTIES
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
