package com.fileexplorerapplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileOperations {
	public static boolean copyFile(File source, File destination) {
		try {
			Files.copy(source.toPath(), destination.toPath(),StandardCopyOption.REPLACE_EXISTING);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public static boolean renameFile(File file, String newName) {
		File newFile = new File(file.getParent(), newName); // rename 
		return file.renameTo(newFile);
	}
	
	public static boolean moveFile(File source, File destination) {
		return source.renameTo(new File(destination, source.getName()));
	}
	
	public static boolean deleteFile(File file) {
		return file.delete();
	}
}
