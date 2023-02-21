package com.myweb.utility.tools.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import com.myweb.utility.tools.persistence.ToolsDao;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Jun 6, 2022
 *
 */
@Service
public class DatabaseSearch {
	@Autowired
	private ToolsDao dao;
	
	public String searchData() {
		List<String> tables = dao.getAllTables();
		List<String> skipData = Arrays.asList("cvs", "diagnosisfindings", "installments");
		for(String tableName : tables) {
			System.out.println("Table name : " + tableName);
			List<String> columnList = dao.getAllColumns(tableName);
			if(!columnList.isEmpty() && !skipData.contains(tableName)) {
				String columns = columnList.stream().collect(Collectors.joining(","));
				System.out.println(columns);
				searchDataByValue("7427", tableName, columnList);
			}
		}
		return null;
	}

	private void searchDataByValue(String data, String tableName, List<String> columnList) {
		String query = getQuery(data, columnList);
		List<String> list = new ArrayList<>();
		try {
			long count = dao.getDataCountForQuery(tableName, query).longValue();
			if(count > 0) {
				System.err.println(tableName.toUpperCase() + ": Data count : " + count);
			}
		} catch (InvalidDataAccessResourceUsageException e) {
			list.add(tableName);
		}
		list.stream().forEach(System.out::println);
	}

	private String getQuery(String data, List<String> columnList) {
		StringJoiner queryBuilder = new StringJoiner(" or ");
		for(String column : columnList) {
			queryBuilder.add(column + " like '" + data + "'");
		}
		return "(".concat(queryBuilder.toString()).concat(")");
	}
}
