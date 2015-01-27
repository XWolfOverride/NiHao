package nihao.context;

import java.io.InputStream;

import nihao.NiHaoException;
import nihao.util.Conversor;
import nihao.util.Resources;

public class FileContext extends Context {

	public FileContext(String path) {
		InputStream input = Resources.getResourceAsStream(path);
		if (input==null)
			throw new NiHaoException("Can't open context file: "+path);
		read(input);
	}

	public FileContext(InputStream input) {
		if (input==null)
			throw new IllegalArgumentException("null InputStream");
		read(input);
	}

	private void read(InputStream input) {
		parse(Conversor.readToString(input));
	}
}
