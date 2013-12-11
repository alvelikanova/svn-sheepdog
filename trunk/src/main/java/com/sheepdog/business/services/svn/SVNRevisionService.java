package com.sheepdog.business.services.svn;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.exceptions.InvalidURLException;

/**
 * SVNRevisionService provides an interface to get information about revisions
 * of required repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */
@Service
public interface SVNRevisionService {

	/**
	 * Get revisions of project from startRevision to endRevision
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param startRevision
	 *            - a revision to start from.
	 * @param endRevision
	 *            - a revision to end at.
	 * @return Set of Revision object.
	 * @throws InvalidURLException
	 *             if URL of Project object is not correct.
	 * @throws SVNException
	 *             a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public Set<Revision> getRevisions(Project project, long startRevision, long endRevision)
			throws InvalidURLException, SVNException;

	/**
	 * Get revision, witch containing required file.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param file
	 *            Required file.
	 * @return Set of Revision object.
	 * @throws InvalidURLException
	 *             if URL of Project object is not correct.
	 * @throws SVNException
	 *             a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public Set<Revision> getRevisionsByFile(Project project, File file) throws InvalidURLException, SVNException;

	/**
	 * Get latest revision from repository by Project object.
	 * 
	 * @return Revision object.
	 * 
	 * @throws InvalidURLException
	 *             if URL of Project object is not correct.
	 * @throws SVNException
	 *             a failure occurred while connecting to a repository or the
	 *             user authentication failed.
	 */
	public Revision getLastRevision(Project project) throws InvalidURLException, SVNException;

}
