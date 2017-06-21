# Dynatrace Business Units Configuration Importer
Automatic importer of Business Units (YAML)


## Set up

The BU Importer can be imported into *Eclipse* by simply selecting `File -> Import`, and then selecting `General -> Existing Projects
into Workspace` after unzipping the compressed archive.

Select the folder where the root directory is for the project.

After it has finished importing, generate the CSV to parse, and open the Parser.java file to set up the configuration for the project.

## Step 1 - Generate CSV of Critical Operations

The first step is to generate a DMI report that shows the most critical operations for your client’s application. To do this
simply go to `Tools -> DMI -> New Report` (for v12.4) or `Reports -> Define Simple Report`, and create a Table with a single dimension
for _Operations_ and the metrics that are the most relevant for your use case.
In this case we sorted on the _Operations_ metric to show the operations that were being called the most, hence the most critical.
If you have a specific use case, feel free to sort on the metric you deem relevant. Also remember to use the _Search_ input box to
filter the operations in order to exclude CSS stylesheets or other static resources you might not want to include as a transaction. For the script
to work correctly you *MUST* filter the report by the Software Service that reports the URLs you want to use as transactions. Filtering by application is also recommended, although the application name can be manually specified in the script configuration. After setting up the filters, click on the cogwheel for the section, and choose the `Export data` option, and save the it to a CSV file.

![](http://i.imgur.com/YVMdBo1.gif)

## Step 2 - Configure the BU Importer

The program comes with a series of configurable parameters that can be configured to a certain degrees.

#### Global configuration

| Global parameters | Type | Default | Description |
| :-------------|:-------------:| :-----:|:-----|
| DEBUG      | Boolean | False | Controls logging to the standard output (console). |
| MAX_URLS   | Integer | 50 | Maximum number of URLs to create transactions.
| SAVETOFILE | Boolean |  true |  Whether the program should save the results to a file. |


#### Script configuration

| Script parameters | Type | Description |
| :-------------|:-------------:|:-----|
| dataCsvFile      | String | This will be the file that will be parsed. |
| resultFile   | String | This is the file where the results will be saved in YAML format if the SAVETOFILE flag is set to true. |
| csvSplitBy | String |  Default: semicolon. This will used to separate the entries in each line from the CSV. |
| application | String | This is the name of the application that you wish to create or add transactions to. |

####IMPORTANT NOTES:
1. The dataCsvFile must point to the location of the file. It's recommended to save it on the root folder of the project (where the src directory is) to avoid issues with relative paths. Then resultFile will also be saved to this same directory after execution.
2. If the application doesn't exist in the Business Units, it will be created. If it _does_ exist, its transactions will be replaced. Make sure you verify that your transactions have all the relevant software services associated with it.
3. If no application name is specified, the importer will attempt to parse it from the generated CSV. This will work if the report is filtered by application.
4. The Software Service name filtered in the report will be used to define the transaction. There is only support for adding 1 Software Service at a time, therefore the operations that we wish to import as transactions must all come from the same Software Service.

## Step 3 - Importing the generated YAML file

To import the file generated, first look for it in the root directory of the Eclipse Project. It should have the name that you
indicated in the resultFile parameter. Simply copy its contents to your clipboard. Then log in to the CAS. Go to the Business Units
page (in v12.4: `Settings -> APM Model -> Business Units`), and click on the _Import_ button on the bottom of the left column. Then
paste the contents to the textarea on the page, verify it detected the content as "YAML" and then click the _Import_ button.
If everything went as expected it should show a message indicating that the import was carried out successfully.
