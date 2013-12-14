package com.sheepdog.dal.providers.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.dal.entities.RevisionEntity;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.dal.providers.RevisionDataProvider;

@Repository
public class RevisionDataProviderImpl extends BaseDataProviderImpl<RevisionEntity,Revision,Integer> implements RevisionDataProvider {
	
	@Transactional
	@Override
	public void createRevisionWithComment(Revision revision, String comment) throws DaoException {
		try {
			revision.setComment(comment);
			save(revision, RevisionEntity.class);
		} catch (Exception ex) {
			LOG.error("Error creating revision", ex.getMessage());
			throw new DaoException(ex);
		}
	}

}
