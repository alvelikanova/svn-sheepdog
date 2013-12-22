package com.sheepdog.business.services;
import java.util.Map;
import java.util.Set;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;

public interface SubscriptionManagementService {
	void saveSubscriptions(Set <Subscription> s);
	Map <Subscription, TypeOfFileChanges> getSubscriptionsByFiles(Map <File, TypeOfFileChanges> files);
}
