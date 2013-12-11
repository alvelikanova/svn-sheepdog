package com.sheepdog.business.services.svn.impl;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProjectFacade;

@Service
public class SVNProjectFacadeImpl implements SVNProjectFacade {

	// @Autowired
	SVNRepositoryManager repositoryManager;

	public SVNProjectFacadeImpl(SVNRepositoryManager repositoryManager) {
		super();
		this.repositoryManager = repositoryManager;
	}

	@Override
	public boolean addSVNProjectConnection(Project project, User user) throws InvalidURLException, SVNException {
		return repositoryManager.addSVNProjectConnection(project, user);
	}

	@Override
	public SVNRepository getRepositoryConnection(Project project) throws InvalidURLException {

		return repositoryManager.getRepositoryConnection(project);
	}

	public SVNRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public void setRepositoryManager(SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

}
