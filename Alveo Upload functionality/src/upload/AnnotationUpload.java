package upload;

import java.io.File;
import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

/** A class to upload annotation files to Alveo System
 * 
 * @author Kun He
 *
 */ 

public class AnnotationUpload {
	
	public static void annUpload(String path, String docID, String key, String collection) throws IOException {
		// TODO Auto-generated method stub
	
		
		HttpClient httpclient = new HttpClient();
		
		String url =  UploadConstants.CATALOG_URL + collection +"/" + docID + "/annotations";
		System.out.println(url);
		File file =  new File(path);

		
		try{
			
		    PostMethod filePost = new PostMethod( url);		    
		    Part[] parts = {new FilePart( "file", file )};
		    
		    filePost.setRequestHeader( "X-API-KEY", key);
		    filePost.setRequestHeader( "Accept","application/json");
		    filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );
	
		    // DEBUG	
		    int response = httpclient.executeMethod( filePost );
		    System.out.println( "Response : "+response );
		    System.out.println( filePost.getResponseBodyAsString());
		    
		}catch( HttpException e ){
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }catch( IOException e ){
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }	
		
		
	}


}
