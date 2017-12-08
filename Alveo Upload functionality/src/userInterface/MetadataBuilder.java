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

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import mapping.JSONMappings;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.sf.json.JSONSerializer;
import upload.InitializeMetadata;
import upload.ReadSpreadsheet;
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
import java.util.Stack;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/** A GUI to edit metadata
 * 
 * 
 * @author Simon Lacis
 *
 */


public class MetadataBuilder {
	private HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	//Keeps track of whether item is new or needs to be updated 
	//null is untouched, 1 is updated, 2 is new item
	private HashMap<String, Integer> recItemStatus = new HashMap<String,Integer>();
	// Document metadata stored as <Itemname, HashMap<Documentname, metadata>
	private HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	// Keeps track of names of documents <itemname, ArrayList<documentname>
	private HashMap<String, ArrayList<String>> docNameMap = new HashMap<String, ArrayList<String>>();
	// List of files to be uploaded as documents <Itemname, List<filenameofdocument>>
	private HashMap<String, List<File>> itemFileList = new HashMap<String, List<File>>();
	// Adds JSON files in collection to list of annotations to be uploaded
	private HashMap<String, File> annotationFileList = new HashMap<String, File>();
	// Stores searchable metadata
	private ArrayList<String> searchedMeta;
	// Maps file extension types
	private Map<String, String> fileExtList;
	// Case insensitive map for meta import
	private Map<String, String> META_MAP = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	// Hashmap for imported metadata
	private static HashMap<String, JSONObject> importedMetadata = new HashMap<String, JSONObject>();
	private ArrayList<String> itemNameList = new ArrayList<String>();
	//General Metadata
	private JSONObject graph_v = new JSONObject();
	//Context and details
	private JSONObject context = new JSONObject();
	//Metadata for collection
	private JSONObject collectionMetadata = new JSONObject();
	// Metadata for context
	private JSONObject contextMetadata = new JSONObject();
	public JFrame frame;
	private JTextField textFieldMeta;
	private AutoCompleteDecorator decorator;
	// Metadata type
	private JRadioButton documentMeta, itemMeta;
	// Update all/individual
	private JRadioButton radioIndividual, radioAll;
	private JTextArea reqJson, upAllJson;
	private JComboBox comboBoxMetadata, itemMetaCombo, docMetaCombo;
	private int textAreaNo = 1;
	private int updateAreaNo = 1;
	private BufferedReader br = null;
	private String[] values;
	private JScrollPane scrollTable1, scrollTable2;


	//Table related 
	private String[] colNames = { "Metadata type", "Metadata" };
	private DefaultTableModel model = new DefaultTableModel(colNames, 0);
	private DefaultTableModel modelAll = new DefaultTableModel(colNames, 0);
	private JTable table = new JTable(model);
	private JTable tableAll = new JTable(modelAll);
	private String itemID;
	private String metadataPath = null;

	/**
	 * Create the application.
	 */
	public MetadataBuilder(String path, HashMap<String, String> collectionDetails, String key, 
			Boolean newItem, Boolean itemMD, Boolean collectionMD, Boolean generateColMD, JSONObject metadataMapping) {
		initialize(path, 
				collectionDetails, 
				key, 
				recItemMetadata, 
				recDocMetadata, 
				newItem, 
				itemMD, 
				collectionMD, 
				generateColMD,
				metadataMapping
				);
	}

