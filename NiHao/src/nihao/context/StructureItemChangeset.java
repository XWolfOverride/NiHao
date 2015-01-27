package nihao.context;

import java.util.ArrayList;

import nihao.db.Changeset;
import nihao.util.Conversor;
import nihao.util.Hasher;

public class StructureItemChangeset extends StructureItem {
	String engine;
	String author;
	private ArrayList<String> queries = new ArrayList<String>();
	private String hash;
	Changeset.ExecutionMode executeMode;

	public StructureItemChangeset(Context owner, String id) {
		super(owner, id);
	}

	public void addQuery(String q) {
		if (q == null || q.length() == 0)
			return;
		queries.add(q);
	}

	@Override
	public String toString() {
		return "changeset: " + id;
	}

	@Override
	public Object get() {
		return new Changeset(id, engine, author, queries.toArray(new String[queries.size()]), hash, executeMode);
	}

	@Override
	public boolean canPreinstantiate() {
		return true;
	}

	@Override
	public Class<?> getStructureClass() {
		return Changeset.class;
	}

	@Override
	public void commit() {
		StringBuilder sb = new StringBuilder();
		for (String s : queries) {
			sb.append(s);
			sb.append(';');
		}
		hash = Conversor.bytesToHex(Hasher.SHA1(Conversor.utf8ToBytes(sb.toString())));
	}
}
