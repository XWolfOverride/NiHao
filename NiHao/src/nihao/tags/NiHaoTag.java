package nihao.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import nihao.NiHaoException;
import nihao.WebCall;
import nihao.tags.types.TagValueType;
import nihao.tags.util.TagBuilder;
import nihao.util.reflection.Reflector;

public abstract class NiHaoTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	protected TagValueType valueType = TagValueType.AUTO;

	protected String htmlid = null;
	protected String styleclass = null;
	protected String fname;
	protected String value;

	protected void initValues() {
		fname = "::" + id;
		if (htmlid == null)
			htmlid = fname;
	}

	private static Object getContextValue(String name, PageContext pageContext) {
		if (!name.contains("."))
			return pageContext.getAttribute(name);
		String[] names = name.split("\\.");
		Object o = pageContext.getAttribute(names[0]);
		if (o == null)
			return null;
		return Reflector.getPathValue(o, names, 1, Object.class);
	}

	protected Object getWSValue(String name) {
		if (name == null)
			return null;
		if (name.startsWith("|"))
			return getContextValue(name, pageContext);
		WebCall c = WebCall.getWebCall(pageContext.getRequest());
		return c.getValue(name);
	}

	protected String getWSValueString(String name) {
		Object v = getWSValue(name);
		if (v == null)
			return null;
		return v.toString();
	}

	static Object getWSValue(String name, PageContext pageContext) {
		if (name.startsWith("|"))
			return getContextValue(name, pageContext);
		WebCall c = WebCall.getWebCall(pageContext.getRequest());
		return c.getValue(name);
	}

	static String getWSValueString(String name, PageContext pageContext) {
		Object v = getWSValue(name, pageContext);
		if (v == null)
			return null;
		return v.toString();
	}

	protected WebCall getWebCall() {
		return WebCall.getWebCall(pageContext.getRequest());
	}

	protected String getDisplayValue() {
		String result;
		switch (valueType) {
		case AUTO:
			if (value == null)
				result = getWSValueString(id);
			else
				result = value;
			break;
		case NONE:
			result = "";
			break;
		case WORKSET:
			result = getWSValueString(id);
			break;
		case VALUE:
			result = value;
			break;
		default:
			throw new NiHaoException("Unknown ValueType on Tag");
		}
		return result;
	}

	protected Object getValue() {
		Object result;
		switch (valueType) {
		case AUTO:
			if (value == null)
				result = getWSValue(id);
			else
				result = value;
			break;
		case NONE:
			result = "";
			break;
		case WORKSET:
			result = getWSValue(id);
			break;
		case VALUE:
			result = value;
			break;
		default:
			throw new NiHaoException("Unknown ValueType on Tag");
		}
		return result;
	}

	protected void fillTag(TagBuilder tb) {
		tb.setAttr("name", fname);
		tb.setAttr("id", htmlid);
		if (styleclass != null)
			tb.setAttr("class", styleclass);
		setValue(tb);
	}

	protected abstract void setValue(TagBuilder tb);

	protected void write(String s) {
		try {
			pageContext.getOut().write(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void write(TagBuilder tb) {
		write(tb.toString());
	}

	public String getHtmlid() {
		return htmlid;
	}

	public void setHtmlid(String htmlid) {
		this.htmlid = htmlid;
	}

	public String getStyleclass() {
		return styleclass;
	}

	public void setStyleclass(String styleclass) {
		this.styleclass = styleclass;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doAfterBody() throws JspException {
		System.out.println("doAfterBody()");
		return super.doAfterBody();
	}

	@Override
	public int doEndTag() throws JspException {
		System.out.println("doEndTag()");
		return super.doEndTag();
	}

	@Override
	public int doStartTag() throws JspException {
		System.out.println("doStartTag()");
		return super.doStartTag();
	}
}
