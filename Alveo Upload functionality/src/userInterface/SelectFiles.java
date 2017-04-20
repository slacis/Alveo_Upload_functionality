package userInterface;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.FilenameUtils;

import mapping.BioC2Alveo;
import mapping.BioC2Json;
import net.sf.json.JSONObject;
import upload.CollectionUploadGeneral;
import upload.Metadata;
import upload.UploadConstants;

/** A upload function GUI class 
 * 
 *
 * @author Kun He
 *
 */

public class SelectFiles {

	JFrame frame;
	private JTextField Filechooser;
	private JButton btnUpload;
	private JButton btnCancel;
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	private String path = null;
	private String absolupath;
	private String filename;
	private JTextField textField;


	/**
	 * Create the application.
	 * @param key 
	 */
	public SelectFiles(String key) {		
		initialize(key);
	}

	/**
	 * Initialize the contents of the frame.
	 * @param key 
	 */
	private void initialize(String key) {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		
		JLabel lblMetadata_1 = new JLabel("Metadata:");
		lblMetadata_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMetadata_1.setBounds(28, 221, 76, 30);
		frame.getContentPane().add(lblMetadata_1);
		
		JLabel lblNewLabel = new JLabel("File Path:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(28, 69, 60, 27);
		frame.getContentPane().add(lblNewLabel);
		
		Filechooser = new JTextField();
		Filechooser.setBounds(100, 68, 200, 30);
		frame.getContentPane().add(Filechooser);
		Filechooser.setColumns(10);
		
		JEditorPane dtrpnJson = new JEditorPane();
		dtrpnJson.setText("Metadata as JSON-LD");
		dtrpnJson.setBounds(100, 167, 200, 159);
		frame.getContentPane().add(dtrpnJson);
		
		textField = new JTextField();
		textField.setBounds(100, 115, 200, 30);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Collection:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(28, 123, 76, 22);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				final JFileChooser selection = new JFileChooser();
				// Allow for selection of individual file or directory
				selection.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				File file;
				
	            int returnVal = selection.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	file = selection.getSelectedFile();
	                if ((file.getName() != null) && (file.getName().length() > 0)) { 
	                    int dot = file.getName().lastIndexOf('.'); 
	                    if ((dot >-1) && (dot < (file.getName().length()))) { 
	                        filename = file.getName().substring(0, dot); 
	                    } 
	                } 
	                
	                path = file.getPath();
	                absolupath = file.getAbsolutePath();
	                System.out.println(absolupath);
	                absolupath = absolupath.substring(0,absolupath.lastIndexOf(File.separator));
	                Filechooser.setText(path);
	            } else {
	                System.out.println("Open command cancelled by user.");
	            }
			}
		});
		btnAdd.setBounds(317, 72, 70, 23);
		frame.getContentPane().add(btnAdd);
		
		// Open Metadata Editor button
		JButton btnMetadata = new JButton("Metadata");
		btnMetadata.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (path == null){
				//Error message : Null Path 
				JOptionPane.showMessageDialog(null, "Please select path", "InfoBox: " + "Error Message", JOptionPane.INFORMATION_MESSAGE);
				} else {
				MetadataBuilder builder = new MetadataBuilder(path, recItemMetadata, recDocMetadata);
				builder.frame.setVisible(true);
				// Listener to get built Metadata
				builder.frame.addWindowListener(new WindowAdapter() {
					  @Override
					  public void windowClosing(WindowEvent e) {
						  recItemMetadata = builder.recItemMetadata;
						  recDocMetadata = builder.recDocMetadata;
						  System.out.println(recItemMetadata);
					  }
					 
					});
				}
	
			}
		});
		btnMetadata.setBounds(317, 100, 110, 23);
		frame.getContentPane().add(btnMetadata);
		
		btnUpload = new JButton("Upload");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					String collection = textField.getText();
					path = Filechooser.getText();
					File f = new File(path);
					
					//To check if the file path is existing
					if(f.exists()){
						 
						String extension = FilenameUtils.getExtension(f.getAbsolutePath());
						System.out.println(extension);
						
						//For BioC format files
						if (extension.equals("xml")){
							
							System.out.println("----------");
							//To generate the plain text file, the original BioC XML format file segment 
							 BioC2Alveo.writeAlveoFiles(path);
							 //To generate annotation file
							 List<String> docIDs = BioC2Json.writeJson(path,filename, collection);					
							 String metadata = dtrpnJson.getText();
							
							 //To upload above files to Alveo server
							 int result = upload.CollectionUpload.upload(absolupath,docIDs,key,collection, metadata);
							 
							 
							 if(result == 2){
								 // Error handling for Invalid metadata
								 JOptionPane.showMessageDialog(null,"Error: Invalid metadata");
							 }else if(result == 1){
								 // A successful prompt
								 JOptionPane.showMessageDialog(null,"The file was uploaded successfully");
							 }
							 Filechooser.setText("");
							 textField.setText(null);							
							
						}
						//For general collection
						else if (f.isDirectory()) {
							CollectionUploadGeneral.upload(path, key, collection, recItemMetadata);
							
						}
						//For Common files
							else{	
							System.out.println("++++++++++");
							HttpClient httpclient = new HttpClient();
							PostMethod filePost = new PostMethod( UploadConstants.CATALOG_URL +collection );
						    
							File txtFile =  new File(path);
							float txtFileBytes = txtFile.length();
						    Part[] parts = {new FilePart( "file", txtFile ), 
						    		new StringPart( "items",Metadata.createMetadata(filename,txtFileBytes).toString()) };
						   
						    filePost.setRequestHeader( "X-API-KEY",key);
						    filePost.setRequestHeader( "Accept","application/json");
						    filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );
		
						    int response = httpclient.executeMethod( filePost );
						    System.out.println( "Response : "+response );
						    System.out.println( filePost.getResponseBodyAsString());
							
						}					 
							
					  }else{
						  JOptionPane.showMessageDialog(null,"File not found!");
					  }
									

					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (XMLStreamException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnUpload.setBounds(100, 353, 89, 23);
		frame.getContentPane().add(btnUpload);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(211, 353, 89, 23);
		frame.getContentPane().add(btnCancel);
		
		
		
		JButton btnNewButton = new JButton("Clear");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dtrpnJson.setText(null);
			}
		});
		btnNewButton.setBounds(317, 255, 71, 25);
		frame.getContentPane().add(btnNewButton);

	}
}
