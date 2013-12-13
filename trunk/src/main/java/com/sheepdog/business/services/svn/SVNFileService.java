package com.sheepdog.business.services.svn;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;

/**
 * SVNFileService class provides methods allowing to get information about files
 * of SVN repository and get content of text files.
 * 
 * @author Ivan Arkhipov.
 * 
 */
// @Service
public interface SVNFileService {

	/**
	 * Get all files of actual repository tree by Project object.
	 * 
	 * @param user
	 *            User object containing authentication info and Project object
	 *            containing URL of required repository.
	 * @return Set of incomplete File objects, that are not containing revision
	 *         and creator information. If repository is empty, method return
	 *         empty set.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Set<File> getAllFiles(User user) throws IllegalArgumentException, IOException,
			RepositoryAuthenticationExceptoin;

	/**
	 * Get files, that were changed in the required revision.
	 * 
	 * @param project
	 *            Project object of required repository.
	 * @param revision
	 *            Revision object containing required number.
	 * @return Map containing File object with the type of the change applied to
	 *         the item represented by this object in that revision. This type
	 *         can be one of the following: 'M' - Modified, 'A' - Added, 'D' -
	 *         Deleted, 'R' - Replaced.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Map<File, Character> getFilesByRevision(User user, Revision revision) throws IllegalArgumentException,
			IOException, RepositoryAuthenticationExceptoin;

	/**
	 * Get files, that were created by required user in required set of
	 * revisions.
	 * 
	 * @param project
	 *            Project object of required repository.
	 * @param user
	 *            Required creator.
	 * @param revisions
	 *            Set of revisions.
	 * @return Set of complete File objects.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Set<File> getFilesByCreator(User user, Set<Revision> revisions) throws IllegalArgumentException,
			RepositoryAuthenticationExceptoin, IOException;

	/**
	 * Get text content of file from repository.
	 * 
	 * @param project
	 *            Project object of required repository.
	 * @param file
	 *            File object containing path to required file.
	 * @return String of text content.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 * @throws InvalidParameterException
	 *             if there's no such path in revision, that is path of
	 *             directory or that is path of non-text file.
	 */
	public String getFileContent(User user, File file) throws IOException, RepositoryAuthenticationExceptoin,
			InvalidParameterException, IllegalArgumentException;
}
