package upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.FilenameUtils;

import net.sf.json.JSONObject;

/** A class to create Alveo item via Alveo API
 * and upload files to that item. 
 * This class will upload all files found in subdirectories
 * 
 * @author Kun He
 * 
 *
 */

public class CollectionUploadGeneral {
//	public static void main(String[] args) throws IOException {    
//		String directoryName = "/media/sf_sharedwithvirtual/pythonupload/exampleupload/children";
//		upload(directoryName, "wCTRNdjyxUxzxvnmmp9B", "uploadertest2");
//	}

	private static final Logger LOGGER = Logger.getLogger( CollectionUploadGeneral.class.getName() );
	

	public static int upload (String path, String key,
			String collection, HashMap<String, JSONObject> itemMeta,
			HashMap<String, HashMap<String, JSONObject>> docMeta ) throws IOException{


		String rcode = upload.CheckCollection.check(collection);
		if(Integer.valueOf(rcode) == 200){

			//Get sub-directories
			List<File> subDirs = getDirs(path);
			//create Alveo item for each subdirectory
			for (File subDir: subDirs){
				// Set Item name to name of subDirectory
				int first = 1;
				String docID = subDir.getName();
				//Get all files from sub-directories and upload to item
				List<File> filesList = listFiles(subDir.toString());
				for(File file: filesList){
					String docName = file.getName();
					HttpClient httpclient = new HttpClient();
					//						String docID = file.getName();
					String fileExs = FilenameUtils.getExtension(file.getAbsolutePath());


					float txtFileBytes = 0;
					float xmlFileBytes = 0;
					float jsonFileBytes = 0;

					//					LOGGER.info( "FILE:" + (txtFile.exists() && xmlFile.exists()));

					// upload plain text file
					if(file.exists()){

						txtFileBytes = file.length();

					}else{
						System.out.println("txtFile does not exists!");
					}

					// upload remaining documents
					if(first == 0){
						xmlFileBytes = file.length();
						System.out.println("xmlFileBytes : " + xmlFileBytes);
						upload.DocUploadGeneral.docsUpload(file.getAbsolutePath(), docName, docID, xmlFileBytes, fileExs, key, collection);

					}else{



						try{
							PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL +collection );

							
//							Part[] parts = {new FilePart( "file", file ), 
//									new StringPart( "items",MetadataGeneral.createMetadata(docID,docName,
//											txtFileBytes, fileExs, itemMeta.get(docID+"_doc"), 
//											itemMeta.get(docID+"_item"))
//											.toString()) };
							Part[] parts = {
									new StringPart( "items",MetadataGeneral.createMetadata(docID,docName,
											txtFileBytes, fileExs, itemMeta.get(docID+"_doc"), 
											itemMeta.get(docID+"_item"))
											.toString()) };

							filePost.setRequestHeader( "X-API-KEY",key);
							filePost.setRequestHeader( "Accept","application/json");
							filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );

							int response = httpclient.executeMethod( filePost );
							System.out.println( "Response : "+response );
							System.out.println( filePost.getResponseBodyAsString());
							first = 0;
						}
						catch( HttpException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch( IOException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			//					// upload annotations file
			//					if(jsonFile.exists()){
			//						jsonFileBytes = jsonFile.length();
			//						System.out.println("jsonFileBytes : " + jsonFileBytes);
			//						upload.AnnotationUpload.annUpload(path, docID, key, collection);
			//						
			//					}else{
			//							 System.out.println("xmlFile does not exists!");
			//					}
			//					


			//				    
			//				}




		}else{
			JOptionPane.showMessageDialog(null, "Invalid Alveo Collection Name", "InfoBox: " + "Error Message", JOptionPane.INFORMATION_MESSAGE);
			return 0;
		}

		return 1;

	}
	// Get Directories
	public static List<File> getDirs(String dirName) {
		// Make file from directory name
		File directory = new File(dirName);
		List<File> filesList = new ArrayList<File>();
		// Find all files/directories within selected directory
		File[] fList = directory.listFiles();
		filesList.addAll(Arrays.asList(fList));
		for (File file : fList) {
			if (file.isFile()) {
				filesList.remove(file);
				//Recursively find files inside of directory
			}
		}
		//System.out.println(fList);
		return filesList;
	} 

	// Get directories and sub-directories
	public static List<File> listFiles(String dirName) {
		// Make file from directory name
		File directory = new File(dirName);
		List<File> filesList = new ArrayList<File>();
		// Find all files/directories within selected directory
		File[] fList = directory.listFiles();
		filesList.addAll(Arrays.asList(fList));
		for (File file : fList) {
			if (file.isFile()) {
				System.out.println(file.getAbsolutePath());
				//Recursively find files inside of directory
			} else if (file.isDirectory()) {
				//Remove directory name itself from final list
				filesList.remove(file);
				filesList.addAll(listFiles(file.getAbsolutePath()));
			}
		}
		//System.out.println(fList);
		return filesList;
	} 


}
