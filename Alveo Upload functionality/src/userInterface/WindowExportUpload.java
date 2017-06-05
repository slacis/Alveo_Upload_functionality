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

/** Window for uploading collection
 * 
 *
 * @author Simon Lacis
 *
 */


public class WindowExportUpload {
	HashMap<String, Integer> uploadResult = new HashMap<String,Integer>();
	JFrame frame;

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
			JSONObject collectionMetadata,
			HashMap<String, File> annotationFileList,
			JSONObject contextMetadata) {

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
				collectionMetadata,
				annotationFileList,
				contextMetadata);
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
			JSONObject collectionMetadata,
			HashMap<String, File> annotationFileList,
			JSONObject contextMetadata) {
		frame = new JFrame();
		frame.setBounds(100, 100, 400, 400);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		JButton btnMetadataEditor = new JButton("Metadata Editor");
		btnMetadataEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
			}
		});
		btnMetadataEditor.setBounds(67, 104, 262, 36);
		frame.getContentPane().add(btnMetadataEditor);

		JButton btnUpload = new JButton("Update/Upload");
		btnUpload.setBounds(67, 159, 262, 36);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int response;
				// create new collection 
				if (generateColMD){
					try {
						response = createCollection(key, collectionMetadata, collectionDetails,  contextMetadata);
						uploadResult.put("Create collection", response);
//						if (response == 200) {
//							JOptionPane.showMessageDialog(null, "Collection created successfuly!", 
//									"InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
//						}

					} catch (IOException e){
						JOptionPane.showMessageDialog(null, "Problem creating collection"
								, "InfoBox: " + "Error Message" + e.getMessage(), JOptionPane.INFORMATION_MESSAGE);
					}
				}
				// If we will delete all items in collection
				String deleteItems = collectionDetails.get("itemDelete");
				if (deleteItems != null && deleteItems.equals("true")){
					response = deleteItems(key, collectionDetails.get("collectionName") );
							uploadResult.put("Delete existing items", response);;
				}
				// If there is item metadata to update
				if (itemMD) {
					try {
						response = updateItems(key, 
								collectionDetails,
								recItemMetadata,
								recItemStatus,
								contextMetadata);
						uploadResult.put("Items updated", response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// If there is collection metadata to update
				if (collectionMD){
					try {
						response = updateCollection(key, collectionMetadata, collectionDetails);
						uploadResult.put("Collection Updated", response);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// If we are creating new items/adding new documents
				if (newItem){
					String collectionName = collectionDetails.get("collectionName");
					for (String itemKey : recItemMetadata.keySet()) {
						try {
						if (recItemStatus.get(itemKey) == 2){
							//Create item
							File firstFile = itemFileList.get(itemKey).get(0);
							JSONObject ausnc_doc_v = 
									recDocMetadata.get(itemKey).get(firstFile.getName());
							response = createItem(key, collectionName, 
									recItemMetadata.get(itemKey), collectionDetails.get("metadataField"), contextMetadata);
							uploadResult.put("Item name: " + itemKey, response);
							System.out.println(response);
							//Upload documents
							for (File fileToUpload: itemFileList.get(itemKey)){
								response = createDocument(key, 
										collectionName, 
										itemKey,
										fileToUpload,
										recDocMetadata.get(itemKey).get(fileToUpload.getName()),
										collectionDetails.get("metadataField"));
								System.out.println(response);

							}
						}
						} catch (java.lang.NullPointerException e) {
							continue;
						}
						// Check if annotations exist and upload them
						if (annotationFileList.containsKey(itemKey)){
							try {
								upload.AnnotationUpload.annUpload(annotationFileList.get(itemKey).getAbsolutePath(), itemKey, key, collectionName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						
					}

				}
				String resultString = "";
				for (String name: uploadResult.keySet()){
					resultString = resultString + name + " - " + "Status Code: "+ uploadResult.get(name) + "\n";
				}
				JOptionPane.showMessageDialog(null, "Result: \n" + resultString, 
						"InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		frame.getContentPane().add(btnUpload);

	}


	// Function to create collection
	public int createCollection(String key, JSONObject collectionMetadata, HashMap<String, String> collectionDetails, JSONObject contextMetadata) throws IOException {
		try {
			JSONObject metadatad = new JSONObject();
			collectionMetadata.put("@context",  contextMetadata.toString());
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
			HashMap<String, Integer> recItemStatus,
			JSONObject contextMetadata) throws IOException {
		try {

			for (String item : recItemMetadata.keySet()) {
				try {
					if (recItemStatus.get(item) == 1) {
						JSONObject metadatad = new JSONObject();
						JSONObject tempItem = recItemMetadata.get(item);
//						tempItem.put("@context", InitializeMetadata.initContext(collectionDetails.get("metadataField")));
						tempItem.put("@context", contextMetadata.toString());
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
	
	//Finalize item metadata
	
	public static JSONArray createMetadata(JSONObject graph_v, JSONObject context, String prefix){

		
		JSONArray docsMetadata = new JSONArray();			
		JSONObject docMetadata = new JSONObject();
		JSONObject docMetadata_v = new JSONObject();		
		JSONArray graph = new JSONArray();
		JSONArray ausnc_doc = new JSONArray();
		docMetadata_v.element("@context", context.toString());			
		graph.add(graph_v.toString());
		docMetadata_v.element("@graph", graph.toString());
		docMetadata.element("metadata", docMetadata_v.toString());
		docsMetadata.add(docMetadata.toString());
			
		return docsMetadata;	
	}

	public int createItem(String key, String collectionName, JSONObject itemMetadata, String metadataField, JSONObject contextMetadata){
		//Create item
		try{
			HttpClient httpclient = new HttpClient();
			PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL +collectionName );
			
//			System.out.println("context");
//			JSONArray test = MetadataGeneral.createMetadata( 
//					itemMetadata, metadataField);
//			System.out.println("item meta: " + test.toString());
//			JSONArray test2 = createMetadata( 
//					itemMetadata, contextMetadata, metadataField);
//			System.out.println("item meta local: " + test2.toString());
			Part[] parts = {
					new StringPart( "items", createMetadata( 
							itemMetadata, contextMetadata, metadataField)
							.toString()) };

			filePost.setRequestHeader( "X-API-KEY",key);
			filePost.setRequestHeader( "Accept","application/json");
			filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );

			int response = httpclient.executeMethod( filePost );
			
			System.out.println( "Response : "+response );
			System.out.println( "item resp: " + filePost.getResponseBodyAsString());
			return response;
			
			
//			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
//			JSONObject items = new JSONObject();
//			items.put("items", MetadataGeneral.createMetadata(itemMetadata));
//
//
//			HttpPost request = new HttpPost(UploadConstants.CATALOG_URL +collectionName);
//			StringEntity params =new StringEntity(items.toString());
//			request.addHeader("X-API-KEY",key);
//			request.addHeader("Accept","application/json");
//			request.addHeader("Content-Type", "application/json; charset=UTF-8");
//			request.setEntity(params);
//			HttpResponse response = httpClient.execute(request);
//			int responseCode = response.getStatusLine().getStatusCode();
//			System.out.println(response);
//			return responseCode;
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
	
	public int createItemFile(String key, String collectionName, JSONObject itemMetadata, JSONObject ausnc_doc_v, File firstFile, String metadataField, JSONObject contextMetadata){
		//Create item
		try{
			HttpClient httpclient = new HttpClient();
			PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL +collectionName );
			JSONArray ausnc_doc = new JSONArray();
			ausnc_doc.add(ausnc_doc_v.toString());
			itemMetadata.element("ausnc:document", ausnc_doc.toString());			
			JSONArray itemMeta = createMetadata(itemMetadata, contextMetadata, metadataField);
			Part[] parts = {new FilePart("file", firstFile),
					new StringPart( "items",itemMeta.toString()) };

			filePost.setRequestHeader( "X-API-KEY",key);
			filePost.setRequestHeader( "Accept","application/json");
			filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );

			int response = httpclient.executeMethod( filePost );
			
			System.out.println( "Response : "+response );
			System.out.println( "item resp: " + filePost.getResponseBodyAsString());
			return response;
			
			
//			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
//			JSONObject items = new JSONObject();
//			items.put("items", MetadataGeneral.createMetadata(itemMetadata));
//
//
//			HttpPost request = new HttpPost(UploadConstants.CATALOG_URL +collectionName);
//			StringEntity params =new StringEntity(items.toString());
//			request.addHeader("X-API-KEY",key);
//			request.addHeader("Accept","application/json");
//			request.addHeader("Content-Type", "application/json; charset=UTF-8");
//			request.setEntity(params);
//			HttpResponse response = httpClient.execute(request);
//			int responseCode = response.getStatusLine().getStatusCode();
//			System.out.println(response);
//			return responseCode;
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
			JSONObject docMetadata,
			String metadataField
			){
	

		HttpClient httpclient = new HttpClient();
		if (fileUpload.exists()){
			try{
				String url = UploadConstants.CATALOG_URL + collectionName + "/" + itemName;
				
				PostMethod filePost = new PostMethod(url);
				System.out.println("url of doc upload: " +  url);
				System.out.println(docMetadata);
				System.out.println(itemName);
				docMetadata.put("@context", InitializeMetadata.initContext(metadataField));


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
	
	//Delete all items
	public int deleteItems(String key, 
			String collectionName
			){

		try {
			JSONObject itemMetadata = RequestHelper.requestToAlveo(key, UploadConstants.CATALOG_URL+ 
					"search?metadata=collection_name:" + collectionName);
			String itemNamestemp1 = itemMetadata.get("items").toString();
			String itemNamestemp2 = itemNamestemp1.substring(1, itemNamestemp1.length() - 1);
			List<String> itemNames = Arrays.asList(itemNamestemp2.split(","));
			System.out.println( itemNames.toString());
			for (String url: itemNames) {
				System.out.println(url);
			}
			for (String url: itemNames) {
				try {
					HttpClient httpclient = new HttpClient();
					DeleteMethod deleteItem = new DeleteMethod( url );

					deleteItem.setRequestHeader( "X-API-KEY",key);
					deleteItem.setRequestHeader( "Accept","application/json");
					//			deleteItem.setRequestEntity( new MultipartRequestEntity(deleteItem.getParams()  );

					int response = httpclient.executeMethod( deleteItem );
					System.out.println( "Response : "+response );
					System.out.println( deleteItem.getResponseBodyAsString());
				} catch (Exception e) {
					System.out.println(e.getMessage());
					continue;
				}
			}

		}catch( HttpException e ){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch( IOException e ){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





		return 0;

	}
	
}
