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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On Jan 10, 2019
 *
 */
@Service
public class ToolsService {
	
	private static final Logger log = LoggerFactory.getLogger(ToolsService.class);

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

	public void createEntityFromTable() {
		String table = "`consult_id` int(11) NOT NULL AUTO_INCREMENT,\r\n" + 
				"  `encounter_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `consult_date` date NOT NULL,\r\n" + 
				"  `ip_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `doctors_id` int(11) NOT NULL DEFAULT '0',\r\n" + 
				"  `consult_fee` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `op_number` varchar(25) DEFAULT NULL,\r\n" + 
				"  `department_id` int(11) NOT NULL DEFAULT '0',\r\n" + 
				"  `insurar_status` char(3) CHARACTER SET latin1 DEFAULT NULL,\r\n" + 
				"  `insurar_id` int(11) DEFAULT NULL,\r\n" + 
				"  `card_no` varchar(51) DEFAULT NULL,\r\n" + 
				"  `deductible` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `lab_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `closed_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `fee_app` varchar(15) DEFAULT NULL,\r\n" + 
				"  `edit_status` varchar(1) NOT NULL DEFAULT 'Y',\r\n" + 
				"  `invoice_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `direct_lab` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `refer_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `clinic_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `rdoctor_id` varchar(5) DEFAULT NULL,\r\n" + 
				"  `copay_amt` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `copay_percent` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `insurar_sub` smallint(5) unsigned DEFAULT NULL,\r\n" + 
				"  `icard_expiry` date DEFAULT NULL,\r\n" + 
				"  `network_type` varchar(105) DEFAULT NULL,\r\n" + 
				"  `bill_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `previous_due` decimal(10,3) DEFAULT '0.000',\r\n" + 
				"  `balance_due` decimal(10,3) DEFAULT '0.000',\r\n" + 
				"  `discount` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `bill_submit` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `entered_date` datetime DEFAULT NULL,\r\n" + 
				"  `patient_age` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `corp_disc` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `patient_agemonth` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `deductable_type` varchar(5) DEFAULT NULL,\r\n" + 
				"  `deductable_amt` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `patient_ageweek` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `sl_certificate` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `sickleave_amt` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `invoice_no` varchar(20) CHARACTER SET latin1 DEFAULT NULL,\r\n" + 
				"  `barcode_no` bigint(20) unsigned DEFAULT NULL,\r\n" + 
				"  `file_no` varchar(45) DEFAULT NULL,\r\n" + 
				"  `billed_to` varchar(1) NOT NULL DEFAULT 'P',\r\n" + 
				"  `patient_name` varchar(145) DEFAULT NULL,\r\n" + 
				"  `rej_amt` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `lab_slno` varchar(45) DEFAULT NULL,\r\n" + 
				"  `enteredby` int(11) DEFAULT NULL,\r\n" + 
				"  `insurar_deduct` varchar(1) DEFAULT 'A',\r\n" + 
				"  `gross_amt` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `gross_amt_curr` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `co_gross` varchar(1) DEFAULT 'N',\r\n" + 
				"  `copay_percenttrt` varchar(45) DEFAULT NULL,\r\n" + 
				"  `visit_status` varchar(2) NOT NULL DEFAULT 'N',\r\n" + 
				"  `cert_no` varchar(45) DEFAULT NULL,\r\n" + 
				"  `dependent_no` varchar(45) DEFAULT NULL,\r\n" + 
				"  `doctor_view` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `deductible1` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `corp_disc1` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `copay_amt1` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `consult_fee1` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `fee_app1` varchar(15) DEFAULT NULL,\r\n" + 
				"  `employer` varchar(145) DEFAULT NULL,\r\n" + 
				"  `leave_days` varchar(405) DEFAULT NULL,\r\n" + 
				"  `appoint_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `valid_upto` date DEFAULT NULL,\r\n" + 
				"  `refer_diag` varchar(405) DEFAULT NULL,\r\n" + 
				"  `file_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `bill_no` varchar(20) DEFAULT NULL,\r\n" + 
				"  `start_time` datetime DEFAULT NULL,\r\n" + 
				"  `end_time` datetime DEFAULT NULL,\r\n" + 
				"  `for_alllab` varchar(5) DEFAULT NULL,\r\n" + 
				"  `deductible_lab` varchar(5) DEFAULT NULL,\r\n" + 
				"  `min_value` varchar(15) DEFAULT NULL,\r\n" + 
				"  `max_value` varchar(15) DEFAULT NULL,\r\n" + 
				"  `copay_consult` varchar(5) DEFAULT NULL,\r\n" + 
				"  `office_id` int(11) NOT NULL DEFAULT '0',\r\n" + 
				"  `deductible_rad` varchar(5) DEFAULT NULL,\r\n" + 
				"  `for_allrad` varchar(5) DEFAULT NULL,\r\n" + 
				"  `copay_medicine` varchar(5) DEFAULT NULL,\r\n" + 
				"  `copay_percentrad` varchar(5) DEFAULT NULL,\r\n" + 
				"  `pat_type` varchar(15) DEFAULT NULL,\r\n" + 
				"  `rej_reason` text,\r\n" + 
				"  `correction_type` varchar(45) DEFAULT NULL,\r\n" + 
				"  `corr_comment` varchar(405) DEFAULT NULL,\r\n" + 
				"  `appr_no` varchar(45) DEFAULT NULL,\r\n" + 
				"  `approved_date` datetime DEFAULT NULL,\r\n" + 
				"  `resub_status` char(1) DEFAULT 'N',\r\n" + 
				"  `write_off` char(1) DEFAULT 'N',\r\n" + 
				"  `writeoff_amt` decimal(10,3) NOT NULL DEFAULT '0.000',\r\n" + 
				"  `renet_claim1` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `reresub_status` char(1) DEFAULT 'N',\r\n" + 
				"  `rerej_amt1` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `renet_claim2` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `rerej_reason1` varchar(45) DEFAULT NULL,\r\n" + 
				"  `writeoff_date` date DEFAULT NULL,\r\n" + 
				"  `referby_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `referby_name` varchar(45) DEFAULT NULL,\r\n" + 
				"  `referto_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `referto_name` varchar(45) DEFAULT NULL,\r\n" + 
				"  `extra_card_no` varchar(50) DEFAULT NULL,\r\n" + 
				"  `daman_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `rerej_reason` varchar(405) DEFAULT NULL,\r\n" + 
				"  `renet_claim` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `package_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `acc_code` varchar(15) DEFAULT NULL,\r\n" + 
				"  `child_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `refer_consult` varchar(45) DEFAULT NULL,\r\n" + 
				"  `icard_effective` date DEFAULT NULL,\r\n" + 
				"  `employee_id` varchar(45) DEFAULT NULL,\r\n" + 
				"  `rerej_amt` decimal(10,3) unsigned NOT NULL DEFAULT '0.000',\r\n" + 
				"  `close_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `copay_percentdental` varchar(5) DEFAULT NULL,\r\n" + 
				"  `deductible_trt` varchar(5) DEFAULT NULL,\r\n" + 
				"  `deductible_dental` varchar(5) DEFAULT NULL,\r\n" + 
				"  `deductible_medicine` varchar(5) DEFAULT NULL,\r\n" + 
				"  `lab_min` varchar(5) DEFAULT NULL,\r\n" + 
				"  `lab_max` varchar(15) DEFAULT NULL,\r\n" + 
				"  `rad_min` varchar(5) DEFAULT NULL,\r\n" + 
				"  `rad_max` varchar(5) DEFAULT NULL,\r\n" + 
				"  `trt_max` varchar(5) DEFAULT NULL,\r\n" + 
				"  `trt_min` varchar(5) DEFAULT NULL,\r\n" + 
				"  `dental_min` varchar(5) DEFAULT NULL,\r\n" + 
				"  `dental_max` varchar(5) DEFAULT NULL,\r\n" + 
				"  `medicine_min` varchar(5) DEFAULT NULL,\r\n" + 
				"  `medicine_max` varchar(5) DEFAULT NULL,\r\n" + 
				"  `for_alltrt` varchar(5) DEFAULT NULL,\r\n" + 
				"  `for_alldental` varchar(5) DEFAULT NULL,\r\n" + 
				"  `for_allmedicine` varchar(5) DEFAULT NULL,\r\n" + 
				"  `invoice_max_deduct` varchar(10) DEFAULT NULL,\r\n" + 
				"  `invoice_max_liability` varchar(10) DEFAULT NULL,\r\n" + 
				"  `visit_doctor` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `visit_dept` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `nurse_view` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `nurse_start_time` datetime DEFAULT NULL,\r\n" + 
				"  `visit_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `nurse_end_time` datetime DEFAULT NULL,\r\n" + 
				"  `visit_type` varchar(5) NOT NULL DEFAULT 'N' COMMENT 'visit_type can be N or F for New & Followup',\r\n" + 
				"  `encounter_type` varchar(10) DEFAULT NULL,\r\n" + 
				"  `balance_due_head` varchar(10) DEFAULT NULL,\r\n" + 
				"  `discount_head` varchar(10) DEFAULT NULL,\r\n" + 
				"  `copay_sponsor` varchar(10) DEFAULT NULL,\r\n" + 
				"  `copay_sponsor_perc` varchar(10) DEFAULT NULL,\r\n" + 
				"  `sponsor_app` varchar(10) DEFAULT NULL,\r\n" + 
				"  `ipFlag` varchar(1) NOT NULL DEFAULT 'O',\r\n" + 
				"  `admit_date` datetime DEFAULT NULL,\r\n" + 
				"  `registration_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `admited_doctor` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `sponsor_deduct_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `price_change1` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `price_change2` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `post_writeoff` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `designation` varchar(45) DEFAULT NULL,\r\n" + 
				"  `verified_date` date DEFAULT NULL,\r\n" + 
				"  `verified_by` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `price_change3` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `disc_deduct` varchar(45) DEFAULT NULL,\r\n" + 
				"  `assess_done` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `vitals_done` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `hopi_done` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `diagnosis_done` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `medicine_prescribed` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `treat_prescribed` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `treat_done` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `history_done` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `invest_prescribed` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `request_mode` varchar(15) DEFAULT NULL,\r\n" + 
				"  `policy_number` varchar(25) DEFAULT NULL,\r\n" + 
				"  `preapp_limit` varchar(12) DEFAULT NULL,\r\n" + 
				"  `networkoffice_id` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `refered_consult_id` int(11) DEFAULT '0',\r\n" + 
				"  `patientName_ar` varchar(60) DEFAULT NULL,\r\n" + 
				"  `referal_notes` text,\r\n" + 
				"  `admitted_doctor` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `queue_id` varchar(200) DEFAULT NULL,\r\n" + 
				"  `copay_patient` varchar(15) DEFAULT NULL,\r\n" + 
				"  `reopen_status` varchar(1) DEFAULT 'N',\r\n" + 
				"  `online_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `oasis_visitId` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `location` varchar(450) DEFAULT NULL,\r\n" + 
				"  `request_status` varchar(1) DEFAULT 'N',\r\n" + 
				"  `ip_number` varchar(10) DEFAULT NULL,\r\n" + 
				"  `category_id` varchar(10) DEFAULT NULL,\r\n" + 
				"  `deductible_ip` varchar(45) DEFAULT NULL,\r\n" + 
				"  `copay_ip` varchar(45) DEFAULT NULL,\r\n" + 
				"  `ip_min` varchar(45) DEFAULT NULL,\r\n" + 
				"  `ip_max` varchar(45) DEFAULT NULL,\r\n" + 
				"  `for_allip` varchar(45) DEFAULT NULL,\r\n" + 
				"  `claim_no` varchar(45) DEFAULT NULL,\r\n" + 
				"  `receipt_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `sms_language` varchar(45) DEFAULT NULL,\r\n" + 
				"  `patient_agedays` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `approval_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `copay_maternity` varchar(45) DEFAULT NULL,\r\n" + 
				"  `maternity_min` varchar(45) DEFAULT NULL,\r\n" + 
				"  `maternity_max` varchar(45) DEFAULT NULL,\r\n" + 
				"  `copay_optical` varchar(45) DEFAULT NULL,\r\n" + 
				"  `optical_min` varchar(45) DEFAULT NULL,\r\n" + 
				"  `optical_max` varchar(45) DEFAULT NULL,\r\n" + 
				"  `copay_diagnostic` varchar(45) DEFAULT NULL,\r\n" + 
				"  `diagnostic_min` varchar(45) DEFAULT NULL,\r\n" + 
				"  `diagnostic_max` varchar(45) DEFAULT NULL,\r\n" + 
				"  `request_no` int(10) unsigned DEFAULT NULL,\r\n" + 
				"  `confidential` varchar(1) DEFAULT 'N',\r\n" + 
				"  `claim_edit_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `refer_consult_status` varchar(1) DEFAULT 'N',\r\n" + 
				"  `client_portal` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `clinic_lab_ack` varchar(1) NOT NULL DEFAULT 'Y',\r\n" + 
				"  `surgery_status` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `dept_confidential` varchar(1) NOT NULL DEFAULT 'N',\r\n" + 
				"  `discount_remarks` longtext,";
		String[] columns = table.split("\r\n");
		log.info("Total Columns: {}", columns.length);
		for (String column : columns) {
			column = column.trim();
			String[] contents = column.split(" ");
			String name = contents[0].trim();
			if(name.startsWith("`")) {
				name = name.substring(1, name.length() - 1);
			}
			String type = contents[1].trim().toLowerCase();
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
			PrintStream stream = className != null ? System.out : System.err;
			stream.println("@Column(name = \"" + name + "\")");
			name = name.toLowerCase();
			stream.println("private " + className + " " + name + ";");
		}
	}
	
}
