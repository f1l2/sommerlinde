package sbcm.supplier;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MainFrame implements ActionListener {

	private JFrame frame;

	private JButton btnCreate;

	private JTextField woodenTF, igniterTF, loadTF;

	private JTable rocketTable;
	private DefaultTableModel rocketTableModel;

	public MainFrame() {
		frame = new JFrame("Firework Factory");
		frame.setBounds(100, 100, 1250, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane);
		this.addMenuBar(this.frame);

		JPanel head_panel = new JPanel();
		splitPane.setTopComponent(head_panel);
		head_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));

		JButton btnSupplier = new JButton("Supplier");
		JButton btnRockets = new JButton("Rockets");
		JButton btnParts = new JButton("Parts");
		head_panel.add(btnSupplier);
		head_panel.add(btnRockets);
		head_panel.add(btnParts);

		JPanel content_panel = new JPanel();
		splitPane.setBottomComponent(content_panel);

		JPanel centerPanel = new JPanel(new GridLayout(6, 1));
		content_panel.add(centerPanel);

		// row supplier
		JPanel rowPanel1 = new JPanel(new FlowLayout());
		centerPanel.add(rowPanel1);

		JLabel supplierLBL = new JLabel("Supplier:");
		rowPanel1.add(supplierLBL);

		JTextField supplierTF = new JTextField();
		supplierTF.setToolTipText("Name of Supplier");
		rowPanel1.add(supplierTF);
		supplierTF.setColumns(10);

		// row wooden
		JPanel rowPanel2 = new JPanel(new FlowLayout());
		centerPanel.add(rowPanel2);

		JLabel woodenLBL = new JLabel("Holzstäbe:");
		rowPanel2.add(woodenLBL);

		woodenTF = new JTextField();
		woodenTF.setToolTipText("# Holzstäbe");
		rowPanel2.add(woodenTF);
		woodenTF.setColumns(10);

		// row igniter
		JPanel rowPanel3 = new JPanel(new FlowLayout());
		centerPanel.add(rowPanel3);

		JLabel igniterLBL = new JLabel("Gehäuse mit Zünder:");
		rowPanel3.add(igniterLBL);

		igniterTF = new JTextField();
		igniterTF.setToolTipText("# Gehäuse mit Zünder");
		rowPanel3.add(igniterTF);
		igniterTF.setColumns(10);

		// row igniter
		JPanel rowPanel4 = new JPanel(new FlowLayout());
		centerPanel.add(rowPanel4);

		JLabel loadLBL = new JLabel("Effektladung: ");
		rowPanel4.add(loadLBL);

		loadTF = new JTextField();
		loadTF.setToolTipText("# Effektladung");
		rowPanel4.add(loadTF);
		loadTF.setColumns(10);

		JButton btnCreate = new JButton("Create");
		centerPanel.add(btnCreate);
		btnCreate.addActionListener(this);

		Object[][] data = {};
		String[] rocketColumnNames = { "Id of Rocket", "Producer" };

		rocketTableModel = new DefaultTableModel(data, rocketColumnNames);

		rocketTable = new JTable(rocketTableModel);

		centerPanel.add(rocketTable);

	}

	private void addMenuBar(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu file = new JMenu("File");
		menuBar.add(file);

		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);

	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCreate) {

			int woodenCount, igniterCount, loadCount;

			String text = woodenTF.getText();
			try {
				woodenCount = Integer.parseInt(text);
			} catch (NumberFormatException nfe) {
				return;
			}

			text = igniterTF.getText();
			try {
				igniterCount = Integer.parseInt(text);
			} catch (NumberFormatException nfe) {
				return;
			}

			text = loadTF.getText();
			try {
				loadCount = Integer.parseInt(text);
			} catch (NumberFormatException nfe) {
				return;
			}

		}

	}
}
