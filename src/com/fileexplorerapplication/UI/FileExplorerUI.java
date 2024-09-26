package com.fileexplorerapplication.UI;

import java.io.File;
import javax.swing.*;
import java.awt.*;
import javax.swing.filechooser.FileSystemView;

import com.fileexplorerapplication.FileOperations;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;
import java.util.*;



@SuppressWarnings("serial")
public class FileExplorerUI extends JFrame {
	
	
	private JList<File> fileList;
	private DefaultListModel<File> listModel;
	private JTextField pathField;
	private JTextField searchField;
	private File currentDirectory;
	private JLabel statusBar;
	private Stack<File> directoryHistory;
	
	
	public FileExplorerUI () {
		
		directoryHistory = new Stack<>();
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		setTitle("File Explorer");
		setSize(900, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
			
		JPanel topPanel = new JPanel (new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5,5,5,5);
		
		
		pathField = new JTextField(25);
		searchField = new JTextField("Search",10);
		JButton refreshButton = new JButton("Refresh");
		JButton goButton = new JButton("Go");
		JButton newFolderButton = new JButton("New Folder");
		JButton backButton = new JButton("",resizeIcon("Images/back_arrow.png", 15,15));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0 ;
		topPanel.add(backButton, gbc);
		
		gbc.gridx = 1;
		topPanel.add(goButton, gbc);
		
		gbc.gridx = 2;
		gbc.weightx = 1;
		topPanel.add(pathField, gbc);
		
		gbc.gridx = 3;
		gbc.weightx = 0;
		topPanel.add(newFolderButton, gbc);
		
		gbc.gridx = 4;
		topPanel.add(refreshButton, gbc);
		
		gbc.gridx = 5;
		gbc.weightx = 0;
		topPanel.add(searchField,gbc);
		
		
		
		
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
		goButton.addActionListener(e -> navigateToDirectory());
		newFolderButton.addActionListener(e -> createNewFolder());
		backButton.addActionListener(e -> goBack());
		
		pathField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					navigateToDirectory();
				}
			}
		});
		
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					performSearch(searchField.getText());
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
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        @Override
	        protected Void doInBackground() {
	            File[] files = directory.listFiles();
	            if (files == null) {
	                SwingUtilities.invokeLater(() -> statusBar.setText("Could not load files."));
	                return null;
	            }

	            SwingUtilities.invokeLater(() -> {
	                listModel.clear(); // Clear the model in the Event Dispatch Thread
	                pathField.setText(directory.getAbsolutePath());
	                for (File file : files) {
	                    listModel.addElement(file); // Safely add elements in the Event Dispatch Thread
	                }
	            });
	            return null;
	        }

	        @Override
	        protected void done() {
	            SwingUtilities.invokeLater(() -> statusBar.setText("Loaded " + listModel.size() + " items."));
	        }
	    };
	    worker.execute();
	}



	private void openFileOrDirectory() {
	    File selectedFile = fileList.getSelectedValue();
	    if (selectedFile == null) {
	        JOptionPane.showMessageDialog(this, "No file selected!", "Warning", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    if (selectedFile.isDirectory()) {
	        directoryHistory.push(currentDirectory);
	        currentDirectory = selectedFile;
	        loadFiles(currentDirectory);
	    } else {
	        JOptionPane.showMessageDialog(this, "File: " + selectedFile.getName(), "File Opened", JOptionPane.INFORMATION_MESSAGE);
	    }
	}

	private void deleteFileOrDirectory() {
	    File selectedFile = fileList.getSelectedValue();

	    if (selectedFile == null) {
	        JOptionPane.showMessageDialog(this, "No file selected!", "Warning", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    int confirm = JOptionPane.showConfirmDialog(this, 
	        "Are you sure you want to delete " + selectedFile.getName() + "?",
	        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

	    if (confirm == JOptionPane.YES_OPTION) {
	        if (FileOperations.deleteFile(selectedFile)) {
	            loadFiles(currentDirectory); // Refresh file list
	            JOptionPane.showMessageDialog(this, 
	                selectedFile.getName() + " deleted successfully.",
	                "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
	        } else {
	            JOptionPane.showMessageDialog(this, 
	                "Failed to delete " + selectedFile.getName() + ".",
	                "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	private void renameFileOrDirectory() {
	    File selectedFile = fileList.getSelectedValue();
	    if (selectedFile == null) {
	        JOptionPane.showMessageDialog(this, "No file selected!", "Warning", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    String newName = JOptionPane.showInputDialog(this, "Enter new name:", selectedFile.getName());
	    if (newName != null && !newName.trim().isEmpty()) {
	        if (FileOperations.renameFile(selectedFile, newName)) {
	            loadFiles(currentDirectory); // Refresh file list
	            JOptionPane.showMessageDialog(this, "Renamed successfully.");
	        } else {
	            JOptionPane.showMessageDialog(this, "Failed to rename " + selectedFile.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	
	private void moveFileOrDirectory() {
	    File selectedFile = fileList.getSelectedValue();
	    if (selectedFile == null) {
	        JOptionPane.showMessageDialog(this, "No file selected!", "Warning", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setDialogTitle("Select Destination Directory");
	    int returnValue = chooser.showOpenDialog(this);
	    if (returnValue == JFileChooser.APPROVE_OPTION) {
	        File destination = chooser.getSelectedFile();
	        if (FileOperations.moveFile(selectedFile, destination)) {
	            loadFiles(currentDirectory); // Refresh file list
	            JOptionPane.showMessageDialog(this, "Moved successfully.");
	        } else {
	            JOptionPane.showMessageDialog(this, "Failed to move " + selectedFile.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}


	
	private void showContextMenu(java.awt.event.MouseEvent evt) {
	    File selectedFile = fileList.getSelectedValue();

	    if (selectedFile != null) {
	        FileContextMenu contextMenu = new FileContextMenu(this);        
	        contextMenu.setSelectedFile(selectedFile);
	        
//	         Add a delete option to the context menu
	        JMenuItem deleteItem = new JMenuItem("Delete 2");
	        deleteItem.addActionListener(e -> deleteFileOrDirectory());
	        contextMenu.add(deleteItem);
	        
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
	    String pathText = pathField.getText().trim();
	    if (pathText.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Please enter a valid path.", "Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    File newDir = new File(pathText);
	    if (newDir.isDirectory()) {
	        directoryHistory.push(currentDirectory);
	        currentDirectory = newDir;
	        loadFiles(currentDirectory);
	    } else {
	        JOptionPane.showMessageDialog(this, "Invalid directory!", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	
	
	private void goBack() {
        if (!directoryHistory.isEmpty()) {
            currentDirectory = directoryHistory.pop();  // Get the last directory from history
            loadFiles(currentDirectory);
        } else {
            JOptionPane.showMessageDialog(this, "No previous directory.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
	
	private void performSearch(String searchQuery) {
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	        ArrayList<File> searchResults = new ArrayList<>();
	        
	        @Override
	        protected Void doInBackground() {
	            searchInDirectory(currentDirectory, searchQuery, searchResults);  // Perform the search in the background
	            return null;
	        }

	        @Override
	        protected void done() {
	            SwingUtilities.invokeLater(() -> {
	                listModel.clear();  // Clear the current list
	                if (searchResults.isEmpty()) {
	                    statusBar.setText("No results found for: " + searchQuery);
	                } else {
	                    for (File file : searchResults) {
	                        listModel.addElement(file);  // Add search results to the list
	                    }
	                    statusBar.setText("Found " + searchResults.size() + " results for: " + searchQuery);
	                }
	            });
	        }
	    };
	    worker.execute();
	}
	
	private void searchInDirectory(File directory, String searchQuery, ArrayList<File> results) {
	    File[] files = directory.listFiles();  // Get list of files in the current directory
	    if (files == null) {
	        return;
	    }

	    System.out.println("Searching in directory: " + directory.getAbsolutePath());

	    for (File file : files) {
	        if (file.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
	            results.add(file);  // Add matching files to results list
	        }

	        // If it's a directory, search inside it recursively
	        if (file.isDirectory()) {
	            searchInDirectory(file, searchQuery, results);
	        }
	    }
	    System.out.println("Found " + results.size() + " results in directory: " + directory.getAbsolutePath());

	}

	
	
	private ImageIcon resizeIcon(String path, int width, int height) {
		ImageIcon icon = new ImageIcon(path);
		if(icon.getIconWidth() == -1) {
			System.err.println("Icon not found: " + path);
			return null;
		}
		Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(img);
	}
}
