package upload;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** A class to generate metadata(JSON string), 
 * this JSON string will be included in the request as a parameter.
 * 
 * 
 * @author Kun He
 *
 */

public class MetadataGeneral {
	
	public static JSONArray createMetadata(JSONObject graph_v){

		
		JSONArray docsMetadata = new JSONArray();			
		JSONObject docMetadata = new JSONObject();
		JSONObject docMetadata_v = new JSONObject();		
			
		JSONObject context = new JSONObject();
		JSONArray graph = new JSONArray();
//		JSONObject graph_v = new JSONObject();
		JSONArray ausnc_doc = new JSONArray();
//		JSONObject ausnc_doc_v = new JSONObject();
		JSONObject hcsvlab_display = new JSONObject();
		JSONObject hcsvlab_indexable = new JSONObject();
		context.element("ausnc" , "http://ns.ausnc.org.au/schemas/ausnc_md_model/");
		context.element("corpus" , "http://ns.ausnc.org.au/corpora/");
		context.element("dc" , "http://purl.org/dc/terms/");
		context.element("dcterms" , "http://purl.org/dc/terms/");
		context.element("foaf" , "http://xmlns.com/foaf/0.1/");
		context.element("hcsvlab" , "http://hcsvlab.org/vocabulary/");	
		context.element("mbep" , "http://hcsvlab.org/vocabulary");	
		docMetadata_v.element("@context", context.toString());			
			
//		graph_v.element("@id" , docID);
//		graph_v.element("@type" , "ausnc:AusNCObject");
		
//		ausnc_doc_v.element("@id" , docName);
//		ausnc_doc_v.element("@type" , "foaf:Document");
//		ausnc_doc_v.element("dcterms:extent" , txtFileBytes);
//		ausnc_doc_v.element("dcterms:identifier" , docName);
//		//Removed #Text
//		ausnc_doc_v.element("dcterms:title" , docName+"#"+fileExs);
//		
//		// Check Extension
//		String value = UploadConstants.EXT_MAP.get("."+fileExs);
//		if (value != null) {
//		ausnc_doc_v.element("dcterms:type" , value);
//		} else {
//		ausnc_doc_v.element("dcterms:type" , "Other");
//		}
					
//		ausnc_doc.add(ausnc_doc_v.toString());
//		graph_v.element("ausnc:document", ausnc_doc.toString());			
//		graph_v.element("dcterms:identifier" , docID);//
//		hcsvlab_display.element("@id" , docID + "#Text");
//		hcsvlab_indexable.element("@id" , docID + fileExs);
//		graph_v.element("hcsvlab:display_document", hcsvlab_display.toString());
//		graph_v.element("hcsvlab:indexable_document", hcsvlab_indexable.toString());
//		graph_v.element("dcterms:creator", "C. Watson and S. Cassidy");
//      graph_v.element("olac:language", "eng");
		graph.add(graph_v.toString());
		docMetadata_v.element("@graph", graph.toString());
		docMetadata.element("metadata", docMetadata_v.toString());
		docsMetadata.add(docMetadata.toString());
			
		return docsMetadata;	
		
}
	


}