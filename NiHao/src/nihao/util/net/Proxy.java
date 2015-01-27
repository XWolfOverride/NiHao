package nihao.util.net;

import java.net.InetSocketAddress;

/**
 * Maneja informacion de un proxy
 * 
 * @author ivan.dominguez
 * 
 */
public class Proxy {
	private boolean isAuth = false;
	private String user = null;
	private String pass = null;
	private String server = null;
	private int port = 0;

	/**
	 * Genera un proxy vacio
	 */
	public Proxy() {
	}

	/**
	 * Genera un proxy normal
	 * 
	 * @param server
	 *            Host del proxy
	 * @param port
	 *            Puerto
	 */
	public Proxy(String server, Integer port) {
		this.server = server;
		this.port = port;
	}

	/**
	 * Genera un proxy con autenticaci�n
	 * 
	 * @param server
	 *            Host del proxy
	 * @param port
	 *            Puerto
	 * @param user
	 *            Usuario
	 * @param pass
	 *            Contrase�a
	 */
	public Proxy(String server, Integer port, String user, String pass) {
		this.server = server;
		this.port = port;
		isAuth = true;
		this.user = user;
		this.pass = pass;
	}

	/**
	 * Genera un <code>java.net.Proxy</code> con los datos actuales
	 * 
	 * @return <doce>java.net.Proxy</code>
	 */
	public java.net.Proxy getJavaProxy() {
		return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(server, port));
	}

	/**
	 * Indica si el proxy tiene autenticaci�n
	 * 
	 * @return <code>true</code> si tiene autenticacion
	 */
	public boolean isAuth() {
		return isAuth;
	}

	/**
	 * Retorna el usuario de la autenticaci�n
	 * 
	 * @return <code>String</code> con el nombre de usuario.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Establece el usuario de la autenticacion, esto activara el modo
	 * autenticado
	 * 
	 * @param user
	 *            <code>String</code> con el nombre de usuario.
	 */
	public void setUser(String user) {
		this.user = user;
		this.isAuth = true;
		if (pass == null)
			pass = "";
	}

	/**
	 * Retorna la contrase�a establecida para este proxy
	 * 
	 * @return <code>String</code> con la contrase�a
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * Establece la contrase�a de la autenticacion, esto activara el modo
	 * autenticado
	 * 
	 * @param user
	 *            <code>String</code> contrase�a
	 */
	public void setPass(String pass) {
		this.pass = pass;
		if (user == null)
			user = "";
	}

	/**
	 * Retorna el host del proxy
	 * 
	 * @return <code>String</code> host
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Asigna el host del proxy
	 * 
	 * @param server
	 *            <code>String</code> host
	 */
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * Retorna el puerto del proxy
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Asigna el puerto del proxy
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}
}
