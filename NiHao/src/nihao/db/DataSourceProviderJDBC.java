package nihao.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nihao.NiHaoException;

public class DataSourceProviderJDBC extends DataSourceProvider {
	private String connectionString;
	private String driver;
	private String user;
	private String password;

	public DataSourceProviderJDBC(String connectionString) {
		this.connectionString = connectionString;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	@Override
	public Connection getConnection() {
		if (driver != null)
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				throw new NiHaoException("Can't find driver class " + driver, e);
			}
		try {
			return DriverManager.getConnection(connectionString, user, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
