package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import nihao.tags.util.TagBuilder;

public class PasswordBox extends NiHaoTagInput{
	private static final long serialVersionUID = 1L;

	

	@Override
	public int doStartTag() throws JspException {
		return Tag.SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		initValues();
		TagBuilder tb=new TagBuilder("input");
		tb.setAttr("type", "password");
		fillTag(tb);
		write(tb);
		return Tag.EVAL_PAGE;
	}
}
