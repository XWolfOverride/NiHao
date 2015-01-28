package nihao;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nihao.log.LogProvider;
import nihao.login.Group;
import nihao.login.LoginConf;
import nihao.login.User;
import nihao.util.Conversor;
import nihao.util.reflection.Reflector;

public class WebCall {
	private static final String LOGOUT_PARAMETER = "logout";
	private static final String INFO_ID = "::NIHAO:INFO";
	private static final String ERROR_ID = "::NIHAO:ERROR";
	public static ThreadLocal<WebCall> currentWebCall = new ThreadLocal<WebCall>();

	/**
	 * Obtiene el WebCall de la ejecución a través del request
	 * 
	 * @param request
	 *            ServletRequest
	 * @return WebCall
	 */
	public static WebCall getWebCall(ServletRequest request) {
		return (WebCall) request.getAttribute("");
	}

	/**
	 * Obtiene el WebCall en ejecucuón
	 * 
	 * @return WebCall
	 */
	public static WebCall getWebCall() {
		return currentWebCall.get();
	}

	HttpServletRequest rq;
	HttpServletResponse rs;
	FilterChain chain;
	HttpSession ses;
	String webUrlRoot;
	String url;
	boolean wantToLogOut;
	SessionController sessionController;
	Page page;
	WorksetHandler workset;
	WorksetController worksetController;
	Map<String, ArrayList<String>> violations;

	public WebCall(ServletRequest request, ServletResponse response, FilterChain chain) {
		rq = (HttpServletRequest) request;
		rs = (HttpServletResponse) response;
		this.chain = chain;
		webUrlRoot = rq.getContextPath() + "/";
		String pathInfo = rq.getPathInfo(); // Faces
		String servletPath = rq.getServletPath();
		if (servletPath!=null)
			url=servletPath;
		else
			url="";
		if (pathInfo!=null)
			url+=pathInfo;
		if (url.length()>0)
			url = url.substring(1);
		if (wantToLogOut = Conversor.isYes(getParameter(LOGOUT_PARAMETER))) {
			ses = rq.getSession(false);
			if (ses != null)
				ses.invalidate();
		} else {
			ses = rq.getSession(true);
			sessionController = SessionController.getSessionController(ses);
			worksetController = WorksetController.getWorksetController(ses);
		}
		rq.setAttribute("", this);
		currentWebCall.set(this);
	}

	/**
	 * Retorna un parametro del request
	 * 
	 * @param key
	 *            String nombre del parametro
	 * @return
	 */
	public String getParameter(String key) {
		return rq.getParameter(key);
	}

	/**
	 * Retorna un atributo del request
	 * 
	 * @param key
	 *            String nombre del atributo
	 * @return
	 */
	public Object getAttribute(String key) {
		return rq.getAttribute(key);
	}

	/**
	 * Retorna un valor almacenado en sesión
	 * 
	 * @param key
	 *            String clave
	 * @return Object valor
	 */
	public Object getSession(String key) {
		return ses.getAttribute(key);
	}

	/**
	 * Obtiene un valor con el formato usado en los tags de la web, intenta
	 * parsear los puntos para obtener los datos hijos
	 * 
	 * @param name
	 *            String clave del valor, 4 tipos:<br>
	 *            · si se antepone "@" se retorna un valor de <b>contexto</b><br>
	 *            · si se antepone "$" se retorna un valor de <b>sesión</b><br>
	 *            · si se antepone "%" se retorna un valor de <b>atributo</b>
	 *            del request<br>
	 *            en cualquier otro caso, el campo hace referencia a un valor en
	 *            el <b>workset</b> de la página
	 * @return
	 */
	public Object getValue(String name) {
		if (name == null || name.length() == 0)
			return null;
		if (name.contains(".")) {
			String[] names = name.split("\\.");
			Object o = getDirectValue(names[0]);
			if (o == null)
				return null;
			return Reflector.getPathValue(o, names, 1, Object.class);
		} else
			return getDirectValue(name);
	}

	/**
	 * Coge el valor del workset sin parsear los puntos
	 * 
	 * @param name
	 *            String clave del valor, 4 tipos:<br>
	 *            · si se antepone "@" se retorna un valor de <b>contexto</b><br>
	 *            · si se antepone "$" se retorna un valor se <b>sesión</b><br>
	 *            · si se antepone "%" se retorna un valor de <b>atributo</b>
	 *            del request<br>
	 *            en cualquier otro caso, el campo hace referencia a un valor en
	 *            el <b>workset</b> de la página
	 * @return
	 */
	public Object getDirectValue(String name) {
		if (name == null || name.length() == 0)
			return null;
		if (name.startsWith("@")) {
			return NiHao.get(name.substring(1));
		} else if (name.startsWith("$")) {
			return getSession(name.substring(1));
		} else if (name.startsWith("%")) {
			return getAttribute(name.substring(1));
		}
		if (workset == null)
			throw new NiHaoException("page '/" + url + "' don't have a workset");
		return workset.get(name, this);
	}

	/**
	 * Retonra una URL con la base de aplicación
	 * 
	 * @param url
	 * @return
	 */
	public String getFullUrl(String url) {
		if (url.startsWith("/"))
			url = url.substring(1);
		return webUrlRoot + url;
	}

	/**
	 * Retorna la url actual con la base de aplicación
	 * 
	 * @return
	 */
	public String getFullUrl() {
		return webUrlRoot + url;
	}

