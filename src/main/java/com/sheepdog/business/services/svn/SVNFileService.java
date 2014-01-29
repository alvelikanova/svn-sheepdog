package com.sheepdog.business.services.svn;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;

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
	 * Get composite of all files of actual repository tree.
	 * 
	 * @param user
	 *            User object containing authentication info and Project object
	 *            containing URL of required repository.
	 * @return Composite object with hierarchy of files and directories in
	 *         repository tree.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public FileTreeComposite getAllFiles(User user) throws IllegalArgumentException, IOException,
			RepositoryAuthenticationExceptoin;

	/**
	 * Get files, that were changed in the required revision.
	 * 
	 * @param project
	 *            Project object of required repository.
	 * @param revision
	 *            Revision object containing required number.
	 * @return Map containing File object with the type of the change applied to
	 *         the item represented by this object in that revision.
	 * @throws IllegalArgumentException
	 *             if user and project are not registered.
	 * 
	 * @throws IOException
	 *             a failure occurred while connecting to a repository
	 * @throws RepositoryAuthenticationExceptoin
	 *             if user authentication failed.
	 */
	public Map<File, TypeOfFileChanges> getFilesByRevision(User user, Revision revision)
			throws IllegalArgumentException, IOException, RepositoryAuthenticationExceptoin;

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
	@Deprecated
	public Set<File> getFilesByCreator(User user, Set<Revision> revisions) throws IllegalArgumentException,
			RepositoryAuthenticationExceptoin, IOException;

	/**
	 * Get text content of file in required revision from repository.
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
	public String getFileContent(User user, File file, int revisionNumber) throws IOException,
			RepositoryAuthenticationExceptoin, InvalidParameterException, IllegalArgumentException;
	
	/**
	 * Get actual text content of file from repository.
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
	public String getActualFileContent(User user, File file) throws IOException,
	RepositoryAuthenticationExceptoin, InvalidParameterException, IllegalArgumentException;
}
