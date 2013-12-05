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
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.SVNFileRevision;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProvider;
import com.sheepdog.business.services.svn.SVNRevisionService;

public class SVNRevisionServiceImpl implements SVNRevisionService {
	// Пока что без спринга.

	// @Autowired
	private SVNProvider provider;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory
			.getLogger(SVNRevisionServiceImpl.class);

	/**
	 * Parse template for date string.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public SVNRevisionServiceImpl(SVNProvider provider) {
		super();
		this.provider = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNRevisionService#getRevisions(com
	 * .sheepdog.business.domain.entities.Project, long, long)
	 */
	@Override
	public Set<Revision> getRevisions(Project project, long startRevision,
			long endRevision) throws InvalidURLException, SVNException {
		Set<Revision> revisions = new HashSet<>();

		Collection logEntries = null;

		// Parameters of 'log' method are recommended by official guide
		logEntries = provider.getRepository(project).log(new String[] { "" },
				null, startRevision, endRevision, true, true);

		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			revisions.add(new Revision(logEntry.getRevision(), logEntry
					.getAuthor(), logEntry.getMessage(), logEntry.getDate()));
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
	public Set<Revision> getRevisionsByFile(Project project, File file)
			throws InvalidURLException, SVNException {

		Set<Revision> revisions = new HashSet<>();

		Collection revisionCollection = provider.getRepository(project)
				.getFileRevisions(file.getQualifiedName(), null, 0,
						provider.getRepository(project).getLatestRevision());

		for (Iterator iterator = revisionCollection.iterator(); iterator
				.hasNext();) {
			SVNFileRevision fileRevision = (SVNFileRevision) iterator.next();

			Revision revision = getRevisionByPropeties(fileRevision
					.getRevisionProperties());
			revision.setRevision_no(fileRevision.getRevision());

			revisions.add(revision);
		}

		return revisions;
	}

	/**
	 * Parse SVNProperties object.
	 * 
	 * @param property
	 *            SVNProperties object containing revision properties.
	 * @return Revision object.
	 */
	private Revision getRevisionByPropeties(SVNProperties property) {

		String author = property.getStringValue("svn:author");

		String message = property.getStringValue("svn:log");

		String dateString = property.getStringValue("svn:date");

		SimpleDateFormat format = new SimpleDateFormat(
				SVNRevisionServiceImpl.DATE_FORMAT, Locale.US);

		Date date = null;
		try {
			date = format.parse(dateString.substring(0, 19));
		} catch (ParseException e) {
			LOG.error(e.toString());
		}

		return new Revision(0, author, message, date);
	}

	public SVNProvider getProvider() {
		return provider;
	}

	public void setProvider(SVNProvider provider) {
		this.provider = provider;
	}

}
