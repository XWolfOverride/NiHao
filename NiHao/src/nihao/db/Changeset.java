package nihao.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import nihao.log.LogProvider;
import nihao.util.Conversor;

public class Changeset {
	public enum ExecutionMode {
		ONCE, ALWAYS, ONCHANGE
	}

	private String id;
	private String engine;
	private String author;
	private String[] query;
	private String hash;
	private ExecutionMode executionMode;

	public Changeset(String id, String engine, String author, String[] query, String hash, ExecutionMode executionMode) {
		this.id = id;
		this.engine = engine;
		this.author = author;
		this.query = query;
		this.hash = hash;
		this.executionMode = executionMode;
	}

	void execute(Connection con) throws SQLException {
		LogProvider.getProvider().info("Changeset \"" + id + "\" for " + Conversor.nvl(engine, "any"));
		Statement st = con.createStatement();
		for (String query : query)
			if (query.length() > 0) {
				LogProvider.getProvider().debug(query);
				st.execute(query);
			}
		st.close();
	}

	public String getId() {
		return id;
	}

	public String getEngine() {
		return engine;
	}

	public String getAuthor() {
		return author;
	}

	public String getHash() {
		return hash;
	}

	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	@Override
	public String toString() {
		return "Changeset '" + id + "'";
	}
}
