package userInterface;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


import java.awt.Font;

/** A Login GUI class 
 * 
 * 
 * @author Kun He
 *
 */


public class WindowLoginInterface {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Check for existing Alveo config file and read in
					String response = "0";
					String apiKey = null;
					//Change hard-coded directory on publish
					File f = new File("data" + File.separator +  "alveo.config");
					if(f.exists() && !f.isDirectory()) {
						String jsonTxt = new String(Files.readAllBytes(Paths.get("data" + File.separator +  "alveo.config")));
						System.out.println(jsonTxt);
						JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);     
						apiKey = json.getString("apiKey"); 

					}
					//					response = testApiKey(apiKey);
					// Check API key from config and login if valid (set to 200)
					if(Integer.valueOf(response) == 0){
						//						SelectFiles window = new SelectFiles(apiKey);
						WindowCollection window = new WindowCollection(apiKey);
						window.frame.setVisible(true);
					} else {
						WindowLoginInterface window = new WindowLoginInterface();
						window.frame.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WindowLoginInterface() {
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 418, 449);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setBounds(63, 155, 271, 31);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String key = textField.getText();
				String response = "0";
				response = testApiKey(key);

				if(Integer.valueOf(response) == 200){
					frame.setVisible(false);
					WindowCollection window = new WindowCollection(key);
					window.frame.setVisible(true);
				}else{

					//Error message : Invalid API key 
					JOptionPane.showMessageDialog(null, "Invalid API key", "InfoBox: " + "Error Message", JOptionPane.INFORMATION_MESSAGE);
					textField.setText(null);
				}

			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(85, 208, 89, 23);
		frame.getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField.setText(null);
			}
		});

		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton_1.setBounds(219, 208, 89, 23);
		frame.getContentPane().add(btnNewButton_1);

		JLabel lblNewLabel = new JLabel("Please input Alveo API key: ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(63, 121, 211, 23);
		frame.getContentPane().add(lblNewLabel);
	}
}
