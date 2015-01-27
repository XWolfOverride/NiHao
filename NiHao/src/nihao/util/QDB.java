package nihao.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class QDB {
	private HashMap<String, QDB> childs = new HashMap<String, QDB>();
	private Object data;

	private boolean empty() {
		if (data != null)
			return false;
		for (QDB q : childs.values())
			if (!q.empty())
				return false;
		return true;
	}

	public QDB ₪(String key) {
		QDB child = childs.get(key);
		if (child == null) {
			child = new QDB();
			childs.put(key, child);
		}
		return child;
	}

	public QDB $(String key) {
		QDB child = childs.get(key);
		if (child == null) {
			child = new QDB();
			childs.put(key, child);
		}
		return child;
	}
	
	public Iterator<QDB> iterator(){
		return childs.values().iterator();
	}

	public void set(Object data) {
		this.data = data;
	}

	public <T> T get() {
		return Conversor.as(data);
	}

	public void purge() {
		for (Entry<String, QDB> e : childs.entrySet()) {
			if (e.getValue().empty())
				childs.remove(e.getKey());
			else
				e.getValue().purge();
		}
	}
}
