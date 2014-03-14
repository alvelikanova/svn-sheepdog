package com.sheepdog.frontend.beans.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.FileManagementService;
import com.sheepdog.business.services.RevisionManagementService;
import com.sheepdog.business.services.SubscriptionManagementService;

@Component(value = "subscriptionBean")
@Scope("session")
public class SubscriptionBean {

	@Autowired
	private LoginManager lm;

	@Autowired
	private SubscriptionManagementService subscrService;

	@Autowired
	private FileManagementService fs;

	@Autowired
	private RevisionManagementService rms;

	private List<Subscription> subscriptions = new ArrayList<>(0);

	public void deleteSubscription(File file) {

		subscrService.deleteSubscription(lm.getCurrentUser(), file);

	}

	public void deleteSubscription(Subscription subscription) {

		subscrService.deleteSubscription(subscription);

	}

	public void createSubscription(File file) {
		File dbFile = fs.getFileByQualifiedName(file.getQualifiedName());

		if (dbFile == null) {
			file.setRevision(rms.getCurrentRevision());
			fs.saveFile(file);
			dbFile = fs.getFileByQualifiedName(file.getQualifiedName());
		}

		Set<Subscription> sub = new HashSet<>();
		sub.add(new Subscription(lm.getCurrentUser(), dbFile));

		subscrService.saveSubscriptions(sub);
	}

	public void saveExistingSubscription(Subscription subscription) {

		Set<Subscription> subSet = new HashSet<>();
		subSet.add(subscription);

		subscrService.saveSubscriptions(subSet);
	}

	public void subscriptionCheck(FileTreeComposite ftc) {
		if (ftc.isSubscribed()) {
			createSubscription(ftc.getFile());
			ftc.setSubscribed(true);

		} else {
			deleteSubscription(ftc.getFile());
			ftc.setSubscribed(false);
		}

	}

	public boolean isSubscribed(File file) {
		if (subscriptions.size() == 0) {
			subscriptions.addAll(subscrService.getSubscriptionsByUser(lm.getCurrentUser()));
		}
		for (Subscription s : subscriptions) {
			if (file.getQualifiedName().equals(s.getFile().getQualifiedName())) {
				System.out.println(s.getFile().getQualifiedName());
				return true;
			}
		}
		return false;

	}

	public void reloadSubscriptions() {
		System.out.println("RELOAD+++++++++++++++++++++++++++++++++++++++++++++++");
		subscriptions.clear();
		subscriptions.addAll(subscrService.getSubscriptionsByUser(lm.getCurrentUser()));

		System.out.println("Subscr size " + subscriptions.size());

		RequestContext.getCurrentInstance().update("subscr_form:subscrTable");
	}

	public void subscriptionChange(Subscription subscription) {
		if (subscriptions.contains(subscription)) {
			
			System.out.println("SUCHESTVUET");
			subscriptions.remove(subscription);
			deleteSubscription(subscription);

		} else {
			System.out.println("NE  SUCHESTVUET");
			subscriptions.add(subscription);
			saveExistingSubscription(subscription);
		}

	}

	public List<Subscription> getSubscriptions() {

		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

}
