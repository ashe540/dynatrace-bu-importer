package BU.transactions;

public class Rule {
	final String TYPE = "operation";

	String softwareService;
	String url;
	
	public Rule(String softwareService, String url) {
		this.softwareService = softwareService;
		this.url = url;
	}

	public String toString() {
		String res = "";
		res += "  - ruletype: software-service" + "\n";
		res += "        softwareService: " + softwareService + "\n";
		res += "        type: " + TYPE + "\n";
		res += "        label: " + url + "\n";
		res += "      filtered: true" + "\n";
		
		return res;
	}
}
