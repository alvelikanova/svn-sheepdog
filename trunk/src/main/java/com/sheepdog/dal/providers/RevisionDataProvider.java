package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.dal.entities.RevisionEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface RevisionDataProvider {
	RevisionEntity findRevisionById(Integer id);
	void createRevision(RevisionEntity revisionEntity);
	void deleteRevisionById(Integer revisionId);
	List<RevisionEntity> findAllRevisions();
	PagedList<Revision> getRevisionObjects(LoadOptions loadOptions);
}
