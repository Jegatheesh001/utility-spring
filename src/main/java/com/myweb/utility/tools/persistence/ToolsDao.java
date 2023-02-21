package com.myweb.utility.tools.persistence;

import java.util.List;
import java.util.Map;

/**
 * @author jegatheesh.mageswaran <br>
           Created on <b>05-Oct-2020</b>
 *
 */
public interface ToolsDao {

	void saveRemittanceDetails(List<Map<String, Object>> claimList, String fileName);

	List<String> getAllTables();

	List<String> getAllColumns(String tableName);

	Number getDataCountForQuery(String tableName, String query);
	
}
