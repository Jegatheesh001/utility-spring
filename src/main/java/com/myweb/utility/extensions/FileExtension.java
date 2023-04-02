package com.myweb.utility.extensions;

import java.io.File;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Apr 2, 2023
 *
 */
public class FileExtension {
	/**
	 * Get Extension of File
	 * 
	 */
	public static void getExtension(File file) {
		// Get the file name
		String fileName = file.getName();
		// Extract the extension from the file name
		int index = fileName.lastIndexOf('.');
		if (index > 0) {
			String extension = fileName.substring(index + 1);
			System.out.println(extension);
		}
	}
}
