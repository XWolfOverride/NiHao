package nihao.util.net;

public class HttpRequest {
	private String method;
	private String url;
	
	public HttpRequest(){
		url="/";
		method=NetMethod.GET.name();
	}
	
	public HttpRequest(NetMethod method,String url){
		this.method=method.name();
		this.url=url;
	}

	public HttpRequest(String method,String url){
		this.method=method;
		this.url=url;
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}
}
