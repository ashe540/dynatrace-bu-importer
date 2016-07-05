package BU.transactions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Script that parses URLs and generates YAML file to be imported into Business Units
 * Sends output either to file or to STDOUT
 *
 * To generate the necessary CSV input file, create a table section in a DC RUM report with the operation as
 * the only dimension. This is so that the operation's URL is the first column entry.
 *
 * @author Miguel Martínez (Dynatrace)
 *
 */

public class Parser {

//	**********************************
// 	* GLOBAL CONFIGURATION VARIABLES *
//	**********************************
  private static boolean DEBUG = false;				// Debug flag for logging
  public static int MAX_URLS = 50;					// Max number of URLs to process
  public static boolean SAVETOFILE = true;			// Control whether to save output to file or to standard output

public static void main(String[] args) throws Exception {

	Parser obj = new Parser();
  	obj.run();
  }

/**
 * Parser main method
 * @throws Exception
 */
public void run() throws Exception {

//	**********************************
// 	* SCRIPT CONFIGURATION VARIABLES *
//	**********************************
	  String dataCsvFile = "iob.csv";
  	String resultFile = "result.yaml";				// If SAVETOFILE flag is set to true, it will save the output to this file
  	String cvsSplitBy = ",";						// Separator for elements within the same line of the CSV
  	String application = "";						// Application name, not necessary if report is filtered by application
  	int numLinesBeforeToSkip = 6;					// Default number of header lines before Filters line
  	int numLinesAfterToSkip = 5;					// Default number of header lines after Filters line & before operations

  	BufferedReader br = null;
  	String softwareService = null;

  	try {

  		//Simplify application name by replacing spaces with underscores
  		String app = application.replaceAll(" ", "_");

  		br = new BufferedReader(new InputStreamReader(
  			    new FileInputStream(dataCsvFile), "UTF-8"));

  		skipFirstLines(br, numLinesBeforeToSkip); //Skip the first 6 lines (header)


  		//String softwareService = br.readLine().trim().replace("# Filters: Software service=", "").split(",")[0];
  		String ssLine = br.readLine().trim();

  		if(!ssLine.contains("Filters:")){
  			if(DEBUG) System.out.println("Verify that you're skipping the correct number of lines for the header.");
  			throw new Exception("Incorrect Software Service line");
  		}

  		if(!ssLine.contains("Software service")){
  			if(DEBUG) System.out.println("The report is not filtered by the application's associated Software Service! Please fix this before continuing.");
  			throw new Exception("Report not filtered by software services");
  		}

  		//Extract software service only if report is filtered by one or more Software Services! This can be list separated by the boolean OR sign (|)
  		// E.g. Web1_App | Web2App | Web3App
  		softwareService = ssLine.split("# Filters:")[1].trim().split("Software service=")[1].split(",")[0];

  		if(application.isEmpty() && ssLine.contains("Application")) {
  			application = ssLine.split("# Filters:")[1].trim().split("Application=")[1].split(",")[0];
  		}
  		else {
  			if(DEBUG) System.out.println("Application name in exported CSV was ignored since one was provided in the script configuration");
  		}

  		if(application.isEmpty()){
  			if(DEBUG) System.out.println("The report is not filtered by application and one was not provided in the script configuration.");
  			throw new Exception("Report not filtered by application and none provided");
  		}

  		skipFirstLines(br, numLinesAfterToSkip); //Skip the rest of header lines

  		String line = "";
  		int counter = 0;

  		ArrayList<Transaction> transactions = new ArrayList<Transaction>();

  		// Loop through each line until reaching MAX_URLS or until reaching end of file
  		while ( counter < MAX_URLS && (line = br.readLine()) != null && !hasEmptyData(line)) {

  			// If "All Other Operations" is in the list, do not add it as a Transaction
  			if(!operationIsAllOther(line)) {

	  			// Use the delimiter defined above in script config variables as separator
	  			String[] lineArr = line.split(cvsSplitBy);

	  			String url = lineArr[0];
	  			String transactionName = Transaction.parseUrl(url);

	  			Rule r = new Rule(softwareService, url);
	  			Rule[] rules = new Rule[1];
	  			rules[0] = r;

	  			Step step = new Step(app, transactionName, rules);
	  			Step[] steps = new Step[1];
	  			steps[0] = step;

	  			Transaction t = new Transaction(transactionName, app, steps);
	  			transactions.add(t);
	  			counter++;
  			}
  		}

		Application a = new Application(application, softwareService, transactions);

		if(SAVETOFILE){
			PrintWriter pw = new PrintWriter(new File(resultFile));
			pw.write(a.toString());
			pw.close();
		}
		else System.out.println(a.toString());



  	} catch (FileNotFoundException e) {
  		e.printStackTrace();
  	} catch (IOException e) {
  		e.printStackTrace();
  	} finally {
  		if (br != null) {
  			try {
  				br.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  			}
  		}
  	}

  }


/**
 * Returns if the line is empty to signal the end of file
 * @param line
 * @return true/false
 */
private boolean hasEmptyData(String line) {
	return line.replaceAll(";", "").replaceAll("-", "").isEmpty();
}

/**
 * Used for detecting if the operation is the infamous "All other Operations"
 * @param line
 * @return true/false
 */
private boolean operationIsAllOther(String line) {
	return line.contains("All other");
}

/**
 * Helper method to skip a certain number of lines. Used to skip header lines
 * @param br
 * @param numLinesToSkip
 */
public static void skipFirstLines(BufferedReader br, int numLinesToSkip) {
	  if(DEBUG) System.out.println("Skipping " + numLinesToSkip);
	  for(int i=0; i< numLinesToSkip; i++) {
		  try {
			br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
  }

}
