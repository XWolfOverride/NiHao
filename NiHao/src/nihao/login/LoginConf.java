package nihao.login;

public class LoginConf {
	private ILoginModule[] modules;
	private String pageLogin;
	private String pageEnd;
	private String pageError;
	private String pageForbidden;
	private String[] excluded;
	private boolean disabled;

	public ILoginModule[] getModules() {
		return modules;
	}

	public String getPageLogin() {
		return pageLogin;
	}

	public String getPageEnd() {
		return pageEnd;
	}

	public String getPageError() {
		return pageError;
	}

	public String getPageForbidden() {
		return pageForbidden;
	}

	public String[] getExcluded() {
		return excluded;
	}

	/**
	 * True if all login and page security is down (not recommended)
	 * @return
	 */
	public boolean isDisabled() {
		return disabled;
	}
}
