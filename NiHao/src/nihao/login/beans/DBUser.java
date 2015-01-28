package nihao.login.beans;

import nihao.login.User;

public class DBUser {
	private long id;
	private String nick;
	private String name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		result.setName(name);
		return result;
	}
}
