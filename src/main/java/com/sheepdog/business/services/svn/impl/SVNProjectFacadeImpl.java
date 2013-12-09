package com.sheepdog.business.services.svn.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
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
	public boolean addSVNProject(Project project, User user)
			throws InvalidURLException, SVNException {
		return repositoryManager.addSVNProject(project, user);
	}

	@Override
	public SVNRepository getRepository(Project project)
			throws InvalidURLException {

		return repositoryManager.getRepository(project);
	}

	@Override
	public boolean checkUpdates(Project project, Revision latestRevision)
			throws SVNException {
		SVNRepository repo = repositoryManager.getRepository(project);
		return latestRevision.getRevision_no() != repo.getLatestRevision();
	}

	public SVNRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public void setRepositoryManager(SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

}
