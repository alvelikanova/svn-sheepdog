package com.sheepdog.business.services;
import java.util.List;
import java.util.Set;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;

public interface SubscriptionManagementService {
	void saveSubscriptions(Set <Subscription> s);
	Set <Subscription> getSubscriptionsByFile(File file);
	void deleteSubscription(Subscription subscr);
	void deleteSubscription(User user, File file);
	Set<Subscription> getAllSubscriptions();
	void createSubscription(User user, File file);
	boolean isSubscribed(User user, File file);
	List<Subscription> getSubscriptionsByUser(User user);
}
