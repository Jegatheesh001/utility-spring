package com.myweb.utility.tools.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
		File folder = new File(folderPath);
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				System.out.println("Reading : " + fileEntry.getName());
				try {
					readContentFromFile(folderPath, fileEntry.getName(), "sql");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public String readContentFromFile(String folderPath, String fileName, String extension) throws IOException {
		if (fileName.endsWith("." + extension)) {
			Path path = FileSystems.getDefault().getPath(folderPath, fileName);
			List<String> lines = new ArrayList<>();
			Files.lines(path, StandardCharsets.UTF_8).forEach(line -> {
				lines.add(line);
			});
			writeToFile(lines, folderPath, extension);
			System.out.println("Records : " + lines.size());
		}
		return null;
	}

	public String writeToFile(List<String> lines, String folderPath, String extension) {
		if (lines.size() > 0) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(folderPath + "/op/outputfile." + extension, true))) {
				for (String line : lines) {
					bw.write(line);
					bw.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}