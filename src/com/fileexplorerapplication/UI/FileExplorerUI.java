package com.fileexplorerapplication.UI;

import java.io.File;

import javax.swing.*;

import java.awt.*;


@SuppressWarnings("serial")
public class FileExplorerUI extends JFrame {
	
	
	private JList<String> fileList;
	private DefaultListModel<String> listModel;
	private JTextField pathField;
	private File currentDirectory;
	
	
	public FileExplorerUI () {
		setTitle("File Explorer");
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		JPanel topPanel = new JPanel (new BorderLayout());
		pathField = new JTextField();
		JButton refreshButton = new JButton("Refresh");
		JButton goButton = new JButton("Go");
		
		topPanel.add(pathField, BorderLayout.CENTER);
		topPanel.add(refreshButton, BorderLayout.EAST);
		topPanel.add(goButton, BorderLayout.WEST);
		
		listModel = new DefaultListModel<>();
		fileList = new JList<>(listModel);
		JScrollPane scrollPane = new JScrollPane(fileList);
		
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		
		currentDirectory = new File(System.getProperty("user.home"));
		loadFiles(currentDirectory);
		
		refreshButton.addActionListener(e -> loadFiles(currentDirectory));
		goButton.addActionListener(e -> {
			File newDir = new File(pathField.getText());
			if(newDir.isDirectory()) {
				currentDirectory = newDir;
				loadFiles(currentDirectory);
			} else {
				JOptionPane.showMessageDialog(this, "Invalid directory!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		fileList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					openFileOrDirectory();
				}
			}
			
			public void mousePressed(java.awt.event.MouseEvent evt) {
				if(evt.isPopupTrigger()) {
					showContextMenu(evt);
				}
			}
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if(evt.isPopupTrigger()) {
					showContextMenu(evt);
				}
			}
		});	
	}
	
	private void loadFiles(File directory) {
		listModel.clear();
		pathField.setText(directory.getAbsolutePath());
		File[] files = directory.listFiles();
		if(files != null) {
			for(File file: files) {
				listModel.addElement(file.getName());
			}
		}
	}

	private void openFileOrDirectory() {
		String selectedFile = fileList.getSelectedValue()
;	
		if(selectedFile != null) {
			File file = new File(currentDirectory, selectedFile);
			if(file.isDirectory()) {
				currentDirectory = file;
				loadFiles(currentDirectory);
			} else {
				JOptionPane.showMessageDialog(this, "File" + file.getName(), "File Opened", JOptionPane.INFORMATION_MESSAGE);;
			}
		}	
	}
	
	private void showContextMenu(java.awt.event.MouseEvent evt) {
		String selectedFile = fileList.getSelectedValue();
	
		if(selectedFile != null) {
			File file = new File (currentDirectory, selectedFile);
			FileContextMenu contextMenu = new FileContextMenu(this);		
			contextMenu.setSelectedFile(file);
			contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}	
}
