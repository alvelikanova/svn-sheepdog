package com.sheepdog.business.services.svn.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;

public class SVNFileServiceImpl implements SVNFileService {

	public static final Character DELETED_FILE = new Character('D');

	public static final Character ADDED_FILE = new Character('A');

	public static final Character MODIFIED_FILE = new Character('M');

	public static final Character REPLACED_FILE = new Character('R');

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
	 * .business.domain.entities.Project)
	 */
	@Override
	public Set<File> getAllFiles(Project project) throws SVNException {
		SVNRepository repos = projectFacade.getRepositoryConnection(project);
		final long rev = repos.getLatestRevision();

		QuickEditor editor = new QuickEditor();

		repos.status(rev, "", SVNDepth.INFINITY, new ISVNReporterBaton() {
			@Override
			public void report(ISVNReporter reporter) throws SVNException {
				reporter.setPath("", null, rev, SVNDepth.INFINITY, true);
				reporter.finishReport();
			}
		}, editor);

		Set<File> files = editor.getFiles();

		for (File f : files)
			f.setProject(project);

		return setNameFieldsToManyFiles(files);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getFilesByRevision(
	 * com.sheepdog.business.domain.entities.Project,
	 * com.sheepdog.business.domain.entities.Revision)
	 */
	@Override
	public Map<File, Character> getFilesByRevision(Project project, Revision revision) throws InvalidURLException,
			SVNException {

		Map<File, Character> files = new HashMap<>();

		Collection logEntries = null;

		File tempFile;

		logEntries = projectFacade.getRepositoryConnection(project).log(new String[] { "" }, null,
				revision.getRevisionNo(), revision.getRevisionNo(), true, true);

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
				tempFile.setPath(entryPath.getPath().substring(entryPath.getPath().indexOf('/'))); // getPath()
				tempFile.setProject(project);
				tempFile.setRevision(revision);

				files.put(setNameFieldsToOneFile(tempFile), new Character(entryPath.getType()));
			}
		}
		return files;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNFileService#getFilesByCreator(com
	 * .sheepdog.business.domain.entities.Project,
	 * com.sheepdog.business.domain.entities.User, java.util.Set)
	 */
	@Override
	public Set<File> getFilesByCreator(Project project, User user, Set<Revision> revisions) throws InvalidURLException,
			SVNException {
		Set<File> authFiles = new HashSet<>();
		Map<File, Character> files;

		for (Revision r : revisions)
			if (user.getLogin().equals(r.getAuthor())) {
				files = getFilesByRevision(project, r);
				for (File f : files.keySet())
					if (SVNFileServiceImpl.ADDED_FILE.equals(files.get(f))) {
						f.setCreatorName(user.getLogin());

						authFiles.add(f);
					}
			}
		return authFiles;
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
	 * Complete File object by name and qualified name of file.
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

}
