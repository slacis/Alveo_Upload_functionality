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
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	JFrame frame;
	private JTextField textField;
	AutoCompleteDecorator decorator;
	JRadioButton documentMeta, reqMeta, itemMeta;
	JTextArea userJson, reqJson, docJson, upAllJson;
	private JComboBox comboBox, itemMetaCombo, docMetaCombo;
	int textAreaNo = 1;
	BufferedReader br = null;
	String[] values;
	JScrollPane scrollBar1, scrollBar2, scrollBar3, scrollBar4, scrollTable1;
	ArrayList<String> searchedMeta;
	Map<String, String> fileExtList;
	//General Metadata
	JSONObject graph_v = new JSONObject();
	//Context and details
	JSONObject context = new JSONObject();
	//Table related 
	private String[] colNames = { "Metadata type", "Metadata" };
	private DefaultTableModel model = new DefaultTableModel(colNames, 0);
	private JTable table = new JTable(model);

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
	public MetadataBuilder(String path, 
			HashMap<String, JSONObject> recItemMetadata, 
			HashMap<String, HashMap<String, JSONObject>> recDocMetadata) {
		initialize(path, recItemMetadata, recDocMetadata);
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
			HashMap<String, JSONObject> recItemMetadata, 
			HashMap<String, HashMap<String, JSONObject>> recDocMetadata) {
		this.recItemMetadata = recItemMetadata;
		this.recDocMetadata = recDocMetadata;
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
		HashMap<String, ArrayList<String>> docNameMap = new HashMap<String, ArrayList<String>>();
		//Get sub-directories
		List<File> subDirs = CollectionUploadGeneral.getDirs(path);
		for (File subDir: subDirs){
			HashMap<String, JSONObject> docs = new HashMap<String, JSONObject>();
			ArrayList<String> docNameList = new ArrayList<String>();
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
				docNameList.add(docName);
				String fileExt = "." + FilenameUtils.getExtension(file.getAbsolutePath());
				if (UploadConstants.EXT_MAP.get(fileExt ) == null) {
					fileExtList.put(fileExt, "Other");
				}
				//Populating metadata on ITEM level 
				if (!recItemMetadata.containsKey(docID+"_doc")){
					System.out.println(recItemMetadata);
					docs.put(docName, InitializeMetadata.initRec(docID, docName, 
							fileBytes, fileExt));
					recItemMetadata.put(docID + "_item", InitializeMetadata.initItem(docID));
//					recDocMetadata.put(docID, InitializeMetadata.initItem(docID));
				}
			}
			if(!recDocMetadata.containsKey(docID)){
			recDocMetadata.put(docID, docs);
			}
			docNameMap.put(docID, docNameList);
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
		itemMeta  = new JRadioButton("Item Metadata");

		ButtonGroup operation = new ButtonGroup();
		operation.add(reqMeta);
		operation.add(documentMeta);
		operation.add(itemMeta);
		reqMeta.setSelected(true);

		JPanel operPanel = new JPanel();
		Border operBorder = BorderFactory.createTitledBorder("Operation");
		operPanel.setBorder(operBorder);
		operPanel.add(reqMeta);
		operPanel.add(documentMeta);
		operPanel.add(itemMeta);
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
					//Document
					DefaultComboBoxModel model = new DefaultComboBoxModel(docNameMap.get(selected.toString()).toArray());
					docMetaCombo.setModel( model );
					
					if (textAreaNo ==2){
//						String tmpStr = recItemMetadata.get(selected + "_doc").toString().
//								replaceAll(",", ",\n");
//						userJson.setText(tmpStr.substring(1, tmpStr.length()-1) + ",\n");
						//Load into table
						jsonToTable(recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
								get(docMetaCombo.getSelectedItem().toString()));
						table.repaint();

						//                 System.out.println(recItemMetadata.get(selected).toString());
					} else if (textAreaNo ==3){
//						String tmpStr = recItemMetadata.get(selected + "_item").toString().
//								replaceAll(",", ",\n");
//						userJson.setText(tmpStr.substring(1, tmpStr.length()-1) + ",\n");
						//Load into table
						jsonToTable(recItemMetadata.get(selected + "_item"));

						//                 System.out.println(recItemMetadata.get(selected).toString());
					}
				}
			}
		});
		
		// Document metadata combo-box
		docMetaCombo = new JComboBox(new DefaultComboBoxModel(docNameMap.get(itemMetaCombo.getSelectedItem().toString()).toArray()));
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
//						String tmpStr = recItemMetadata.get(selected + "_doc").toString().
//								replaceAll(",", ",\n");
//						userJson.setText(tmpStr.substring(1, tmpStr.length()-1) + ",\n");
						//Load into table
