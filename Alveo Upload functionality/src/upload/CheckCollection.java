package upload;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/** A class to check whether collection name is valid
 * 
 * @author Kun He
 *
 */

public class CheckCollection {
	public static String check(String collection)throws IOException {
		// TODO Auto-generated method stub
		
		String serviceURL =	UploadConstants.CATALOG_URL + collection;
			
		URL myURL = new URL(serviceURL);
		HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("X-API-KEY", "PFBu5ydwAAL9iSYqqq1A");
		conn.setRequestProperty("Accept", "application/json");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		
		//To get Alveo server HTTP response code
		int responseCode = conn.getResponseCode();		
		String rCode =  Integer.toString(responseCode);
		System.out.println(rCode);
		return rCode;
		
		
		
	}
}
