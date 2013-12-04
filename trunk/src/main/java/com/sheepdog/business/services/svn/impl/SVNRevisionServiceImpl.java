package com.sheepdog.business.services.svn.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProvider;
import com.sheepdog.business.services.svn.SVNRevisionService;

public class SVNRevisionServiceImpl implements SVNRevisionService {

	/*
	 * Пока что без спринга.
	 */
	private SVNProvider provider;
	
	

	public SVNRevisionServiceImpl(SVNProvider provider) {
		super();
		this.provider = provider;
	}



	@Override
	public Set<Revision> getRevisions(Project project, long startRevision,
			long endRevision) {
		Set<Revision> revisions = new HashSet<>();

		Collection logEntries = null;

		try {
			logEntries = provider.getRepository(project).log(
					new String[] { "" }, null, startRevision, endRevision,
					true, true);
		} catch (InvalidURLException e) {

			e.printStackTrace();
		} catch (SVNException e) {
			e.printStackTrace();
		}

		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			revisions.add(new Revision(logEntry.getRevision(), logEntry
					.getAuthor(), logEntry.getMessage(), logEntry.getDate()));
		}
		return revisions;
	}
}
