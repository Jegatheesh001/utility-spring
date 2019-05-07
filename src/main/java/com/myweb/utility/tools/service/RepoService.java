package com.myweb.utility.tools.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On May 6, 2019
 *
 */
@Service
public class RepoService {

	public SVNRepository getSVNFactory() {
		DAVRepositoryFactory.setup();

		String url;
		String password;
		String name = "jegatheesh";

		// url = "https://localhost/svn/medasrepository/";
		// password = "pass";
		url = "https://dc.medas.local/svn/medas-hims/";
		password = "jegatheesh123";

		SVNRepository repository = null;
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
			repository.setAuthenticationManager(authManager);
		} catch (SVNException e) {
		}
		return repository;
	}

	@SuppressWarnings("unchecked")
	public String getHistory(int limit) {
		long startRevision;
		long endRevision; // HEAD (the latest) revision

		StringBuilder contentLog = new StringBuilder();
		try {
			SVNRepository repository = getSVNFactory();
			Collection<SVNLogEntry> logEntries = null;

			// To get latest Version Details
			endRevision = repository.getLatestRevision();

			// Last 25 Details
			startRevision = endRevision - limit;

			logEntries = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);

			for (Iterator<SVNLogEntry> entries = logEntries.iterator(); entries.hasNext();) {
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				log(true, "---------------------------------------------", contentLog);
				log(true, "revision: " + logEntry.getRevision(), contentLog);
				log(true, "author: " + logEntry.getAuthor(), contentLog);
				log(true, "date: " + logEntry.getDate(), contentLog);
				log(true, "log message: " + logEntry.getMessage(), contentLog);

				if (logEntry.getChangedPaths().size() > 0) {
					String path = ((SVNLogEntryPath) logEntry.getChangedPaths().values().toArray()[0]).getPath();
					log(true, "branch: " + path.split("/")[2], contentLog);
					log(true, "", contentLog);
					log(true, "changed paths " + "(" + logEntry.getChangedPaths().size() + ") :", contentLog);
					Set<SVNLogEntryPath> changedPathsSet = logEntry.getChangedPaths().keySet();

					for (Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths()
								.get(changedPaths.next());
						log(true,
								" " + entryPath.getType() + " " + entryPath.getPath()
										+ ((entryPath.getCopyPath() != null) ? " (from " + entryPath.getCopyPath()
												+ " revision " + entryPath.getCopyRevision() + ")" : ""),
								contentLog);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentLog.toString();
	}

	String lineSeperator = "<br>";

	private void log(boolean log, String logContent, StringBuilder logBuilder) {
		if (log) {
			logBuilder.append(logContent + lineSeperator);
		}
		System.out.println(logContent);
	}
}
