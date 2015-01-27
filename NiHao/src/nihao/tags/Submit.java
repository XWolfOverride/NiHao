package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import nihao.tags.util.TagBuilder;

public class Submit extends NiHaoTag {
	private static final long serialVersionUID = 1L;

	private String label;
	private String execute;

	@Override
	public int doStartTag() throws JspException {
		return Tag.SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		TagBuilder tb = new TagBuilder("input");
		tb.setAttr("type", "submit");
		if (styleclass != null)
			tb.setAttr("class", styleclass);
		tb.setAttr("value", label);
		if (execute != null)
			tb.setAttr("name", "@" + execute);
		write(tb);
		return Tag.EVAL_PAGE;
	}

	@Override
	protected void setValue(TagBuilder tb) {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getExecute() {
		return execute;
	}

	public void setExecute(String execute) {
		this.execute = execute;
	}
}