	// Search through array for relevant metadata name
	private static ArrayList<String> metadataSearch(String[] values, String q){
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
	//Converts a string seperated by commas (i.e. csv data) to a map
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
	private void initialize(String path, 
			HashMap<String, String> collectionDetails,
			String key,
			HashMap<String, JSONObject> recItemMetadata, 
			HashMap<String, HashMap<String, JSONObject>> recDocMetadata,
			Boolean newItem,
			Boolean itemMD,
			Boolean collectionMD,
			Boolean generateColMD,
			JSONObject metadataMapping) {
		this.recItemMetadata = recItemMetadata;
		this.recDocMetadata = recDocMetadata;
		System.out.println(collectionMD);
		contextMetadata = InitializeMetadata.initContext(collectionDetails.get("metadataField"));
		// Get collection metadata
		//		if (collectionMD == true) {
		//			try{
		//				collectionMetadata = (requestToAlveo(key, UploadConstants.CATALOG_URL + collectionDetails.get("collectionName"))).getJSONObject("metadata");
		//			} catch (IOException e) {
		//
		//			}
		//
		//		}

		if (generateColMD == true){
			collectionMetadata = InitializeMetadata.initCollection(collectionDetails.get("metadataField"));
			System.out.println(collectionMetadata);
		}
		// Hash map for unknown file extensions
		fileExtList = new HashMap<String, String>();


		//This code is used to get directories/sub-directories and then search for
		//their file types. File types that are not in the list of known file extensions
		//Will be populated and can be specified by the user, or will otherwise be set to
		//"Other" by default
		//An ArrayList containing JSONObjects, which contains item level metadata, will be
		//populated and can be changed by the user in the editor.

		//If option set to update existing item metadata is true
		if (itemMD) {
			getItemMeta(collectionDetails.get("collectionName"), key);
		}
		// Read in files based on document filenames
		// NOTE: try and add document metadata to item, if fails, create the item
		if (newItem) {
			//Get sub-directories
			ArrayList<File> files = new ArrayList<File>();
			listf(path, files);
			// Get the extensions to be used when scanning for metadata
			String[] extensionsToScan = metadataMapping.get("extensions").toString().split(",");

			String delimeter = metadataMapping.get("delim").toString();
			for (File file: files){

				int first = 1;

				//Define check variable for if metadata has been added from filename or not
				Boolean fileMetaAdded = false;
				//				itemFileList.put(docID, filesList);
				// Set Item name to specified filename portions
				itemID = "";
				String docName = file.getName();
				String docNameNoExt = FilenameUtils.removeExtension(docName);
				System.out.println(docName);
				String[] splitDoc = docNameNoExt.split("\\" + delimeter);
				System.out.println(splitDoc);
				if ( (Boolean) metadataMapping.get("useFirst")) {
					itemID = itemID + splitDoc[0];
				}
				if ( (Boolean) metadataMapping.get("useSecond")) {
					itemID = itemID + delimeter + splitDoc[1];
				}
				if ( (Boolean) metadataMapping.get("useThird")) {
					itemID = itemID + delimeter + splitDoc[2];
				}
				if ( (Boolean) metadataMapping.get("useForth")) {
					itemID = itemID + delimeter + splitDoc[3];
				}
				if (!itemNameList.contains(itemID)) {
					itemNameList.add(itemID);
				}
				// extent of file in bytes
				float fileBytes = file.length();
				// add name of document to list
				//				docNameList.add(docName);
				// get file extension of document
				String fileExt = "." + FilenameUtils.getExtension(file.getAbsolutePath());
				// check if document extension is in list of known extensions
				if (UploadConstants.EXT_MAP.get(fileExt ) == null) {
					fileExtList.put(fileExt, "Other");
				}

				System.out.println(recItemMetadata);

				//Check if file is JSON annotation file
				if ((FilenameUtils.getExtension(file.getAbsolutePath()).equals("json"))) {
					annotationFileList.put(itemID, file);
				} else {
					//Populating metadata on ITEM level 
					System.out.println("contains id?:" + !recItemMetadata.containsKey(itemID) + " correct ext?:" + Arrays.asList(extensionsToScan).contains(fileExt) );
					if (!recItemMetadata.containsKey(itemID) && Arrays.asList(extensionsToScan).contains(fileExt.substring(1)))
					{
						//Add metadata from filename
						recItemStatus.put(itemID, 2);
						JSONObject itemMetaJSON = InitializeMetadata.initItem(itemID);
						String firstMeta = ((JSONObject) metadataMapping.get("mapping")).get("first").toString();
						String secondMeta = ((JSONObject) metadataMapping.get("mapping")).get("second").toString();
						String thirdMeta = ((JSONObject) metadataMapping.get("mapping")).get("third").toString();
						String forthMeta = ((JSONObject) metadataMapping.get("mapping")).get("fourth").toString();
						if (!(firstMeta.equals(""))){
							String[] multipleUse = firstMeta.split(",");
							for (String metaTerm: multipleUse){
								itemMetaJSON.put(metaTerm, splitDoc[0]);
							}

						}
						if (!(secondMeta.equals(""))){
							String[] multipleUse = secondMeta.split(",");
							for (String metaTerm: multipleUse){
								itemMetaJSON.put(metaTerm, splitDoc[1]);
							}

						}
						if (!(thirdMeta.equals(""))){
							String[] multipleUse = thirdMeta.split(",");
							for (String metaTerm: multipleUse){
								itemMetaJSON.put(metaTerm, splitDoc[2]);
							}

						}
						if (!(forthMeta.equals(""))){
							String[] multipleUse = forthMeta.split(",");
							for (String metaTerm: multipleUse){
								itemMetaJSON.put(metaTerm, splitDoc[3]);
							}

						}
						recItemMetadata.put(itemID, itemMetaJSON);
						System.out.println("made it!" + recItemMetadata.toString());
						//					recDocMetadata.put(docID, InitializeMetadata.initItem(docID));
					}
					// Check if the document metadata contains the item and add it + the doc metadata if it doesnt
					if (recDocMetadata.containsKey(itemID)){
						recDocMetadata.get(itemID).put(docName, InitializeMetadata.initRec(itemID, docName, 
								fileBytes, fileExt));
					} else {
						HashMap<String, JSONObject> innerJSON = new HashMap<String, JSONObject>();
						innerJSON.put(docName, InitializeMetadata.initRec(itemID, docName, 
								fileBytes, fileExt));
						recDocMetadata.put(itemID, innerJSON);
					}
					// Add document name to document name map for combobox
					if (docNameMap.containsKey(itemID)){
						docNameMap.get(itemID).add(docName);
					} else {
						ArrayList<String> innerString = new ArrayList<String>();
						innerString.add(docName);
						docNameMap.put(itemID, innerString);
					}
					// Keep track of file for documents
					if (itemFileList.containsKey(itemID)){
						itemFileList.get(itemID).add(file);
					} else {
						ArrayList<File> innerFile = new ArrayList<File>();
						innerFile.add(file);
						itemFileList.put(itemID, innerFile);
					}
				}

			}
			// Check if metadata excel file exists and import
			if (metadataPath != null) {
				ReadSpreadsheet.readMeta(metadataPath, 
						"Recordings", 
						ReadSpreadsheet.getMap(ReadSpreadsheet.MAPPING,"Mapping", META_MAP), 
						collectionDetails.get("metadataField"), 
						(Boolean) metadataMapping.get("useFirst"), 
						(Boolean) metadataMapping.get("useSecond"), 
						(Boolean) metadataMapping.get("useThird"), 
						delimeter,
						importedMetadata);
				// Combine with existing metadata
				for (String itemKey: importedMetadata.keySet()){
					if (recItemMetadata.containsKey(itemKey)) {
						JSONMappings.combineJSONObject(importedMetadata.get(itemKey), recItemMetadata.get(itemKey));
					}
				}
			}
		}

		System.out.println(fileExtList);

		Font font1 = new Font("SansSerif", Font.BOLD, 12);
		frame = new JFrame();
		frame.setSize(850,650);
		frame.setLocationRelativeTo(null);
		//		frame.setBounds(100, 100, 418, 449);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Metadata Builder");

		// ButtonGroup for metadata type

		documentMeta  = new JRadioButton("Document Metadata");

		ButtonGroup operation = new ButtonGroup();
		operation.add(documentMeta);


		JPanel operPanel = new JPanel();
		Border operBorder = BorderFactory.createTitledBorder("Operation");
		operPanel.setBorder(operBorder);

		JRadioButton rdbtnCollectionMetadata = new JRadioButton("Collection Metadata");
		operPanel.add(rdbtnCollectionMetadata);
		operation.add(rdbtnCollectionMetadata);
		rdbtnCollectionMetadata.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(rdbtnCollectionMetadata.isSelected()) {
					textAreaNo = 4;
					jsonToTable(JSONObject.fromObject(collectionMetadata));
					table.repaint();
				}

			}

		});

		itemMeta  = new JRadioButton("Item Metadata");
		operation.add(itemMeta);
		operPanel.add(itemMeta);


		//Label for Alveo Item
		JLabel lblAlveoItem = new JLabel("Alveo Item");
		lblAlveoItem.setFont(font1);
		lblAlveoItem.setBounds(50, 270, 311, 23);
		lblAlveoItem.setVisible(false);

		//Label for Alveo Doc
		JLabel lblAlveoDocument = new JLabel("Alveo Document");
		lblAlveoDocument.setFont(font1);
		lblAlveoDocument.setBounds(50, 370, 311, 23);
		lblAlveoDocument.setVisible(false);

		itemMeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(itemMeta.isSelected()) {
					lblAlveoDocument.setVisible(true);
					lblAlveoItem.setVisible(true);
					textAreaNo = 3;
					itemMetaCombo.setVisible(true);
					docMetaCombo.setVisible(true);
					jsonToTable(recItemMetadata.get(itemMetaCombo.getSelectedItem()));
				}

			}

		});
		operPanel.add(documentMeta);
		operPanel.setBounds(50, 100, 200, 150);

		// ButtonGroup for Update Individual Item/Update All

		radioIndividual  = new JRadioButton("Individual");
		radioAll= new JRadioButton("All");

		ButtonGroup uploadGroup = new ButtonGroup();
		uploadGroup.add(radioIndividual);
		uploadGroup.add(radioAll);
		radioIndividual.setSelected(true);

		JPanel operPanelUpload = new JPanel();
		Border uploadBorder = BorderFactory.createTitledBorder("Update");
		operPanelUpload.setBorder(uploadBorder);
		operPanelUpload.add(radioIndividual);
		operPanelUpload.add(radioAll);
		operPanelUpload.setBounds(50, 450, 200, 100);

		// JComboBox for metadata type selection
		try{
			br = new BufferedReader(new FileReader("data" + File.separator + "metadatatypes.csv"));
			String line = null;
			line = br.readLine();
			values = line.split(",");
			Arrays.sort(values);
			comboBoxMetadata = new JComboBox(new DefaultComboBoxModel(values));
			comboBoxMetadata.setEditable(true);
			comboBoxMetadata.setBounds(34,50,281,30);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Create an ActionListener for the JComboBox component.
		//
		comboBoxMetadata.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				JComboBox comboBoxMetadata = (JComboBox) event.getSource();
				Object selected = comboBoxMetadata.getSelectedItem();
				System.out.println("Selected Item  = " + selected);
				String command = event.getActionCommand();
				System.out.println("Action Command = " + command);
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
					//Document
					if (newItem){
						DefaultComboBoxModel model = new DefaultComboBoxModel(docNameMap.get(selected.toString()).toArray());
						docMetaCombo.setModel( model );
					}

					if (textAreaNo ==2){

						try {
							jsonToTable(recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
									get(docMetaCombo.getSelectedItem().toString()));
							table.repaint();
						} catch (java.lang.NullPointerException e) {
							JOptionPane.showMessageDialog(null,"No document metadata found");
						}

						//                 System.out.println(recItemMetadata.get(selected).toString());
					} else if (textAreaNo ==3){
						//Load into table
						try {
							jsonToTable(recItemMetadata.get(selected));
						} catch (java.lang.NullPointerException e) {
							JOptionPane.showMessageDialog(null,"No item metadata found. Check readable file extensions"
									+ "in filename metadata");
						}

						//                 System.out.println(recItemMetadata.get(selected).toString());
					}
				}
			}
		});

		// Document metadata combo-box
		if (newItem){
			System.out.println(itemMetaCombo.getSelectedItem());
			docMetaCombo = new JComboBox(new DefaultComboBoxModel(docNameMap.get(itemMetaCombo.getSelectedItem().toString()).toArray()));
		} else {
			docMetaCombo = new JComboBox(new DefaultComboBoxModel());
		}
		docMetaCombo.setBounds(50,400,200,30);
		docMetaCombo.setVisible(false);

		docMetaCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JComboBox docMetaCombo = (JComboBox) event.getSource();

				//
				// Print the selected items and the action command.
				//
				Object selected = docMetaCombo.getSelectedItem();
				System.out.println("Selected Item  = " + selected);
				String command = event.getActionCommand();
				System.out.println("Action Command = " + command);

				//
				// Detect whether the action command is "comboBoxEdited"
				// or "comboBoxChanged"
				//
				if ("comboBoxChanged".equals(command)) {
					//Document 
					if (textAreaNo ==2){
						jsonToTable(recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
								get(docMetaCombo.getSelectedItem().toString()));

						//                 System.out.println(recItemMetadata.get(selected).toString());
					} else if (textAreaNo ==3){


					}
				}
			}
		});




		// Text field for adding metadata info
		textFieldMeta = new JTextField();
		textFieldMeta.setFont(font1);
		textFieldMeta.setBounds(420, 50, 271, 30);
		textFieldMeta.setColumns(20);

		// Button to add metadata
		JButton btnAddMeta = new JButton("Add");
		btnAddMeta.setFont(font1);
		btnAddMeta.setBounds(680, 50, 80, 30);


		// Button to update
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (newItem || itemMD){
					String selectedItem = itemMetaCombo.getSelectedItem().toString();

					if (textAreaNo == 1){
						fileExtList = strtoHash(reqJson.getText());
						System.out.print(fileExtList);
						// Update selected document
					} else if (updateAreaNo == 1 && textAreaNo == 2){
						recDocMetadata.get(selectedItem).
						put(docMetaCombo.getSelectedItem().toString(), getTableData(table));
						// Update all documents
					} else if (updateAreaNo == 2 && textAreaNo == 2){
						for (String key : recDocMetadata.get(selectedItem).keySet()) {
							System.out.println("Test key: " + key);
							JSONObject tempItem = recDocMetadata.get(selectedItem).get(key);
							recDocMetadata.get(selectedItem).put(key, addTableToJSONObject(tableAll, tempItem));
						} 
						// Update selected item
					}else if (updateAreaNo == 1 && textAreaNo == 3){
						recItemMetadata.put(selectedItem,
								getTableData(table));
						if (recItemStatus.get(selectedItem) == null){
							recItemStatus.put(selectedItem, 1);
						}
						// Update all items
					}else if (updateAreaNo == 2 && textAreaNo == 3){
						for (String key : recItemMetadata.keySet()) {
							JSONObject tempItem = recItemMetadata.get(key);
							recItemMetadata.put(key, addTableToJSONObject(tableAll, tempItem));
							if (recItemStatus.get(key) == null){
								recItemStatus.put(key, 1);
							}
						}

					} // Update context
					else if (textAreaNo == 5){
						System.out.println("Update context");
						contextMetadata = getTableData(table);
					}
					// Update collection
				} else if (textAreaNo == 4){
					collectionMetadata = getTableData(table);

				}

			}
		});


		btnUpdate.setFont(font1);
		btnUpdate.setBounds(680, 488, 88, 30);


		// Button to delete selected row
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.removeRow(table.getSelectedRow());
			}
		});

		btnDelete.setFont(font1);
		btnDelete.setBounds(680, 100, 100, 30);
		// Button to update all
		JButton btnContinue = new JButton("Continue");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WindowExportUpload windowUpload = new WindowExportUpload(path, 
						collectionDetails, 
						key, 
						recItemMetadata,
						recItemStatus,
						itemFileList,
						recDocMetadata, 
						newItem, 
						itemMD, 
						collectionMD, 
						generateColMD,
						collectionMetadata,
						annotationFileList,
						contextMetadata);
				windowUpload.frame.setVisible(true);
				// Listener to get built Metadata



			}


		});
		btnContinue.setFont(font1);
		btnContinue.setBounds(681, 524, 98, 30);

		// Button to search for metadata
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selected = comboBoxMetadata.getSelectedItem().toString();
				searchedMeta = metadataSearch(values,selected.toString());
				System.out.println(searchedMeta.toArray());
				DefaultComboBoxModel model = new DefaultComboBoxModel( searchedMeta.toArray());
				comboBoxMetadata.setModel( model );

			}

		});

		btnSearch.setFont(font1);
		btnSearch.setBounds(315, 50, 100, 30);


		//Label for Metadata type
		JLabel lblMetadataT = new JLabel("Metadata Type");
		lblMetadataT.setFont(font1);
		lblMetadataT.setBounds(50, 25, 311, 23);

		//Label for Metadata
		JLabel lblMetadata = new JLabel("Metadata");
		lblMetadata.setFont(font1);
		lblMetadata.setBounds(420, 25, 311, 23);


		//Scrollpane for metadata editing
		scrollTable1 = new JScrollPane(table);
		scrollTable1.setBounds(268, 99, 400, 250);

		//ScrollPane for adding to all metadata
		scrollTable2 = new JScrollPane(tableAll);
		scrollTable2.setBounds(268, 369, 400, 200);


		//Button to add metadata to box
		btnAddMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] array = {(String) comboBoxMetadata.getSelectedItem(), (String) textFieldMeta.getText()};
				if (updateAreaNo ==1) {
					if (table.getSelectedRow() == -1) {
						model.addRow(array);
					} else {
						model.insertRow(table.getSelectedRow(), array);
					}
				} else {
					if (tableAll.getSelectedRow() == -1) {
						modelAll.addRow(array);
					} else {
						modelAll.insertRow(tableAll.getSelectedRow(), array);
					}
				}
			}


		});

		documentMeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(documentMeta.isSelected()) {
					textAreaNo = 2;
					lblAlveoDocument.setVisible(true);
					lblAlveoItem.setVisible(true);
					itemMetaCombo.setVisible(true);
					docMetaCombo.setVisible(true);
					jsonToTable(recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
							get(docMetaCombo.getSelectedItem().toString()));
				}
			}

		});

		radioIndividual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(radioIndividual.isSelected()) {
					updateAreaNo = 1;
				}
			}

		});

		radioAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(radioAll.isSelected()) {
					updateAreaNo = 2;
				}
			}

		});


		frame.getContentPane().add(scrollTable1);
		frame.getContentPane().add(scrollTable2);
		frame.getContentPane().add(comboBoxMetadata);
		frame.getContentPane().add(itemMetaCombo);
		frame.getContentPane().add(docMetaCombo);
		frame.getContentPane().add(textFieldMeta);
		frame.getContentPane().add(btnAddMeta);
		frame.getContentPane().add(btnDelete);
		frame.getContentPane().add(btnUpdate);
		frame.getContentPane().add(btnContinue);
		frame.getContentPane().add(btnSearch);
		frame.getContentPane().add(lblMetadataT);
		frame.getContentPane().add(lblMetadata);
		frame.getContentPane().add(lblAlveoDocument);
		frame.getContentPane().add(lblAlveoItem);
		frame.getContentPane().add(operPanel);

		JRadioButton rdbtnContext = new JRadioButton("Context");
		operPanel.add(rdbtnContext);
		operation.add(rdbtnContext);
		rdbtnContext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(rdbtnContext.isSelected()) {
					textAreaNo = 5;
					jsonToTable(JSONObject.fromObject(contextMetadata));
					table.repaint();
				}

			}

		});
		frame.getContentPane().add(operPanelUpload);
		frame.setVisible(true);
	}

	//Table related auxillary functions

	// Get data from table to JSONObject
	// Based off code from stackoverflow
	public static JSONObject getTableData (JTable table) {
		JSONObject dataFromTable = new JSONObject();
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		int i = 0;
		//	    Object[][] tableData = new Object[nRow][nCol];
		for (; i < nRow ; i++){
			//For multi-layered json
			if (i+1 < nRow && Character.toLowerCase(dtm.getValueAt(i+1,0).toString().charAt(0)) == '-'  ){
				JSONObject innerJSON = getTableDataMulti(dtm, i+1);
				dataFromTable.element((String)dtm.getValueAt(i,0), innerJSON);
				i = i + innerJSON.size();
			}
			else {
				dataFromTable.element((String)dtm.getValueAt(i,0), (String)dtm.getValueAt(i,1));
			}
		}
		System.out.println(dataFromTable.toString());
		return dataFromTable;


	}

	// Get data from table, add to previous JSONObject
	// Unlike other function this ADDS to a pre-existing object
	public JSONObject addTableToJSONObject (JTable table, JSONObject prevJSON) {
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		int i = 0 ;
		for (; i < nRow ; i++){
			if (i+1 < nRow && Character.toLowerCase(dtm.getValueAt(i+1,0).toString().charAt(0)) == '-'  ){
				System.out.println("i before: " + i);
				JSONObject innerJSON = getTableDataMulti(dtm, i+1);
				prevJSON.element((String)dtm.getValueAt(i,0), innerJSON);
				i = i + innerJSON.size();
			} else {
				prevJSON.element((String)dtm.getValueAt(i,0), (String)dtm.getValueAt(i,1));
			}
		}
		System.out.println(prevJSON.toString());
		return prevJSON;

	}

	//Helper function for tableToJSON
	//Inner JSON details
	public static JSONObject getTableDataMulti (DefaultTableModel dtm, int i) {
		JSONObject tmpJSON =  new JSONObject();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		for ( ; i < nRow ; i++){
			//For multi-layered json
			if (Character.toLowerCase(dtm.getValueAt(i,0).toString().charAt(0)) == '-' ){
				tmpJSON.element(dtm.getValueAt(i,0).toString().substring(1), (String)dtm.getValueAt(i,1));
			} else {
				break;
			}
		}
		return tmpJSON;
	}

	// Converts JSONObject to JTable
	public void jsonToTable (JSONObject tableData) {
		clearTable();
		for (Object key : tableData.keySet()) {
			String keyN = (String) key;
			System.out.println(keyN);
			System.out.println(tableData.get(keyN));
			Object value = tableData.get(keyN);
			// For inner JSONObject
			if (value instanceof JSONObject) {
				String[] JSONCont = {keyN,""};
				model.addRow(JSONCont);
				for (Object keyI : ((JSONObject) value).keySet()) {
					String valueI = ((JSONObject) value).get(keyI).toString();
					String[] array = {"-" + keyI.toString(), valueI};
					model.addRow(array);
				}	
			} else {
				String[] array = {keyN, value.toString()};
				model.addRow(array);	
			}
		}
	}

	public void clearTable(){
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}


	// Get metadata from Items
	public void getItemMeta(String collectionName, String key) {
		try {
			JSONObject itemMetadata = RequestHelper.requestToAlveo(key, UploadConstants.CATALOG_URL+ "search?metadata=collection_name:" + collectionName);

			String itemNamestemp1 = itemMetadata.get("items").toString();
			String itemNamestemp2 = itemNamestemp1.substring(1, itemNamestemp1.length() - 1);
			List<String> itemNames = Arrays.asList(itemNamestemp2.split(","));
			for (String temp : itemNames) {
				try {
					ArrayList<String> docNameList = new ArrayList<String>();
					JSONObject request = RequestHelper.requestToAlveo(key, temp.substring(1, temp.length()-1));
					JSONObject temp1 = (JSONObject) request.get("alveo:metadata");
					String itemName = temp1.get("dc:identifier").toString();
					recItemMetadata.put(itemName, temp1);
					itemNameList.add(itemName);
					System.out.println(temp1.get("dc:identifier").toString());
					System.out.println(temp1);
					docNameMap.put(itemName, docNameList);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					continue;
				}
			}
			System.out.println(recItemMetadata.toString());





			//		return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Get all files from directory and find metadata
	// based off code from stackoverflow
	public void listf(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			// look for metadata file
			if (file.getName().equals("metadata.xlsx")){
				metadataPath = file.getAbsolutePath();
			}else 
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					listf(file.getAbsolutePath(), files);
				}
		}
	}
}
