package nihao.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nihao.util.IoUtil;
import nihao.util.Serializer;

@WebServlet(name = "NiHao", urlPatterns = "/nihao/*")
public class WebAjax extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getPathInfo();
		if (url.endsWith(".js")) {
			url = url.substring(1, url.length() - 3);
			AjaxObject o = AjaxObject.get(url);
			PrintWriter out = response.getWriter();
			if (o != null)
				pushObjectJS(out, o, request.getContextPath() + request.getServletPath());
			else
				out.print("Error");
		} else if (url.contains(".")) {
			int point = url.indexOf('.');
			String method = url.substring(point + 1);
			url = url.substring(1, point);
			AjaxObject o = AjaxObject.get(url);
			if (o == null)
				response.getWriter().print("Call Error");
			else
				try {
					Object result = o.invoke(method, IoUtil.readStringToEnd(request.getReader()));
					response.getWriter().write(Serializer.serializeToJSON(result));
				} catch (Throwable t) {
					response.getWriter().print("Error " + t.getClass().getName() + ": " + t.getMessage());
				}
		} else
			response.getWriter().print("Error");
	}

	private void pushObjectJS(PrintWriter out, AjaxObject o, String basepath) throws IOException {
		String oname = o.getName();
		out.print("var ");
		out.print(oname);
		out.println(" = new (function(){");
		out.print("var enc=");
		AjaxSerializer.printJavascriptSerializer(out);
		out.print(";var dec=");
		AjaxSerializer.printJavascriptDeserializer(out);
		out.print(";var call=function(mth,args){");
		out.print("xhr = new XMLHttpRequest();");
		out.print("xhr.open('post', '");
		out.print(basepath);
		out.print("/");
		out.print(oname);
		out.print(".'+mth, false);");
		out.print("xhr.send(enc(args));");
		out.print("return dec(xhr.responseText);");
		out.println("};");
		for (String s : o.getPublicMethods()) {
			String[] pars = o.getPublicMethodParams(s);
			out.print("this.");
			out.print(s);
			out.print("=function(");
			boolean first = true;
			for (String param : pars) {
				if (first)
					first = false;
				else
					out.print(", ");
				out.print(param);
			}
			out.print("){return call('");
			out.print(s);
			out.print("',{");
			first = true;
			for (String param : pars) {
				if (first)
					first = false;
				else
					out.print(", ");
				out.print(param);
				out.print(": ");
				out.print(param);
			}
			out.println("});}");
		}
		out.println("})()");
	}
}
