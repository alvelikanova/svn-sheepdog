package com.sheepdog.mail.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.RefreshFailedException;
import javax.xml.transform.TransformerException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.mail.MailService;

public class MailServiceTest {
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(MailServiceTest.class);

	public static void main(String[] args) {
		User user = new User();
		user.setEmail("sheepdog.svn@gmail.com");
		user.setFirstName("Ivan");
		user.setLastName("Ivanov");

		User user2 = new User();
		user2.setEmail("sheepdog.svn@gmail.com");
		user2.setFirstName("Ivan");
		user2.setLastName("Arkhipov");

		Map<Subscription, TypeOfFileChanges> necessarySubscriptions = new HashMap<>(0);

		necessarySubscriptions.put(new Subscription(user, new File(null, new Revision(null, 5, "ivan.spread@gmail.com",
				null, new Date()), "first.java", null, null, true)), TypeOfFileChanges.ADDED);
		necessarySubscriptions.put(new Subscription(user, new File(null, new Revision(null, 10,
				"ivan.spread@gmail.com", null, new Date()), "second.java", null, null, true)),
				TypeOfFileChanges.MODIFIED);

		necessarySubscriptions.put(new Subscription(user2, new File(null, new Revision(null, 8,
				"ivan.spread@gmail.com", null, new Date()), "seco.java", null, null, true)), TypeOfFileChanges.DELETED);


		MailConnector mailConnector = new MailConnector();

		MailService mailService = new MailServiceImpl(mailConnector);
		
		try {
			mailService.resetConfig();
		} catch (RefreshFailedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		long time = System.currentTimeMillis();

		try {
			mailService.sendMailBySubscription(necessarySubscriptions);
		} catch (RefreshFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.warn("COMPLETED!! time: " + (System.currentTimeMillis() - time));

		time = System.currentTimeMillis();

		Tweet tweet = new Tweet(new Revision(null, 20, "Ivanov", null, new Date()), "Arkhipov", "That's good!");

		try {
			mailService.sendMailByTweet(tweet, user);
		} catch (RefreshFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.warn("COMPLETED!! time: " + (System.currentTimeMillis() - time));
	}

}