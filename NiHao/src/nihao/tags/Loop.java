package nihao.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import nihao.NiHaoException;

public class Loop extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	int times = 0;

	@Override
	public int doStartTag() throws JspException {
		if (id != null) {
			Object o = NiHaoTag.getWSValue(id, pageContext);
			if (o instanceof Integer)
				this.times = (Integer) o;
			else if (o instanceof String)
				this.times = Integer.parseInt((String) o);
			else
				throw new NiHaoException("Q:Loop times value for '" + times + "' is not integer");
		}
		if (times > 0) {
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}

	public int doAfterBody() throws JspException {
		if (times > 1) {
			times--;
			return EVAL_BODY_AGAIN;
		} else {
			return SKIP_BODY;
		}
	}

	public int doEndTag() throws JspException {
		try {
			if (bodyContent != null) {
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
			}
		} catch (IOException e) {
			throw new JspException("Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
}
