package com.sheepdog.business.services.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.services.SubscriptionManagementService;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.providers.FileDataProvider;
import com.sheepdog.dal.providers.SubscriptionDataProvider;

@Service
public class SubscriptionManagementServiceImpl implements SubscriptionManagementService{

	private static final Logger LOG = LoggerFactory.getLogger(SubscriptionManagementServiceImpl.class);

	@Autowired
	private SubscriptionDataProvider subscriptionDataProvider;
	
	@Autowired
	private FileDataProvider fileDataProvider;
	
	@Override
	public void saveSubscriptions(Set<Subscription> s) {
		for(Subscription subscr:s) {
			subscriptionDataProvider.save(subscr, SubscriptionEntity.class);
		}
	}

	@Override
	public Set<Subscription> getSubscriptionsByFile(File file) {
		Set <Subscription> subscrs = new HashSet<Subscription>();
		List<Subscription> subs = subscriptionDataProvider
				.getSubscriptionsByQualifiedName(file.getQualifiedName());
		for (Subscription s : subs){
			subscrs.add(s);
		}
		return subscrs;
	}



}
