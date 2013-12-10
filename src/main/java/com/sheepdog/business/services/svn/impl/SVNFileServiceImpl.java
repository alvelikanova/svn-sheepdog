package com.sheepdog.business.services.svn.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.SVNRevisionService;

public class SVNFileServiceImpl implements SVNFileService {

	// @Autowired
	private SVNProjectFacade projectFacade;

	// @Autowired
	private SVNRevisionService revisionService;

	public SVNFileServiceImpl(SVNProjectFacade projectFacade,
			SVNRevisionService revisionService) {
		super();
		this.projectFacade = projectFacade;
		this.revisionService = revisionService;
	}

	@Override
	public List<File> getAllFiles(Project project) throws SVNException {
		List<File> files = new ArrayList<>();

		getEntries(project, projectFacade.getRepositoryConnection(project), "", files);

		return files;
	}

	//TODO For Ivan что значит Map<File, String> и зачем, надо определиться List<File> или Map<File, String> или Set<File>
	@Override
	public Map<File, String> getFilesByRevision(Project project,
			Revision revision) {

		Map<File, String> files = new HashMap<>();

		Collection logEntries = null;

		try {
			logEntries = projectFacade.getRepositoryConnection(project).log(
					new String[] { "" }, null, revision.getRevisionNo(),
					revision.getRevisionNo(), true, true);
		} catch (InvalidURLException e) {
			e.printStackTrace();
		} catch (SVNException e) {
			e.printStackTrace();
		}

		for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			Collection changedPaths = logEntry.getChangedPaths().keySet();

			if (logEntry.getChangedPaths().size() < 1)
				continue;

			for (Iterator iterator = changedPaths.iterator(); iterator.hasNext();) {

				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(iterator.next());

				//TODO For Ivan set Revision also
				files.put((new File(project, null, entryPath.getPath(), entryPath.getCopyPath(), null, false)), 
						"" + entryPath.getType());
			}
		}
		return files;
	}

	@Override
	public Set<File> getFilesByCreator(Project project, User user)
			throws InvalidURLException, SVNException {

		Set<File> authFiles = new HashSet<>();

		Set<Revision> revisions = revisionService.getRevisions(project, 0, -1);
		Map<File, String> files;

		for (Revision r : revisions)
			if (user.getLogin().equals(r.getAuthor())) {
				files = getFilesByRevision(project, r);
				for (File f : files.keySet())
					if ("A".equals(files.get(f))) {
						f.setCreatorName(user.getLogin());
						authFiles.add(f);
					}
			}
		return authFiles;
	}

	/**
	 * Recursive method to traverse the repository tree starting at a particular
	 * path.
	 * 
	 * @param repo
	 *            SVNRepository object of needed project.
	 * @param path
	 *            Particular path.
	 * @param outputList
	 *            Result list of File object.
	 * @throws SVNException
	 */
	private void getEntries(Project project, SVNRepository repo, String path, List<File> outputList) 
			throws SVNException {
		Collection entries = repo.getDir(path, -1, null, (Collection) null);

		Iterator iterator = entries.iterator();

		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();

			File file = new File();
			file.setName(entry.getName());
			file.setQualifiedName(entry.getURL().getPath());
			//TODO For Ivan file.setCreatorName(Имя пользователя создавшего файл);
			//Set<Revision> revisions = revisionService.getRevisions(project, 0, -1);
			//TODO For Ivan setRevision(Revision) - подумать как лучше сделать
			file.setProject(project);
			
			outputList.add(file);

			if (entry.getKind() == SVNNodeKind.DIR) {
				getEntries(project, repo, (path.equals("")) ? entry.getName() : path
						+ "/" + entry.getName(), outputList);
			}

		}
	}
}
