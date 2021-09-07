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
import java.io.PrintStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.myweb.utility.tools.persistence.ToolsDao;
import com.myweb.utility.trails.service.ImportDataFromExcel;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On Jan 10, 2019
 *
 */
@Service
public class ToolsService {
	private static final Logger log = LoggerFactory.getLogger(ToolsService.class);
	
	@Value("${api.path.xmlBasePath}")
	private String xmlBasePath;
	@Value("${api.path.tempPath}")
	private String tempPath;

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
				System.out.println("Reading : " + fileEntry.getName());
				try {
					readContentFromFile(folderPath, fileEntry.getName(), "sql", files);
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

	public String readContentFromFile(String folderPath, String fileName, String extension, List<String> files) throws IOException {
		if (fileName.endsWith("." + extension)) {
			Path path = FileSystems.getDefault().getPath(folderPath, fileName);
			List<String> lines = new ArrayList<>();
			Files.lines(path, StandardCharsets.UTF_8).forEach(line -> {
				lines.add(line);
			});
			fileFiter(lines, folderPath, extension, true);
			// If tables are not empty
			if (lines.size() > 0) {
				if (!(lines.size() == 1 && cleanTextContent(lines.get(0)).trim().length() == 0))
					files.add(fileName);
			}
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
					// checking unwanted character endings in file
					String cleanLine = cleanTextContent(line);
					cleanLine = cleanLine.trim().length() == 0 ? "" : line;
					bw.write(cleanLine);
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

	public String logicToFindDuplicateFilesFromAnotherFolder(String folder, String folderToCheck) {
		// Fetching already uploaded files
		File folderToCheckPath = new File(folderToCheck);
		List<String> files = new ArrayList<>();
		for (final File fileEntry : folderToCheckPath.listFiles()) {
			String fileName = fileEntry.getName();
			files.add(fileName);
		}
		// base path of the images
		long duplicates = 0;
		File folderPath = new File(folder);
		StringBuilder logBuilder = new StringBuilder();
		for (final File fileEntry : folderPath.listFiles()) {
			String fileName = fileEntry.getName();
			if(files.contains(fileName)) {
				duplicates++;
				log(true, fileName, logBuilder);
				fileEntry.delete();
			}
		}
		log(true, "Total: " + duplicates, logBuilder);
		return logBuilder.toString();
	}

	/**
	 * @param filePath
	 * @param seconds
	 * @param milli
	 * @param from
	 * @created 2019-06-28
	 */
	public String logicToReadSubtitles(String filePath, Long seconds, Long milli, String from) {
		Path path = FileSystems.getDefault().getPath(filePath);
		List<String> lines = new ArrayList<>();
		try {
			DateTimeFormatter f1 = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");
			Files.lines(path, StandardCharsets.UTF_8).map(line -> {
				LocalTime fromTime = null;
				LocalTime startTime = null;
				LocalTime endTime = null;
				if (from != null) {
					fromTime = LocalTime.parse(from, f1);
				}
				if (line.contains("-->")) {
					String[] arr = line.split("-->");
					startTime = LocalTime.parse(arr[0].trim(), f1);
					if (fromTime == null || startTime.isAfter(fromTime)) {
						startTime = startTime.plus(seconds, ChronoUnit.SECONDS);
						startTime = startTime.plus(milli, ChronoUnit.MILLIS);
						endTime = LocalTime.parse(arr[1].trim(), f1);
						endTime = endTime.plus(seconds, ChronoUnit.SECONDS);
						endTime = endTime.plus(milli, ChronoUnit.MILLIS);
						return startTime.format(f1) + " --> " + endTime.format(f1);
					} else {
						return line;
					}
				} else {
					return line;
				}
			}).forEach(line -> {
				lines.add(line);
			});
			writeToFile(lines, path.getParent().toFile().getAbsolutePath(), "srt", path.toFile().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createEntityFromTable(String table) {
		String[] columns = table.split("\n");
		log.info("Total Columns: {}", columns.length);
		for (String column : columns) {
			column = column.trim();
			if (column.length() == 0)
				continue;
			String[] contents = column.split(" ");
			String name = contents[0].trim();
			if (name.startsWith("`")) {
				name = name.substring(1, name.length() - 1);
			}
			String type = contents[1].trim().toLowerCase();
			String className = getClassName(type);
			PrintStream stream = className != null ? System.out : System.err;
			stream.println("@Column(name = \"" + name + "\")");
			name = name.toLowerCase();
			stream.println("private " + className + " " + camelCase(name) + ";");
		}
	}

	private String getClassName(String type) {
		String className = null;
		if (type.contains("varchar") || type.contains("longtext") || type.contains("char") || type.contains("text")) {
			className = "String";
		} else if(type.contains("date")) {
			className = "Date";
		} else if(type.contains("int")) {
			className = "Integer";
		} else if(type.contains("decimal")) {
			className = "Double";
		}
		return className;
	}
	
	public String camelCase(String name) {
	    StringBuilder sb = new StringBuilder();
	    boolean capitalizeNext = false;
	    for (char c : name.toCharArray()) {
	        if (c == '_') {
	            capitalizeNext = true;
	        } else {
	            if (capitalizeNext) {
	                sb.append(Character.toUpperCase(c));
	                capitalizeNext = false;
	            } else {
	                sb.append(c);
	            }
	        }
	    }
	    return sb.toString();
	}
	
	private ImportDataFromExcel excelService;
	@Autowired
	public void setExcelService(ImportDataFromExcel excelService) {
		this.excelService = excelService;
	}

	public void importFromExcelToTable(String tableName, Map<String, String> requestParam) {
		excelService.importFromExcelToTable(tableName, requestParam);
	}

	@Autowired
	private ToolsDao dao;
	public void readFromXMLForRemittance(String fileName) {
		File inputFile = getFile(fileName);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			RemittanceSAXHandler handler = new RemittanceSAXHandler();
			parser.parse(inputFile.toURI().toString(), handler);

			dao.saveRemittanceDetails(handler.getClaimList(), fileName);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private File getFile(String fileName) {
		if (xmlBasePath.startsWith("http:") || xmlBasePath.startsWith("https:")) {
			try {
				URL website = new URL(xmlBasePath + fileName);
				File f = new File(tempPath + fileName);
				try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
						FileOutputStream fos = new FileOutputStream(f.getAbsolutePath())) {
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				}
				return f;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return new File(xmlBasePath + fileName);
	}

	public void readFromRemittance() {
		File folderPath = new File(xmlBasePath);
		for (final File fileEntry : folderPath.listFiles()) {
			readFromXMLForRemittance(fileEntry.getName());
		}
	}
}
