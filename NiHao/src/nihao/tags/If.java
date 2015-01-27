package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import nihao.NiHaoException;

public class If extends TagSupport {
	private static final long serialVersionUID = 1L;

	static final String ATTRIBUTE_NAME = "@IF@";

	private String eval;

	@Override
	public int doStartTag() throws JspException {
		boolean enter;
		if (id != null) {
			Object o = NiHaoTag.getWSValue(id, pageContext);
			if (o instanceof Boolean)
				enter = o == Boolean.TRUE;
			else
				enter = o != null;
		} else {
			if (eval == null)
				throw new NiHaoException("Q:If needs id or eval attributes");
			throw new NiHaoException("Q:If eval not implemented");
		}
		pageContext.setAttribute(ATTRIBUTE_NAME, enter);
		return enter ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}

	public String getEval() {
		return eval;
	}

	public void setEval(String eval) {
		this.eval = eval;
	}
}
