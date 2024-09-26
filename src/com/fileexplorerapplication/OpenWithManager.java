package com.fileexplorerapplication;

import java.io.File;
import java.io.IOException;

public class OpenWithManager {
	public static void openwith(File file, String appPath) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(appPath, file.getAbsolutePath());
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
