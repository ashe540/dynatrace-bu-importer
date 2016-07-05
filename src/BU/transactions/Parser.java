package BU.transactions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


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
  
public static void main(String[] args) {

	Parser obj = new Parser();
  	obj.run();
  }

/**
 * Parser main method
 */
public void run() {
	  
//	**********************************
// 	* SCRIPT CONFIGURATION VARIABLES *
//	**********************************
	String dataCsvFile = "iob.csv";
  	String resultFile = "result.yaml";	// If SAVETOFILE flag is set to true, it will save the output to this file
  	BufferedReader br = null;
  	String cvsSplitBy = ";";			// Separator for elements within the same line of the CSV
  	String application = "IOB";			// Application name
  	
  	
  	try {
  		
  		br = new BufferedReader(new InputStreamReader(
  			    new FileInputStream(dataCsvFile), "UTF-8"));
  		
  		skipFirstLines(br, 6); //Skip the first 6 lines (header)

  		//Extract software service from Filter line (make sure that the report you're exporting is filtered on Software Service!
  		String softwareService = br.readLine().trim().replace("# Filters: Software service=", "").split(",")[0];
  		
  		skipFirstLines(br, 5); //Skip the rest of header lines
  		
  		String line = "";
  		int counter = 0;
  		
  		Transaction[] transactions = new Transaction[MAX_URLS];
  		
  		// Loop through each line until reaching MAX_URLS or until reaching end of file
  		while ( counter < MAX_URLS && (line = br.readLine()) != null && !hasEmptyData(line)) {
  			
  			// If "All Other Operations" is in the list, do not add it as a Transaction
  			if(!operationIsAllOther(line)) {
  				
	  			// Use semicolon as separator defined above in script config variables
	  			String[] lineArr = line.split(cvsSplitBy);
	  			
	  			String url = lineArr[0];
	  			String transactionName = Transaction.parseUrl(url);
	  			
	  			Rule r = new Rule(softwareService, url);
	  			Rule[] rules = new Rule[1];
	  			rules[0] = r;
	  			
	  			Step step = new Step(application, transactionName, rules);
	  			Step[] steps = new Step[1];
	  			steps[0] = step;
	  			
	  			Transaction t = new Transaction(transactionName, application, steps);
	  			transactions[counter] = t;
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

