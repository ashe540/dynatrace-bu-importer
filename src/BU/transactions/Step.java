package BU.transactions;

public class Step {
	String name;
	final String TYPE = "BSTEP";
	Rule[] rules;
	String application;
	String transactionName;
	
	public Step(String application, String transactionName, Rule[] rules) {
		this.application = application;
		this.transactionName = transactionName;
		this.rules = rules;
	}
	
	public String toString() {
		String res = "";
		
		res += "- name: Step 1" + "\n";
		res += "      type: " + TYPE + "\n";
		res += "      seqNo: 1" + "\n";
		res += "      uid: Step1_STEP_" + transactionName + "_TRANS_" + application + "\n";
		res += "      rules: \n";

		for(Rule r : rules)
			res += "    " + r.toString();
		
		return res;
	}

}