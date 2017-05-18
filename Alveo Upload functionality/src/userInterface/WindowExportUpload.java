package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import upload.InitializeMetadata;
import upload.MetadataGeneral;
import upload.UploadConstants;

import javax.swing.SwingConstants;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
			HashMap<String, Integer> recItemStatus,
			HashMap<String, List<File>> itemFileList,
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
				recItemStatus,
				itemFileList,
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
			HashMap<String, Integer> recItemStatus,
			HashMap<String, List<File>> itemFileList,
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
				int response;
				// create new collection 
				if (generateColMD){
					try {
						response = createCollection(key, collectionMetadata, collectionDetails);
						if (response == 200) {
							JOptionPane.showMessageDialog(null, "Collection created successfuly!", 
									"InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
						}

					} catch (IOException e){
						JOptionPane.showMessageDialog(null, "Problem creating collection"
								, "InfoBox: " + "Error Message" + e.getMessage(), JOptionPane.INFORMATION_MESSAGE);
					}
				}
				if (itemMD) {
					try {
						updateItems(key, 
								collectionDetails,
								recItemMetadata,
								recItemStatus);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (collectionMD){
					try {
						updateCollection(key, collectionMetadata, collectionDetails);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (newItem){
					for (String itemKey : recItemMetadata.keySet()) {
						if (recItemStatus.get(itemKey) == 2){
							//Create item
							response = createItem(key, collectionDetails.get("collectionName"), 
									recItemMetadata.get(itemKey));
							System.out.println(response);
							//Upload documents
							for (File fileToUpload: itemFileList.get(itemKey)){
								response = createDocument(key, 
										collectionDetails.get("collectionName"), 
										itemKey,
										fileToUpload,
										recDocMetadata.get(itemKey).get(fileToUpload.getName()));
								System.out.println(response);

							}
						}
					}
				}
			}
		});
		frame.getContentPane().add(btnUpload);

	}


	// Function to create collection
	public int createCollection(String key, JSONObject collectionMetadata, HashMap<String, String> collectionDetails) throws IOException {
		try {
			JSONObject metadatad = new JSONObject();
			metadatad.put("collection_metadata", collectionMetadata);
			String url = UploadConstants.CATALOG_URL.substring(0, UploadConstants.CATALOG_URL.length()-1 )+ 
					"?name=" + collectionDetails.get("collectionName")
					+ "&licence_id=" + collectionDetails.get("license")
					+ "&private=" + collectionDetails.get("private");

			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 


			HttpPost request = new HttpPost(url);
			StringEntity params =new StringEntity(metadatad.toString());
			request.addHeader("X-API-KEY",key);
			request.addHeader("Accept","application/json");
			request.addHeader("Content-Type", "application/json; charset=UTF-8");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			System.out.println(response);
			return responseCode;


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

	// Function to update items
	public int updateItems(String key, 
			HashMap<String, String> collectionDetails,
			HashMap<String, JSONObject> recItemMetadata,
			HashMap<String, Integer> recItemStatus) throws IOException {
		try {

			for (String item : recItemMetadata.keySet()) {
				try {
					if (recItemStatus.get(item) == 1) {
						JSONObject metadatad = new JSONObject();
						JSONObject tempItem = recItemMetadata.get(item);
						tempItem.put("@context", InitializeMetadata.initContext());
						metadatad.put("metadata", tempItem);
						System.out.println(metadatad.toString());
						String url = UploadConstants.CATALOG_URL 
								+ collectionDetails.get("collectionName") + "/"
								+ item;
						System.out.println(url);
						CloseableHttpClient httpClient = HttpClientBuilder.create().build();

						HttpPut request = new HttpPut(url);
						StringEntity params =new StringEntity(metadatad.toString());
						request.addHeader("X-API-KEY",key);
						request.addHeader("Accept","application/json");
						request.addHeader("Content-Type", "application/json; charset=UTF-8");
						request.setEntity(params);
						HttpResponse response = httpClient.execute(request);
						int responseCode = response.getStatusLine().getStatusCode();
						System.out.println(response);
						System.out.println(responseCode);
					}
				} catch (java.lang.NullPointerException e){
					continue;
				}
			}

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
	// Function to update collection
	public int updateCollection(String key, 
			JSONObject collectionMetadata, 
			HashMap<String, String> collectionDetails) throws IOException {
		try {
			JSONObject metadatad = new JSONObject();
			metadatad.put("collection_metadata", collectionMetadata);
			metadatad.put("replace", false);
			String url = UploadConstants.CATALOG_URL 
					+ collectionDetails.get("collectionName");
			System.out.println(url);


			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 


			HttpPut request = new HttpPut(url);
			StringEntity params =new StringEntity(metadatad.toString());
			request.addHeader("X-API-KEY",key);
			request.addHeader("Accept","application/json");
			request.addHeader("Content-Type", "application/json; charset=UTF-8");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			System.out.println(response);
			return responseCode;


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

	public int createItem(String key, String collectionName, JSONObject itemMetadata){
		//Create item
		try{
			HttpClient httpclient = new HttpClient();
			PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL +collectionName );
			Part[] parts = {
					new StringPart( "items",MetadataGeneral.createMetadata( 
							itemMetadata)
							.toString()) };

			filePost.setRequestHeader( "X-API-KEY",key);
			filePost.setRequestHeader( "Accept","application/json");
			filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );

			int response = httpclient.executeMethod( filePost );
			System.out.println( "Response : "+response );
			System.out.println( filePost.getResponseBodyAsString());
			return response;
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
		return 0;
	}
	//Upload document to item
	public int createDocument(String key, 
			String collectionName, 
			String itemName,
			File fileUpload,
			JSONObject docMetadata
			){
	

		HttpClient httpclient = new HttpClient();
		if (fileUpload.exists()){
			try{
				String url = UploadConstants.CATALOG_URL + collectionName + "/" + itemName;
				PostMethod filePost = new PostMethod(url);
				System.out.println(docMetadata);
				System.out.println(itemName);
				docMetadata.put("@context", InitializeMetadata.initContext());


				Part[] parts = {new FilePart( "file", fileUpload ), new StringPart( "metadata",docMetadata.toString()) };

				filePost.setRequestHeader( "X-API-KEY",key);
				filePost.setRequestHeader( "Accept","application/json");
				filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );	

				int response = httpclient.executeMethod( filePost );
				System.out.println( "Response : "+response );
				System.out.println( filePost.getResponseBodyAsString());
				return response;

			}catch( HttpException e ){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch( IOException e ){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




		}
		return 0;

	}
	
}
