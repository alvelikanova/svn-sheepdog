package com.sheepdog.dal.providers.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.UserDataProvider;

@Repository
public class UserDataProviderImpl extends BaseDataProviderImpl<UserEntity,User,Integer> implements UserDataProvider{

	@Transactional
	@Override
	public User getUserByLogin(String login) {
		User user = null;
		try{
		Criteria cr = sessionFactory.getCurrentSession()
				.createCriteria(UserEntity.class).add(Restrictions.eq("login", login));
		cr.setMaxResults(1);
		UserEntity userEntity = (UserEntity) cr.uniqueResult();
		user =	mappingService.map(userEntity, User.class);
		} catch (Exception ex){
			LOG.error("Error loading user", ex.getMessage());
		}
		return user;
	}

	@Transactional
	@Override
	public User getUserByEmail(String email) {
		User user = null;
		try{
		Criteria cr = sessionFactory.getCurrentSession()
				.createCriteria(UserEntity.class).add(Restrictions.eq("email", email));
		cr.setMaxResults(1);
		UserEntity userEntity = (UserEntity) cr.uniqueResult();
		user =	mappingService.map(userEntity, User.class);
		} catch (Exception ex){
			LOG.error("Error loading user", ex.getMessage());
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
	
//	public void changePassword(User user, String password) {
//		UserEntity userEntity = null;
//		Criteria cr = sessionFactory.getCurrentSession()
//				.createCriteria(UserEntity.class).
//				add(Restrictions.eq("LOGIN", user.getLogin()));
//		cr.setMaxResults(1);
//		userEntity = (UserEntity) cr.uniqueResult();
//		user.setPassword(password);
//		merge(user,UserEntity.class);
//	}
	
}
