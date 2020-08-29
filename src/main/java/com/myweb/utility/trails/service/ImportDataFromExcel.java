package com.myweb.utility.trails.service;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * @author jegatheesh.mageswaran <br>
 *         Created on <b>29-Aug-2020</b>
 *
 */
@Service
public class ImportDataFromExcel {
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void importFromExcelToTable(String tableName, Map<String, String> requestParam) {
		int rowNumber = 0;
		int position = 0;
		try (Workbook workbook = new XSSFWorkbook(new FileInputStream("C:\\Users\\ACER\\Desktop\\DDC 29-08.xlsx"))) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();

			int columns = 0;
			StringBuilder tableBuilder = new StringBuilder("create table " + tableName + "(");
			StringJoiner insertQuery = new StringJoiner(",");
			
			int startFrom = requestParam.containsKey("startFrom") ? Integer.parseInt(requestParam.get("startFrom")) : 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();
				Iterator<Cell> cellsInRow = currentRow.iterator();
				Query query = null;
				if (rowNumber == 0) {
					columns = currentRow.getLastCellNum();
					StringJoiner columnBuilder = new StringJoiner(",");
					while (cellsInRow.hasNext()) {
						Cell currentCell = cellsInRow.next();
						String columnName = currentCell.getStringCellValue();
						String key = "dt_" + columnName;
						String dataType = requestParam.containsKey(key) ? requestParam.get(key) : "varchar(255)";
						columnBuilder.add(columnName + " " + dataType);
						insertQuery.add(columnName);
					}
					tableBuilder.append(columnBuilder.toString());
					tableBuilder.append(")  ENGINE=InnoDB;");
					query = em.createNativeQuery(tableBuilder.toString());
				} else {
					query = em.createNativeQuery("insert into " + tableName + " (" + insertQuery.toString() + ") values ("
							+ Stream.generate(() -> "?").limit(columns).collect(Collectors.joining(",")) + ");");
					position = 0;
					while (cellsInRow.hasNext()) {
						Cell currentCell = cellsInRow.next();
						query.setParameter(++position, currentCell.getStringCellValue());
					}
				}
				if(startFrom > rowNumber) {
					rowNumber++;
					continue;
				}
				query.executeUpdate();
				rowNumber++;
			}
			System.out.println("Total Lines: " + (rowNumber - 1));
		} catch (Exception e) {
			System.out.println("Current Line: " + rowNumber + " " + position);
			e.printStackTrace();
		}
	}

}
