package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class Else extends TagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		boolean enter = pageContext.getAttribute(If.ATTRIBUTE_NAME) != Boolean.TRUE;
		return enter ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}
}
