package upload;


import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import net.sf.json.JSONObject;


/** A class to upload files to Alveo system.
 * 
 * 
 * @author Kun He
 *
 */


public class DocUpload {
	
	private static final Logger LOGGER = Logger.getLogger( DocUpload.class.getName() );
	
	public static void docsUpload(String path, String docID, float xmlFileBytes,String key, String collection) throws IOException {
		// TODO Auto-generated method stub
			
		HttpClient httpclient = new HttpClient();
		
		
		File file =  new File(path + File.separator + docID + ".xml");
		LOGGER.info( "FILE:" + file.exists() );
		
		try{
			
		    PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL + collection + "/" + docID);
			    
		    JSONObject jsonParam = new JSONObject();
		    JSONObject context = new JSONObject(); 
	
		    context.put("dcterms", "http://purl.org/dc/terms/");
		    context.put("foaf", "http://xmlns.com/foaf/0.1/");
	
	        jsonParam.put("@context", context.toString());
	        jsonParam.put("@id", docID + ".xml" );
	        jsonParam.put("@type","foaf:Document" );
	        jsonParam.put("dcterms:identifier", docID + ".xml");
	        jsonParam.put("dcterms:title", docID + ".xml#Xml" );
	        jsonParam.put("dcterms:type", "XML");
	        jsonParam.put("dcterms:extent",  xmlFileBytes);
		    
		    Part[] parts = {new FilePart( "file", file ), new StringPart( "metadata",jsonParam.toString()) };
		    
		    filePost.setRequestHeader( "X-API-KEY",key);
		    filePost.setRequestHeader( "Accept","application/json");
		    filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );	
	
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
