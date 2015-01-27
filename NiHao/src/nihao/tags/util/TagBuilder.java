package nihao.tags.util;

import nihao.NiHaoException;

public class TagBuilder {
	private StringBuilder sb = new StringBuilder(1024);

	private String tagName;
	private boolean body = false;
	private boolean close = false;

	public TagBuilder(String tagName) {
		this.tagName = tagName;
		sb.append('<');
		sb.append(tagName);
	}

	public void setAttr(String name, String value) {
		if (body)
			throw new NiHaoException("Can't build tag attribute before tag body");
		sb.append(' ');
		sb.append(name);
		sb.append('=');
		sb.append('"');
		sb.append(value);
		sb.append('"');
	}

	public void setBody(String body) {
		if (this.body)
			throw new NiHaoException("Can't build multiple tag bodyes");
		this.body = true;
		sb.append('>');
		sb.append(body);
		sb.append('<');
	}

	public void close() {
		if (body) {
			sb.append('/');
			sb.append(tagName);
			sb.append('>');
		} else
			sb.append("/>");
		close = true;
	}

	@Override
	public String toString() {
		if (!close)
			close();
		return sb.toString();
	}
}
