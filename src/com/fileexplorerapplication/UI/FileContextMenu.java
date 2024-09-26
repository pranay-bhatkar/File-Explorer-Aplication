package com.fileexplorerapplication.UI;

import com.fileexplorerapplication.FileOperations;

import java.awt.event.ActionListener;

import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.*;

@SuppressWarnings("serial")
public class FileContextMenu extends JPopupMenu {
	private JMenuItem openItem;
	private JMenuItem renameItem;
	private JMenuItem moveItem;
	private JMenuItem deleteItem;
	private File selectedFile;
	private JFrame parent;
	
	public FileContextMenu(JFrame parent) {
		this.parent = parent;
		
		openItem = new JMenuItem("Open");
		renameItem = new JMenuItem("Rename");
		moveItem = new JMenuItem("Move");
		deleteItem = new JMenuItem("Delete");
		
		add(openItem);
		add(renameItem);
		add(moveItem);
		add(deleteItem);
		
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		
		renameItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameFile();
			}
		});
		
		moveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveFile();
			}
		});
		
		deleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteFile();
			}
		});
	}
	
	public void setSelectedFile(File file) {
		this.selectedFile = file;
	}
	
	private void openFile() {
		JOptionPane.showMessageDialog(parent, "Opening file :" + selectedFile.getName());
		// Handle Actual File opening
	}
	
	private void renameFile() {
		String newName = JOptionPane.showInputDialog(parent, "Rename file to", selectedFile.getName());
		if(newName != null && !newName.trim().isEmpty()) {
			if(FileOperations.renameFile(selectedFile, newName)) {
				JOptionPane.showConfirmDialog(parent, "File renamed Successfully.");
			} else {
				JOptionPane.showConfirmDialog(parent, "Failed to renmae file.");
			}
		}
	}
	
	private void moveFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);;
		
		int returnValue = fileChooser.showOpenDialog(parent);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			File destinationDir = fileChooser.getSelectedFile();
			
			if(FileOperations.moveFile(selectedFile, destinationDir)) {
				JOptionPane.showMessageDialog(parent, "File moved Sucessfully to : " + destinationDir.getAbsolutePath());
			} else {
				JOptionPane.showMessageDialog(parent, "Failed to move the file.");
			}
		}
	}
	
	
	private void deleteFile() {
		int confirmation = JOptionPane.showConfirmDialog(parent, "Are you sure you want to delete this file?", "confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION);
		if(confirmation == JOptionPane.YES_NO_OPTION) {
			if(FileOperations .deleteFile(selectedFile)) {
				JOptionPane.showConfirmDialog(parent, "File deleted successfully.");
			} else {
				JOptionPane.showConfirmDialog(parent, "Failed to delete file.");
			}
		}
	}
}
