package userInterface;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private String path = null;
	private String absolupath;
	private String filename;
	private JTextField textField_1;
	private JTextField textField;
	HashMap<String, Integer> licenseList = new HashMap<String, Integer>();





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
		frame.setBounds(100, 100, 400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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



		JRadioButton publicCollection, privateCollection;
		publicCollection  = new JRadioButton("Public Collection");
		privateCollection  = new JRadioButton("Private Collection");
		ButtonGroup operation = new ButtonGroup();
		operation.add(privateCollection);
		operation.add(publicCollection);
		privateCollection.setSelected(true);

		JPanel operPanel = new JPanel();
		Border operBorder = BorderFactory.createTitledBorder("Collection Type");
		operPanel.setBorder(operBorder);
		operPanel.add(privateCollection);
		operPanel.add(publicCollection);
		operPanel.setBounds(77, 194, 200, 96);
		frame.getContentPane().add(operPanel);

		JButton btnCreateNewCollection = new JButton("Create New Collection");
		btnCreateNewCollection.setBounds(64, 302, 262, 36);
		frame.getContentPane().add(btnCreateNewCollection);

		JLabel lblLicense = new JLabel("License");
		lblLicense.setBounds(41, 72, 130, 15);
		frame.getContentPane().add(lblLicense);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(180, 24, 186, 29);
		frame.getContentPane().add(textField_1);

		JLabel lblCollectionName = new JLabel("Collection Name");
		lblCollectionName.setBounds(41, 38, 130, 15);
		frame.getContentPane().add(lblCollectionName);

		JComboBox comboBox = new JComboBox(new DefaultComboBoxModel(licenseList.keySet().toArray()));
		comboBox.setBounds(180, 58, 183, 29);
		frame.getContentPane().add(comboBox);


		JTextField Filechooser = new JTextField();
		Filechooser.setBounds(167, 140, 200, 30);
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
		btnAdd.setBounds(41, 143, 114, 23);
		frame.getContentPane().add(btnAdd);

		JLabel lblMetadataPrefix = new JLabel("Metadata Prefix");
		lblMetadataPrefix.setBounds(41, 106, 130, 15);
		frame.getContentPane().add(lblMetadataPrefix);

		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(180, 99, 186, 29);
		frame.getContentPane().add(textField);

	}
}