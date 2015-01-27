package nihao.login;

import java.util.HashMap;

import nihao.NiHaoException;
import nihao.db.DataManager;
import nihao.db.DataSourceProvider;
import nihao.login.beans.DBUser;
import nihao.util.Conversor;
import nihao.util.Hasher;

public class LoginDataManager extends DataManager {

	public LoginDataManager() {
		super();
	}

	public LoginDataManager(String connection) {
		super(connection);
	}

	public LoginDataManager(DataSourceProvider connection) {
		super(connection);
	}

	public User getUserByNickPwd(String nick, String pwd) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("nick", nick);
		map.put("pwd", Conversor.bytesToHex(Hasher.SHA1(Conversor.utf8ToBytes(pwd))));
		DBUser[] users = selectAs("login_getUserByNickPwd", map, DBUser[].class);
		if (users.length == 0)
			return null;
		if (users.length > 1)
			throw new NiHaoException("Inconsistance error");
		return users[0].toUser();
	}

	public User getUserByNick(String nick) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("nick", nick);
		DBUser[] users = selectAs("login_getUserByNick", map, DBUser[].class);
		if (users.length == 0)
			return null;
		if (users.length > 1)
			throw new NiHaoException("Inconsistance error");
		return users[0].toUser();
	}

	public Group[] getUserGroups(User user) {
		Group[] result = selectAs("login_getGroupsForUser", user, Group[].class);
		return result;
	}

	public Group getGroupByName(String name) {
		return selectAs("login_getGroupByName", name,Group.class);
	}
}
