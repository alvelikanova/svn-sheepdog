package com.sheepdog.business.services.svn.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;

/**
 * Singleton provides connection to repositories.
 * 
 * @author Ivan Arkhipov
 * 
 */
@Service
@Scope("singleton")
public class SVNRepositoryManager {

	/**
	 * Map containing SVNRepository object of Project object.
	 */
	private Map<Project, SVNRepository> repositories = new HashMap<Project, SVNRepository>();

	/**
	 * Create and setup new repository connection.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param user
	 *            User object containing authentication info.
	 * @return Success flag.
	 * @throws InvalidURLException
	 *             - if URL of repository is not correct or protocol is not
	 *             supported.
	 * @throws SVNException
	 */
	public boolean addSVNProjectConnection(Project project, User user)
			throws SVNException, InvalidURLException {

		if (repositories.get(project.getUrl()) != null)
			return true;

		//TODO For Ivan добавить проверку - что будет если подключение не получено(Логировать пока что)? А если ошибка во время логики?
		SVNRepository repo = createSVNProjectConnection(project, user);
		repo.testConnection();

		repositories.put(project, repo);

		return true;
	}

	/**
	 * Get existing SVNRepository object by Project object.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @return SVNRepository object.
	 * @throws InvalidURLException
	 *             - if URL of repository is not correct.
	 */
	public SVNRepository getRepositoryConnection(Project project) {

		SVNRepository repo = repositories.get(project);

		if (repo == null)
			new InvalidURLException(project.getUrl());

		return repo;
	}

	/**
	 * Create SVNRepository object and set ISVNAuthenticationManager object with
	 * user's name and password.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param user
	 *            User object containing authentication info.
	 * @return Configured SVNRepository object.
	 * @throws SVNException
	 * @throws InvalidURLException
	 *             - if URL of repository is not correct or protocol is not
	 *             supported.
	 */
	private SVNRepository createSVNProjectConnection(Project project, User user)
			throws SVNException {

		connectionSetup(project.getUrl());

		SVNURL repositoryURL = SVNURL.parseURIEncoded(project.getUrl());

		SVNRepository repository = SVNRepositoryFactory.create(repositoryURL);

		ISVNAuthenticationManager authManager = SVNWCUtil.
				createDefaultAuthenticationManager(user.getLogin(), user.getPassword());

		repository.setAuthenticationManager(authManager);

		return repository;
	}

	/**
	 * Set up connection by protocol.
	 * 
	 * @param url
	 *            Repository URL.
	 */
	private void connectionSetup(String url) {
		if (url == null || url.length() < 6)
			throw new InvalidURLException(url);

		if (url.startsWith("http://") || url.startsWith("https://") || 
				url.startsWith("http://") || url.startsWith("https://") || 
				url.startsWith("file://")) {
			DAVRepositoryFactory.setup();
		} else {
			throw new InvalidURLException(url);
		}
	}

}
