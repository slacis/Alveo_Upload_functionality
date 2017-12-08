Alveo Uploader:
This is an extension to the Alveo Uploader application. It aims to extend the uploader to cater to different data types (currently, 
only BioC is supported). 

Build instructions:

NOTES: Along with the source code a built jar file has been included in the repository.
This is a Java project, and can be built at the discretion of the user.
There are no special instructions required for building this software. Simply adjust your build path to ensure the libraries are included and then build as normal for a java project. Build has been tested in Eclipse IDE.

Using the software:
Along with the source code a built jar file has been included in the repository, and SHOULD run on any Windows or Linux build that supports JRE 1.8.(Tested on Windows 10 and Ubuntu 16.04). Please download the “data” folder along with it, as it contains a csv necessary to run the program
Before trying to run this software, please ensure that you:
Include the config file containing your API key inside the “data” same directory as the built jar file before running. Include your metadata.xlsx file inside of your collection and have the metadata in a page called ‘Recordings’
Have data owner permissions in your Alveo account.

Please prefer to thesis for detailed images describing usage
Example: Creating an Alveo collection along with items and documents:


-Open the software
-Click on ‘Create New Collection’
-Enter your collection name
-Choose your desired License
-Choose a metadata prefix (Automatically added to metadata tags when they are not defined in the Alveo system)
-Choose the parent directory of your collection
-Click on the Filename Metadata button and choose settings accordingly (refer to section 5.2.2. for further information) and then press return
NOTE: If you don’t have at least one file extension in the Item contained in “Read From”, the item metadata won’t be created and will not be uploaded.

-Choose your desired collection type (private or public) 
-Click on Create New Collection

-You will be brought to the metadata editor. Adjust your Collection/Item/Document metadata as desired. Item can be changed with the Alveo Item combobox. Document can be changed with the Alveo Document combobox. Choose what type of metatada you’d like to edit with the radio button. After you choose, please open the combobox and click on the item/document in order to show the metadata in the table.
-You can type in what type of metadata you are searching for in the “Metadata Type” box and click search if you want suggestions. (e.g. a query of “code” will show you predefined metadata types that contain the word “code”). 
-Type in the metadata itself into the the “Metadata” box (e.g. “C. Watson and S. Cassidy”)

-By choosing from either ‘Individual’ or ‘All’ in the bottom left radio button box, you can choose whether you’d like to add the metadata to an individual item or document, or to ALL items or ALL documents.
-Press ‘Add’ to add the metadata. If ‘Individual’ is selected, it will be added directly to the item/document in question. If  ‘All’ is selected, the metadata will be added to the lower table, where you can continue building a set of metadata that can be added to all items/documents upon pressing “Update”
-Press ‘Update’ to save the metadata additions.
-Press continue when finished editing metadata

-Press Update/Upload to create your collection
-When the creation is finished, the dialog box seen in the right picture will be displayed
Congratulations, you’ve created an Alveo collection!
