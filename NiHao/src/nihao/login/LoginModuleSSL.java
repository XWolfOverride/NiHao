package nihao.login;

import java.security.cert.X509Certificate;

import nihao.WebCall;

public class LoginModuleSSL implements ILoginModule {
	private String sslCertificateAttributeName = "javax.servlet.request.X509Certificate";

	@Override
	public User login(WebCall call) {
		X509Certificate[] certs = call.getSSLCertificates(sslCertificateAttributeName);
		if (certs==null)
			return null;
		if (certs.length==0)
			return null;
		X509Certificate user=certs[0];
		String sub=user.getSubjectDN().toString();
		//realizar un ocsp del certificado
		//obtener DNI
		if (!sub.contains("DNI"))
			return null;
		return null;
	}

	public void setSslCertificateAttributeName(String sslCertificateAttributeName) {
		this.sslCertificateAttributeName = sslCertificateAttributeName;
	}
}
