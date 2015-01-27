package nihao;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import nihao.util.Conversor;
import nihao.util.LRUCache;

public class WorksetController implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String CTL_ID = "::QCORE:WORKSET_CONTROLLER";
	private LRUCache<String, WorksetHandler> cache = new LRUCache<String, WorksetHandler>(NiHao.getConf().getWorksetCacheSize());

	private WorksetController() {
	}

	public static WorksetController getWorksetController(HttpSession session) {
		WorksetController result = Conversor.as(session.getAttribute(CTL_ID), WorksetController.class);
		if (result == null) {
			result = new WorksetController();
			session.setAttribute(CTL_ID, result);
		}
		return result;
	}

	public WorksetHandler get(String name) {
		if (cache.containsKey(name))
			return cache.get(name);
		WorksetHandler r = new WorksetHandler(name);
		cache.put(name, r);
		return r;
	}
}
