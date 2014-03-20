package com.sheepdog.dal.providers.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.dal.providers.UserDataProvider;

@Repository
public class UserDataProviderImpl extends BaseDataProviderImpl<UserEntity,User,Integer> implements UserDataProvider{

	@Transactional
	@Override
	public User getUserByLogin(String login) throws DaoException {
		User user = null;
		try{
		Criteria cr = sessionFactory.getCurrentSession()
				.createCriteria(UserEntity.class).add(Restrictions.eq("login", login));
		cr.setMaxResults(1);
		UserEntity userEntity = (UserEntity) cr.uniqueResult();
		user =	mappingService.map(userEntity, User.class);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while getting user by login", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex){
			LOG.error("Unknown error occured while getting user by login", ex.getMessage());
			throw new DaoException(ex);
		}
		return user;
	}

	@Transactional
	@Override
	public User getUserByEmail(String email) throws DaoException {
		User user = null;
		try{
		Criteria cr = sessionFactory.getCurrentSession()
				.createCriteria(UserEntity.class).add(Restrictions.eq("email", email));
		cr.setMaxResults(1);
		UserEntity userEntity = (UserEntity) cr.uniqueResult();
		user =	mappingService.map(userEntity, User.class);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while getting user by email", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while getting user by email", ex.getMessage());
			throw new DaoException(ex);
		}
		return user;
	}

	@Transactional
	@Override
	public void changePassword(User user, String password) {
		//TODO for Alena - possible error. Entity in detached state
		user.setPassword(password);
		merge(user, UserEntity.class);
	}

	@Transactional
	@Override
	public void deleteUserById(Integer id) throws DaoException {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria crsubscr = session.createCriteria(SubscriptionEntity.class)
					.add(Restrictions.eq("userEntity.id", id));
			List<SubscriptionEntity> related_subs = crsubscr.list();
			for(SubscriptionEntity se: related_subs) {
				session.delete(se);
			}
			Criteria cr = session.createCriteria(UserEntity.class).add(Restrictions.eq("id", id));
			UserEntity userEntity = (UserEntity) cr.uniqueResult();
			session.delete(userEntity);
		} catch (HibernateException ex) {
        	LOG.error("Hibernate error occured while deleting user by id", ex.getMessage());
        	throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while deleting user by id", ex.getMessage());
			throw new DaoException(ex);
		}
	}
}
