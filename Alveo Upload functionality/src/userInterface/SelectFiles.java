package userInterface;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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

import mapping.BioC2Alveo;
import mapping.BioC2Json;
import upload.Metadata;

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

	private String path;
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
		frame.setBounds(100, 100, 418, 449);
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
	                absolupath = absolupath.substring(0,absolupath.lastIndexOf(File.separator));
	                Filechooser.setText(path);
	            } else {
	                System.out.println("Open command cancelled by user.");
	            }
			}
		});
		btnAdd.setBounds(317, 72, 60, 23);
		frame.getContentPane().add(btnAdd);
		
		btnUpload = new JButton("Upload");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					String collection = textField.getText();
					path = Filechooser.getText();
					File f = new File(path);
					
					//To check if the file path is existing
					if(f.exists()){
						 
						String extension = path.substring(path.lastIndexOf('.'));
						
						//For BioC format files
						if (extension.equals(".XML")){
							
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
						//For common files
						else{	
							HttpClient httpclient = new HttpClient();
							PostMethod filePost = new PostMethod( "https://app.alveo.edu.au/catalog/" +collection );
						    
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