	/**
	 * Retorna la url actual (sin la base de aplicación, ni "/" inicial)
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	private void go(String url) throws IOException, ServletException {
		if (url.startsWith(webUrlRoot))
			url = url.substring(webUrlRoot.length() - 1);
		if (ses != null)
			ses.setAttribute("@@LAST", url);
		RequestDispatcher rdispatcher = rq.getRequestDispatcher(url);
		rdispatcher.forward(rq, rs);
	}

	/**
	 * Va a la página de cierre de sesión
	 */
	public void goEnd() throws IOException, ServletException {
		go(NiHao.getConf().getLoginConf().getPageEnd());
	}

	/**
	 * Va a la página de error
	 */
	public void goError(Throwable t) throws IOException, ServletException {
		LogProvider.getProvider().error("Call error: ",t);
		rq.setAttribute(ERROR_ID, t);
		go(NiHao.getConf().getLoginConf().getPageError());
	}

	/**
	 * Va a la página de Login
	 */
	public void goLogin() throws IOException, ServletException {
		go(NiHao.getConf().getLoginConf().getPageLogin());
	}

	/**
	 * Va a la página de Prohibido
	 */
	public void goForbidden() throws IOException, ServletException {
		go(NiHao.getConf().getLoginConf().getPageForbidden());
	}

	/**
	 * Continua sin redireccion, no se procesa la llamada por elframework
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public void goExcluded() throws IOException, ServletException {
		chain.doFilter(rq, rs);
	}

	/**
	 * Chequea el flujo de validaciones, y continua sin redireccion o va para
	 * atras.
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public void go() throws IOException, ServletException {
		obtainWorkset();
		if (haveValidationViolations()) {
			goBack();
			return;
		}
		ses.setAttribute("@@LAST", url);
		chain.doFilter(rq, rs);
	}

	/**
	 * Cancela la navegación
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public void goBack() throws IOException, ServletException {
		if (ses == null) {
			goRoot();
			return;
		}
		String to = (String) ses.getAttribute("@@LAST");
		if (to == null) {
			goRoot();
			return;
		}
		go(to);
	}

	/**
	 * Se dirige al indice del sitio.
	 */
	public void goRoot() throws IOException, ServletException {
		go("");
	}

	boolean login() {
		return sessionController.login(this);
	}

	public X509Certificate[] getSSLCertificates(String sslCertificateAttributeName) {
		return (X509Certificate[]) rq.getAttribute(sslCertificateAttributeName);
	}

	public boolean canEnterThePage() {
		page = NiHao.getPage(url);
		if (page == null) {
			String group = NiHao.getConf().getDefaultGroupForUndefinedPage();
			if (group == null) {
				rq.setAttribute(INFO_ID, "Unaccessible page ever.");
				return false;
			}
			if ("*".equals(group))
				return true;
			for (Group g : sessionController.getUser().getGroups())
				if (group.equalsIgnoreCase(g.getName()))
					return true;
			return false;
		}
		return page.allow(sessionController.getUser());
	}

	public void obtainWorkset() {
		if (page != null && page.getWorkset() != null)
			workset = worksetController.get(page.getWorkset());
		if (workset != null)
			workset.execute(rq.getParameterMap(), this);
	}

	public WorksetHandler getWorkset() {
		return workset;
	}

	/**
	 * True if page is excluded, excluded pages will no have any login
	 * 
	 * @return true si la página no requiere seguridad
	 */
	public boolean isExcluded() {
		LoginConf conf = NiHao.getConf().getLoginConf();
		if (conf.isDisabled())
			return true;
		if (conf.getExcluded() == null)
			return false;
		for (String ex : conf.getExcluded())
			if (ex.equalsIgnoreCase(url))
				return true;
		return false;
	}

	/**
	 * Retorna un error
	 * 
	 * @return
	 */
	public Throwable getError() {
		return (Throwable) rq.getAttribute(ERROR_ID);
	}

	/**
	 * Retorna un info
	 * 
	 * @return
	 */
	public String getInfo() {
		return (String) rq.getAttribute(INFO_ID);
	}

	/**
	 * Retorna el usuario en sesión
	 * 
	 * @return User
	 */
	public User getUser() {
		if (sessionController == null)
			return null;
		return sessionController.getUser();
	}

	/**
	 * Añade una violación de validación, si hay alguna violación de validacion
	 * la página no coninuará.<br>
	 * Para que se muestren las violaciones en la página, hay que utilizar el
	 * tag &lt;Q:Violation /&gt;
	 * 
	 * @param name
	 *            String. Nombre de la validación violada (nombre de campo)
	 * @param violation
	 *            String. Descripción de la violación.
	 */
	public void addValidationViolation(String name, String violation) {
		if (violations == null)
			violations = new HashMap<String, ArrayList<String>>();
		if (!violations.containsKey(name))
			violations.put(name, new ArrayList<String>());
		violations.get(name).add(violation);
	}

	/**
	 * Retorna las violaciones definidas para un nombre
	 * 
	 * @param name
	 *            Nombre de la validación violada
	 * @return String[] o null en caso de no existir violaciones
	 */
	public String[] getValidationViolations(String name) {
		if (violations == null)
			return null;
		if (!violations.containsKey(name))
			return null;
		ArrayList<String> vios = violations.get(name);
		return vios.toArray(new String[vios.size()]);
	}

	/**
	 * Retorna true en caso de que se haya producido algún tipo de violación de
	 * validaión.
	 * 
	 * @return True en caso de que haya validaciones violadas.
	 */
	public boolean haveValidationViolations() {
		return violations != null;
	}
}
