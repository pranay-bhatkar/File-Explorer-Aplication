package com.fileexplorerapplication.UI;

import java.io.File;

import javax.swing.*;

import java.awt.*;

import javax.swing.filechooser.FileSystemView;

import java.awt.event.KeyAdapter;

import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class FileExplorerUI extends JFrame {
	
	
	private JList<File> fileList;
	private DefaultListModel<File> listModel;
	private JTextField pathField;
	private File currentDirectory;
	private JLabel statusBar;
	
	
	public FileExplorerUI () {
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.nimbusLookAndFeel");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		setTitle("File Explorer");
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
			
		JPanel topPanel = new JPanel (new BorderLayout());
		pathField = new JTextField();
		JButton refreshButton = new JButton("Refresh");
		JButton goButton = new JButton("Go");
		JButton newFolderButton = new JButton("New Folder");
		
		topPanel.add(pathField, BorderLayout.CENTER);
		topPanel.add(refreshButton, BorderLayout.EAST);
		topPanel.add(goButton, BorderLayout.WEST);
		topPanel.add(newFolderButton, BorderLayout.SOUTH);
		
		listModel = new DefaultListModel<>();
		fileList = new JList<>(listModel);
		fileList.setCellRenderer(new FileCellRenderer());
		JScrollPane scrollPane = new JScrollPane(fileList);
		
		statusBar = new JLabel(" ");
		statusBar.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		
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
		newFolderButton.addActionListener(e -> createNewFolder());
		
		pathField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					navigateToDirectory();
				}
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
	
	private class FileCellRenderer extends DefaultListCellRenderer{
		private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		private JLabel label;
		
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			File file = (File) value;
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(file.getName());
			return label;
		}
		
	}
	
	private void loadFiles(File directory) {
		listModel.clear();
		pathField.setText(directory.getAbsolutePath());
		File[] files = directory.listFiles();
		
		 if (files == null) {
		        statusBar.setText("Could not load files.");
		        return;
		    }
		    
		    if (files.length == 0) {
		        statusBar.setText("No files found in this directory");
		        return;
		    }
		
		for(File file : files) {
			listModel.addElement(file);
		}
		
		statusBar.setText("Loaded" + files.length + "items.");
	}

	private void openFileOrDirectory() {
		File selectedFile = fileList.getSelectedValue()
;	
		if(selectedFile != null) {
			if(selectedFile.isDirectory()) {
				currentDirectory = selectedFile;
				loadFiles(currentDirectory);
			} else {
				JOptionPane.showMessageDialog(this, "File" + selectedFile.getName(), "File Opened", JOptionPane.INFORMATION_MESSAGE);;
			}
		}	
	}
	
	private void showContextMenu(java.awt.event.MouseEvent evt) {
		File selectedFile = fileList.getSelectedValue();
	
		if(selectedFile != null) {
			FileContextMenu contextMenu = new FileContextMenu(this);		
			contextMenu.setSelectedFile(selectedFile);
			contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}	
	
	private void createNewFolder() {
		String folderName = JOptionPane.showInputDialog(this, "Enter folder name : ");
		if(folderName != null && !folderName.trim().isEmpty()) {
			File newFolder = new File(currentDirectory, folderName);
			if(newFolder.exists()) {
				JOptionPane.showMessageDialog(this, "Folder already exists.", "Error", JOptionPane.ERROR_MESSAGE);
			}else if(newFolder.mkdir()) {
				loadFiles(currentDirectory);
				JOptionPane.showMessageDialog(this, "Folder created successfully.");
			} else {
				JOptionPane.showMessageDialog(this, "Failed to create folder.");
			}
		}
	}
	
	private void navigateToDirectory() {
		File newDir = new File(pathField.getText());
		if(newDir.isDirectory()) {
			currentDirectory = newDir;
			loadFiles(currentDirectory);
		} else {
			JOptionPane.showMessageDialog(this, "Invalid directory!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
