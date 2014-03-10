package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while creating revision with comment", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("unknown error occured while creating revision with comment", ex.getMessage());
			throw new DaoException(ex);
		}
	}
	
	@Transactional
	@Override
	public Revision getLatestRevision() throws DaoException {
		Revision revision = null;
		try{
			Session session = sessionFactory.getCurrentSession();
			Criteria cr = session
				    .createCriteria(RevisionEntity.class)
				    .setProjection(Projections.max("revisionNo"));
			cr.setMaxResults(1);
			int latestRevisionNumber = (int)cr.uniqueResult();
			cr = session.createCriteria(RevisionEntity.class).add(Restrictions.eq("revisionNo", latestRevisionNumber));
			RevisionEntity re = (RevisionEntity)cr.uniqueResult();
			revision = mappingService.map(re, Revision.class);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while getting latest revision", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while getting latest revision", ex.getMessage());
			throw new DaoException(ex);
		}
		return revision;
	}

}
