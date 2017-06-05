package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;



import net.sf.json.JSONObject;

/** Main menu screen
 * 
 *
 * @author Simon Lacis
 *
 */

public class WindowCollection {

	JFrame frame;
	HashMap<String, JSONObject> recItemMetadata = new HashMap<String,JSONObject>();
	HashMap<String, HashMap<String, JSONObject>> recDocMetadata = new HashMap<String,HashMap<String,JSONObject>>();
	private String path = null;
	private String absolupath;
	private String filename;


	/**
	 * Create the application.
	 * @param key 
	 */
	public WindowCollection(String key) {		
		initialize(key);
	}

	/**
	 * Initialize the contents of the frame.
	 * @param key 
	 */
	private void initialize(String key) {
		frame = new JFrame();
		frame.setBounds(100, 100, 350, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		
		// Open Metadata Editor button
		JButton btnUpdateExistingCol = new JButton("Update Existing Collection");
		btnUpdateExistingCol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WindowUpdateCollection updateWindow= new WindowUpdateCollection(key);
				updateWindow.frame.setVisible(true);
				}
		});
		btnUpdateExistingCol.setBounds(39, 140, 262, 36);
		frame.getContentPane().add(btnUpdateExistingCol);
		
		JButton btnCreateNewCollection = new JButton("Create New Collection");
		btnCreateNewCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WindowCreateCollection createWindow= new WindowCreateCollection(key);
				createWindow.frame.setVisible(true);
				}
		});
		btnCreateNewCollection.setBounds(39, 66, 262, 36);
		frame.getContentPane().add(btnCreateNewCollection);
		
		JButton btnConvertCollectionTo = new JButton("Convert Collection ");
		btnConvertCollectionTo.setBounds(39, 211, 262, 36);
		btnConvertCollectionTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WindowConvert convertWindow= new WindowConvert();
				convertWindow.frame.setVisible(true);
				}
		});
		frame.getContentPane().add(btnConvertCollectionTo);

	}
}
