package nihao;

import nihao.login.LoginConf;

public class NiHaoConf {
	private LoginConf loginConf;
	private int worksetCacheSize = 2;
	private String defaultGroupForUndefinedPage;

	public LoginConf getLoginConf() {
		return loginConf;
	}

	public int getWorksetCacheSize() {
		return worksetCacheSize;
	}

	/**
	 * Retorna el grupo por defecto de las páginas que no se definen en el
	 * contexto, si no se define un grupo se redirige a forbidden
	 * 
	 * @return
	 */
	public String getDefaultGroupForUndefinedPage() {
		return defaultGroupForUndefinedPage;
	}
}
