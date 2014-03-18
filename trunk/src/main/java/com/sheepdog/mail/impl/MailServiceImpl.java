package com.sheepdog.mail.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.RefreshFailedException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.mail.MailService;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	private MailConnector mailConnector;

	public MailServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sheepdog.mail.MailService#sendMailBySubscription(java.util.Map)
	 */
	@Override
	public synchronized void sendMailBySubscription(Map<Subscription, TypeOfFileChanges> necessarySubscriptions)
			throws RefreshFailedException, IOException, TransformerException {

		Map<User, Map<Subscription, TypeOfFileChanges>> subscriptions = new HashMap<>(0);

		for (Map.Entry<Subscription, TypeOfFileChanges> entry : necessarySubscriptions.entrySet()) {
			if (!subscriptions.containsKey(entry.getKey().getUser())) {
				subscriptions.put(entry.getKey().getUser(), new HashMap<Subscription, TypeOfFileChanges>(0));
			}
			subscriptions.get(entry.getKey().getUser()).put(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<User, Map<Subscription, TypeOfFileChanges>> entry : subscriptions.entrySet()) {
			mailConnector.sendSuscriptionMessage(entry.getKey(), entry.getValue());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.mail.MailService#sendMailByTweet(com.sheepdog.business.domain
	 * .entities.Tweet, com.sheepdog.business.domain.entities.User)
	 */
	@Override
	public synchronized void sendMailByTweet(Tweet tweet, User user) throws RefreshFailedException, IOException,
			TransformerException {	

		mailConnector.sendTweetMessage(user, tweet);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sheepdog.mail.MailService#resetConfig()
	 */
	@Override
	public synchronized void resetConfig() throws RefreshFailedException {
		mailConnector.setup();

	}

}
