package com.sheepdog.business.services.svn;

import java.util.Map;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;

public interface SVNFileService {

	/**
	 * Get all files of actual repository tree by Project object.
	 * 
	 * @param project
	 *            Project object of required repository.
	 * @return Set of incomplete File objects, that are not containing revision
	 *         and creator information.
	 * @throws SVNException
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
	 * @throws SVNException
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
	 * @throws SVNException
	 */
	public Set<File> getFilesByCreator(Project project, User user, Set<Revision> revisions) throws InvalidURLException,
			SVNException;

}
