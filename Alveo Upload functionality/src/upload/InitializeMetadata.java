package upload;

import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A class to generate metadata for metadata builder
 * Reads CSV file if exists and supports mapping
 * 
 * 
 * @author Simon Lacis
 *
 */
public class InitializeMetadata {

	public JSONObject initContext() {
		// Default context metadata
		JSONObject context = new JSONObject();
		context.element("ausnc", "http://ns.ausnc.org.au/schemas/ausnc_md_model/");
		context.element("corpus", "http://ns.ausnc.org.au/corpora/");
		context.element("dc", "http://purl.org/dc/terms/");
		context.element("dcterms", "http://purl.org/dc/terms/");
		context.element("foaf", "http://xmlns.com/foaf/0.1/");
		context.element("hcsvlab", "http://hcsvlab.org/vocabulary/");
		return context;
	}
	
	// Initialize editable item level metadata
	// First file uploaded selected arbitrarily
	public static JSONObject initRec(String docID, String docName, 
			float txtFileBytes, String fileExs){
		// Default required/recommended metadata
		JSONObject ausnc_doc_v = new JSONObject();
		ausnc_doc_v.element("@id", docName);
		ausnc_doc_v.element("@type", "foaf:Document");
		ausnc_doc_v.element("dcterms:extent", txtFileBytes);
		ausnc_doc_v.element("dcterms:identifier", docName);
		ausnc_doc_v.element("dcterms:title", docName + "#" + fileExs);
		// Check Extension
		String value = UploadConstants.EXT_MAP.get(fileExs);
		if (value != null) {
			ausnc_doc_v.element("dcterms:type", value);
		} else {
			ausnc_doc_v.element("dcterms:type", "Other");
		}
		return ausnc_doc_v;
	}
	

	
	

}
