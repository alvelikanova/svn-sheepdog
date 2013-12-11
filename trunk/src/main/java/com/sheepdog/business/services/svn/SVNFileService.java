package com.sheepdog.business.services.svn;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;

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
	 * @param project
	 *            Project object of required repository.
	 * @return Set of incomplete File objects, that are not containing revision
	 *         and creator information.
	 * @throws SVNException
	 *             a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public Set<File> getAllFiles(Project project) throws SVNException;

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
	 * @throws InvalidURLException
	 *             - if URL of Project object is not correct.
	 * @throws SVNException
	 *             - a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public Map<File, Character> getFilesByRevision(Project project, Revision revision) throws InvalidURLException,
			SVNException;

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
	 * @throws InvalidURLException
	 *             - if URL of Project object is not correct.
	 * @throws SVNException
	 *             - a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public Set<File> getFilesByCreator(Project project, User user, Set<Revision> revisions) throws InvalidURLException,
			SVNException;

	/**
	 * Get text content of file from repository.
	 * 
	 * @param project
	 *            Project object of required repository.
	 * @param file
	 *            File object containing path to required file.
	 * @return String of text content.
	 * @throws SVNException
	 *             - a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 * @throws InvalidParameterException
	 *             - if File object containing incorrect path, path of non-text
	 *             object. The message of exception contains that information.
	 */
	public String getFileContent(Project project, File file) throws SVNException, InvalidParameterException;
}
