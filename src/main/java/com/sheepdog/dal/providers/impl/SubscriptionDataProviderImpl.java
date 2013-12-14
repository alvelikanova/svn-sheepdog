package com.sheepdog.dal.providers.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.SubscriptionDataProvider;

@Repository
public class SubscriptionDataProviderImpl extends BaseDataProviderImpl<SubscriptionEntity,Subscription,Integer> implements SubscriptionDataProvider{

	@Transactional
	@Override
	public List<File> getFilesByUserName(String userName) {
		List<File> resultList = null;
		try {
			resultList = new ArrayList<>();
			Session session = sessionFactory.getCurrentSession();
			
			//Load user by login
			Criteria cr = session.createCriteria(UserEntity.class)
					.add(Restrictions.eq("LOGIN", userName));
			cr.setMaxResults(1);
			UserEntity userEntity = (UserEntity) cr.uniqueResult();
			
			//Load all users' subscriptions
			cr = session.createCriteria(SubscriptionEntity.class)
					.add(Restrictions.eq("USER_ID", userEntity.getId()));
			@SuppressWarnings("unchecked")
			List<SubscriptionEntity> subscriptionList = cr.list();
			
			for (SubscriptionEntity se: subscriptionList) {
				FileEntity fileEntity = se.getFileEntity();
				File file = mappingService.map(fileEntity, File.class);
				resultList.add(file);
			}
		} catch (Exception ex) {
			LOG.error("Error loading files", ex.getMessage());
		}
		return resultList;
	}

}
