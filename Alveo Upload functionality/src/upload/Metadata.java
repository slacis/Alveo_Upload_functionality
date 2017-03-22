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

public class Metadata {
	
	public static JSONArray createMetadata(String docID, float txtFileBytes){
		
		JSONArray docsMetadata = new JSONArray();			
		JSONObject docMetadata = new JSONObject();
		JSONObject docMetadata_v = new JSONObject();		
			
		JSONObject context = new JSONObject();
		JSONArray graph = new JSONArray();
		JSONObject graph_v = new JSONObject();
		JSONArray ausnc_doc = new JSONArray();
		JSONObject ausnc_doc_v = new JSONObject();
		JSONObject hcsvlab_display = new JSONObject();
		JSONObject hcsvlab_indexable = new JSONObject();
		context.element("ausnc" , "http://ns.ausnc.org.au/schemas/ausnc_md_model/");
		context.element("corpus" , "http://ns.ausnc.org.au/corpora/");
		context.element("dc" , "http://purl.org/dc/terms/");
		context.element("dcterms" , "http://purl.org/dc/terms/");
		context.element("foaf" , "http://xmlns.com/foaf/0.1/");
		context.element("hcsvlab" , "http://hcsvlab.org/vocabulary/");	
		docMetadata_v.element("@context", context.toString());			
			
		graph_v.element("@id" , docID);
		graph_v.element("@type" , "ausnc:AusNCObject");
		
		ausnc_doc_v.element("@id" , docID + ".text");
		ausnc_doc_v.element("@type" , "foaf:Document");
		ausnc_doc_v.element("dcterms:extent" , txtFileBytes);
		ausnc_doc_v.element("dcterms:identifier" , docID + ".txt");
		ausnc_doc_v.element("dcterms:title" , docID + "#Text");
		ausnc_doc_v.element("dcterms:type" , "Text");
					
		ausnc_doc.add(ausnc_doc_v.toString());
		graph_v.element("ausnc:document", ausnc_doc.toString());			
		graph_v.element("dcterms:identifier" , docID);//
		hcsvlab_display.element("@id" , docID + "#Text");
		hcsvlab_indexable.element("@id" , docID + ".txt");
		graph_v.element("hcsvlab:display_document", hcsvlab_display.toString());
		graph_v.element("hcsvlab:indexable_document", hcsvlab_indexable.toString());
			
		graph.add(graph_v.toString());
		docMetadata_v.element("@graph", graph.toString());
		docMetadata.element("metadata", docMetadata_v.toString());
		docsMetadata.add(docMetadata.toString());
			
		return docsMetadata;	
		
}
	


}