package nihao.login;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String nick;
	private String name;
	private String surname;
	private Group[] groups;

	public User(long id) {
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

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Group[] getGroups() {
		return groups;
	}

	public void setGroups(Group[] groups) {
		this.groups = groups;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + ": " + nick;
	}
}
