package nihao;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

@javax.servlet.annotation.WebFilter(filterName = "SessionFilter", urlPatterns = "/*")
public class WebFilter implements Filter {

	@Override
	public void destroy() {
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		WebCall call = new WebCall(srequest, sresponse, chain);
		try {
			if (call.wantToLogOut)
				call.goEnd();
			else if (call.isExcluded())
				call.goExcluded();
			else if (call.sessionController.isLogged())
				enterPage(call, false);
			else if (call.login())
				enterPage(call, false);
			else
				enterPage(call, true);
		} catch (Throwable t) {
			call.goError(t);
		}
	}

	private void enterPage(WebCall call, boolean tologin) throws IOException,
			ServletException {
		boolean icango = call.canEnterThePage();
		if (icango)
			call.go();
		else if (tologin)
			call.goLogin();
		else
			call.goForbidden();
	}

	public void init(FilterConfig cfg) throws ServletException {
		ServletContext context = cfg.getServletContext();
		NiHao.servletContext=context;
		Enumeration<?> e = context.getInitParameterNames();
		String key = null;
		while (e.hasMoreElements()) {
			key = e.nextElement().toString();
			NiHao.initParameters.put(key, context.getInitParameter(key));
		}
	}
}
