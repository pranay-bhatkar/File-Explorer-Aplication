package com.fileexplorerapplication;

import com.fileexplorerapplication.UI.FileExplorerUI;

import javax.swing.*;

public class FileExplorer {

    public static void main(String[] args) {
        // Set the look and feel to match the system UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize the File Explorer UI
        SwingUtilities.invokeLater(() -> {
            FileExplorerUI fileExplorerUI = new FileExplorerUI();
            fileExplorerUI.setVisible(true);  // Show the UI
            
            
            
        });
    }
}