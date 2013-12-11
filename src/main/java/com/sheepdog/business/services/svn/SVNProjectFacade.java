package com.sheepdog.business.services.svn;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;

/**
 * SVNProjectFacade service provides connection to required repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */
@Service
public interface SVNProjectFacade {

	/**
	 * Create new repository connection.
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
	 *             a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public boolean addSVNProjectConnection(Project project, User user) throws InvalidURLException, SVNException,
			RepositoryAuthenticationExceptoin;

	/**
	 * Get existing SVNRepository object by URL.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @return SVNRepository object.
	 * @throws InvalidURLException
	 *             - if URL of repository is not correct.
	 */
	public SVNRepository getRepositoryConnection(Project project) throws InvalidURLException;

}
