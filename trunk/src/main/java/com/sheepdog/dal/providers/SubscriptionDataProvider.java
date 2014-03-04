package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.providers.base.PageableDataProvider;

public interface SubscriptionDataProvider extends PageableDataProvider<SubscriptionEntity, Subscription, Integer>{
	List<File> getFilesByUserName(String userName);
	List<Subscription> getSubscriptionsByQualifiedName(String qualifiedName);
	void deleteSubscription(User user, File file);
	void createSubscription(User user, File file);
	boolean isSubscribed(User user, File file);
}
