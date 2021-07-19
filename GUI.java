import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Element;

public class GUI {

	private static JFrame frame;
	private static JTextPane textPane;
	private static JPanel writingPanel;

	public static void main(String[] args) {
		Init();
	}

	public static void Init() {
		frame = new JFrame();
		frame.setSize(new Dimension(1000, 875));
		writingPanel = new JPanel(new BorderLayout());
		textPane = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(textPane);
		TextLineNumber tln = new TextLineNumber(textPane);
		scrollPane.setRowHeaderView(tln);
		writingPanel.add(scrollPane, BorderLayout.CENTER);
		frame.add(writingPanel);
		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu runMenu = new JMenu("Run");
		JMenu helpMenu = new JMenu("Help");
		frame.setLocationRelativeTo(null);
		menu.add(fileMenu);
		JMenuItem newFile = new JMenuItem("New");
		newFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textPane.setText("");
			}
		});
		JMenuItem loadFile = new JMenuItem("Load");
		loadFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Files with .csp extension", "csp");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(chooser);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("File Choose: " + chooser.getSelectedFile().getName());
					String directory = chooser.getSelectedFile().getAbsolutePath();
					System.out.println("Directory: " + directory);
					File file = chooser.getSelectedFile().getAbsoluteFile();
					try {
						BufferedReader br = new BufferedReader(new FileReader(file));
						textPane.setText(br.readLine());
						String line = "";
						while((line = br.readLine()) != null) {
							textPane.setText(textPane.getText() + "\n" + line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
			
		});
		JMenuItem saveFile = new JMenuItem("Save");
		saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int saveFileVal = chooser.showSaveDialog(frame);
				if (saveFileVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("Save file name: " + chooser.getSelectedFile().getName());
					File saved = new File(chooser.getSelectedFile().getAbsolutePath() + ".csp");
					try {
						String[] lines = textPane.getText().split("\\r?\\n");
						PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(saved)));
						for(String s : lines) {
							pw.println(s);
						}
						pw.close();
					} catch (IOException e) {
						System.out.println("File Save As error.");
						e.printStackTrace();
					}
					
					try {
						saved.getParentFile().mkdirs();
						saved.createNewFile();
					} catch (IOException e) {
						System.out.println("Saving File Errors.");
						e.printStackTrace();
					}
					
				}	
			}
		});
		JMenuItem run = new JMenuItem("Run");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] lines = textPane.getText().split("\\r?\\n");
				ArrayList<String> code = new ArrayList<String>();
				for(String s : lines) {
					code.add(s);
				}
				System.out.println(code);
				try {
					Console.runCode(code);
				} catch (ScriptException e1) {
					System.out.println("This shouldn't have happened?");
					e1.printStackTrace();
				}
			}
		});		
		runMenu.add(run);
		JMenuItem help = new JMenuItem("Help");
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Desktop.isDesktopSupported()) {
					try {
						File pdf = new File("csp.pdf");
						Desktop.getDesktop().open(pdf);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			
		});
		helpMenu.add(help);
		menu.add(helpMenu);
		menu.add(runMenu);
		fileMenu.add(newFile);
		fileMenu.add(saveFile);
		fileMenu.add(loadFile);
		frame.setJMenuBar(menu);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
