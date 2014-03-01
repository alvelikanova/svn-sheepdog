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
					.add(Restrictions.eq("login", userName));
			cr.setMaxResults(1);
			UserEntity userEntity = (UserEntity) cr.uniqueResult();
			
			//Load all users' subscriptions
			cr = session.createCriteria(SubscriptionEntity.class)
					.add(Restrictions.eq("userEntity.id", userEntity.getId()));
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
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<Subscription> getSubscriptionsByQualifiedName(String qualifiedName) {
		List <Subscription> sublist = null;
		try {
			Session session = sessionFactory.getCurrentSession();
			
			//load file with qname from dbase
			Criteria crfile = session.createCriteria(FileEntity.class)
					.add(Restrictions.eq("qualifiedName", qualifiedName));
			FileEntity fe = (FileEntity)crfile.uniqueResult();
			
			//load subscriptions by file's id
			Criteria crsub = session.createCriteria(SubscriptionEntity.class)
					.add(Restrictions.eq("fileEntity.id", fe.getId()));
			sublist = crsub.list();
		} catch (Exception ex) {
			LOG.error("Error loading subscriptions", ex.getMessage());
		}
		return sublist;
	}

}
