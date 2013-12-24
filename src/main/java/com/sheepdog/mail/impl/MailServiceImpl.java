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
		Map<User, MessageBuilder> messages = new HashMap<User, MessageBuilder>(0);
		MessageBuilder tempBuilder;

		for (Subscription s : necessarySubscriptions.keySet()) {
			tempBuilder = messages.get(s.getUser());

			if (tempBuilder == null) {
				tempBuilder = new MessageBuilder();
				messages.put(s.getUser(), tempBuilder);
			}

			tempBuilder.addSubscriptionUpdate(s, necessarySubscriptions.get(s));
		}

		for (User u : messages.keySet()) {
			mailConnection.send(u, messages.get(u).getFinalMessage());
		}

		mailConnection.closeConnection();
	}

	@Override
	public synchronized void sendMailByTweet(Tweet tweet) {
		// TODO Auto-generated method stub

	}

}
