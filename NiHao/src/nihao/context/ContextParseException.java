package nihao.context;

public class ContextParseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ContextParseException() {
	}

	public ContextParseException(String message) {
		super(message);
	}

	public ContextParseException(String message, Throwable t) {
		super(message, t);
	}
}
