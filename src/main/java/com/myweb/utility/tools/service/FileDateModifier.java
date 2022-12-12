package com.myweb.utility.tools.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Dec 11, 2022
 *
 */
public class FileDateModifier {
	public static void main(String[] args) {
		String filePath = "D:\\My Docs\\Gallery\\Camera Photos";
		// Creating a File object for directory
		File directory = new File(filePath);
		// List of all files and directories
		File[] fileList = directory.listFiles();
		System.out.println("List of files and directories in the specified directory:");
		String startsWith = "IMG-";
		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
			if (file.getName().startsWith(startsWith)) {
				setModifiedDateToFile(file, startsWith, false);
			}
		}
	}
	
	private static void setModifiedDateToFile(File file, String startsWith, boolean dateOnly) {
		String name = file.getName();
		String dateStr = null;
		if(dateOnly) {
			dateStr = name.substring(startsWith.length()).substring(0, 8).concat("_000000");
		} else {
			dateStr = name.substring(startsWith.length()).substring(0, 15);
		}
		setDateTimeToFile(file, startsWith, dateStr, false);
	}

	private static void setDateTimeToFile(File file, String startsWith, String dateStr, boolean updateFlag) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
			Path path = Paths.get(file.getPath());
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
			System.out.println("File: " + file.getName());
			System.out.println("Created time: " + attr.creationTime());
			System.out.println("Modified time: " + attr.lastModifiedTime());
			System.out.println(date);
			if(updateFlag) {
				Files.setLastModifiedTime(path, FileTime.from(date.toInstant(ZoneOffset.UTC)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
