package com.sheepdog.business.services.svn;

import java.util.Set;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;

public interface SVNRevisionService {

	public Set<Revision> getRevisions(Project project, long startRevision,
			long endRevision);

}
