package BU.transactions;

import java.net.URI;
import java.net.URISyntaxException;

public class Transaction {
	String name;
	String application;
	String uid;
	final String TYPE = "TRANSACTION";
	Step[] steps;
	
	public Transaction(String name, String application, Step[] steps) {
		this.name = name;
		this.application = application;
		this.steps = steps;
	}

	/**
	 * Takes in a URL and returns the last part of it to be used as the name of the transaction.
	 * If the URL has several query parameters this method will return the value of the first one (see Ex. 3)
	 * @param url
	 * @return name of transaction
	 * Ex. 1 http://example.com/abc/action?query=value -> value 
	 * Ex. 2 http://example.com/abc/-> abc
	 * Ex. 3 http://example.com/abc/action?query=value&subquery=asd -> value 
	 * Ex. 4 http://example.aspx -> example
	 * Ex. 5 http://example.com/test/index.aspx -> index
	 */
	public static String parseUrl(String url) {
		String res = null;
		
		if(url.contains("?")) {
			res = url.split("\\?")[1].split("&")[0].split("=")[1];
		}
		else if(url.split("http://")[1].contains("/")) {
			String path;
			try {
				path = new URI(url).getPath();
				
				if(path.contains(".")) {
					String[] arr = path.split("\\/");
					res = arr[arr.length - 1];
				}
				else res = path.replaceAll("\\/", "");
				//String lastPartOfUrl = path.substring(path.lastIndexOf('/') + 1);
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		if(res != null && !res.isEmpty()){ 
			if(res.charAt(0) == '/')
				res = res.substring(1);
			return res.replaceAll("/", "_");
		}

		// URL is of type http://example.aspx
		return url.replace("http://", "").replace("https://", "").split("\\.")[0];
	}
	
	public String toString() {
		String res = "";
		
		res += "- name: " + name + "\n";
		res += "    uid: " + name + "_TRANS_" + application + "\n";
		res += "    filtered: true" + "\n";
		res += "    type: " + TYPE + "\n";
		res += "    steps: \n";

		for(Step s : steps)
			res += "    " + s.toString();
	
		return res;
	}
	
}

