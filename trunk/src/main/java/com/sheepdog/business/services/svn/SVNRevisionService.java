package com.sheepdog.business.services.svn;

import java.io.IOException;
import java.util.Set;


import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;

/**
 * SVNRevisionService provides an interface to get information about revisions
 * of required repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public interface SVNRevisionService {

	/**
	 * Get revisions of project from startRevision to endRevision. If you need
	 * just a single revision, then set startRevision, that is equal to
	 * endRevision. First repository revision is 0 and head repository revision
	 * is -1.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param startRevision
	 *            a revision to start from.
	 * @param endRevision
	 *            a revision to end at.
	 * @return Set of Revision object.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Set<Revision> getRevisions(User user, long startRevision, long endRevision) throws IOException,
			RepositoryAuthenticationExceptoin, IllegalArgumentException;

	/**
	 * Get revision, witch containing required file.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param file
	 *            Required file.
	 * @return Set of Revision object.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Set<Revision> getRevisionsByFile(User user, File file) throws IOException,
			RepositoryAuthenticationExceptoin, RepositoryAuthenticationExceptoin;

	/**
	 * Get latest revision from repository by Project object.
	 * 
	 * @return Revision object.
	 * 
	 * @throws IllegalArgumentException
	 *             if user and project are not registered or required repository
	 *             is incorrect.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Revision getLastRevision(User user) throws RepositoryAuthenticationExceptoin, IllegalArgumentException,
			IOException;

}
