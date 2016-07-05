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
	 */
	public static String parseUrl(String url) {

		if(url.contains("?")) {
			return url.split("\\?")[1].split("&")[0].split("=")[1];
		}

		String path;
		try {
			path = new URI(url).getPath();
			String lastPartOfUrl = path.substring(path.lastIndexOf('/') + 1);
			return lastPartOfUrl;
		} catch (URISyntaxException e) {
			e.printStackTrace();

		}
		return url;
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

