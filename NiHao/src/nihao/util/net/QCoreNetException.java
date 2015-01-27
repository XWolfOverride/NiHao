package nihao.util.net;

import java.io.InputStream;

import nihao.NiHaoException;

public class QCoreNetException extends NiHaoException {
	private static final long serialVersionUID = 1L;

	private int code;
	private InputStream errstream;
	
	public QCoreNetException(int code, String msg, InputStream errstream) {
		super(msg);
		this.code = code;
		this.errstream = errstream;
	}

	public int getHttpCode() {
		return code;
	}
	
	public String getCodeDescription(){
		return null;
	}

	public InputStream getErrorResponse() {
		return errstream;
	}
}
