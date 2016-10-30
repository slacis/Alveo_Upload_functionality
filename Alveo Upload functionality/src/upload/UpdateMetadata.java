package upload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.OutputStreamWriter;;


/** A class to updating collection metadata to Alveo system.
 * 
 * 
 * @author Kun He
 *
 */

@SuppressWarnings("serial")
public class UpdateMetadata  extends JSONException{


	
	public static int uploadMetadata(String data, String collection) {

	    try {
	    	
	    if(data.length()!=0){
	    	
	    	String url = "https://app.alveo.edu.au/catalog/" + collection;

		    URL obj = new URL(url);
		    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		    conn.setRequestProperty("Content-Type", "application/json");
		    conn.setRequestProperty( "X-API-KEY","PFBu5ydwAAL9iSYqqq1A");;
		    conn.setRequestProperty( "Accept","application/json");
		    conn.setDoOutput(true);
		    conn.setRequestMethod("PUT");
		    
		    JSONObject datajson = null;
		    try{
		    	datajson = JSONObject.fromObject(data);
		    }catch( JSONException expected ){
		    	return 3;
		    }
		    
		    JSONObject jsonParam = new JSONObject();
		    jsonParam.put("replace", true);
		    jsonParam.put("collection_metadata",datajson.toString());
		    
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    out.write(jsonParam.toString());
		    out.close();

		    BufferedReader br = new BufferedReader( new InputStreamReader(conn.getInputStream()));
		    String sCurrentLine;
		    while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

	    }

	    
	    } catch (Exception e) {
	    e.printStackTrace();
	    }
	    
	    return 4;

	  }
	

}
