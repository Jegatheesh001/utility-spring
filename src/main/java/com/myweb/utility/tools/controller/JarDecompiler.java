package com.myweb.utility.tools.controller;

import static com.myweb.utility.tools.controller.Utils.get;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar Decompiler
 * 
 * @author jegatheesh.mageswaran <br>
           Created on <b>21-Jul-2020</b>
 *
 */
public class JarDecompiler {
	public static void readJarFile(String jarPath, String outputPath) {
		try (JarFile jarFile = new JarFile(jarPath)) {
			Enumeration<JarEntry> enumOfJar = jarFile.entries();
			while (enumOfJar.hasMoreElements()) {
				JarEntry entry = enumOfJar.nextElement();
				String name = entry.getName();
				if (name.endsWith(".xml") || name.endsWith(".properties")) {
					System.out.println(name);
					File file = new File(outputPath + File.separator + name);
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					try (InputStream inputStream = jarFile.getInputStream(entry);
							OutputStream outputStream = new FileOutputStream(file)) {
						byte[] buffer = new byte[10 * 1024];
						for (int length; (length = inputStream.read(buffer)) != -1;) {
							outputStream.write(buffer, 0, length);
						}
					}
				}
			}
		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe);
		}
	}
	
	public static void fetchDecompilers(String decompilerPath, String jarPath, String outputPath) {
		File decompilerFolder = new File(decompilerPath);
		for (File decompilerFile : decompilerFolder.listFiles()) {
			decompile(jarPath, outputPath, decompilerFile);
		}
	}

	private static void decompile(String jarPath, String outputPath, File decompilerFile) {
		String line = "---------------------------------------------";
		System.out.println("Decompiler : " + decompilerFile.getName());
		System.out.println(line);
		String newOutputPath = null;
		String command = null;
		String decompiler = decompilerFile.getAbsolutePath();
		boolean execute = false;
		if (decompilerFile.getName().startsWith("cfr")) {
			newOutputPath = outputPath + "-cfr";
			command = get("java -jar {} {} --outputdir {}", decompiler, jarPath, newOutputPath);
		} else if (decompilerFile.getName().startsWith("procyon")) {
			newOutputPath = outputPath + "-procyon";
			command = get("java -jar {} -jar {} -o {}", decompiler, jarPath, newOutputPath);
		}
		System.out.println(line);
		File newFile = new File(newOutputPath);
		if (!newFile.exists()) {
			newFile.mkdir();
		}
		System.out.println("Command : " + command);
		System.out.println(line);
		if (execute) {
			callShellCommand(command);
		}
		readJarFile(jarPath, newOutputPath);
		System.out.println(line);
	}
	
	public static void callShellCommand(String command) {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command.split(" "));
		try {
			Process p = builder.start();
			inheritIO(p.getInputStream(), System.out);
			inheritIO(p.getErrorStream(), System.err);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void inheritIO(final InputStream src, final PrintStream dest) {
		Scanner sc = new Scanner(src);
		while (sc.hasNextLine()) {
			dest.println(sc.nextLine());
		}
		dest.println("--------- Completed --------------");
	}
	
	public static void main(String[] args) {
		String projectName = null;
		String projectFile = null;
		if (args == null || args.length == 0) {
			projectName = "mosafal";
			projectFile = "circlecare-modules.jar";
			System.out.println("Arguments: Project-Name Project-file(jar)");
		} else {
			projectName = args[0];
			projectFile = args[1];
		}
		fetchDecompilers("D:\\Softwares\\Decompiler\\core", 
				"D:\\Softwares\\Decompiler\\input\\" + projectFile,
				"D:\\Softwares\\Decompiler\\output\\" + projectName);
	}
}
