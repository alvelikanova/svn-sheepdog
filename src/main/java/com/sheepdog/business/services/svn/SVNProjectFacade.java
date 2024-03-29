package com.sheepdog.business.services.svn;

import java.io.IOException;

import javax.security.auth.RefreshFailedException;

import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;

/**
 * SVNProjectFacade service provides connection to required repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */

public interface SVNProjectFacade {

	/**
	 * Create new repository connection. If user in admin role, create a main
	 * connection to repository, that mapping by User.UPDATE_USER and needed to
	 * update services.
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
	 *             a failure occurred while connecting to a repository.
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication is failed.
	 */
	public boolean addSVNProjectConnection(User user) throws InvalidURLException, IllegalArgumentException,
			IOException, RepositoryAuthenticationExceptoin;

	/**
	 * Get existing SVNRepository object User object containing URL. If you need
	 * to get update connection, set User.UPDATE_USER to parameter 'user'.
	 * 
	 * @param user
	 *            User object containing URL of required repository.
	 * @return SVNRepository object.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 */
	public SVNRepository getRepositoryConnection(User user) throws IllegalArgumentException;

	/**
	 * Creating main connection to repository. Load properties of connection
	 * from property file.
	 * 
	 * @param url
	 *            URL of repository.
	 * @param login
	 *            Login of main connection.
	 * @param password
	 *            Password of main connection.
	 * 
	 * @throws RefreshFailedException
	 *             if properties loading is failed.
	 * @throws InvalidURLException
	 *             if URL of repository is not correct or protocol is not
	 *             supported.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository.
	 * @throws RepositoryAuthenticationExceptoin
	 *             if repository authentication is failed.
	 */
	public void createMainConnection(String url, String login, String password) throws RefreshFailedException,
			InvalidURLException, RepositoryAuthenticationExceptoin, IOException;

}
