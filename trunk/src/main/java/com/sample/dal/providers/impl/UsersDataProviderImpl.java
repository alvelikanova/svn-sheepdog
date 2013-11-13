package com.sample.dal.providers.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sample.business.domain.entities.User;
import com.sample.dal.entities.UserEntity;
import com.sample.dal.exceptions.DaoException;
import com.sample.dal.providers.UsersDataProvider;
import com.sample.infrastructure.services.MappingService;
import com.sample.utils.annotations.DefaultProfile;

@Repository
@DefaultProfile
public class UsersDataProviderImpl implements UsersDataProvider {

	private static final Logger LOG = LoggerFactory.getLogger(UsersDataProviderImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MappingService mappingService;

	@Transactional
	@Override
	public List<User> getUsers() {
		Session session = sessionFactory.getCurrentSession();

		List<User> users = new ArrayList<>();

		try {
			@SuppressWarnings("unchecked")
			List<UserEntity> dataEntities = session.createCriteria(UserEntity.class).list();

			for (UserEntity de : dataEntities) {
				User u = mappingService.map(de, User.class);
				users.add(u);
			}
		} catch (Exception ex) {
			LOG.error("Error loading users", ex);
		}

		return users;
	}

	@Transactional
	@Override
	public User getUserByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();

		User user = null;

		try {
			Criteria criteria = session.createCriteria(UserEntity.class);
			criteria.add(Restrictions.eq("email", email));

			UserEntity dataEntity = (UserEntity) criteria.uniqueResult();
			if (dataEntity != null) {
				user = mappingService.map(dataEntity, User.class);
			}
		} catch (Exception ex) {
			LOG.error(String.format("Error loading user by email=%s", email), ex);
		}

		return user;
	}

	@Transactional
	@Override
	public void saveUser(User user) throws DaoException {
		Session session = sessionFactory.getCurrentSession();

		try {
			UserEntity dataEntity = mappingService.map(user, UserEntity.class);
			session.saveOrUpdate(dataEntity);

			user.setId(dataEntity.getId());
		} catch (Exception ex) {
			LOG.error("Error creating or updating user", ex);
			throw new DaoException(ex);
		}
	}
}