//						jsonToTable(recItemMetadata.get(selected + "_doc"));
//						table.repaint();
						jsonToTable(recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
								get(docMetaCombo.getSelectedItem().toString()));

						//                 System.out.println(recItemMetadata.get(selected).toString());
					} else if (textAreaNo ==3){
//						String tmpStr = recItemMetadata.get(selected + "_item").toString().
//								replaceAll(",", ",\n");
//						userJson.setText(tmpStr.substring(1, tmpStr.length()-1) + ",\n");
						//Load into table
//						jsonToTable(recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
//								get(docMetaCombo.getSelectedItem().toString()));

						//                 System.out.println(recItemMetadata.get(selected).toString());
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
				if (textAreaNo == 1){
					fileExtList = strtoHash(reqJson.getText());
					System.out.print(fileExtList);
				} else if (textAreaNo == 2){
					recDocMetadata.get(itemMetaCombo.getSelectedItem().toString()).
					put(docMetaCombo.getSelectedItem().toString(), getTableData(table));
					
				}
				else if (textAreaNo == 3){
					recItemMetadata.put(itemMetaCombo.getSelectedItem().toString() + "_item",
							getTableData(table));

				}


			}
		});
		btnUpdate.setFont(font1);
		btnUpdate.setBounds(700, 500, 80, 30);

		// Button to update all
		JButton btnUpAll = new JButton("All");
		btnUpAll.addActionListener(new ActionListener() {
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
		btnUpAll.setFont(font1);
		btnUpAll.setBounds(700, 535, 80, 30);

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

		//Table for metadata editing
		scrollTable1 = new JScrollPane(table);
		scrollTable1.setBounds(300, 100, 400, 250);


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

		//User
		userJson = new JTextArea();
		userJson.setLineWrap(true);
		scrollBar2 = new JScrollPane(userJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar2.setBounds(300, 100, 400, 250);

		//Doc
		docJson = new JTextArea();
		docJson.setLineWrap(true);
		scrollBar3 = new JScrollPane(docJson, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBar3.setBounds(300, 100, 400, 250);



		//Button to add metadata to box
		btnAddMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (textAreaNo == 1) {
					//				reqJson.append("\"" + comboBox.getSelectedItem().toString() +
					//						"\"" + ":" + " \"" + textField.getText().toString() + "\",\n");
					//				System.out.println(reqJson.getText());
					//				JSONObject jsonObj = JSONObject.fromObject("{" + reqJson.getText() + "}");
					//				System.out.println(jsonObj);
				} else if (textAreaNo == 2 || textAreaNo == 3) {
//					String tmpStr = userJson.getText();
//					userJson.append("\"" + comboBox.getSelectedItem().toString() +
//							"\"" + ":" + " \"" + textField.getText().toString() + "\",\n");
					String[] array = {(String) comboBox.getSelectedItem(), (String) textField.getText()};
					model.addRow(array);
					getTableData(table);

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
					scrollBar1.setVisible(false);
					scrollBar2.setVisible(true);
					scrollBar3.setVisible(false);
					itemMetaCombo.setVisible(true);
					docMetaCombo.setVisible(true);
				}
			}

		});

		itemMeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(itemMeta.isSelected()) {
					textAreaNo = 3;
					scrollBar1.setVisible(false);
					scrollBar2.setVisible(true);
					//	                scrollBar3.setVisible(true);
					itemMetaCombo.setVisible(true);
					docMetaCombo.setVisible(true);
				}

			}

		});


		frame.getContentPane().add(scrollTable1);
		frame.getContentPane().add(scrollBar2);
		frame.getContentPane().add(scrollBar3);
		frame.getContentPane().add(scrollBar4);
		frame.getContentPane().add(comboBox);
		frame.getContentPane().add(itemMetaCombo);
		frame.getContentPane().add(docMetaCombo);
		frame.getContentPane().add(textField);
		frame.getContentPane().add(btnAddMeta);
		frame.getContentPane().add(btnUpdate);
		frame.getContentPane().add(btnUpAll);
		frame.getContentPane().add(btnSearch);
		frame.getContentPane().add(lblMetadataT);
		frame.getContentPane().add(lblMetadata);
		frame.getContentPane().add(operPanel);
		frame.setVisible(true);
	}
	
	//Table related auxillary functions
	
	// Get data from table to array 
	//REWRITE THIS CODE
	public JSONObject getTableData (JTable table) {
		JSONObject dataFromTable = new JSONObject();
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		//	    Object[][] tableData = new Object[nRow][nCol];
		for (int i = 0 ; i < nRow ; i++){
			dataFromTable.element((String)dtm.getValueAt(i,0), (String)dtm.getValueAt(i,1));
		}
		System.out.println(dataFromTable.toString());
		return dataFromTable;


	}
	public void jsonToTable (JSONObject tableData) {
		clearTable();
		for (Object key : tableData.keySet()) {
			//based on you key types
			String keyN = (String) key;
			System.out.println(keyN);
			System.out.println(tableData.get(keyN));
			String value = tableData.get(keyN).toString();
			String[] array = {keyN, value};
			model.addRow(array);	
		}
	}
	public void clearTable(){
		int rowCount = model.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
}
