package com.sheepdog.business.services.svn;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;

public interface SVNFileService {

	public List<File> getAllFiles(Project project) throws SVNException;

	public Map<File, String> getFilesByRevision(Project project,
			Revision revision);

	public Set<File> getFilesByCreator(Project project, User user)
			throws InvalidURLException, SVNException;
	

}