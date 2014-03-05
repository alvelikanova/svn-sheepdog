package com.sheepdog.business.services.svn.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.sheepdog.business.domain.entities.Project;
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
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(SVNRepositoryManager.class);

	/**
	 * Map containing SVNRepository object of Project object.
	 */
	private Map<User, SVNRepository> repositories = new ConcurrentHashMap<User, SVNRepository>(0);

	public SVNRepositoryManager() {

	}

	/**
	 * Create and setup new repository connection for user.
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

		if (user == null || user.getProject() == null) {
			LOG.info("Creating connection is failed, because User object is invalid.");
			throw new IllegalArgumentException("Invalid User object: ");
		}
		if (repositories.get(user) != null) {
			repositories.remove(user);
		}

		SVNRepository repo;

		try {
			repo = createSVNProjectConnection(user);
		} catch (SVNException e) {
			LOG.info("Creating connection is failed, because User object is invalid.");
			throw new InvalidURLException(user.getProject().getUrl());
		}

		try {
			repo.testConnection();
		} catch (SVNAuthenticationException e) {
			LOG.info("Creating connection is failed, because user authentication is failed.");
			throw new RepositoryAuthenticationExceptoin(user);
		} catch (SVNException e) {
			LOG.info("Creating connection is failed, because connection is failed.");
			throw new IOException("Failed connection to URL:" + user.getProject().getUrl());
		}

		repositories.put(user, repo);

		LOG.info("Connection for user: " + user.getLogin() + " was added.");

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
	public SVNRepository getRepositoryConnection(User user) throws IllegalArgumentException {

		SVNRepository repo = repositories.get(user);

		if (repo == null) {
			new IllegalArgumentException("Invalid User object: " + user.getLogin());
		}
		return repo;
	}

	/**
	 * Create a main connection to repository.
	 * 
	 * @param url
	 *            URL of repository.
	 * @param login
	 *            Login of main connection.
	 * @param password
	 *            Password of main connection.
	 * @throws InvalidURLException
	 *             if URL of repository is not correct or protocol is not
	 *             supported.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public void createMainConnection(String url, String login, String password) throws InvalidURLException,
			IOException, RepositoryAuthenticationExceptoin {

		User tempUser = new User();
		tempUser.setLogin(login);
		tempUser.setPassword(password);
		tempUser.setProject(new Project(null, url));

		SVNRepository repo;

		try {
			repo = createSVNProjectConnection(tempUser);
		} catch (SVNException e) {
			throw new InvalidURLException(tempUser.getProject().getUrl());
		}

		try {
			repo.testConnection();
		} catch (SVNAuthenticationException e) {
			throw new RepositoryAuthenticationExceptoin(tempUser);
		} catch (SVNException e) {
			throw new IOException("Failed connection to URL:" + tempUser.getProject().getUrl());
		}

		repositories.put(User.getUpdateUser(), repo);
		LOG.info("Main connection to repository (URL: " + url + " ) was created");
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
