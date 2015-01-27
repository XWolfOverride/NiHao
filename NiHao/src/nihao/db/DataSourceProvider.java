package nihao.db;

import java.sql.Connection;

import nihao.Provider;
import nihao.ProviderInfo;
import nihao.NiHao;

public abstract class DataSourceProvider extends Provider{
	private static ProviderInfo info=ProviderInfo.get(DataSourceProvider.class);
	
	public static DataSourceProvider getProvider(){
		return instanceAs(info, DataSourceProvider.class);
	}

	public static DataSourceProvider getProvider(String name){
		return NiHao.getAs(name, DataSourceProvider.class);
	}

	String engine;
	
	public abstract Connection getConnection();

	/**
	 * retorna el tipo de engine del proveedor
	 * @return
	 */
	public String getEngine() {
		return engine;
	}
}
