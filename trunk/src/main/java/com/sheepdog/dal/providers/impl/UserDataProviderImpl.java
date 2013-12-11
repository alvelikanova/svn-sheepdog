package com.sheepdog.dal.providers.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.UserDataProvider;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

@Repository
public class UserDataProviderImpl extends BaseDataProviderImpl<UserEntity,User,Integer> implements UserDataProvider{

	@Override
	public UserEntity findUserById(Integer id) {
		return (UserEntity)findById(UserEntity.class, id);
	}

	@Override
	public UserEntity findUserByLogin(String login) {
		Query query = sessionFactory.getCurrentSession().createQuery("from User where login=:login");
		query.setString("login", login);
		return (UserEntity)query.uniqueResult();
	}

	@Override
	public UserEntity findUserByEmail(String email) {
		Query query = sessionFactory.getCurrentSession().createQuery("from User where email=:email");
		query.setString("email", email);
		return (UserEntity)query.uniqueResult();
	}

	@Override
	public void createUser(User user) {
		save(user, UserEntity.class);
	}

	@Override
	public void deleteUserById(Integer userId) {
		UserEntity userEntity = findById(UserEntity.class, userId);
		delete(userEntity);
	}

	@Override
	public List<User> findAllUsers() {
		return findAll(UserEntity.class, User.class);
	}

	@Override
	public void changeUserPassword(UserEntity userEntity, String pass) {
		userEntity.setPassword(pass);
		merge(userEntity);
	}

	@Override
	public PagedList<User> getUserBusinessObjects(LoadOptions loadOptions) {
		return getObjects(loadOptions, UserEntity.class, User.class);
	}

}
