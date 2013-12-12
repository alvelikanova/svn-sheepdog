package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.providers.base.GenericDataProvider;

public interface SubscriptionDataProvider extends GenericDataProvider<SubscriptionEntity, Subscription, Integer>{
	List<File> getFilesByUserName(String userName);
}
