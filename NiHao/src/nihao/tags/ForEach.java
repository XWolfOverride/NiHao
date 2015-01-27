package nihao.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import nihao.NiHaoException;
import nihao.util.cursor.Cursor;

public class ForEach extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String as;

	@Override
	public int doStartTag() throws JspException {
		Cursor c = Cursor.getCursor(NiHaoTag.getWSValue(id, pageContext));
		pageContext.setAttribute("@C:" + as, c);
		if (!c.hasMoreElements())
			return SKIP_BODY;
		pageContext.setAttribute(as, c.next());
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doAfterBody() throws JspException {
		Cursor c = (Cursor) pageContext.getAttribute("@C:" + as);
		if (!c.hasMoreElements())
			return SKIP_BODY;
		pageContext.setAttribute(as, c.next());
		return EVAL_BODY_AGAIN;
	}

	@Override
	public int doEndTag() throws JspException {
		pageContext.removeAttribute("@C:" + as);
		pageContext.removeAttribute(as);
		return EVAL_PAGE;
	}

	public String getAs() {
		return as;
	}

	public void setAs(String as) {
		if (as == null)
			throw new NiHaoException("Q:ForEach.as can't be null");
		if (!as.startsWith("|"))
			throw new NiHaoException("Q:ForEach.as must start with \"|\"");
		this.as = as;
	}
}
