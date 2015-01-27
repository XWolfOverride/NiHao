package nihao.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import nihao.NiHaoException;

public class DataSourceProviderJNDI extends DataSourceProvider {
	private String jndiConnectionName;

	public DataSourceProviderJNDI(String jndiConnectionName) {
		this.jndiConnectionName = jndiConnectionName;
	}

	public void setJndiConnectionName(String jndiConnectionName) {
		this.jndiConnectionName = jndiConnectionName;
	}

	@Override
	public Connection getConnection() {
		Context initialContext;
		try {
			initialContext = new InitialContext();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
		DataSource datasource = null;
		try {
			datasource = (DataSource) initialContext.lookup(jndiConnectionName);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
		if (datasource == null)
			throw new NiHaoException("Can't get datasource");
		try {
			return datasource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
