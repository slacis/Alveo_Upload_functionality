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
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	private JFrame frame;
	private JTextField textField;
    AutoCompleteDecorator decorator;
    JRadioButton userMeta, reqMeta, docMeta;
    JTextArea userJson, reqJson, docJson;
    private JComboBox comboBox;
    int textAreaNo = 1;
    BufferedReader br = null;
    String[] values;
    JScrollPane scrollBar1, scrollBar2, scrollBar3;
    ArrayList<String> searchedMeta;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MetadataBuilder window = new MetadataBuilder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MetadataBuilder() {
		initialize();
	}
	
	//Test API Key
	public static String testApiKey(String key){
	String response = "0";
	
	try {
		response = LoginCheck.check(key);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return response;
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Font font1 = new Font("SansSerif", Font.BOLD, 12);
		frame = new JFrame();
		frame.setSize(800,600);
		frame.setLocationRelativeTo(null);
//		frame.setBounds(100, 100, 418, 449);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Metadata Builder");
		
		// ButtonGroup for metadata type
		
		userMeta  = new JRadioButton("User Metadata");
		reqMeta  = new JRadioButton("Required Metadata");
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
                if ("comboBoxEdited".equals(command)) {
                	
                	searchedMeta = metadataSearch(values,selected.toString());
                	System.out.println(searchedMeta.toArray());
                	DefaultComboBoxModel model = new DefaultComboBoxModel( searchedMeta.toArray());
                		comboBox.setModel( model );


                    System.out.println("User has typed a string in " +
                            "the combo box.");
                }
            }
        });
		
		// Text field for adding metadata info
		textField = new JTextField();
		textField.setFont(font1);
		textField.setBounds(360, 50, 271, 30);
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
		btnAddMeta.setBounds(640, 50, 80, 30);
		
		
		//Label for Metadata type
		JLabel lblMetadataT = new JLabel("Metadata Type");
		lblMetadataT.setFont(font1);
		lblMetadataT.setBounds(50, 25, 311, 23);
		
		//Label for Metadata
		JLabel lblMetadata = new JLabel("Metadata");
		lblMetadata.setFont(font1);
		lblMetadata.setBounds(360, 25, 311, 23);
		
		//TextAreas for Metadata
		
		//Required
		reqJson = new JTextArea();
//		reqJson.setText("Metadata as JSON-LD");
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
				} else if (textAreaNo == 2) {
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
		frame.getContentPane().add(textField);
		frame.getContentPane().add(btnAddMeta);
		frame.getContentPane().add(lblMetadataT);
		frame.getContentPane().add(lblMetadata);
		frame.getContentPane().add(operPanel);
		frame.setVisible(true);
	}
}
