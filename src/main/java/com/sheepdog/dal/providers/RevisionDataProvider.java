package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.dal.entities.RevisionEntity;
import com.sheepdog.dal.providers.base.PageableDataProvider;

public interface RevisionDataProvider extends PageableDataProvider<RevisionEntity, Revision, Integer>{
	void createRevisionWithComment(Revision revision, String comment);
	Revision getLatestRevision();
}
