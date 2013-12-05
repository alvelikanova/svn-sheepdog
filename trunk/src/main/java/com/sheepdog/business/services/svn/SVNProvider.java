package com.sheepdog.business.services.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;

public interface SVNProvider {

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
	 */
	public boolean addSVNProject(Project project, User user)
			throws InvalidURLException, SVNException;

	/**
	 * Get existing SVNRepository object by URL.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @return SVNRepository object.
	 * @throws InvalidURLException
	 *             - if URL of repository is not correct.
	 */
	public SVNRepository getRepository(Project project)
			throws InvalidURLException;

}
