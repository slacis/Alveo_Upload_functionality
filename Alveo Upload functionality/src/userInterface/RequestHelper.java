package userInterface;

/** Class for request related commands that are used by multiple classes
 * 
 *
 * @author Simon Lacis
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

public final class RequestHelper {
	private RequestHelper(){}
	
	// make JSON request to Alveo server
	public static JSONObject requestToAlveo(String key, String appendToUrl) throws IOException {
		JSONObject tmpJSON =  new JSONObject();
		String serviceURL =	appendToUrl;
		URL myURL = new URL(serviceURL);
		HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("X-API-KEY", key);
		conn.setRequestProperty("Accept", "application/json");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		JSONObject metadata = readResponse(conn);
		System.out.print(metadata.toString());
		return metadata;
	}

	// Read response from request made
	public static JSONObject readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();
		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();
		System.out.println(response.toString());
		JSONObject JSONresponse = JSONObject.fromObject(response.toString());
		System.out.println(JSONresponse.toString());
		return JSONresponse;
	}


}
