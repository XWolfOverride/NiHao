package nihao.dto;

import java.sql.Timestamp;

public class ChangesetDTO {
	private String id;
	private String author;
	private Timestamp executed;
	private String hash;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Timestamp getExecuted() {
		return executed;
	}

	public void setExecuted(Timestamp executed) {
		this.executed = executed;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
