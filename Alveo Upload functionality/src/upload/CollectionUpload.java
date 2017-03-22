package upload;

import java.io.File;
import java.io.IOException;
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

/** A class to create Alveo item via Alveo API
 * and upload files to that item
 * 
 * @author Kun He
 *
 */

public class CollectionUpload {
	
	private static final Logger LOGGER = Logger.getLogger( CollectionUpload.class.getName() );
	
	public static int upload (String path, List<String> docIDs, String key, String collection, String metadata) throws IOException{
			
		
		String rcode = upload.CheckCollection.check(collection);
		if(Integer.valueOf(rcode) == 200){
			
			if (upload.UpdateMetadata.uploadMetadata(metadata, collection)==3){
				return 2;
			}else{
				
				//create Alveo item for each BioC document
				for(int i=0;i<docIDs.size();i++){
					
					String docID = docIDs.get(i);				    
				    
				    HttpClient httpclient = new HttpClient();
					File txtFile =  new File(path + File.separator + docID + ".txt");			
					File xmlFile =  new File(path + File.separator + docID + ".xml");
					File jsonFile =  new File(path + File.separator + docID + ".json");
					float txtFileBytes = 0;
					float xmlFileBytes = 0;
					float jsonFileBytes = 0;
					
					LOGGER.info( "FILE:" + (txtFile.exists() && xmlFile.exists()));
					
					// upload plain text file
					if(txtFile.exists()){

						txtFileBytes = txtFile.length();
						
					}else{
						System.out.println("txtFile does not exists!");
					}
										
					try{
					    PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL +collection );
				    
					    Part[] parts = {new FilePart( "file", txtFile ), 
					    		new StringPart( "items",Metadata.createMetadata(docID,txtFileBytes).toString()) };
					   
					    filePost.setRequestHeader( "X-API-KEY",key);
					    filePost.setRequestHeader( "Accept","application/json");
					    filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );
	
					    int response = httpclient.executeMethod( filePost );
					    System.out.println( "Response : "+response );
					    System.out.println( filePost.getResponseBodyAsString());
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
					
					
					// upload annotations file
					if(jsonFile.exists()){
						jsonFileBytes = jsonFile.length();
						System.out.println("jsonFileBytes : " + jsonFileBytes);
						upload.AnnotationUpload.annUpload(path, docID, key, collection);
						
					}else{
							 System.out.println("xmlFile does not exists!");
					}
					
					
					// upload the original BioC XML format file segment 
					if(xmlFile.exists()){
						xmlFileBytes = xmlFile.length();
						System.out.println("xmlFileBytes : " + xmlFileBytes);
						upload.DocUpload.docsUpload(path, docID, xmlFileBytes, key, collection);
						
					}else{
							 System.out.println("xmlFile does not exists!");
					}
				    
				}
				
			}
			
			 
		}else{
			JOptionPane.showMessageDialog(null, "Invalid Alveo Collection Name", "InfoBox: " + "Error Message", JOptionPane.INFORMATION_MESSAGE);
			return 0;
		}
		
		return 1;
		
	}
	

}
