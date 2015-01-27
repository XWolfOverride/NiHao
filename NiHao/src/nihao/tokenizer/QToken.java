package nihao.tokenizer;

public class QToken {
	private QTokenType type;
	private String value;

	public QToken(QTokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	public QTokenType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return type.name() + " " + value;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof QToken))
			return false;
		QToken t = (QToken) o;
		return t.type == type && value.equals(t.value);
	}

	public boolean equals(QTokenType type, String value) {
		return this.type == type && this.value.equals(value);
	}
}
