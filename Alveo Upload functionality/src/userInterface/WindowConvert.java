package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;



import net.sf.json.JSONObject;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.xml.stream.XMLStreamException;

import mapping.BioC2Alveo;
import mapping.BioC2Json;

/** Conversion screen
 * 
 *
 * @author Simon Lacis
 *
 */

public class WindowConvert {

	JFrame frame;
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	private String path = null;
	private String absolupath;
	private String biocFileName;
	private String filename;
	private JTextField txtFile;
	private JTextField txtOutput;
	private JTextField txtCollection;


	/**
	 * Create the application.
	 * @param key 
	 */
	public WindowConvert() {		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @param key 
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 500);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		
		JLabel lblConvertFrom = new JLabel("Convert from:");
		lblConvertFrom.setBounds(163, 79, 110, 15);
		frame.getContentPane().add(lblConvertFrom);
		
		JComboBox comboBox = new JComboBox();
		// Can add to this list if new functions are added
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"BioC"}));
		comboBox.setBounds(163, 106, 226, 24);
		frame.getContentPane().add(comboBox);
		
		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setBounds(94, 180, 114, 23);
		btnSelectFile.addActionListener(new ActionListener() {
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
	                txtFile.setText(path);
	            } else {
	                System.out.println("Open command cancelled by user.");
	            }
			}
		});
		frame.getContentPane().add(btnSelectFile);
		
		txtFile = new JTextField();
		txtFile.setColumns(10);
		txtFile.setBounds(220, 177, 200, 30);
		frame.getContentPane().add(txtFile);
		
		JButton btnStartConversion = new JButton("Start Conversion");
		btnStartConversion.setBounds(127, 329, 262, 36);
		btnStartConversion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					if (txtFile.getText() != null && txtOutput.getText() != null && txtCollection.getText() != null) {
						try {
							BioC2Alveo.writeAlveoFiles(txtFile.getText(), txtOutput.getText());
							BioC2Json.writeJson(txtFile.getText(),biocFileName, txtCollection.getText(), txtOutput.getText());
						} catch (XMLStreamException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
		});
		frame.getContentPane().add(btnStartConversion);
		
		JButton btnOutputPath = new JButton("Output Path");
		btnOutputPath.setBounds(81, 218, 127, 23);
		btnOutputPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				final JFileChooser selection = new JFileChooser();
				// Allow for selection of directory only
				selection.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
	                biocFileName = file.getName();
	                absolupath = file.getAbsolutePath();
	                System.out.println(absolupath);
	                absolupath = absolupath.substring(0,absolupath.lastIndexOf(File.separator));
	                txtOutput.setText(path);
	            } else {
	                System.out.println("Open command cancelled by user.");
	            }
			}
		});
		frame.getContentPane().add(btnOutputPath);
		
		txtOutput = new JTextField();
		txtOutput.setColumns(10);
		txtOutput.setBounds(220, 215, 200, 30);
		frame.getContentPane().add(txtOutput);
		
		txtCollection = new JTextField();
		txtCollection.setColumns(10);
		txtCollection.setBounds(220, 142, 186, 29);
		frame.getContentPane().add(txtCollection);
		
		JLabel labelName = new JLabel("Collection Name");
		labelName.setBounds(95, 156, 130, 15);
		frame.getContentPane().add(labelName);

	}
}
