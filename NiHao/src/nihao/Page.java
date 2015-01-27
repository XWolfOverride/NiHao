package nihao;

import nihao.login.Group;
import nihao.login.User;
import nihao.types.PageSecuityType;

public class Page {
	private String url;
	private String workset;
	private String[] groupAllow;
	private String[] groupDeny;
	private PageSecuityType security;
	private boolean regex;

	public Page(String url,PageSecuityType security) {
		this.url = url;
		this.security=security;
	}

	public String getUrl() {
		return url;
	}

	public void setGroupAllow(String[] groupAllow) {
		this.groupAllow = groupAllow;
	}

	public void setGroupDeny(String[] groupDeny) {
		this.groupDeny = groupDeny;
	}

	public void setWorkset(String workset) {
		this.workset = workset;
	}

	public String getWorkset() {
		return workset;
	}

	private boolean inList(String name, String[] groups) {
		for (String g : groups)
			if (name.equalsIgnoreCase(g))
				return true;
		return false;
	}

	/**
	 * Retorna si un usuario puede entrar en una página a partir de los grupos
	 * definidos.<br>
	 * Si un usuario no contiene ningún grupo permitido, no puede entrar.<br>
	 * Si un usuario contiene algún grupo permitido y ninguno restringido, puede
	 * entrar.<br>
	 * Si un usuario contiene algún grupo restringido, no puede entrar.<br>
	 * 
	 * @param user
	 *            User
	 * @return
	 */
	public boolean allow(User user) {
		if (security==PageSecuityType.PUBLIC)
			return true;
		if (security==PageSecuityType.PRIVATE)
			return user!=SessionController.guestUser;
		boolean allow = false;
		for (Group g : user.getGroups()) {
			if (groupDeny!=null && inList(g.getName(), groupDeny))
				return false; // la denegación es inmediata
			if (groupAllow!=null && inList(g.getName(), groupAllow))
				allow = true;
		}
		return allow;
	}

	public boolean isRegex() {
		return regex;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}
	
	@Override
	public String toString() {
		return "Page: "+(regex?"~":"")+url;
	}
}
