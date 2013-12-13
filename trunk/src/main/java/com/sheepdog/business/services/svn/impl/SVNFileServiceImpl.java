package com.sheepdog.business.services.svn.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;

/**
 * SVNFileServiceImpl is a simple implementation of SVNFileService interface.
 * This class using SVNProjectFacade to get access to required repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */
// @Service
public class SVNFileServiceImpl implements SVNFileService {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNFileServiceImpl.class);

	/**
	 * SVNProjectFacade object is provides connection to required repository.
	 */
	// @Autowired
	private SVNProjectFacade projectFacade;

	public SVNFileServiceImpl(SVNProjectFacade projectFacade) {
		super();
		this.projectFacade = projectFacade;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getAllFiles(com.sheepdog
	 * .business.domain.entities.User)
	 */
	@Override
	public Set<File> getAllFiles(User user) throws IllegalArgumentException, IOException,
			RepositoryAuthenticationExceptoin {
		SVNRepository repos = projectFacade.getRepositoryConnection(user);

		QuickEditor editor = new QuickEditor();

		try {
			final long rev = repos.getLatestRevision();

			repos.status(rev, "", SVNDepth.INFINITY, new ISVNReporterBaton() {
				@Override
				public void report(ISVNReporter reporter) throws SVNException {
					reporter.setPath("", null, rev, SVNDepth.INFINITY, true);
					reporter.finishReport();
				}
			}, editor);
		} catch (SVNAuthenticationException e) {
			LOG.info("User authentication failed. User: " + user.getLogin());
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Connection to repository failed. User: " + user.getLogin());
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		Set<File> files = editor.getFiles();

		for (File f : files)
			f.setProject(user.getProject());

		try {
			files = setNameFieldsToManyFiles(files);
		} catch (NullPointerException e) {
			if (files == null || files.isEmpty())
				return new HashSet<File>(0);
		}

		return files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getFilesByRevision(
	 * com.sheepdog.business.domain.entities.User,
	 * com.sheepdog.business.domain.entities.Revision)
	 */
	@Override
	public Map<File, TypeOfFileChanges> getFilesByRevision(User user, Revision revision)
			throws IllegalArgumentException, IOException, RepositoryAuthenticationExceptoin {

		Map<File, TypeOfFileChanges> files = new HashMap<>();

		Collection logEntries = null;

		File tempFile;

		try {
			logEntries = projectFacade.getRepositoryConnection(user).log(new String[] { "" }, null,
					revision.getRevisionNo(), revision.getRevisionNo(), true, true);

		} catch (SVNAuthenticationException e) {
			LOG.info("User authentication failed. User: " + user.getLogin());
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Connection to repository failed. User: " + user.getLogin());
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			Collection changedPaths = logEntry.getChangedPaths().keySet();

			if (logEntry.getChangedPaths().size() < 1)
				continue;

			for (Iterator iterator = changedPaths.iterator(); iterator.hasNext();) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(iterator.next());
				if (SVNNodeKind.DIR.equals(entryPath.getKind()))
					continue;

				tempFile = new File();
				tempFile.setPath(entryPath.getPath().substring(entryPath.getPath().indexOf('/')));
				tempFile.setProject(user.getProject());
				tempFile.setRevision(revision);

				files.put(setNameFieldsToOneFile(tempFile), TypeOfFileChanges.getType(entryPath.getType()));
			}
		}
		return files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getFilesByCreator(com
	 * .sheepdog.business.domain.entities.User, java.util.Set)
	 */
	@Override
	public Set<File> getFilesByCreator(User user, Set<Revision> revisions) throws IllegalArgumentException,
			RepositoryAuthenticationExceptoin, IOException {
		Set<File> authFiles = new HashSet<>();
		Map<File, TypeOfFileChanges> files;

		for (Revision r : revisions)
			if (user.getLogin().equals(r.getAuthor())) {
				files = getFilesByRevision(user, r);
				for (File f : files.keySet())
					if (TypeOfFileChanges.ADDED.equals(files.get(f))) {
						f.setCreatorName(user.getLogin());

						authFiles.add(f);
					}
			}
		return authFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getFileContent(com.
	 * sheepdog.business.domain.entities.User,
	 * com.sheepdog.business.domain.entities.File)
	 */
	@Override
	public String getFileContent(User user, File file) throws IOException, RepositoryAuthenticationExceptoin,
			InvalidParameterException, IllegalArgumentException {
		SVNRepository repo = projectFacade.getRepositoryConnection(user);

		SVNNodeKind nodeKind = null;
		try {
			nodeKind = repo.checkPath(file.getPath(), -1);
		} catch (SVNAuthenticationException e) {
			LOG.info("User authentication failed. User: " + user.getLogin());
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Connection to repository failed. User: " + user.getLogin());
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		if (nodeKind == SVNNodeKind.NONE || nodeKind == SVNNodeKind.DIR)
			throw new InvalidParameterException("That file is not exist: " + file.getPath());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SVNProperties properties = new SVNProperties();

		try {
			repo.getFile(file.getPath(), -1, properties, baos);
		} catch (SVNException e) {
			throw new InvalidParameterException("That file is not exist: " + file.getPath());
		}

		String mimeType = (String) properties.getStringValue((SVNProperty.MIME_TYPE));
		boolean isTextType = SVNProperty.isTextMimeType(mimeType);
		String content = "";

		if (isTextType)
			content = baos.toString();
		else
			throw new InvalidParameterException("That file is not containing a text: " + file.getPath());

		return content;
	}

	/**
	 * Complete all File objects by name and qualified name of file.
	 * 
	 * @param files
	 *            Set of files.
	 * @return Completed Files objects.
	 * @throws NullPointerException
	 */
	private Set<File> setNameFieldsToManyFiles(Set<File> files) throws NullPointerException {
		Set<File> tempFiles = new HashSet<>(files);

		for (File f : tempFiles)
			f = setNameFieldsToOneFile(f);

		return tempFiles;
	}

	/**
	 * Complete File object by name and qualified name of file. This method
	 * parse path field of File object.
	 * 
	 * @param f
	 *            File object.
	 * @return Completed Files object.
	 */
	private File setNameFieldsToOneFile(File f) {
		String path = f.getPath();

		f.setQualifiedName(f.getProject().getName() + "/" + path);
		f.setName(path.substring(path.lastIndexOf('/') + 1));
		return f;
	}

	public SVNProjectFacade getProjectFacade() {
		return projectFacade;
	}

	public void setProjectFacade(SVNProjectFacade projectFacade) {
		this.projectFacade = projectFacade;
	}

}
