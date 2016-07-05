package BU.transactions;

public class Application {

	private String name;
	private String softwareService;
	private Transaction[] transactions;
	private final String TYPE = "APPLICATION";

	public Application(String name, String softwareService, Transaction[] transactions) {
		this.name = name;
		this.softwareService = softwareService;
		this.transactions = transactions;
	}
	
	public String toString() {
		String res = "";
		res += "- name: " + name + "\n";
		res += "  uid: " + name + "_APPL" + "\n";
		res += "  type: " + TYPE + "\n";
		res += "  primarySource: probe" + "\n";
		res += "  filtered: true" + "\n";
		res += "  transactions: " + "\n";

		for(Transaction t : transactions)
			res += "  " + t.toString();

		res += "  rules: " + "\n";
		res += "  - ruletype: software-service" + "\n";
		res += "    softwareService: " + softwareService + "\n";
		res += "    type: operation" + "\n";
		
		
		return res;
	}
}
