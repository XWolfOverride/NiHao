package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import nihao.tags.util.TagBuilder;

public class Text extends NiHaoTag{
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		return Tag.SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		write(getWSValueString(id));
		return Tag.EVAL_PAGE;
	}
	
	@Override
	protected void setValue(TagBuilder tb) {
	}
}
