package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.dal.entities.RevisionEntity;
import com.sheepdog.dal.providers.base.GenericDataProvider;

public interface RevisionDataProvider extends GenericDataProvider<RevisionEntity, Revision, Integer>{
	void createRevisionWithComment(Revision revision, String comment);
}
