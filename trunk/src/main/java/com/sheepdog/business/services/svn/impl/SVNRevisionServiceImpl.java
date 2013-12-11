package com.sheepdog.business.services.svn.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.SVNRevisionService;

@Service
public class SVNRevisionServiceImpl implements SVNRevisionService {

	@Autowired
	SVNProjectFacade projectFacade;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNRevisionServiceImpl.class);

	/**
	 * Parse template for date string.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public SVNRevisionServiceImpl(SVNProjectFacade projectFacade) {
		super();
		this.projectFacade = projectFacade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNRevisionService#getRevisions(com
	 * .sheepdog.business.domain.entities.Project, long, long)
	 */
	@Override
	public Set<Revision> getRevisions(Project project, long startRevision, long endRevision)
			throws InvalidURLException, SVNException {
		Set<Revision> revisions = new HashSet<>();

		Collection logEntries = null;

		logEntries = projectFacade.getRepositoryConnection(project).log(new String[] { "" }, null, startRevision,
				endRevision, true, true);

		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			revisions.add(new Revision(project, (int) logEntry.getRevision(), logEntry.getAuthor(), logEntry
					.getMessage(), logEntry.getDate()));
		}

		return revisions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNRevisionService#getRevisionsByFile
	 * (com.sheepdog.business.domain.entities.Project,
	 * com.sheepdog.business.domain.entities.File)
	 */
	@Override
	public Set<Revision> getRevisionsByFile(Project project, File file) throws InvalidURLException, SVNException {

		Set<Revision> revisions = new HashSet<>();

		Collection revisionCollection = projectFacade.getRepositoryConnection(project).getFileRevisions(
				file.getQualifiedName(), null, 0, projectFacade.getRepositoryConnection(project).getLatestRevision());

		for (Iterator iterator = revisionCollection.iterator(); iterator.hasNext();) {
			SVNFileRevision fileRevision = (SVNFileRevision) iterator.next();

			Revision revision = getRevisionByPropeties(project, fileRevision.getRevisionProperties());
			revision.setRevisionNo((int) fileRevision.getRevision());

			revisions.add(revision);
		}

		return revisions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNRevisionService#getLastRevision
	 * (com.sheepdog.business.domain.entities.Project)
	 */
	@Override
	public Revision getLastRevision(Project project) throws InvalidURLException, SVNException {

		long latestRevision = projectFacade.getRepositoryConnection(project).getLatestRevision();

		Set<Revision> revision = getRevisions(project, latestRevision - 1, latestRevision);
		for (Revision r : revision) {
			if (r != null)
				return r;
		}

		throw new SVNException(null);
	}

	/**
	 * Parse SVNProperties object.
	 * 
	 * @param property
	 *            SVNProperties object containing revision properties.
	 * @return Revision object.
	 */
	private Revision getRevisionByPropeties(Project project, SVNProperties property) {

		String author = property.getStringValue("svn:author");
		String message = property.getStringValue("svn:log");
		String dateString = property.getStringValue("svn:date");

		SimpleDateFormat format = new SimpleDateFormat(SVNRevisionServiceImpl.DATE_FORMAT, Locale.US);

		Date date = null;
		try {
			date = format.parse(dateString.substring(0, 19));
		} catch (ParseException e) {
			LOG.error(e.toString());
		}

		return new Revision(project, 0, author, message, date);
	}

	public SVNProjectFacade getProjectFacade() {
		return projectFacade;
	}

	public void setProjectFacade(SVNProjectFacade projectFacade) {
		this.projectFacade = projectFacade;
	}

}
