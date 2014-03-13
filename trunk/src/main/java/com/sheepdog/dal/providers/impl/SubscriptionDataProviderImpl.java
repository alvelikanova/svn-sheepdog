package com.sheepdog.dal.providers.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.dal.providers.SubscriptionDataProvider;

@Repository
public class SubscriptionDataProviderImpl extends BaseDataProviderImpl<SubscriptionEntity, Subscription, Integer>
		implements SubscriptionDataProvider {

	@Transactional
	@Override
	public List<File> getFilesByUserName(String userName) throws DaoException {
		List<File> resultList = null;
		try {
			resultList = new ArrayList<>();
			Session session = sessionFactory.getCurrentSession();

			// Load user by login
			Criteria cr = session.createCriteria(UserEntity.class).add(Restrictions.eq("login", userName));
			cr.setMaxResults(1);
			UserEntity userEntity = (UserEntity) cr.uniqueResult();

			// Load all users' subscriptions
			cr = session.createCriteria(SubscriptionEntity.class).add(
					Restrictions.eq("userEntity.id", userEntity.getId()));
			@SuppressWarnings("unchecked")
			List<SubscriptionEntity> subscriptionList = cr.list();
			for (SubscriptionEntity se : subscriptionList) {
				FileEntity fileEntity = se.getFileEntity();
				File file = mappingService.map(fileEntity, File.class);
				resultList.add(file);
			}
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while getting files by username", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while getting files by username", ex.getMessage());
			throw new DaoException(ex);
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<Subscription> getSubscriptionsByQualifiedName(String qualifiedName) throws DaoException {
		List<Subscription> sublist = null;
		try {
			Session session = sessionFactory.getCurrentSession();

			// load file with qname from dbase
			Criteria crfile = session.createCriteria(FileEntity.class).add(
					Restrictions.eq("qualifiedName", qualifiedName));
			FileEntity fe = (FileEntity) crfile.uniqueResult();

			// load subscriptions by file's id
			Criteria crsub = session.createCriteria(SubscriptionEntity.class).add(
					Restrictions.eq("fileEntity.id", fe.getId()));
			sublist = crsub.list();
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while getting subscriptions by qualified name", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while getting subscriptions by qualified name", ex.getMessage());
			throw new DaoException(ex);
		}
		return sublist;
	}

	@Transactional
	@Override
	public void deleteSubscription(User user, File file) throws DaoException {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria fileCriteria = session.createCriteria(FileEntity.class).add(
					Restrictions.eq("qualifiedName", file.getQualifiedName()));
			FileEntity fe = (FileEntity) fileCriteria.uniqueResult();
			Criteria subscrCriteria = session.createCriteria(SubscriptionEntity.class).add(
					Restrictions.and(Restrictions.eq("fileEntity.id", fe.getId()),
							Restrictions.eq("userEntity.id", user.getId())));
			SubscriptionEntity se = (SubscriptionEntity) subscrCriteria.uniqueResult();
			session.delete(se);
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while deleting subscription", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while deleting subscription", ex.getMessage());
			throw new DaoException(ex);
		}
	}

	@Transactional
	@Override
	public void createSubscription(User user, File file) throws DaoException {
		try {
			Session session = sessionFactory.getCurrentSession();

			Criteria fileCriteria = session.createCriteria(FileEntity.class).add(
					Restrictions.eq("qualifiedName", file.getQualifiedName()));
			FileEntity fe = (FileEntity) fileCriteria.uniqueResult();

			File f = mappingService.map(fe, File.class);

			Subscription s = new Subscription(user, f);
			save(s, SubscriptionEntity.class);
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while creating subscription", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while creating subscription", ex.getMessage());
			throw new DaoException(ex);
		}
	}

	@Transactional
	@Override
	public boolean isSubscribed(User user, File file) throws DaoException {
		long rows = 0;
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria fileCriteria = session.createCriteria(FileEntity.class).add(
					Restrictions.eq("qualifiedName", file.getQualifiedName()));
			FileEntity fe = (FileEntity) fileCriteria.uniqueResult();

			Criteria subscrCriteria = session
					.createCriteria(SubscriptionEntity.class)
					.add(Restrictions.and(Restrictions.eq("fileEntity.id", fe.getId()),
							Restrictions.eq("userEntity.id", user.getId()))).setProjection(Projections.rowCount());
			rows = (long) subscrCriteria.uniqueResult();
			return (rows != 0);
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while checking subscription", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while checking subscription", ex.getMessage());
			throw new DaoException(ex);
		}
	}

	@Transactional
	@Override
	public List<Subscription> getSubscriptionsByUser(User user) throws DaoException {
		List<Subscription> resultList = new ArrayList<>();
		List<SubscriptionEntity> entityList = new ArrayList<>();
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria subscrCriteria = session.createCriteria(SubscriptionEntity.class).add(
					Restrictions.eq("userEntity.id", user.getId()));
			entityList = subscrCriteria.list();
			for (SubscriptionEntity se : entityList) {
				Subscription subscr = mappingService.map(se, Subscription.class);
				resultList.add(subscr);
			}
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while checking subscription", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while checking subscription", ex.getMessage());
			throw new DaoException(ex);
		}
		return resultList;
	}
}
