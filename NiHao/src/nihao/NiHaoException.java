package nihao;

public class NiHaoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NiHaoException(String message) {
		super(message);
	}

	public NiHaoException(String message, Throwable t) {
		super(message, t);
	}

	public NiHaoException(Throwable t) {
		super(t);
	}
}
