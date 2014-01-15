package com.sheepdog.business.services;
import java.util.Set;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;

public interface SubscriptionManagementService {
	void saveSubscriptions(Set <Subscription> s);
	Set <Subscription> getSubscriptionsByFile(File file);
}
