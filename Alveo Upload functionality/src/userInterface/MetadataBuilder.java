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
import java.util.Stack;
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
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	//Keeps track of whether item is new or needs to be updated 
	//null is untouched, 1 is updated, 2 is new item
	HashMap<String, Integer> recItemStatus = new HashMap<String,Integer>();
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	HashMap<String, ArrayList<String>> docNameMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, List<File>> itemFileList = new HashMap<String, List<File>>();
	ArrayList<String> itemNameList = new ArrayList<String>();
	JFrame frame;
	private JTextField textField;
	AutoCompleteDecorator decorator;
	// Metadata type
	JRadioButton documentMeta, reqMeta, itemMeta;
	// Update all/individual
	JRadioButton radioIndividual, radioAll;
	JTextArea reqJson, upAllJson;
	private JComboBox comboBox, itemMetaCombo, docMetaCombo;
	int textAreaNo = 1;
	int updateAreaNo = 1;
	BufferedReader br = null;
	String[] values;
	JScrollPane scrollBar1, scrollBar4, scrollTable1, scrollTable2;
	ArrayList<String> searchedMeta;
	Map<String, String> fileExtList;
	//General Metadata
	JSONObject graph_v = new JSONObject();
	//Context and details
	JSONObject context = new JSONObject();
	//Table related 
	private String[] colNames = { "Metadata type", "Metadata" };
	private DefaultTableModel model = new DefaultTableModel(colNames, 0);
	private DefaultTableModel modelAll = new DefaultTableModel(colNames, 0);
	private JTable table = new JTable(model);
	private JTable tableAll = new JTable(modelAll);
	JSONObject collectionMetadata = new JSONObject();

	/**
	 * Create the application.
	 */
	public MetadataBuilder(String path, HashMap<String, String> collectionDetails, String key, 
			Boolean newItem, Boolean itemMD, Boolean collectionMD, Boolean generateColMD) {
		initialize(path, 
				collectionDetails, 
				key, 
				recItemMetadata, 
				recDocMetadata, 
				newItem, 
				itemMD, 
				collectionMD, 
				generateColMD
				);
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
	private void initialize(String path, 
			HashMap<String, String> collectionDetails,
			String key,
			HashMap<String, JSONObject> recItemMetadata, 
			HashMap<String, HashMap<String, JSONObject>> recDocMetadata,
			Boolean newItem,
			Boolean itemMD,
			Boolean collectionMD,
			Boolean generateColMD) {
		this.recItemMetadata = recItemMetadata;
		this.recDocMetadata = recDocMetadata;
		System.out.println(collectionMD);

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
			collectionMetadata = InitializeMetadata.initCollection();
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

		//Populate item metadata into these ArrayLists

		//		ArrayList<JSONObject> finalItemMetadata = new ArrayList<JSONObject>();

		//If option set to update existing item metadata is true
		if (itemMD) {
			getItemMeta(collectionDetails.get("collectionName"), key);
		}
		//If option set to have new items/documents is true
		if (newItem) {
			//Get sub-directories
			List<File> subDirs = CollectionUploadGeneral.getDirs(path);
			for (File subDir: subDirs){
				HashMap<String, JSONObject> docs = new HashMap<String, JSONObject>();
				ArrayList<String> docNameList = new ArrayList<String>();
				int first = 1;
				// Set Item name to name of subDirectory
				String docID = subDir.getName();
				if (!itemNameList.contains(docID)) {
					itemNameList.add(docID);
				}
				//Get all files from sub-directories
				List<File> filesList = CollectionUploadGeneral.listFiles(subDir.toString());
				itemFileList.put(docID, filesList);
				for(File file: filesList){

					//Need first file metadata for item creation so take from here and mark
					//if its the first iteration or not
					float fileBytes = file.length();
					String docName = file.getName();
					docNameList.add(docName);
					String fileExt = "." + FilenameUtils.getExtension(file.getAbsolutePath());
					if (UploadConstants.EXT_MAP.get(fileExt ) == null) {
						fileExtList.put(fileExt, "Other");
					}
					//Populating metadata on ITEM level 

					System.out.println(recItemMetadata);
					docs.put(docName, InitializeMetadata.initRec(docID, docName, 
							fileBytes, fileExt));
					if (!recItemMetadata.containsKey(docID))
					{
						recItemStatus.put(docID, 2);
						recItemMetadata.put(docID, InitializeMetadata.initItem(docID));
						//					recDocMetadata.put(docID, InitializeMetadata.initItem(docID));
					}
				}
				if(!recDocMetadata.containsKey(docID)){
					recDocMetadata.put(docID, docs);
				}
				docNameMap.put(docID, docNameList);
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

		documentMeta  = new JRadioButton("Document Metadata");
		reqMeta  = new JRadioButton("File Extensions");

		ButtonGroup operation = new ButtonGroup();
		operation.add(reqMeta);
		operation.add(documentMeta);
		reqMeta.setSelected(true);

		JPanel operPanel = new JPanel();
		Border operBorder = BorderFactory.createTitledBorder("Operation");
		operPanel.setBorder(operBorder);
		operPanel.add(reqMeta);

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
					scrollBar1.setVisible(false);
					//	                scrollBar3.setVisible(true);
					itemMetaCombo.setVisible(true);
					docMetaCombo.setVisible(true);
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
			br = new BufferedReader(new FileReader("csv/metadatatypes.csv"));
			String line = null;
			line = br.readLine();
			values = line.split(",");
			Arrays.sort(values);
			comboBox = new JComboBox(new DefaultComboBoxModel(values));
			comboBox.setEditable(true);
			comboBox.setBounds(34,50,281,30);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Create an ActionListener for the JComboBox component.
		//
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				JComboBox comboBox = (JComboBox) event.getSource();
				Object selected = comboBox.getSelectedItem();
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
						jsonToTable(recItemMetadata.get(selected));

						//                 System.out.println(recItemMetadata.get(selected).toString());
					}
				}
			}
		});

		// Document metadata combo-box
		if (newItem){
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
		textField = new JTextField();
		textField.setFont(font1);
		textField.setBounds(420, 50, 271, 30);
		textField.setColumns(20);

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
						// Update collection
					}
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
						collectionMetadata);
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
				String selected = comboBox.getSelectedItem().toString();
				searchedMeta = metadataSearch(values,selected.toString());
				System.out.println(searchedMeta.toArray());
				DefaultComboBoxModel model = new DefaultComboBoxModel( searchedMeta.toArray());
				comboBox.setModel( model );

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

		//TextAreas for Metadata

		//Required
		reqJson = new JTextArea();
		reqJson.setText(fileExtList.toString());
		reqJson.setLineWrap(true);
		scrollBar1 = new JScrollPane(reqJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar1.setBounds(300, 100, 400, 250);

		//Update all
		upAllJson = new JTextArea();
		upAllJson.setText(fileExtList.toString());
		upAllJson.setLineWrap(true);
		scrollBar4 = new JScrollPane(upAllJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar4.setBounds(300, 370, 400, 200);



		//Button to add metadata to box
		btnAddMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] array = {(String) comboBox.getSelectedItem(), (String) textField.getText()};
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



		// Listeners for radio buttons
		reqMeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(reqMeta.isSelected()) {
					textAreaNo = 1;
					lblAlveoDocument.setVisible(false);
					lblAlveoItem.setVisible(false);
					scrollBar1.setVisible(true);
					//					scrollBar2.setVisible(false);
					//					scrollBar3.setVisible(false);
					itemMetaCombo.setVisible(false);
					docMetaCombo.setVisible(false);
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
					scrollBar1.setVisible(false);
					itemMetaCombo.setVisible(true);
					docMetaCombo.setVisible(true);
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
		//		frame.getContentPane().add(scrollBar4);
		frame.getContentPane().add(comboBox);
		frame.getContentPane().add(itemMetaCombo);
		frame.getContentPane().add(docMetaCombo);
		frame.getContentPane().add(textField);
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
		frame.getContentPane().add(operPanelUpload);
		frame.setVisible(true);
	}

	//Table related auxillary functions

	// Get data from table to JSONObject
	//REWRITE THIS CODE
	public JSONObject getTableData (JTable table) {
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
		//	    Object[][] tableData = new Object[nRow][nCol];
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
	public JSONObject getTableDataMulti (DefaultTableModel dtm, int i) {
		JSONObject tmpJSON =  new JSONObject();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		//	    Object[][] tableData = new Object[nRow][nCol];
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
			//based on you key types
			String keyN = (String) key;
			System.out.println(keyN);
			System.out.println(tableData.get(keyN));
			Object value = tableData.get(keyN);
			// For inner JSONObject
			if (value instanceof JSONObject) {
				String[] JSONCont = {keyN,""};
				model.addRow(JSONCont);
				System.out.println("value is json!!!!");
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
			JSONObject itemMetadata = requestToAlveo(key, UploadConstants.CATALOG_URL+ "search?metadata=collection_name:" + collectionName);

			String itemNamestemp1 = itemMetadata.get("items").toString();
			String itemNamestemp2 = itemNamestemp1.substring(1, itemNamestemp1.length() - 1);
			List<String> itemNames = Arrays.asList(itemNamestemp2.split(","));
			for (String temp : itemNames) {
				try {
					ArrayList<String> docNameList = new ArrayList<String>();
					JSONObject request = requestToAlveo(key, temp.substring(1, temp.length()-1));
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

	// make JSON request to Alveo server
	public static JSONObject requestToAlveo(String key, String appendToUrl) throws IOException {
		JSONObject tmpJSON =  new JSONObject();
		String serviceURL =	appendToUrl;
		URL myURL = new URL(serviceURL);
		HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("X-API-KEY", key);
		conn.setRequestProperty("Accept", "application/json");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		JSONObject metadata = readResponse(conn);
		return metadata;
	}


	public static JSONObject readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();
		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();
		System.out.println(response.toString());
		JSONObject JSONresponse = JSONObject.fromObject(response.toString());
		System.out.println(JSONresponse.toString());
		return JSONresponse;
	}
}
