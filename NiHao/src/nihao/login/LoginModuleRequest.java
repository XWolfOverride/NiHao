package nihao.login;

import nihao.WebCall;

public class LoginModuleRequest implements ILoginModule{
	private String userParamId = "user";
	private String passwordParamId = "pwd";

	@Override
	public User login(WebCall call) {
		String usr = call.getParameter(userParamId);
		String pwd = call.getParameter(passwordParamId);
		if (usr==null || pwd==null)
			return null;
		LoginDataManager ldm=new LoginDataManager();
		return ldm.getUserByNickPwd(usr, pwd);
	}

	public void setUserParamId(String userParamId) {
		this.userParamId = userParamId;
	}

	public void setPasswordParamId(String passwordParamId) {
		this.passwordParamId = passwordParamId;
	}
}
