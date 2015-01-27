package nihao.util;

import java.util.HashMap;

/**
 * Fast HashMap Building Class
 * 
 * @author XWolf Override
 *
 */
public class HM<K, V> {
	private HashMap<K, V> data;

	public HM() {
		data = new HashMap<K, V>();
	}

	public HM<K, V> put(K k, V v) {
		data.put(k, v);
		return this;
	}

	public HashMap<K, V> get() {
		return data;
	}
}
