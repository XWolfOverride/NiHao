package nihao.util.net;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Gestiona una Url
 * 
 * @author ivan.dominguez
 * 
 */
public class Url {
	/**
	 * Proxy por defecto, si no es null, cada new de Url asignara este proxy si
	 * este Proxy cambia <b>NO</b> afectara a las Urls ya instanciadas.
	 */
	public static Proxy DEFAULTPROXY = null;

	private String url;
	private Proxy proxy;

	/**
	 * Crea un objeto vacio
	 */
	public Url() {
		this.proxy = DEFAULTPROXY;
	}

	/**
	 * Crea una Url a la ruta indicada, y con el proxy por defecto
	 * 
	 * @param url
	 *            Url del endpoint
	 */
	public Url(String url) {
		this.url = url;
		this.proxy = DEFAULTPROXY;
	}

	/**
	 * Crea una Url con endpoint y proxy especificado
	 * 
	 * @param url
	 *            Endpoint
	 * @param proxy
	 *            Informacion del proxy
	 */
	public Url(String url, Proxy proxy) {
		this.url = url;
		this.proxy = proxy;
	}

	/**
	 * Genera una <code>java.net.URL</code> con la informacion actual
	 * 
	 * @return Nuevo <code>java.net.URL</code>
	 */
	public URL getJavaUrl() {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Crea una conexi�n plenamente configurada
	 * 
	 * @return <code>NetConnection</code> configurada y abierta la conexi�n
	 */
	public NetConnection getConnection(NetMethod method) {
		return new NetConnection(this, method);
	}

	/**
	 * Crea una conexi�n plenamente configurada
	 * 
	 * @return <code>HttpConnection</code>
	 */
	public HttpClient getHttpClient() {
		return new HttpClient(this);
	}

	/**
	 * Url del endpoint
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Url del endpoint
	 * 
	 * @param url
	 *            <code>String</code>
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Proxy de la conexi�n, o proxy por defecto en el momento del
	 * <code>new</code>
	 * 
	 * @return
	 */
	public Proxy getProxy() {
		return proxy;
	}

	/**
	 * Establece el proxy para esta conexi�n
	 * 
	 * @param proxy
	 *            <code>Proxy</code> de la conexi�n
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}
}
