package com.sheepdog.business.services.svn.impl;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.io.SVNRepository;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProjectFacade;

/**
 * SVNProjectFacadeImpl class is implementing SVNProjectFacade interface. That
 * class is decorator for SVNRepositoryManager singleton.
 * 
 * @author Ivan Arkhipov.
 * 
 */
@Service
public class SVNProjectFacadeImpl implements SVNProjectFacade {

	/**
	 * SVNRepositoryManager singleton containing {@link SVNRepository} objects.
	 */
	// @Autowired
	private SVNRepositoryManager repositoryManager;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNProjectFacadeImpl.class);

	public SVNProjectFacadeImpl(SVNRepositoryManager repositoryManager) {
		super();
		this.repositoryManager = repositoryManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNProjectFacade#addSVNProjectConnection
	 * (com.sheepdog.business.domain.entities.User)
	 */
	@Override
	public boolean addSVNProjectConnection(User user) throws InvalidURLException, RepositoryAuthenticationExceptoin,
			IllegalArgumentException, IOException {

		boolean added = false;

		added = repositoryManager.addSVNProjectConnection(user);

		return added;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNProjectFacade#getRepositoryConnection
	 * (com.sheepdog.business.domain.entities.User)
	 */
	@Override
	public SVNRepository getRepositoryConnection(User user) throws IllegalArgumentException {

		return repositoryManager.getRepositoryConnection(user);
	}

	public SVNRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public void setRepositoryManager(SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

}
