package userInterface;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.sf.json.JSONSerializer;
import upload.CollectionUploadGeneral;
import upload.InitializeMetadata;
import upload.UploadConstants;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** A GUI to edit metadata
 * 
 * 
 * @author Simon Lacis
 *
 */


public class MetadataBuilder {
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	JFrame frame;
	private JTextField textField;
    AutoCompleteDecorator decorator;
    JRadioButton userMeta, reqMeta, docMeta;
    JTextArea userJson, reqJson, docJson;
    private JComboBox comboBox, itemMetaCombo;
    int textAreaNo = 1;
    BufferedReader br = null;
    String[] values;
    JScrollPane scrollBar1, scrollBar2, scrollBar3;
    ArrayList<String> searchedMeta;
    Map<String, String> fileExtList;
    //General Metadata
    JSONObject graph_v = new JSONObject();
    //Context and details
    JSONObject context = new JSONObject();

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MetadataBuilder window = new MetadataBuilder();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public MetadataBuilder(String path, HashMap<String, JSONObject> recItemMetadata) {
		initialize(path, recItemMetadata);
	}
	
	// Search through array for relevant metadata name
	public static ArrayList<String> metadataSearch(String[] values, String q){
    ArrayList<String> ar = new ArrayList<String>();
    System.out.println("hello" + q);
    for (String s: values) {           
        if(s.contains(q)){
        ar.add(s); 
        System.out.println(ar.toArray());
        }
    }
    return ar;
	}
	
