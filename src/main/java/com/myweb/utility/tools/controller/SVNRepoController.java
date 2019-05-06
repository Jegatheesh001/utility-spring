package com.myweb.utility.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.myweb.utility.aspects.Loggable;
import com.myweb.utility.tools.service.RepoService;

/**
 * Get Information About Repository
 * 
 * @author Jegatheesh <br>
 *         <b>Created</b> On May 6, 2019 <br>
 *
 * @Ref <a href=https://wiki.svnkit.com/>Svn kit Tutorials</a>
 */
@RestController
@RequestMapping(value = "${context.name}/svn")
public class SVNRepoController {

	@Autowired
	private RepoService repoService;

	@Loggable
	@RequestMapping(method = RequestMethod.GET, value = "/history")
	public String getHistory() {
		int limit = 25;
		return repoService.getHistory(limit);
	}

}
