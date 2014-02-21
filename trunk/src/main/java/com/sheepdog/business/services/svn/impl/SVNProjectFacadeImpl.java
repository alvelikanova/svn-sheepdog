package com.sheepdog.business.services.svn.impl;

import java.io.IOException;

import javax.security.auth.RefreshFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.io.SVNRepository;

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
	@Autowired
	private SVNRepositoryManager repositoryManager;

	@Value("${repository.url}")
	private String repoUrl;

	@Value("${repository.login}")
	private String repoLogin;

	@Value("${repository.password}")
	private String repoPass;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(SVNProjectFacadeImpl.class);

	public SVNProjectFacadeImpl() {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNProjectFacade#createMainConnection
	 * ()
	 */
	@Override
	public void createMainConnection() throws RefreshFailedException, InvalidURLException,
			RepositoryAuthenticationExceptoin, IOException {
		
		if(repoUrl == null || repoLogin == null || repoPass == null){
			throw new RefreshFailedException();
		}

		repositoryManager.createMainConnection(repoUrl, repoLogin, repoPass);
	}

	public SVNRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public void setRepositoryManager(SVNRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

}
