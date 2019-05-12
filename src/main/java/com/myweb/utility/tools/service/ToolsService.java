package com.myweb.utility.tools.service;

import static com.myweb.utility.tools.controller.Utils.cleanTextContent;
import static com.myweb.utility.tools.controller.Utils.log;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On Jan 10, 2019
 *
 */
@Service
public class ToolsService {

	public String logicForImportDataFromExcel(String filePath) {
		Workbook workbook = fetchExcelDocument(filePath);
		return processExcelDocument(workbook);
	}

	private Workbook fetchExcelDocument(String filePath) {
		Workbook workbook = null;
		try (FileInputStream excelFile = new FileInputStream(new File(filePath))) {
			workbook = new HSSFWorkbook(excelFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}

	String lineSeperator = "<br>";
	String colSeperator = " | ";

	private String processExcelDocument(Workbook workbook) {
		if (workbook != null) {
			StringBuilder contentLog = new StringBuilder();
			Sheet sheet = null;
			Row row = null;
			Cell contentCell = null;
			String tableName = null;
			String[] tableColumns = null;
			String contentStr = null;
			for (int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {
				sheet = workbook.getSheetAt(sheetNo);
				if (sheet != null) {
					row = null;
					contentCell = null;
					contentLog.append("Reading Sheet : " + sheet.getSheetName() + lineSeperator);
					for (int rowNo = 0; rowNo < sheet.getLastRowNum(); rowNo++) {
						row = sheet.getRow(rowNo);
						if (row != null) {
							for (int colNo = 0; colNo < row.getLastCellNum(); colNo++) {
								contentCell = row.getCell(colNo);
								if (contentCell != null) {
									contentStr = contentCell.toString();
									contentLog.append(contentStr);
								}
								contentLog.append(colSeperator);
							}
							contentLog.append(lineSeperator);
						}
					}
				}
			}
			return contentLog.toString();
		}
		return null;
	}

	public String logicToReadSQLFromFolder(String folderPath) {
		linesWrited = 0;
		File folder = new File(folderPath);
		List<String> files = new ArrayList<>();
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				files.add(fileEntry.getName());
				System.out.println("Reading : " + fileEntry.getName());
				try {
					readContentFromFile(folderPath, fileEntry.getName(), "sql");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (files.size() > 0) {
			List<String> temp = files.stream().map(e -> {
				return "truncate table " + e.split("\\.")[0] + ";"; 
			}).collect(Collectors.toList());
			writeToFile(temp, folderPath, "sql", "TruncateQuery");
		}
		System.out.println("Total Records : " + linesWrited);
		return null;
	}

	public String readContentFromFile(String folderPath, String fileName, String extension) throws IOException {
		if (fileName.endsWith("." + extension)) {
			Path path = FileSystems.getDefault().getPath(folderPath, fileName);
			List<String> lines = new ArrayList<>();
			Files.lines(path, StandardCharsets.UTF_8).forEach(line -> {
				lines.add(line);
			});
			fileFiter(lines, folderPath, extension, true);
			System.out.println("Records : " + lines.size());
		}
		return null;
	}
	
	long linesWrited = 0;
	long linesPerFile = 65000;
	String fileName;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
	private void fileFiter(List<String> lines, String folderPath, String extension, boolean seperateFiles) {
		long currentLoad = lines.size();
		String fileBeg = "test_";
		if (currentLoad > 0) {
			if (!seperateFiles) {
				linesWrited += currentLoad;
				if (fileName == null)
					fileName = fileBeg + sdf.format(new Date());
				writeToFile(lines, folderPath, extension, fileName);
			} else {
				long lastFileLines = linesWrited % linesPerFile;
				int fromIndex = 0;
				int toIndex = 0;
				while (currentLoad > 0) {
					if (lastFileLines == 0)
						fileName = fileBeg + sdf.format(new Date()) + "_" + currentLoad;
					if ((lastFileLines + currentLoad) > linesPerFile)
						toIndex = (int) (linesPerFile - lastFileLines);
					else
						toIndex = (int) currentLoad;
					System.out.println(fileName + " index : " + fromIndex + " to " + (fromIndex + toIndex));
					writeToFile(lines.subList(fromIndex, fromIndex + toIndex), folderPath, extension, fileName);
					lastFileLines = 0;
					linesWrited += toIndex;
					currentLoad -= toIndex;
					fromIndex += toIndex;
				}
			}
		}
	}

	public String writeToFile(List<String> lines, String folderPath, String extension, String fileName) {
		if (lines.size() > 0) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(folderPath + "/op/" + fileName + "." + extension, true))) {
				for (String line : lines) {
					bw.write(cleanTextContent(line));
					bw.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String logicToRenameFiles(String folderPath) {
		File folder = new File(folderPath);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				System.out.println("Reading : " + fileEntry.getName());
				String[] temp = fileEntry.getName().split("-");
				String revised = null;
				System.out.println("Revised : " + revised);
				try {
					revised = temp[0] + "-" + temp[1] + "-" + temp[2] + "-" + "P" +"-" + temp[3];
					Files.copy(fileEntry.toPath(), new FileOutputStream(folderPath + "/o/"+revised));
					revised = temp[0] + "-" + temp[1] + "-" + temp[2] + "-" + "C" +"-" + temp[3];
					Files.copy(fileEntry.toPath(), new FileOutputStream(folderPath + "/o/"+revised));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	public String logicToMergeImages(String folderPath) {
		// base path of the images
		File path = new File(folderPath);
		String extension = ".png";
		long err = 0;
		long success = 0;
		for (final File fileEntry : path.listFiles()) {
			if (!fileEntry.isDirectory()) {
				String fileName = fileEntry.getName().split("\\.")[0];
				String[] tempArr = fileName.split("-");
				String tooth = tempArr[tempArr.length-1];
				boolean isNumber = false;
				try { Integer.parseInt(tooth); isNumber = true; } catch(Exception e) {}
				if (isFolderExists(path.getAbsolutePath() + "/output/" + tooth)) {
					if(isNumber) {
						mergeImage(path.getAbsolutePath() + "/base/to-" + tooth + extension, fileEntry.getAbsolutePath(),
								path.getAbsolutePath() + "/output/" + tooth + "/" + fileEntry.getName());
					}
					success++;
					String msg = success + ": " + tooth + " -> " + fileEntry.getName() + " || " + "base/to-" + tooth + extension
							+ " || " + "output/" + tooth + "/" + fileEntry.getName();
					if(isNumber)
						System.out.println(msg);
					else
						System.err.println("NAN:: " + msg);
				} else {
					System.err.println(tooth + " -> " + fileEntry.getName());
					err++;
				}
			}
			// mergeImage(path.getAbsolutePath() + "/" + "image.png", path.getAbsolutePath() + "/" + "overlay.png",
				//overlayFile.getParentFile().getAbsolutePath() + "/" + overlayFile.getName().split("\\.")[0] + "_merged"+extension);
		}
		System.out.println("Success: " + success + " || Errors: " + err);
		return "";
	}

	private boolean mergeImage(String baseImagePath, String overlayPath, String outputFilePath) {
		BufferedImage image = null, overlay = null;
		try {
			// load source images
			image = ImageIO.read(new File(baseImagePath));
			overlay = ImageIO.read(new File(overlayPath));

			// create the new image, canvas size is the max. of both image sizes
			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			// paint both images, preserving the alpha channels
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 0, 0, null);

			// Save as new image
			ImageIO.write(combined, "PNG", new File(outputFilePath));
			image.flush(); overlay.flush(); combined.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean isFolderExists(String folderPath) {
		File folder = new File(folderPath);
		if(!folder.exists())
			folder.mkdirs();
		return folder.exists();
	}

	public String logicToFindDuplicatedFilesInFolder(String folderPath) {
		Map<String, Long> fileCount = new LinkedHashMap<>();
		StringBuilder logBuilder = new StringBuilder();
		boolean log = true;
		log(log, "==== Printing Folder Structure ====", logBuilder);
		loopFolder(folderPath, 1, fileCount, log, logBuilder);
		// Sorting by value
		Map<String, Long> sorted = fileCount.entrySet().stream().sorted(comparingByKey())
				.sorted(Collections.reverseOrder(comparingByValue()))
				.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
		// Looping Hash map
		log(log, "==== Printing Count ====", logBuilder);
		sorted.forEach((key, value) -> {
			log(log, key + " -> " + value, logBuilder);
		});
		return logBuilder.toString();
	}
	
	private String loopFolder(String folderPath, int folderLevel, Map<String, Long> fileCount, boolean log, StringBuilder logBuilder) {
		File folder = new File(folderPath);
		File[] folderFiles = folder.listFiles();
		
		String folderTitle = "==" + folder.getName() + " [" +  folderFiles.length + "]";
		if (folderLevel > 1) {
			Closure logg = new Closure("");
			IntStream.range(0, folderLevel - 1).forEach(i -> logg.append(" |"));
			logg.append(folderTitle);
			log(log, logg.getValue(), logBuilder);
		} else {
			log(log, folderTitle, logBuilder);
		}
		for (final File fileEntry : folderFiles) {
			String fileName = fileEntry.getName();
			if(fileEntry.isDirectory()) {
				loopFolder(fileEntry.getAbsolutePath(), folderLevel + 1, fileCount, log, logBuilder);
			} else {
				Closure logg = new Closure("");
				IntStream.range(0, folderLevel).forEach(i -> logg.append(" |"));
				logg.append("--" + fileName);
				log(log, logg.getValue(), logBuilder);
				Long count = fileCount.get(fileName);
				if(count == null) {
					count = 0l;
				}
				fileCount.put(fileName, ++count);
			}
		}
		return "";
	}
	
}
