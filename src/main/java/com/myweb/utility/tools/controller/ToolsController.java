package com.myweb.utility.tools.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.myweb.utility.aspects.Loggable;
import com.myweb.utility.tools.service.DatabaseSearch;
import com.myweb.utility.tools.service.ToolsService;

/**
 * @author Jegatheesh <br>
 *         <b>Created</b> On Jan 10, 2019
 *
 */
@RestController
@RequestMapping(value = "${context.name}/tools")
public class ToolsController {
	@Autowired
	private ToolsService toolsService;
	@Autowired
	private DatabaseSearch searchService;
	
	@Value("${api.path.attachments}")
	private String attachmentPath;

	@RequestMapping(method = RequestMethod.GET)
	public String welcome() {
		return "Welcome";
	}

	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/importDataFromExcel/{filePath}")
	public String importDataFromExcel(@PathVariable(name = "filePath") String filePath) {
		return toolsService.logicForImportDataFromExcel(filePath);
	}
	
	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/readSQLFromFolder")
	public String readSQLFromFolder() {
		return toolsService.logicToReadSQLFromFolder("D:\\MyShareFolder\\SAP_DB");
	}
	
	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/renameFiles")
	public String renameFiles() {
		String folderPath = "\\\\192.168.0.135\\Shared_D\\Divya\\Dental_KNG\\12-13_03_19\\After Modification\\Treatment\\Filling\\Lingual\\Porcelain";
		toolsService.logicToRenameFiles(folderPath);
		return "";
	}
	
	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/mergeImages")
	public String mergeImages() {
		String folderPath = "\\\\192.168.0.135\\Shared_D\\Jegatheesh\\Tooth Image";
		toolsService.logicToMergeImages(folderPath);
		return "";
	}
	
	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/findDuplicatedFiles")
	public String findDuplicatedFilesInFolder() {
		String folderPath = "I:\\Movies";
		return toolsService.logicToFindDuplicatedFilesInFolder(folderPath);
	}
	
	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/findDuplicatesForUpload")
	public String duplicateCheckToUploadFromOneDrive() {
		String folder = "C:\\Users\\ACER\\Downloads\\OneDrive Photos\\ToUpload";
		String folderToCheck = "C:\\Users\\ACER\\Downloads\\OneDrive Photos\\Uploaded";
		return toolsService.logicToFindDuplicateFilesFromAnotherFolder(folder, folderToCheck);
	}
	
	@Loggable
	@GetMapping("/readSubtitles")
	public String readSubtitles() {
		String filePath = "I:\\Movies\\English\\Shazam (2019) [BluRay] [720p] [YTS.LT]\\Shazam!.2019.720p.BluRay.x264-[YTS.LT].srt";
		String fromTime = null;
		return toolsService.logicToReadSubtitles(filePath, 2l, 400l, fromTime);
	}
	
	@Loggable
	@PostMapping("/createEntityFromTable")
	public void createEntityFromTable(@RequestBody String table) {
		toolsService.createEntityFromTable(table);
	}
	
	@Loggable
	@GetMapping("/importFromExcelToTable/{tableName}")
	public void importFromExcelToTable(@PathVariable(name = "tableName") String tableName, @RequestParam Map<String, String> requestParam) {
		toolsService.importFromExcelToTable(tableName, requestParam);
	}
	
	@Loggable
	@GetMapping("/readFromXML/{fileName}")
	public void readFromXML(@PathVariable(name = "fileName") String fileNames) {
		for (String fileName : fileNames.split(",")) {
			toolsService.readFromXMLForRemittance(fileName);
		}
	}
	
	@Loggable
	@GetMapping("/readFromXML/fromPath")
	public void readFromRemittance() {
		toolsService.readFromRemittance();
	}
	
	@Loggable
	@GetMapping("/readFilesFromFolder")
	public String readFilesFromFolder() {
		return toolsService.readAttachmentsFromFolder(attachmentPath);
	}
	
	@GetMapping("/searchData")
	public String searchData() {
		return searchService.searchData();
	}
}
