package mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.stream.XMLStreamException;
import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCNode;
import bioc.BioCPassage;
import bioc.BioCRelation;
import bioc.BioCSentence;
import bioc.io.BioCCollectionReader;
import bioc.io.standard.BioCFactoryImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import upload.UploadConstants;

/**
 * The primary class to use to parsed the BioC format file and extract 
 * all kinds of annotations such passage， sentence， annotation and relation.
 * To generate a new annotation file which contains a series of different annotations 
 * including annotation of type “Passage”, annotation of type “Sentence”, 
 * annotation of type “Annotation” and annotation of type “Relation”. 
 * 
 * @author Kun He
 * 
 */

public class BioC2Json {
		
	public static void writeFile(String filename, String sets, File file, String outputPath)  
            throws IOException { 
		File tf = new File(outputPath, filename);
        FileWriter fw = new FileWriter(tf);  
        PrintWriter out = new PrintWriter(fw);  
        out.write(sets);  
        out.println();  
        fw.close();  
        out.close();  
    }
	
	public static List<String> writeJson(String path, String filename, String collection, String outputPath) throws XMLStreamException, IOException{
		
		List<String> docIDs = new ArrayList<String>();
	
		BioCFactoryImpl format = new BioCFactoryImpl();

		// Load BioC model in memory
		File bf = new File(path);
		FileReader fr = new FileReader(bf);
		BufferedReader br = new BufferedReader(fr);
		BioCCollectionReader r = format.createBioCCollectionReader(br);
		BioCCollection c = r.readCollection();
	
		// To traverse each BioC documents from BioC collection
		for (BioCDocument d : c.getDocuments()){
			
			//docid
			String docID = d.getID();
			docIDs.add(docID);
			
			String docUrl = UploadConstants.CATALOG_URL+ filename + "/" + docID + ".txt";
		
			JSONObject docjsonObject = new JSONObject();
			JSONObject properties = new JSONObject();
	        JSONArray annArray = new JSONArray();
	        
			docjsonObject.element("@context", "https://app.alveo.edu.au/schema/json-ld");
			properties.element("alveo:annotates", docUrl);
			docjsonObject.element("commonProperties", properties);
			
			// To initialize passage ID
	        int passageId = 1;
	        
			//To traverse passage from BioC document
			for (BioCPassage p : d.getPassages()){
     	        
				// offset
		        int passageOffset = p.getOffset();
		        String ptext = p.getText();
		        
				JSONObject passagejsonObject = new JSONObject();
				passagejsonObject.element("@id", UploadConstants.CATALOG_URL+ collection + "/" + docID +"/annotation/passage" + Integer.toString(passageId) );
				passageId = passageId+1;
				passagejsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/passage");
				passagejsonObject.element("start", passageOffset);
				passagejsonObject.element("end", ptext.length()-1);
				annArray.add(passagejsonObject);
		        
		
		        //To traverse passage level annotation
		        for (BioCAnnotation a : p.getAnnotations()){
					
		        	// id
					String annID = a.getID();
					
					JSONObject pannjsonObject = new JSONObject();				    
				    pannjsonObject.element("ann-id", annID);
				    pannjsonObject.element("@id", UploadConstants.CATALOG_URL+collection + "/" + docID + "/annotation/" + annID);
				    pannjsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/annotation");
		            
				    
					// infon
					for (Entry<String, String> infon : a.getInfons().entrySet()) {
						
						JSONObject infonjsonObject = new JSONObject();
			            String key =  infon.getKey();
			            String value = infon.getValue();
			            infonjsonObject.element(key, value);
			            pannjsonObject.accumulate ("infon", infonjsonObject);
			          }
					
					
					//text
					String annText = a.getText();
					pannjsonObject.element("txt", annText);
					
					
					// location
					for (BioCLocation bcl : a.getLocations()){
						
						int annOffset = bcl.getOffset();
						int annLength = bcl.getLength();
						pannjsonObject.element("start", annOffset);
						pannjsonObject.element("end", annLength-1);
						
					}
					
					annArray.add(pannjsonObject);
					
				}
				
		        // To initialize sentence ID
		        int sentenceID = 1;
				//To traverse passage level sentence
				for (BioCSentence sen : p.getSentences()){
					
					int index = p.getSentences().indexOf(sen);
					System.out.println("\nsen index: " + index);
					int lastindex = p.getSentences().size() - 1;
					System.out.println("\nlast sen index: " + lastindex);
					
					if (index != lastindex){
					BioCSentence nextSen = p.getSentence(index+1);
					int nextsenOffset = nextSen.getOffset();
					int senOffset = sen.getOffset();
					int senlength = nextsenOffset - senOffset;
					System.out.println("\nlength: " + senlength);
					}
					
					
			        // offset
			        int senOffset = sen.getOffset();
			        String stext = sen.getText();
			        
					JSONObject senpassagejsonObject = new JSONObject();
					senpassagejsonObject.element("@id", UploadConstants.CATALOG_URL+collection + "/" + docID + "/annotation/passage" + Integer.toString(passageId)+"/sentence"+ Integer.toString(sentenceID) );
					senpassagejsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/sentence");
					sentenceID = sentenceID +1;
					senpassagejsonObject.element("start", senOffset);
					senpassagejsonObject.element("end", stext.length()-1);
					annArray.add(senpassagejsonObject);
		
					//To traverse sentence level annotation
					for (BioCAnnotation sa : sen.getAnnotations()){
						
						// id
						String annID = sa.getID();
						
						JSONObject senannjsonObject = new JSONObject();					    
					    senannjsonObject.element("ann-id", annID);
					    senannjsonObject.element("@id", UploadConstants.CATALOG_URL+collection + "/" + docID + "/annotation/" + annID);
					    senannjsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/annotation");
			            
					    
						// infon
						for (Entry<String, String> infon : sa.getInfons().entrySet()) {
							
							JSONObject infonjsonObject = new JSONObject();
				            String key =  infon.getKey();
				            String value = infon.getValue();
				            infonjsonObject.element(key, value);
				            senannjsonObject.accumulate ("infon", infonjsonObject);
				          }
						
						
						//text
						String annText = sa.getText();
						senannjsonObject.element("txt", annText);
						
						
						// location
						for (BioCLocation bcl : sa.getLocations()){
							
							int annOffset = bcl.getOffset();
							int annLength = bcl.getLength();
							senannjsonObject.element("start", annOffset);
							senannjsonObject.element("end", annLength-1);							
						}
						
						annArray.add(senannjsonObject);
					}
					
					//To traverse sentence level relation
					for (BioCRelation sre : sen.getRelations()){

					    // id
						String relationID = sre.getID();
						
						JSONObject senrejsonObject = new JSONObject();					    
						senrejsonObject.element("r-id", relationID);
						senrejsonObject.element("@id", UploadConstants.CATALOG_URL+collection + "/" + docID + "/annotation/" + relationID);
						senrejsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/relation");
						
						
						// infon
						for (Entry<String, String> infon : sre.getInfons().entrySet()) {
				        	
							JSONObject infonjsonObject = new JSONObject();
				            String key =  infon.getKey();
				            String value = infon.getValue();
				            infonjsonObject.element(key, value);
				            senrejsonObject.accumulate ("infon", infonjsonObject);
				          }
						
					    // labels
					    for (BioCNode label : sre.getNodes()) {
					    	
					        // id
					    	String refID = label.getRefid();
					        // role
					    	String role = label.getRole();
					    	senrejsonObject.element(refID, role); 
					    }
					    annArray.add(senrejsonObject);
	          
					}				
				}
				
				//To traverse passage level relation
				for (BioCRelation pre : p.getRelations()){
					
					 // id
					String relationID = pre.getID();
					JSONObject passagerejsonObject = new JSONObject();					    
					passagerejsonObject.element("r-id", relationID);
					passagerejsonObject.element("@id", UploadConstants.CATALOG_URL+collection + "/" + docID + "/annotation/" + relationID);
					passagerejsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/relation");
					
					// infon
					for (Entry<String, String> infon : pre.getInfons().entrySet()) {
			        	
			            JSONObject infonjsonObject = new JSONObject();
			            String key =  infon.getKey();
			            String value = infon.getValue();
			            infonjsonObject.element(key, value);
			            passagerejsonObject.accumulate ("infon", infonjsonObject);
			          }
					
				    // labels
				    for (BioCNode label : pre.getNodes()) {
				    	
				        // id
				    	String refID = label.getRefid();
				        // role
				    	String role = label.getRole();
				    	passagerejsonObject.element(refID, role); 				        
				    }
				    annArray.add(passagerejsonObject);
				}
				
			}
			
			//To traverse doc level relation
			for (BioCRelation dre : d.getRelations()){
				
				// id
				String relationID = dre.getID();
				JSONObject docrejsonObject = new JSONObject();					    
				docrejsonObject.element("r-id", relationID);
				docrejsonObject.element("@id", UploadConstants.CATALOG_URL+collection + "/" + docID + "/annotation/" + relationID);
				docrejsonObject.element("type", "http://ns.ausnc.org.au/schemas/annotation/collection/relation");
				
				// infon
				for (Entry<String, String> infon : dre.getInfons().entrySet()) {
		        	
		            JSONObject infonjsonObject = new JSONObject();
		            String key =  infon.getKey();
		            String value = infon.getValue();
		            infonjsonObject.element(key, value);
		            docrejsonObject.accumulate ("infon", infonjsonObject);
		          }
				
			    // labels
			    for (BioCNode label : dre.getNodes()) {
			    	
			        // id
			    	String refID = label.getRefid();
			        // role
			    	String role = label.getRole();
			    	docrejsonObject.element(refID, role); 				        
			    }  
			    annArray.add(docrejsonObject);
      
			}
			
			
			String text_fileName;
			text_fileName =  docID + ".json";
			docjsonObject.element("alveo:annotations", annArray);
			writeFile(text_fileName.toLowerCase(), docjsonObject.toString(),bf.getParentFile(), outputPath);
			
		}
				
		r.close();
		return docIDs;
		
	}

}