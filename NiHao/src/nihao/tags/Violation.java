package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import nihao.WebCall;

public class Violation extends TagSupport {
	private static final long serialVersionUID = 1L;

	@Override
	public int doStartTag() throws JspException {
		WebCall call = WebCall.getWebCall(pageContext.getRequest());
		String[] vios = call.getValidationViolations(id);
		if (vios == null)
			return SKIP_BODY;
		return EVAL_BODY_INCLUDE;
	}
}
