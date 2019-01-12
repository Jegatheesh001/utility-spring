package com.myweb.utility.trails.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myweb.utility.aspects.Loggable;
import com.myweb.utility.trails.service.TrailsService;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On Jan 10, 2019
 *
 */
@RestController
@RequestMapping(value = "/mytrails")
public class TrailsController {

	@Autowired
	private TrailsService trailsService;

	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/findQueens/{queenNo}")
	public String findQueens(@PathVariable(name = "queenNo") Integer queenNo) {
		return trailsService.logicForFindingQueens(queenNo);
	}
}
