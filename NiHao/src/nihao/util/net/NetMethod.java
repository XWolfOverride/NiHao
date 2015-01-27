package nihao.util.net;

public enum NetMethod {
 /**
  * Standard Web HTTP get, generates a GET request
  */
 GET,

 /**
  * Standard Web HTTP post, generates a POST request
  */
 POST,
 /**
  * Standard Web HTTP head, return only the header of a GET request
  */
 HEAD,
 
 /**
  * Standard Web HTTP options, return a list of the suppoted options
  */
 OPTIONS
}
