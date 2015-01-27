package nihao;

public abstract class Provider {
	protected static <T extends Provider> T instanceAs(ProviderInfo info, Class<T> cls) {
		return NiHao.getAs(info.defaultProvider, cls);
	}

	protected static <T extends Provider> T instanceAs(ProviderInfo info, String name, Class<T> cls) {
		return NiHao.getAs(name, cls);
	}
}
