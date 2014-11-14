package at.tuwien.sbcm.supplier;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class MainFrame {
	
	private JFrame frame;

	
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
		
		JPanel centerPanel = new JPanel(new GridLayout(3,1));
		content_panel.add(centerPanel);
		
		JPanel rowPanel1 = new JPanel(new FlowLayout());
		centerPanel.add(rowPanel1);
		
		JLabel supplierLBL = new JLabel("Supplier:");
		rowPanel1.add(supplierLBL);
		
		JTextField supplierTF = new JTextField();
		supplierTF.setToolTipText("Name of Supplier");
		rowPanel1.add(supplierTF);
		supplierTF.setColumns(10);
		
		JPanel rowPanel2 = new JPanel(new FlowLayout());
		centerPanel.add(rowPanel2);
		
		JLabel woodenLBL = new JLabel("Holzstäbe:");
		rowPanel2.add(woodenLBL);
		
		JTextField woodenTF = new JTextField();
		woodenTF.setToolTipText("# Holzstäbe");
		rowPanel2.add(woodenTF);
		woodenTF.setColumns(10);
	
		
		
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
}
