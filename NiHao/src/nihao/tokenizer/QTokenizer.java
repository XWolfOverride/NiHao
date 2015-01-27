package nihao.tokenizer;

public class QTokenizer {
	private static final String ignore = " \t\n\r";
	private static final String number = "0123456789.";
	private static final String letters = "abcdefghijklmnñopqrstuvwxyzáéíóúàèìòùâêîôûäëïöüãõ_ABCDEFGHIJKLMNÑOPQRSTUVWXYZÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÄËÏÖÜÃÕ0123456789";
	private String s;
	private int index = 0;

	public QTokenizer(String input) {
		s = input;
	}

	public QToken patrol() {
		int i = index;
		QToken result = next();
		index = i;
		return result;
	}

	public QToken next() {
		if (index >= s.length())
			return null;
		while (ignore.contains(new String(new char[] { s.charAt(index) }))) {
			index++;
			if (index >= s.length())
				return null;
		}
		char ch = s.charAt(index);
		String range;
		String sch = new String(new char[] { ch });
		if (number.contains(sch) && ch != '.')
			range = number;
		else if (letters.contains(sch))
			range = letters;
		else {
			index++;
			if (ch == '"' || ch == '\'') { // String
				StringBuilder sb = new StringBuilder();
				char ich;
				while ((ich = s.charAt(index)) != ch) {
					sb.append(ich);
					index++;
					if (index >= s.length())
						break;
				}
				index++;
				return new QToken(QTokenType.LITERAL, sb.toString());
			} else if (index < s.length() && ch == '/' && s.charAt(index) == '/') { // Commentary jump
				while (s.charAt(index) != '\n') {
					index++;
					if (index >= s.length())
						break;
				}
				return next();
			} else
				return new QToken(QTokenType.SYMBOL, sch);
		}
		StringBuilder sb = new StringBuilder();
		while (range.contains(sch)) {
			sb.append(ch);
			index++;
			if (index >= s.length())
				break;
			ch = s.charAt(index);
			sch = new String(new char[] { ch });
		}
		return new QToken(range == number ? QTokenType.NUMBER : QTokenType.WORD, sb.toString());
	}
}
