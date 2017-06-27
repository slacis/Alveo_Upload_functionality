package userInterface;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;


import net.sf.json.JSONObject;
import javax.swing.JComboBox;

/** Window for creating new collection
 * 
 *
 * @author Simon Lacis
 *
 */

public class WindowCreateCollection {

	JFrame frame;
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	JSONObject metadataMapping = new JSONObject();
	private String path = null;
	private String absolupath;
	private String filename;
	private JTextField textField_1;
	private JTextField prefix;
	HashMap<String, Integer> licenseList = new HashMap<String, Integer>();
	String privateField;
	Boolean fileMetadataSet = false;





	/**
	 * Create the application.
	 * @param key 
	 */
	public WindowCreateCollection(String key) {		
		initialize(key);
	}

	/**
	 * Initialize the contents of the frame.
	 * @param key 
	 */
	private void initialize(String key) {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 500);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		licenseList.put("AusTalk Terms of Use", 1);
		licenseList.put("Creative Commons v3.0 BY-NC",2);
		licenseList.put("Creative Commons v3.0 BY-NC-ND",3);
		licenseList.put("AVOZES Non-Commercial (Academic) License",4);
		licenseList.put("Creative Commons v3.0 BY-ND",5);
		licenseList.put("AusNC Terms of Use",6);
		licenseList.put("Creative Commons v3.0 BY-SA",7);
		licenseList.put("LLC Terms of Use",8);
		licenseList.put("ClueWeb Terms of Use",9);
		licenseList.put("Creative Commons v3.0 BY",10);
		licenseList.put("Creative Commons v3.0 BY-NC-SA",11);
		licenseList.put("PARADISEC Conditions of Access",12);
		privateField = "true";



		JRadioButton publicCollection, privateCollection;
		publicCollection  = new JRadioButton("Public Collection");
		publicCollection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(publicCollection.isSelected()) {
					privateField = "false";
				}

			}
		});
		privateCollection  = new JRadioButton("Private Collection");
		privateCollection.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(privateCollection.isSelected()) {
				privateField = "true";
			}

		}
		});
		
		ButtonGroup operation = new ButtonGroup();
		operation.add(privateCollection);
		operation.add(publicCollection);
		privateCollection.setSelected(true);

		JPanel operPanel = new JPanel();
		Border operBorder = BorderFactory.createTitledBorder("Collection Type");
		operPanel.setBorder(operBorder);
		operPanel.add(privateCollection);
		operPanel.add(publicCollection);
		operPanel.setBounds(125, 263, 200, 78);
		frame.getContentPane().add(operPanel);
		
		JComboBox comboBox = new JComboBox(new DefaultComboBoxModel(licenseList.keySet().toArray()));
		comboBox.setBounds(228, 109, 183, 29);
		frame.getContentPane().add(comboBox);

		JButton btnCreateNewCollection = new JButton("Create New Collection");
		btnCreateNewCollection.setBounds(112, 353, 262, 36);
		btnCreateNewCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (path == null || fileMetadataSet == false){
				//Error message : Null Path 
				JOptionPane.showMessageDialog(null, "Please select path and set file metadata", "Error Message", JOptionPane.INFORMATION_MESSAGE);
				} else {
				HashMap<String, String> collectionDetails = new HashMap<String,String>();
						collectionDetails.put("collectionName",textField_1.getText());
						collectionDetails.put("metadataField",prefix.getText());
						collectionDetails.put("private", privateField);
						collectionDetails.put("license", licenseList.get(comboBox.getSelectedItem()).toString());
				try {
				MetadataBuilder builder = new MetadataBuilder(path, collectionDetails, key, true, false, false, true, metadataMapping);
				builder.frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Problem occurred. Please ensure delimeter matches", "Error Message", JOptionPane.INFORMATION_MESSAGE);
				}

				}
	
			}
		});
		frame.getContentPane().add(btnCreateNewCollection);

		JLabel lblLicense = new JLabel("License");
		lblLicense.setBounds(89, 123, 130, 15);
		frame.getContentPane().add(lblLicense);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(228, 75, 186, 29);
		frame.getContentPane().add(textField_1);

		JLabel lblCollectionName = new JLabel("Collection Name");
		lblCollectionName.setBounds(89, 89, 130, 15);
		frame.getContentPane().add(lblCollectionName);


		JTextField Filechooser = new JTextField();
		Filechooser.setBounds(215, 191, 200, 30);
		frame.getContentPane().add(Filechooser);
		Filechooser.setColumns(10);
		JButton btnAdd = new JButton("Directory");
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
		btnAdd.setBounds(89, 194, 114, 23);
		frame.getContentPane().add(btnAdd);

		JLabel lblMetadataPrefix = new JLabel("Metadata Prefix");
		lblMetadataPrefix.setBounds(89, 157, 130, 15);
		frame.getContentPane().add(lblMetadataPrefix);

		prefix = new JTextField();
		prefix.setColumns(10);
		prefix.setBounds(228, 150, 186, 29);
		frame.getContentPane().add(prefix);
		
		JButton btnFilenameMetadata = new JButton("Filename Metadata");
		btnFilenameMetadata.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileMetadataSet = true;
				WindowFileMetadata fileMeta = new WindowFileMetadata(metadataMapping);
				fileMeta.frame.setVisible(true);
				// Listener to get built Metadata
				fileMeta.frame.addWindowListener(new WindowAdapter() {
					  @Override
					  public void windowClosing(WindowEvent e) {
						  metadataMapping = fileMeta.metadataMapping;
						  System.out.println(metadataMapping.toString());
					  }
					 
					});
				
	
			}
		});
		btnFilenameMetadata.setBounds(150, 231, 200, 23);
		frame.getContentPane().add(btnFilenameMetadata);

	}
}
