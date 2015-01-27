package nihao.util;

public enum HashType {

	SHA1("SHA-1", "1.3.14.3.2.26"), SHA256("SHA-256","2.16.840.1.101.3.4.2.1"), SHA384("SHA-384","2.16.840.1.101.3.4.2.2"), SHA512("SHA-512","2.16.840.1.101.3.4.2.3"), SHA224("SHA-224","2.16.840.1.101.3.4.2.4"), MD5("MD5", "1.2.840.113549.2.5"), MD2("MD2","1.2.840.113549.2.2");

//	static final String digestAlgorithm = "1.2.840.113549.2";
//	static final String md2 = digestAlgorithm + ".2";
//	static final String md5 = digestAlgorithm + ".5";
//
//	static final String nistAlgorithm = "2.16.840.1.101.3.4";
//	static final String id_sha256 = nistAlgorithm + ".2.1";
//	static final String id_sha384 = nistAlgorithm + ".2.2";
//	static final String id_sha512 = nistAlgorithm + ".2.3";
//	static final String id_sha224 = nistAlgorithm + ".2.4";

	private HashType(String v, String oid) {
		value = v;
		this.oid = oid;
	}

	private String value;
	private String oid;

	public String getValue() {
		return value;
	}

	public String getOID() {
		return oid;
	}
	
	/**
	 * Retorna un HashType desde su OID, si no lo encuentra retorna null
	 * @param oid String
	 * @return HashType
	 */
	public static HashType fromOID(String oid){
		for(HashType t:values())
			if (t.oid.equals(oid))
				return t;
		return null;
	}
}
