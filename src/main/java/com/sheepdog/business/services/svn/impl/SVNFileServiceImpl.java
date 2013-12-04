package com.sheepdog.business.services.svn.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.sheepdog.business.services.svn.SVNProvider;

public class SVNFileServiceImpl implements SVNFileService {

	/*
	 * Пока что без спринга.
	 */
	private SVNProvider provider;
	
	
	public SVNFileServiceImpl(SVNProvider provider) {
		super();
		this.provider = provider;
	}

	@Override
	public List<File> getAllFiles(Project project) throws SVNException {
		List<File> files = new ArrayList<>();

		getEntries(provider.getRepository(project), "", files);

		return files;
	}

	@Override
	public Set<File> getFilesByRevision(Project project, Revision revision) {

		Set<File> files = new HashSet<>();

		Collection logEntries = null;

		try {
			logEntries = provider.getRepository(project).log(
					new String[] { "" }, null, revision.getRevision_no(),
					revision.getRevision_no(), true, true);
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

			for (Iterator iterator = changedPaths.iterator(); iterator
					.hasNext();) {

				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
						.getChangedPaths().get(iterator.next());
				if (entryPath.getType() == 'D')
					continue;
				
				files.add(new File(entryPath.getPath(), entryPath.getCopyPath(), null, false));  

			}
		}
		return null;
	}

	@Override
	public Set<File> getFilesByCreator(Project project, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	private void getEntries(SVNRepository repo, String path,
			List<File> outputLIst) throws SVNException {
		Collection entries = repo.getDir(path, -1, null, (Collection) null);

		Iterator iterator = entries.iterator();

		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();

			File file = new File();
			file.setName(entry.getName());

			// можно дополнительно забить поля файла

			outputLIst.add(file);

			if (entry.getKind() == SVNNodeKind.DIR) {
				getEntries(repo, (path.equals("")) ? entry.getName() : path
						+ "/" + entry.getName(), outputLIst);
			}

		}
	}
}
