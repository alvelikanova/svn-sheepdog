package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
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
	
	@Transactional
	@Override
	public Revision getLatestRevision(){
		Revision revision = null;
		try{
			Criteria cr = sessionFactory.getCurrentSession()
				    .createCriteria(RevisionEntity.class)
				    .setProjection(Projections.max("revisionNo"));
			cr.setMaxResults(1);
			RevisionEntity re = (RevisionEntity)cr.uniqueResult();
			revision = mappingService.map(re, Revision.class);
		} catch (Exception ex){
			LOG.error("Error loading revision", ex.getMessage());
			throw new DaoException(ex);
		}
		return revision;
	}

}
