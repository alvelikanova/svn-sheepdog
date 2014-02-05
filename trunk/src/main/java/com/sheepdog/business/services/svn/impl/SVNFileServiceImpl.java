package com.sheepdog.business.services.svn.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import com.sheepdog.business.domain.entities.FileTreeComposite;
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
@Service
public class SVNFileServiceImpl implements SVNFileService {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNFileServiceImpl.class);

	/**
	 * SVNProjectFacade object is provides connection to required repository.
	 */
	@Autowired
	private SVNProjectFacade projectFacade;

	public SVNFileServiceImpl() {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getAllFiles(com.sheepdog
	 * .business.domain.entities.User)
	 */
	@Override
	public FileTreeComposite getAllFiles(User user) throws IllegalArgumentException, IOException,
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

		Map dirProps = new HashMap<>(editor.getMyDirProps());
		Map fileProps = new HashMap<>(editor.getMyFileProps());

		LinkedList<File> files = editor.getFiles();

		try {
			for (File f : files)
				f.setProject(user.getProject());

			files = setNameFieldsToManyFiles(files);
		} catch (NullPointerException e) {
			if (files == null || files.isEmpty())
				return new FileTreeComposite();
		}

		FileTreeComposite root = createComposite(files, dirProps, fileProps);

		return root;
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
				for (Map.Entry<File, TypeOfFileChanges> entry : files.entrySet()) {
					if (TypeOfFileChanges.ADDED.equals(entry.getValue())) {
						entry.getKey().setCreatorName(user.getLogin());

						authFiles.add(entry.getKey());
					}
				}
			}
		return authFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getActualFileContent
	 * (com.sheepdog.business.domain.entities.User,
	 * com.sheepdog.business.domain.entities.File)
	 */
	@Override
	public String getActualFileContent(User user, File file) throws IOException, RepositoryAuthenticationExceptoin,
			InvalidParameterException, IllegalArgumentException {

		return getFileContent(user, file, -1);
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
	public String getFileContent(User user, File file, int revisionNumber) throws IOException,
			RepositoryAuthenticationExceptoin, InvalidParameterException, IllegalArgumentException {
		SVNRepository repo = projectFacade.getRepositoryConnection(user);

		SVNNodeKind nodeKind = null;
		try {
			nodeKind = repo.checkPath(file.getPath(), revisionNumber);
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
			repo.getFile(file.getPath(), revisionNumber, properties, baos);
		} catch (SVNException e) {
			throw new InvalidParameterException("That file is not exist: " + file.getPath());
		}

		String mimeType = (String) properties.getStringValue((SVNProperty.MIME_TYPE));
		boolean isTextType = SVNProperty.isTextMimeType(mimeType);
		String content = "";

		if (isTextType) {
			content = baos.toString();
		} else {
			throw new InvalidParameterException("That file is not containing a text: " + file.getPath());
		}
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
	private LinkedList<File> setNameFieldsToManyFiles(LinkedList<File> files) throws NullPointerException {

		for (File f : files) {
			f = setNameFieldsToOneFile(f);
		}

		return files;
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

	/**
	 * Method sorting input files by hierarchy levels and root composite
	 * recursively filling. Files and appropriate properties inserted into
	 * composites.
	 * 
	 * @param files
	 *            All repository files and directories.
	 * @param dirProps
	 *            Properties of directories from repository.
	 * @param fileProps
	 *            Properties of files from repository.
	 * @return Complete {@link FileTreeComposite} root object.
	 */
	private FileTreeComposite createComposite(LinkedList<File> files, Map dirProps, Map fileProps) {
		String path = "";
		Integer level = 0;

		List<FileTreeComposite> tempComposites = new LinkedList<>();
		Map tempProps = new HashMap<>();

		FileTreeComposite root = new FileTreeComposite();

		Map<Integer, Collection<FileTreeComposite>> levelFiles = new HashMap<>(0);

		for (File f : files) {
			path = f.getPath();
			level = path.split("/").length;

			if (f.isDir()) {
				tempProps = (Map) dirProps.get("/" + f.getPath());
			} else {
				tempProps = (Map) fileProps.get("/" + f.getPath());
			}

			if (tempProps == null) {
				tempProps = new HashMap<>(0);
			}

			if (levelFiles.containsKey(level)) {
				levelFiles.get(level).add(new FileTreeComposite(f, tempProps));
			} else {
				tempComposites.clear();
				tempComposites.add(new FileTreeComposite(f, tempProps));
				levelFiles.put(level, new ArrayList<>(tempComposites));
			}
		}

		for (FileTreeComposite ftc : levelFiles.get(1)) {

			root.addChild(ftc);

			fillComposite(ftc, levelFiles, 1);
		}

		root.setFile(File.getRootDir());

		return root;
	}

	/**
	 * If current composite is directory with not empty childs
	 * collection,recursive method fills composite files by checking for
	 * compliance with the hierarchy levels. If hierarchy of composite is
	 * defined, that composite is removing from collection of files levels.
	 * 
	 * @param composite
	 *            Current composite object.
	 * @param levelFiles
	 *            Collection containing composites by hierarchy level.
	 * @param position
	 *            Deep of hierarchy.
	 */
	private void fillComposite(FileTreeComposite composite, Map<Integer, Collection<FileTreeComposite>> levelFiles,
			int position) {

		if (!composite.getFile().isDir()) {
			return;
		}

		Collection<FileTreeComposite> levelDown = levelFiles.get(position + 1);

		if (levelDown != null) {

			String tempPath;

			Collection<FileTreeComposite> completeComposite = new LinkedList<>();

			for (FileTreeComposite ftc : levelDown) {
				tempPath = ftc.getFile().getPath();

				if (tempPath.split("/")[tempPath.split("/").length - 2].equals(composite.getFile().getName())) {
					composite.addChild(ftc);
					completeComposite.add(ftc);
				}

				fillComposite(ftc, levelFiles, position + 1);
			}

			for (FileTreeComposite ftcRemove : completeComposite) {
				levelDown.remove(ftcRemove);
			}
		}

	}

	public SVNProjectFacade getProjectFacade() {
		return projectFacade;
	}

	public void setProjectFacade(SVNProjectFacade projectFacade) {
		this.projectFacade = projectFacade;
	}

}
