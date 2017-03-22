package userInterface;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import upload.UploadConstants;

/** A class to check if the API key is valid
 * 
 * 
 * @author Kun He
 *
 */


public class LoginCheck {

	public static String check(String key) throws IOException {
		// TODO Auto-generated method stub
		
		String serviceURL =	UploadConstants.CATALOG_URL;
			
		URL myURL = new URL(serviceURL);
		HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-API-KEY", key);
		conn.setRequestProperty("Accept", "application/json");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		
		//To get Alveo server HTTP response code
		int responseCode = conn.getResponseCode();		
		String rCode =  Integer.toString(responseCode);
		
		return rCode;	
		
	}
}
