package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface SubscriptionDataProvider {
	SubscriptionEntity findSubscriptionById(Integer id);
	void createSubscription(SubscriptionEntity subscriptionEntity);
	void deleteSubscriptionById(Integer subscriptionId);
	List<SubscriptionEntity> findAllSubscriptions();
	PagedList<Subscription> getSubscriptionObjects(LoadOptions loadOptions);

}
