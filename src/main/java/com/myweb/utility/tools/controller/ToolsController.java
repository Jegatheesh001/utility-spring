package com.myweb.utility.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myweb.utility.aspects.Loggable;
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
}
