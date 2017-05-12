package userInterface;

import java.awt.*;
import java.util.Stack;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.sf.json.JSONObject;

public class Test {

	private String[] colNames = { "Metadata type", "Metadata" };

	private DefaultTableModel model = new DefaultTableModel(colNames, 0);
	private JTable table = new JTable(model);
	private MyStack myStack = new MyStack();
	private Stack<Integer[]> stack;

	private JButton button = new JButton("Add Row");
	private JButton button2 = new JButton("Delete Row");
	int count = 0;
	public Test() {
		stack = myStack.getStack();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!stack.isEmpty()) {
					//	                    Integer[] array = stack.pop();
					String[] array = {"test" + count,"nigga"};
					count++;
					model.addRow(array);
					getTableData(table);
				}
			}
		});

		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!stack.isEmpty()) {
					model.removeRow(table.getSelectedRow());
				}
			}
		});

		JFrame frame = new JFrame();
		frame.add(new JScrollPane(table), BorderLayout.CENTER);
		frame.add(button, BorderLayout.SOUTH);
		frame.add(button2, BorderLayout.EAST);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Test();
			}
		});
	}

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
}

class MyStack {
	public Stack<Integer[]> stack = new Stack<Integer[]>();

	public MyStack() {
		int k = 1;
		for (int i = 0; i < 20; i++) {
			Integer[] array = new Integer[5];
			for (int j = 0; j < 5; j++) {
				array[j] = i * k * (j + 1);
			}
			k++;
			stack.push(array);
		}
	}

	public Stack<Integer[]> getStack() {
		return stack;
	}



}

