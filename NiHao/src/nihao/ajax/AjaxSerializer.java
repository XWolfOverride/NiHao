package nihao.ajax;

import java.io.PrintWriter;

public class AjaxSerializer {
	static void printJavascriptSerializer(PrintWriter out) {
		out.print("function(data){");
		out.print("return JSON.stringify(data);");
		out.print("}");
	}

	static void printJavascriptDeserializer(PrintWriter out) {
		out.print("function(data){");
		out.print("try{return data.length==0?undefined:JSON.parse(data);}catch(e){throw new Error(data);}");
		out.print("}");
	}
}
