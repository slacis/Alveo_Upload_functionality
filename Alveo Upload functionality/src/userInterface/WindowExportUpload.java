package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import upload.UploadConstants;

import javax.swing.SwingConstants;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;



public class WindowExportUpload {

	JFrame frame;
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	private String path = null;
	private String absolupath;
	private String filename;


	/**
	 * Create the application.
	 * @param key 
	 */
	public WindowExportUpload(String path, 
			HashMap<String, String> collectionDetails,
			String key,
			HashMap<String, JSONObject> recItemMetadata, 
			HashMap<String, HashMap<String, JSONObject>> recDocMetadata,
			Boolean newItem,
			Boolean itemMD,
			Boolean collectionMD,
			Boolean generateColMD,
			JSONObject collectionMetadata) {
		
		initialize(path, 
				collectionDetails, 
				key, 
				recItemMetadata, 
				recDocMetadata, 
				newItem, 
				itemMD, 
				collectionMD, 
				generateColMD,
				collectionMetadata
				);
	}

	/**
	 * Initialize the contents of the frame.
	 * @param key 
	 */
	private void initialize(String path, 
			HashMap<String, String> collectionDetails,
			String key,
			HashMap<String, JSONObject> recItemMetadata, 
			HashMap<String, HashMap<String, JSONObject>> recDocMetadata,
			Boolean newItem,
			Boolean itemMD,
			Boolean collectionMD,
			Boolean generateColMD,
			JSONObject collectionMetadata) {
		frame = new JFrame();
		frame.setBounds(100, 100, 400, 400);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		
		// Open Metadata Editor button
		JButton btnExport = new JButton("Export Metadata");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WindowUpdateCollection updateWindow= new WindowUpdateCollection(key);
				updateWindow.frame.setVisible(true);
				}
		});
		btnExport.setBounds(67, 157, 262, 36);
		frame.getContentPane().add(btnExport);
		
		JButton btnMetadataEditor = new JButton("Metadata Editor");
		btnMetadataEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WindowCreateCollection createWindow= new WindowCreateCollection(key);
				createWindow.frame.setVisible(true);
				}
		});
		btnMetadataEditor.setBounds(67, 104, 262, 36);
		frame.getContentPane().add(btnMetadataEditor);
		
		JButton btnUpload = new JButton("Update/Upload");
		btnUpload.setBounds(68, 206, 262, 36);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// create new collection 
				if (generateColMD){
					try {
				int response = createCollection(key, collectionMetadata, collectionDetails);
				JOptionPane.showMessageDialog(null, "Collection created successfuly!", 
						"InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e){
						JOptionPane.showMessageDialog(null, "Problem creating collection"
								, "InfoBox: " + "Error Message" + e.getMessage(), JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		frame.getContentPane().add(btnUpload);

	}
	
	
	// Function to create collection
	public int createCollection(String key, JSONObject collectionMetadata, HashMap<String, String> collectionDetails) throws IOException {
		try {
		JSONObject tmpJSON =  new JSONObject();
		HttpClient httpclient = new HttpClient();
		JSONObject metadatad = new JSONObject();
		metadatad.put("collection_metadata", collectionMetadata);
		String url = UploadConstants.CATALOG_URL.substring(0, UploadConstants.CATALOG_URL.length()-1 )+ 
				"?name=" + collectionDetails.get("collectionName")
				+ "&licence_id=" + collectionDetails.get("license")
				+ "&private=" + collectionDetails.get("private");
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 

		
		    HttpPost request = new HttpPost(url);
		    StringEntity params =new StringEntity(metadatad.toString());
		    request.addHeader("X-API-KEY",key);
		    request.addHeader("Accept","application/json");
		    request.addHeader("Content-Type", "application/json; charset=UTF-8");
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    System.out.println(response);
		    return 1;
		
//		System.out.println(url);
//	    PostMethod filePost = new PostMethod( url);
//	    JSONArray Metadata = new JSONArray();
//	    Metadata.add(collectionMetadata.toString());
//	    System.out.println(collectionMetadata.toString());
//	    Part[] parts = {new StringPart( "{collection_metadata", collectionMetadata.toString()+ "}") };
//	    System.out.println(new StringPart( "collection_metadata", collectionMetadata.toString()));
//	    
//	    filePost.setRequestHeader( "X-API-KEY",key);
//	    filePost.setRequestHeader( "Accept","application/json");
//	    filePost.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
//	    filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );	
//	    
//	 
//	    int response = httpclient.executeMethod( filePost );
//	    System.out.println( "Response : "+response );
//	    System.out.println( filePost.getResponseBodyAsString());
//	    return response;
	    
	}	
		
		catch( HttpException e ){
        // TODO Auto-generated catch block
        e.printStackTrace();
    }catch( IOException e ){
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
		return 0; 
	}
}
