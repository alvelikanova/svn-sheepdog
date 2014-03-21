package com.sheepdog.business.services.svn.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.SVNRevisionService;

/**
 * SVNRevisionServiceImpl class allows to get information about any revisions of
 * repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */
@Service
public class SVNRevisionServiceImpl implements SVNRevisionService {

	/**
	 * SVNProjectFacade object is provides connection to required repository.
	 */
	@Autowired
	SVNProjectFacade projectFacade;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(SVNRevisionServiceImpl.class);

	/**
	 * Template of parsing the date string.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public SVNRevisionServiceImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNRevisionService#getRevisions(com
	 * .sheepdog.business.domain.entities.User, long, long)
	 */
	@Override
	public Set<Revision> getRevisions(User user, long startRevision, long endRevision) throws IOException,
			RepositoryAuthenticationExceptoin, IllegalArgumentException {
		Set<Revision> revisions = new HashSet<>(0);

		Collection logEntries = null;

		try {
			logEntries = projectFacade.getRepositoryConnection(user).log(new String[] { "" }, null, startRevision,
					endRevision, true, true);
		} catch (SVNAuthenticationException e) {
			LOG.info("User authentication failed. User: " + user.getLogin());
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Connection to repository failed. User: " + user.getLogin());
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			if (logEntry.getRevision() == 1) {
				continue;
			}
			revisions.add(new Revision(user.getProject(), (int) logEntry.getRevision(), logEntry.getAuthor(), logEntry
					.getMessage(), logEntry.getDate()));
		}

		if (revisions == null || revisions.isEmpty()) {
			return new HashSet<Revision>(0);
		}

		return revisions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNRevisionService#getRevisionsByFile
	 * (com.sheepdog.business.domain.entities.User,
	 * com.sheepdog.business.domain.entities.File)
	 */
	@Override
	public Set<Revision> getRevisionsByFile(User user, File file) throws IOException,
			RepositoryAuthenticationExceptoin, IllegalArgumentException {

		Set<Revision> revisions = new HashSet<>();

		Collection revisionCollection;

		try {
			revisionCollection = projectFacade.getRepositoryConnection(user).getFileRevisions(file.getPath(), null, 0,
					projectFacade.getRepositoryConnection(user).getLatestRevision());
		} catch (SVNAuthenticationException e) {
			LOG.info("User authentication failed. User: " + user.getLogin());
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Connection to repository failed. User: " + user.getLogin());
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		for (Iterator iterator = revisionCollection.iterator(); iterator.hasNext();) {
			SVNFileRevision fileRevision = (SVNFileRevision) iterator.next();

			Revision revision = getRevisionByPropeties(user.getProject(), fileRevision.getRevisionProperties());
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
	 * (com.sheepdog.business.domain.entities.User)
	 */
	@Override
	public Revision getLastRevision(User user) throws RepositoryAuthenticationExceptoin, IllegalArgumentException,
			IOException {

		long latestRevision = 0;

		try {
			latestRevision = projectFacade.getRepositoryConnection(user).getLatestRevision();
		} catch (SVNAuthenticationException e) {
			LOG.info("User authentication failed. User: " + user.getLogin());
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Connection to repository failed. User: " + user.getLogin());
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		Set<Revision> revision = getRevisions(user, latestRevision, latestRevision);
		for (Revision r : revision) {
			if (r != null)
				return r;
		}

		throw new IllegalArgumentException("Invalid repository");
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
