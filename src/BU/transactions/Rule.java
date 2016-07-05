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
		String[] ssArray = this.softwareService.replaceAll(" ", "").split("\\|");

		for(int i=0; i < ssArray.length; i++)
		{
			String ss = ssArray[i];

			res += "  - ruletype: software-service" + "\n";
			res += "        softwareService: \"" + ss + "\"\n";
			res += "        type: " + TYPE + "\n";
			res += "        label: " + url + "\n";

			if(i < ssArray.length - 1)
				res += "    ";
		}

		res += "      filtered: true" + "\n";

		return res;
	}
}
