package com.sheepdog.business.services.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;

public interface SVNProvider {

	public boolean addSVNProject(Project project, User user)
			throws InvalidURLException, SVNException;

	public SVNRepository getRepository(Project project)
			throws InvalidURLException;

}
