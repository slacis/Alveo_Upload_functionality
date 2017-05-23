package mapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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


/**
 * The primary class to use to parsed the BioC format file and extract 
 * each BioC documents information from a BioC collection.
 * 
 * To generate the original BioC XML format file segment for each BioC documents.
 * In order to preserve the original BioC XML format file, 
 * each BioC documents are extracted from the original BioC collection. 
 * 
 * To generate a plain text file for each BioC documents
 * The plain text file is composed of a series of the original text which extracts from the BioC passages. 
 * It is the textual version of the original document. 
 * 
 * 
 * @author Kun He
 * 
 */



public class BioC2Alveo {
	
	public static void writeAlveoFiles(String path, String outputPath) throws XMLStreamException, IOException{
	
		BioCFactoryImpl format = new BioCFactoryImpl();

		// Load BioC model in memory
		File bf = new File(path);
		FileReader fr = new FileReader(bf);
		BufferedReader br = new BufferedReader(fr);
		BioCCollectionReader r = format.createBioCCollectionReader(br);
		BioCCollection c = r.readCollection();
	
		// To traverse each BioC documents from BioC collection
		for (BioCDocument d : c.getDocuments()){
			
			String docID = d.getID();
			String dtext = "";	
			String doc_fileName = docID.toLowerCase() + ".xml";
			System.out.println("Filename: " + doc_fileName);			
	        XMLOutputFactory xof = XMLOutputFactory.newInstance();
	        XMLStreamWriter xmlWriter = null;	        
	        File af = new File(outputPath,doc_fileName);
	        xmlWriter = xof.createXMLStreamWriter(new FileWriter(af));
	        	        
	        
	        xmlWriter.writeStartDocument();
	        xmlWriter.writeStartElement("document");
	        
	        //id
	        xmlWriter.writeStartElement("id");
	        xmlWriter.writeCharacters(docID);
			xmlWriter.writeEndElement();	
	        			
	        //To traverse infon elements from BioC document 
	        for (Entry<String, String> infon : d.getInfons().entrySet()) {
	        	xmlWriter.writeStartElement("infon");
	        	xmlWriter.writeAttribute("key", infon.getKey());
	        	xmlWriter.writeCharacters(infon.getValue());
	        	xmlWriter.writeEndElement();
	          }
	        
	        
			//To traverse BioC passage from BioC document
			for (BioCPassage p : d.getPassages()){
     
		        xmlWriter.writeStartElement("passage");
		        
		        // To traverse infon elements from BioC passage
		        for (Entry<String, String> infon : p.getInfons().entrySet()) {
		        	xmlWriter.writeStartElement("infon");
		        	xmlWriter.writeAttribute("key", infon.getKey());
		        	xmlWriter.writeCharacters(infon.getValue());
		        	xmlWriter.writeEndElement();
		          }
		        
		        // offset
		        xmlWriter.writeStartElement("offset");
		        xmlWriter.writeCharacters(Integer.toString(p.getOffset()));
		        xmlWriter.writeEndElement();
		        
		        //passage level text
		        xmlWriter.writeStartElement("text");
				xmlWriter.writeCharacters(p.getText());
				xmlWriter.writeEndElement();
		        String ptext = p.getText();
		        dtext = dtext + ptext + "\n";
				
		        //To traverse passage level annotation
				for (BioCAnnotation a : p.getAnnotations()){
					
					xmlWriter.writeStartElement("annotation");
					// id
					if (a.getID() != null){
						xmlWriter.writeAttribute("id", a.getID());
					}
					// infon
					for (Entry<String, String> infon : a.getInfons().entrySet()) {
			        	xmlWriter.writeStartElement("infon");
			        	xmlWriter.writeAttribute("key", infon.getKey());
			        	xmlWriter.writeCharacters(infon.getValue());
			        	xmlWriter.writeEndElement();
			          }					
					// location
					for (BioCLocation bcl : a.getLocations()){
						
						xmlWriter.writeStartElement("location");
						xmlWriter.writeAttribute("offset", Integer.toString(bcl.getOffset()));
						xmlWriter.writeAttribute("length", Integer.toString(bcl.getLength()));
						xmlWriter.writeEndElement();						
					}
					
					//text
					xmlWriter.writeStartElement("text");
					xmlWriter.writeCharacters(a.getText());
					xmlWriter.writeEndElement();
					
					xmlWriter.writeEndElement();
				}
				
				//To traverse passage level sentence
				for (BioCSentence sen : p.getSentences()){
					
					xmlWriter.writeStartElement("sentence");
					// infon 
			        for (Entry<String, String> infon : sen.getInfons().entrySet()) {
			        	xmlWriter.writeStartElement("infon");
			        	xmlWriter.writeAttribute("key", infon.getKey());
			        	xmlWriter.writeCharacters(infon.getValue());
			        	xmlWriter.writeEndElement();
			          }
					
			        // offset
			        xmlWriter.writeStartElement("offset");
			        xmlWriter.writeCharacters(Integer.toString(sen.getOffset()));
			        xmlWriter.writeEndElement();
					
			        //text
					xmlWriter.writeStartElement("text");
					xmlWriter.writeCharacters(sen.getText());
					xmlWriter.writeEndElement();
					
					dtext = dtext + sen.getText() + "\n";
					
					//To traverse sentence level annotation
					for (BioCAnnotation sa : sen.getAnnotations()){
						
						xmlWriter.writeStartElement("annotation");
						// id
						if (sa.getID() != null){
							xmlWriter.writeAttribute("id", sa.getID());
						}
						// infon
						for (Entry<String, String> infon : sa.getInfons().entrySet()) {
				        	xmlWriter.writeStartElement("infon");
				        	xmlWriter.writeAttribute("key", infon.getKey());
				        	xmlWriter.writeCharacters(infon.getValue());
				        	xmlWriter.writeEndElement();
				          }					
						// location
						for (BioCLocation bcl : sa.getLocations()){
							
							xmlWriter.writeStartElement("location");
							xmlWriter.writeAttribute("offset", Integer.toString(bcl.getOffset()));
							xmlWriter.writeAttribute("length", Integer.toString(bcl.getLength()));
							xmlWriter.writeEndElement();						
						}
						
						//text
						xmlWriter.writeStartElement("text");
						xmlWriter.writeCharacters(sa.getText());
						xmlWriter.writeEndElement();
						
						//
						xmlWriter.writeEndElement();
					}
					
					//To traverse sentence level relation
					for (BioCRelation sre : sen.getRelations()){
						
						xmlWriter.writeStartElement("relation");
					    // id
						xmlWriter.writeAttribute("id", sre.getID());
					    // infon
						for (Entry<String, String> infon : sre.getInfons().entrySet()) {
				        	xmlWriter.writeStartElement("infon");
				        	xmlWriter.writeAttribute("key", infon.getKey());
				        	xmlWriter.writeCharacters(infon.getValue());
				        	xmlWriter.writeEndElement();
				          }
					    // labels
					    for (BioCNode label : sre.getNodes()) {
					    	
					    	xmlWriter.writeStartElement("node");
					        // id
					    	xmlWriter.writeAttribute("refid", label.getRefid());
					        // role
					    	xmlWriter.writeAttribute("role", label.getRole());
					        //
					    	xmlWriter.writeEndElement();
					    }
					    //
					    xmlWriter.writeEndElement();
	          
					}				
					
					xmlWriter.writeEndElement();
				}
				
				
				//To traverse passage level relation
				for (BioCRelation pre : p.getRelations()){
					
					xmlWriter.writeStartElement("relation");
				    // id
					xmlWriter.writeAttribute("id", pre.getID());
				    // infon
					for (Entry<String, String> infon : pre.getInfons().entrySet()) {
			        	xmlWriter.writeStartElement("infon");
			        	xmlWriter.writeAttribute("key", infon.getKey());
			        	xmlWriter.writeCharacters(infon.getValue());
			        	xmlWriter.writeEndElement();
			          }
				    // labels
				    for (BioCNode label : pre.getNodes()) {
				    	
				    	xmlWriter.writeStartElement("node");
				        // id
				    	xmlWriter.writeAttribute("refid", label.getRefid());
				        // role
				    	xmlWriter.writeAttribute("role", label.getRole());
				        //
				    	xmlWriter.writeEndElement();
				    }
				    //
				    xmlWriter.writeEndElement();          
				}
				
				xmlWriter.writeEndElement();
				
			}
			
			//To traverse doc level relation
			for (BioCRelation dre : d.getRelations()){
				
				xmlWriter.writeStartElement("relation");
			    // id
				xmlWriter.writeAttribute("id", dre.getID());
			    // infon
				for (Entry<String, String> infon : dre.getInfons().entrySet()) {
		        	xmlWriter.writeStartElement("infon");
		        	xmlWriter.writeAttribute("key", infon.getKey());
		        	xmlWriter.writeCharacters(infon.getValue());
		        	xmlWriter.writeEndElement();
		          }
			    // labels
			    for (BioCNode label : dre.getNodes()) {
			    	
			    	xmlWriter.writeStartElement("node");
			        // id
			    	xmlWriter.writeAttribute("refid", label.getRefid());
			        // role
			    	xmlWriter.writeAttribute("role", label.getRole());
			        //
			    	xmlWriter.writeEndElement();
			    }
			    //
			    xmlWriter.writeEndElement();
      
			}
			
			xmlWriter.writeEndElement();				
			xmlWriter.flush();
			xmlWriter.close();
			
			String text_fileName;
			text_fileName =  docID + ".txt";		
			File tf = new File(outputPath,text_fileName.toLowerCase());
			FileWriter fw = new FileWriter(tf);
			BufferedWriter bw = new BufferedWriter(fw);	        
			bw.write(dtext);
			bw.close();

		}
				
		r.close();
		
	}

}