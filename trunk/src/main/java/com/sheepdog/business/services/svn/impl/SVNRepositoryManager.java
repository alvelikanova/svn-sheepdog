package com.sheepdog.business.services.svn.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;

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
	private Map<User, SVNRepository> repositories = new ConcurrentHashMap<User, SVNRepository>(0);

	/**
	 * Create and setup new repository connection.
	 * 
	 * @param user
	 *            User object containing authentication info and Project object
	 *            containing URL of required repository.
	 * @return Success flag.
	 * @throws InvalidURLException
	 *             if URL of repository is not correct or protocol is not
	 *             supported.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public boolean addSVNProjectConnection(User user) throws InvalidURLException, IllegalArgumentException,
			IOException, RepositoryAuthenticationExceptoin {

		if (user == null || user.getProject() == null)
			throw new IllegalArgumentException("Invalid User object: " + user.getLogin());

		if (repositories.get(user) != null)
			return true;

		SVNRepository repo;

		try {
			repo = createSVNProjectConnection(user);
		} catch (SVNException e) {
			throw new InvalidURLException(user.getProject().getUrl());
		}

		try {
			repo.testConnection();
		} catch (SVNAuthenticationException e) {
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		if (user.isAdmin()) {
			repositories.remove(User.UPDATE_USER);
			try {
				repositories.put(User.UPDATE_USER, createSVNProjectConnection(user));
			} catch (SVNException e) {
				throw new InvalidURLException(user.getProject().getUrl());
			}
		}

		repositories.put(user, repo);

		return true;
	}

	/**
	 * Get existing SVNRepository object by Project object.
	 * 
	 * @param user
	 *            User object containing URL of repository.
	 * @return SVNRepository object.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 */
	public SVNRepository getRepositoryConnection(User user) {

		SVNRepository repo = repositories.get(user);

		if (repo == null)
			new IllegalArgumentException("Invalid User object: " + user.getLogin());

		return repo;
	}

	/**
	 * Create SVNRepository object and set ISVNAuthenticationManager object with
	 * user's name and password.
	 * 
	 * @param user
	 *            User object containing authentication info and Project object
	 *            containing URL of required repository.
	 * 
	 * @return Configured SVNRepository object.
	 * @throws SVNException
	 *             if URL is malformed.
	 * @throws InvalidURLException
	 *             if URL of repository is not correct or protocol is not
	 *             supported.
	 */
	private SVNRepository createSVNProjectConnection(User user) throws SVNException, InvalidURLException {

		connectionSetup(user.getProject().getUrl());

		SVNURL repositoryURL = SVNURL.parseURIEncoded(user.getProject().getUrl());

		SVNRepository repository = SVNRepositoryFactory.create(repositoryURL);

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user.getLogin(),
				user.getPassword());

		repository.setAuthenticationManager(authManager);

		return repository;
	}

	/**
	 * Set up connection by protocol.
	 * 
	 * @param url
	 *            Repository URL.
	 * @throws InvalidURLException
	 *             if URL of repository is not correct or protocol is not
	 *             supported.
	 */
	private void connectionSetup(String url) throws InvalidURLException {
		if (url == null || url.length() < 6)
			throw new InvalidURLException(url);

		if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("http://")
				|| url.startsWith("https://") || url.startsWith("file://")) {
			DAVRepositoryFactory.setup();
		} else {
			throw new InvalidURLException(url);
		}
	}

}
