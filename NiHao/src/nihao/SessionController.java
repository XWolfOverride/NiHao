package nihao;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import nihao.login.Group;
import nihao.login.ILoginModule;
import nihao.login.LoginDataManager;
import nihao.login.User;
import nihao.util.Conversor;

public class SessionController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String CTL_ID = "::QCORE:SESSION_CONTROLLER";
	private static final long USERCACHETIMEOUT = 60000; // 1 minuto, una vez que
														// pase se rechequea el
														// usuario (no se sale
														// de la sesion)

	/**
	 * Usuario GUEST
	 */
	public static final Group guestGroup;
	public static final User guestUser;

	static {
		guestGroup=new Group();
		guestGroup.setId(-1);
		guestGroup.setName("Guest");
		guestUser = new User(-1);
		guestUser.setName("GUEST");
		guestUser.setNick("GUEST");
		guestUser.setGroups(new Group[] { guestGroup });
	}

	private String sessionId = null;
	private long usercachetimectl = 0;
	private User user;

	private SessionController(HttpSession session) {
		sessionId = session.getId();
	}

	/**
	 * Retorna true si esta logado
	 * 
	 * @return boolean
	 */
	public boolean isLogged() {
		return user != null && user != guestUser;
	}

	/**
	 * Realiza el proceso de login
	 * 
	 * @param call
	 *            WebCall
	 * @return boolean
	 */
	boolean login(WebCall call) {
		if (isLogged()) {
			if (System.currentTimeMillis() - usercachetimectl > USERCACHETIMEOUT) {
				// Comprueba si el usuario sigue siendo válido
				usercachetimectl = System.currentTimeMillis();
				LoginDataManager ldm = new LoginDataManager();
				user = ldm.getUserByNick(user.getNick());
				if (user == null)
					return login(call);
				user.setGroups(ldm.getUserGroups(user));
			}
			return true;
		}
		try {
			usercachetimectl = System.currentTimeMillis();
			ILoginModule[] lmods = NiHao.getConf().getLoginConf().getModules();
			for (ILoginModule lmod : lmods) {
				user = lmod.login(call);
				if (user != null) {
					LoginDataManager ldm = new LoginDataManager();
					user.setGroups(ldm.getUserGroups(user));
					return true;
				}
			}
			user = guestUser;
			return false;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Obtiene el usuario en sesión
	 * 
	 * @return
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Recoge el seesionController de la sesión, si no hay crea una nueva
	 * 
	 * @param session
	 *            HttpSession
	 * @return WebSessionHandler
	 */
	public static SessionController getSessionController(HttpSession session) {
		SessionController result = Conversor.as(session.getAttribute(CTL_ID), SessionController.class);
		if (result == null) {
			result = new SessionController(session);
			session.setAttribute(CTL_ID, result);
		}
		return result;
	}

	/**
	 * Obtiene el identificador de la sesion
	 * 
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}
}
