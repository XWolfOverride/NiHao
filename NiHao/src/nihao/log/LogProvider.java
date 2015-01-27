package nihao.log;

import nihao.NiHao;
import nihao.Provider;
import nihao.ProviderInfo;

public abstract class LogProvider extends Provider {
	private static ProviderInfo info = ProviderInfo.get(LogProvider.class,NilLogProvider.class);
	private static LogProvider prov;

	public static LogProvider getProvider() {
		if (prov == null) {
			prov = instanceAs(info, LogProvider.class);
			if (prov == null)
				prov = new NilLogProvider();
		}
		return prov;
	}

	public static LogProvider getProvider(String name) {
		return NiHao.getAs(name, LogProvider.class);
	}

	public abstract void writeLog(long timestamp, LogLevel level, String s);

	public void debug(Object... o) {
		writeLog(System.currentTimeMillis(), LogLevel.DEBUG, compute(o));
	}

	public void info(Object... o) {
		writeLog(System.currentTimeMillis(), LogLevel.INFO, compute(o));
	}

	public void warn(Object... o) {
		writeLog(System.currentTimeMillis(), LogLevel.WARNING, compute(o));
	}

	public void error(Object... o) {
		writeLog(System.currentTimeMillis(), LogLevel.ERROR, compute(o));
	}

	public void exception(Throwable t) {
		writeLog(System.currentTimeMillis(), LogLevel.ERROR, compute(new Object[] { t }));
	}

	private String compute(Object[] data) {
		StringBuffer sb = new StringBuffer();
		for (Object o : data) {
			if (sb.length() > 0)
				sb.append(' ');
			if (o instanceof Throwable) {

			} else
				sb.append(o);
		}
		return sb.toString();
	}
}
