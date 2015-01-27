package nihao;

public class ProviderInfo {

	public static ProviderInfo get(Class<? extends Provider> provider) {
		return get(provider, null);
	}

	public static ProviderInfo get(Class<? extends Provider> provider, Class<? extends Provider> defaultProvider) {
		ProviderInfo info = NiHao.getAs(provider.getSimpleName(), ProviderInfo.class);
		if (info == null)
			if (defaultProvider == null)
				throw new NiHaoException("Can't locate provider " + provider.getSimpleName());
			else {
				info = new ProviderInfo(provider.getSimpleName(), defaultProvider.getSimpleName(), new String[0]);
			}

		return info;
	}

	String providerName;
	String defaultProvider;
	String[] alternatives;

	public ProviderInfo(String name, String defaultProvider, String[] alternatives) {
		providerName = name;
		this.defaultProvider = defaultProvider;
		this.alternatives = alternatives;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getDefaultProvider() {
		return defaultProvider;
	}

	public String[] getAlternatives() {
		return alternatives;
	}
}
