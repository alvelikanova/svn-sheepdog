package com.sheepdog.mail.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.mail.MailService;

public class MailServiceImpl implements MailService {

	@Autowired
	private MailConnection mailConnection;

	public MailServiceImpl(MailConnection mailConnection) {
		super();
		this.mailConnection = mailConnection;
	}

	@Override
	public synchronized void sendMailBySubscription(Map<Subscription, TypeOfFileChanges> necessarySubscriptions) {
		mailConnection.openConnection();
		
		Map<User, Map<Subscription, TypeOfFileChanges>> subscriptions = new HashMap<>(0);


		for (Subscription s : necessarySubscriptions.keySet()) {
			
			if (!subscriptions.containsKey(s.getUser())){
				subscriptions.put(s.getUser(), new HashMap<Subscription,TypeOfFileChanges>(0));
				subscriptions.get(s.getUser()).put(s, necessarySubscriptions.get(s));
				continue;
			}

			subscriptions.get(s.getUser()).put(s, necessarySubscriptions.get(s));
			
		}

		for (User u : subscriptions.keySet()) {
			mailConnection.send(u, subscriptions.get(u), null);
		}

		mailConnection.closeConnection();
	}

	@Override
	public synchronized void sendMailByTweet(Tweet tweet, User user) {
		mailConnection.openConnection();
		 
		mailConnection.send(user, new HashMap(0), tweet);

		mailConnection.closeConnection();
	}

}
