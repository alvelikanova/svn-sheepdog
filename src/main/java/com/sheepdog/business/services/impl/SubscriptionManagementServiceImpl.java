package com.sheepdog.business.services.impl;

import java.util.HashMap;
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
	public Map<Subscription, TypeOfFileChanges> getSubscriptionsByFiles(
			Map<File, TypeOfFileChanges> files) {
		
		Map <Subscription, TypeOfFileChanges> resultmap = new HashMap<Subscription, TypeOfFileChanges>();
		try {
			List <Subscription> subscrs = subscriptionDataProvider
					.findAll(SubscriptionEntity.class, Subscription.class);
			
			Set<File> keys = files.keySet();
			for(File file: keys) {
				File f = fileDataProvider
						.findFileByQualifiedName(file.getQualifiedName());
				if (f!=null) {
					Integer id = f.getId();
					for (Subscription sub:subscrs){
						if (sub.getFile().getId().equals(id)){
							resultmap.put(sub, files.get(file));
						}
					}
				}
			}
		}
		catch (Exception exc){
			LOG.error("Error loading subscriptions", exc.getMessage());
		}
		return resultmap;
	}

}
