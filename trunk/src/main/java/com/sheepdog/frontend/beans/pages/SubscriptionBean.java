package com.sheepdog.frontend.beans.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.FileManagementService;
import com.sheepdog.business.services.SubscriptionManagementService;

@Component(value = "subscriptionBean")
@Scope("session")
public class SubscriptionBean {

	@Autowired
	private SubscriptionManagementService subscrService;

	@Autowired
	private FileManagementService fs;

	private List<Subscription> subscriptions = new ArrayList<>(0);

	public void deleteSubscription(File file) {

		// subscrService.delete(file); TODO

	}

	public void deleteSubscription(Subscription subscription) {

		// subscrService.delete(subscr); TODO

	}

	public void createSubscription(File file) {
		User user = null;// TODO GET ACTIVE USER

		// fs.saveFile(file);TODO

		Set<Subscription> sub = new HashSet<>();
		sub.add(new Subscription(user, file));

		subscrService.saveSubscriptions(sub);
	}

	public void saveSubscription(Subscription subscription) {
		Set<Subscription> subSet = new HashSet<>();
		subSet.add(subscription);

		subscrService.saveSubscriptions(subSet);
	}

	public void subscriptionCheck(File file, Boolean flag) {
		if (flag) {
			createSubscription(file);
		} else {
			deleteSubscription(file);
		}

	}

	public void subscriptionChange(Subscription subscription) {
		if (subscriptions.contains(subscription)) {
			deleteSubscription(subscription);
			subscriptions.remove(subscription);
		} else {
			subscriptions.add(subscription);
			saveSubscription(subscription);
		}

	}

	public List<Subscription> getSubscriptions() {
//		subscrService.getAllsubscr//TODO
		
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public void setSubscrService(SubscriptionManagementService subscrService) {
		this.subscrService = subscrService;
	}

}
