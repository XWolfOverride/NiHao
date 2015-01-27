package nihao.db;

import java.sql.SQLException;
import java.sql.Timestamp;

import nihao.NiHaoException;
import nihao.context.Context;
import nihao.db.Changeset.ExecutionMode;
import nihao.dto.ChangesetDTO;

public class ChangesetDataManager extends DataManager {
	public ChangesetDataManager() {
		super();
	}

	public ChangesetDataManager(String connection) {
		super(connection);
	}

	public ChangesetDataManager(DataSourceProvider connection) {
		super(connection);
	}

	public void runChagesets(Context ctx) {
		for (Changeset ch : ctx.getChangesets(getEngine()))
			runChangeset(ch);
	}

	private void runChangeset(Changeset ch) {
		DataTransaction trans = beginTransaction();
		try {
			ChangesetDTO change;
			try {
				change = trans.selectAs("nhaochse_getChange", ch.getId(), ChangesetDTO.class);
			} catch (SQLException e) {
				if ("bootstrap".equals(ch.getId()))
					change = null;
				else
					throw e;
			}
			if (change == null) {
				ch.execute(con);
				change = new ChangesetDTO();
				change.setAuthor(ch.getAuthor());
				change.setHash(ch.getHash());
				change.setId(ch.getId());
				change.setExecuted(new Timestamp((new java.util.Date()).getTime()));
				execute("nhaochse_insertChange", change);
			} else {
				boolean changed = !ch.getHash().equals(change.getHash());
				if (ch.getExecutionMode() == ExecutionMode.ALWAYS || (changed) && ch.getExecutionMode() == ExecutionMode.ONCHANGE) {
					ch.execute(con);
					change.setExecuted(new Timestamp((new java.util.Date()).getTime()));
					execute("nhaochse_updateChange", change);
				} else if (changed)
					throw new NiHaoException("Changeset '" + ch.getId() + "' executed with hash " + change.getHash() + " and now the hash is " + ch.getHash());
			}

		} catch (Throwable t) {
			rollbackTransaction(trans);
			System.err.println();
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new NiHaoException(t);
		}
	}
}
