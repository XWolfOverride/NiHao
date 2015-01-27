package nihao.login.beans;

import nihao.login.User;

public class DBUser {
	private long id;
	private String nick;
	private String display;
	private String pwd;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public User toUser(){
		User result=new User(id);
		result.setNick(nick);
		result.setName(display);
		return result;
	}
}
