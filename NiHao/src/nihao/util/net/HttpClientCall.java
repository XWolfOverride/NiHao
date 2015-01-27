package nihao.util.net;

public class HttpClientCall {
	protected HttpRequest request;
	protected HttpResponse response;

	public HttpClientCall() {
		this.request = new HttpRequest();
	}

	public HttpClientCall(HttpRequest request) {
		this.request = request;
	}

	public HttpClientCall(NetMethod method, String url) {
		request = new HttpRequest(method, url);
	}

	public HttpClientCall(String method, String url) {
		request = new HttpRequest(method, url);
	}
}