	// Function acquired from stackoverflow for convenience
	public static Map<String, String> strtoHash (String value){
		value = value.substring(1, value.length()-1);          
		String[] keyValuePairs = value.split(",");             
		Map<String,String> map = new HashMap<>();               
		//iterate over the pairs
		for(String pair : keyValuePairs)                        
		{
			//split the pairs to get key and value 
		    String[] entry = pair.split("=");                   
		    map.put(entry[0].trim(), entry[1].trim());      
		}	//add them to the hashmap and trim whitespace
		return map;
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String path, HashMap<String, JSONObject> recItemMetadata) {
		this.recItemMetadata = recItemMetadata;
		// Hash map for unknown file extensions
		fileExtList = new HashMap<String, String>();
		
		
		//This code is used to get directories/sub-directories and then search for
		//their file types. File types that are not in the list of known file extensions
		//Will be populated and can be specified by the user, or will otherwise be set to
		//"Other" by default
		//An ArrayList containing JSONObjects, which contains item level metadata, will be
		//populated and can be changed by the user in the editor.
		
		//Populate item metadata into these ArrayLists
		
//		ArrayList<JSONObject> finalItemMetadata = new ArrayList<JSONObject>();
		ArrayList<String> itemNameList = new ArrayList<String>();
		//Get sub-directories
		List<File> subDirs = CollectionUploadGeneral.getDirs(path);
		for (File subDir: subDirs){
			int first = 1;
			// Set Item name to name of subDirectory
			String docID = subDir.getName();
			itemNameList.add(docID);
			//Get all files from sub-directories
			List<File> filesList = CollectionUploadGeneral.listFiles(subDir.toString());
			for(File file: filesList){
				//Need first file metadata for item creation so take from here and mark
				//if its the first iteration or not
				float fileBytes = file.length();
				String docName = file.getName();
				String fileExt = "." + FilenameUtils.getExtension(file.getAbsolutePath());
				if (UploadConstants.EXT_MAP.get(fileExt ) == null) {
					fileExtList.put(fileExt, "Other");
				}
				//Populating metadata on ITEM level with first document found
				//Check if metadata has been initialized previously first
				if (first == 1 && !recItemMetadata.containsKey(docID)){
					recItemMetadata.put(docID, InitializeMetadata.initRec(docID, docName, 
							fileBytes, fileExt));
					System.out.println(recItemMetadata);
					first = 0;
				}
			}
		}
		System.out.println(fileExtList);
			
		Font font1 = new Font("SansSerif", Font.BOLD, 12);
		frame = new JFrame();
		frame.setSize(800,600);
		frame.setLocationRelativeTo(null);
//		frame.setBounds(100, 100, 418, 449);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Metadata Builder");
		
		// ButtonGroup for metadata type
		
		userMeta  = new JRadioButton("Item Metadata");
		reqMeta  = new JRadioButton("File Extensions");
		docMeta  = new JRadioButton("Document Metadata");
		
		ButtonGroup operation = new ButtonGroup();
		operation.add(reqMeta);
		operation.add(userMeta);
		operation.add(docMeta);
		reqMeta.setSelected(true);
		
		JPanel operPanel = new JPanel();
		Border operBorder = BorderFactory.createTitledBorder("Operation");
		operPanel.setBorder(operBorder);
		operPanel.add(reqMeta);
		operPanel.add(userMeta);
		operPanel.add(docMeta);
		operPanel.setBounds(50, 100, 200, 150);
		
		// JComboBox for metadata type selection
	    try{
	    	br = new BufferedReader(new FileReader("csv/metadatatypes.csv"));
	        String line = null;
	        line = br.readLine();
	        values = line.split(",");
	        Arrays.sort(values);
	    	comboBox = new JComboBox(new DefaultComboBoxModel(values));
//	    	AutoCompleteDecorator.decorate(comboBox);
	    	comboBox.setEditable(true);
	    	comboBox.setBounds(50,50,300,30);
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	    
        // Create an ActionListener for the JComboBox component.
        //
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                //
                // Get the source of the component, which is our combo
                // box.
                //
                JComboBox comboBox = (JComboBox) event.getSource();

                //
                // Print the selected items and the action command.
                //
                Object selected = comboBox.getSelectedItem();
                System.out.println("Selected Item  = " + selected);
                String command = event.getActionCommand();
                System.out.println("Action Command = " + command);

                //
                // Detect whether the action command is "comboBoxEdited"
                // or "comboBoxChanged"
                //
//                if ("comboBoxEdited".equals(command)) {
//                	
//                	searchedMeta = metadataSearch(values,selected.toString());
//                	System.out.println(searchedMeta.toArray());
//                	DefaultComboBoxModel model = new DefaultComboBoxModel( searchedMeta.toArray());
//                		comboBox.setModel( model );
//
//
//                    System.out.println("User has typed a string in " +
//                            "the combo box.");
//                }
            }
        });
        
	    //JComboBox for item metadata
    	itemMetaCombo = new JComboBox(new DefaultComboBoxModel(itemNameList.toArray()));
    	itemMetaCombo.setBounds(50,300,200,30);
    	itemMetaCombo.setVisible(false);
    	
    	 itemMetaCombo.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent event) {
                 JComboBox itemMetaCombo = (JComboBox) event.getSource();

                 //
                 // Print the selected items and the action command.
                 //
                 Object selected = itemMetaCombo.getSelectedItem();
                 System.out.println("Selected Item  = " + selected);
                 String command = event.getActionCommand();
                 System.out.println("Action Command = " + command);

                 //
                 // Detect whether the action command is "comboBoxEdited"
                 // or "comboBoxChanged"
                 //
                 if ("comboBoxChanged".equals(command)) {
                	String tmpStr = recItemMetadata.get(selected).toString().
                 			replaceAll(",", ",\n");
                 	userJson.setText(tmpStr.substring(1, tmpStr.length()-1) + ",\n");
                 			
                 System.out.println(recItemMetadata.get(selected).toString());
                 }
             }
         });
		
		// Text field for adding metadata info
		textField = new JTextField();
		textField.setFont(font1);
		textField.setBounds(420, 50, 271, 30);
		textField.setColumns(20);
	    
		// Button to add metadata
		JButton btnAddMeta = new JButton("Add");
		btnAddMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Code to add metadata-type and metadata
				
			}
		});
		btnAddMeta.setFont(font1);
		btnAddMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnAddMeta.setBounds(680, 50, 80, 30);
		
		// Button to update
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (textAreaNo == 1){
				fileExtList = strtoHash(reqJson.getText());
				System.out.print(fileExtList);
				} else {
					if (textAreaNo == 2){
						recItemMetadata.put(itemMetaCombo.getSelectedItem().toString(),
								JSONObject.fromObject("{" + userJson.getText() + "}"));
				
					}
				}
				
			}
		});
		btnUpdate.setFont(font1);
		btnUpdate.setBounds(640, 500, 80, 30);
		
		// Button to search for metadata
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selected = comboBox.getSelectedItem().toString();
               	searchedMeta = metadataSearch(values,selected.toString());
            	System.out.println(searchedMeta.toArray());
            	DefaultComboBoxModel model = new DefaultComboBoxModel( searchedMeta.toArray());
            		comboBox.setModel( model );

				}

			});
		
		btnSearch.setFont(font1);
		btnSearch.setBounds(345, 50, 70, 30);

		
		//Label for Metadata type
		JLabel lblMetadataT = new JLabel("Metadata Type");
		lblMetadataT.setFont(font1);
		lblMetadataT.setBounds(50, 25, 311, 23);
		
		//Label for Metadata
		JLabel lblMetadata = new JLabel("Metadata");
		lblMetadata.setFont(font1);
		lblMetadata.setBounds(420, 25, 311, 23);
		
		//TextAreas for Metadata
		
		//Required
		reqJson = new JTextArea();
		reqJson.setText(fileExtList.toString());
		reqJson.setLineWrap(true);
		scrollBar1 = new JScrollPane(reqJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar1.setBounds(300, 100, 400, 400);
		
		//User
		userJson = new JTextArea();
		userJson.setLineWrap(true);
		scrollBar2 = new JScrollPane(userJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar2.setBounds(300, 100, 400, 400);
		
		//Doc
		docJson = new JTextArea();
		docJson.setLineWrap(true);
		scrollBar3 = new JScrollPane(docJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar3.setBounds(300, 100, 400, 400);
		
		
		//Button to add metadata to box
		btnAddMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (textAreaNo == 1) {
				reqJson.append("\"" + comboBox.getSelectedItem().toString() +
						"\"" + ":" + " \"" + textField.getText().toString() + "\",\n");
				System.out.println(reqJson.getText());
				JSONObject jsonObj = JSONObject.fromObject("{" + reqJson.getText() + "}");
				System.out.println(jsonObj);
				} else if (textAreaNo == 2) {
				String tmpStr = userJson.getText();
				userJson.append("\"" + comboBox.getSelectedItem().toString() +
						"\"" + ":" + " \"" + textField.getText().toString() + "\",\n");
				
				} else {
				docJson.append("\"" + comboBox.getSelectedItem().toString() +
						"\"" + ":" + " \"" + textField.getText().toString() + "\",\n");
				}
			
			
			}	
		});
		
	    // Listeners for radio buttons
	    reqMeta.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // TODO Auto-generated method stub
	            if(reqMeta.isSelected()) {
	            	textAreaNo = 1;
	                scrollBar1.setVisible(true);
	                scrollBar2.setVisible(false);
	                scrollBar3.setVisible(false);
	                itemMetaCombo.setVisible(false);
	            }
	        }

	    });
	    
	    userMeta.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // TODO Auto-generated method stub
	            if(userMeta.isSelected()) {
	            	textAreaNo = 2;
	                scrollBar1.setVisible(false);
	                scrollBar2.setVisible(true);
	                scrollBar3.setVisible(false);
	                itemMetaCombo.setVisible(true);
	            }
	        }

	    });
	    
	    docMeta.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // TODO Auto-generated method stub
	            if(docMeta.isSelected()) {
	            	textAreaNo = 3;
	                scrollBar1.setVisible(false);
	                scrollBar2.setVisible(false);
	                scrollBar3.setVisible(true);
	            }
	        }

	    });

		
		frame.getContentPane().add(scrollBar1);
		frame.getContentPane().add(scrollBar2);
		frame.getContentPane().add(scrollBar3);
	    frame.getContentPane().add(comboBox);
	    frame.getContentPane().add(itemMetaCombo);
		frame.getContentPane().add(textField);
		frame.getContentPane().add(btnAddMeta);
		frame.getContentPane().add(btnUpdate);
		frame.getContentPane().add(btnSearch);
		frame.getContentPane().add(lblMetadataT);
		frame.getContentPane().add(lblMetadata);
		frame.getContentPane().add(operPanel);
		frame.setVisible(true);
	}
}
