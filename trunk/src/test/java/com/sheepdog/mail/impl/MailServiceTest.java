package com.sheepdog.mail.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.mail.MailService;

public class MailServiceTest {

	public static void main(String[] args) {
		User user = new User();
		user.setEmail("sheepdog.svn@gmail.com");
		user.setFirstName("Ivan");
		user.setLastName("Arkhipov");

		User user2 = new User();
		user2.setEmail("sheepdog.svn@gmail.com");
		user2.setFirstName("Ivan");
		user2.setLastName("Ivanov");

		Map<Subscription, TypeOfFileChanges> necessarySubscriptions = new HashMap<>(0);

		necessarySubscriptions.put(new Subscription(user, new File(null, new Revision(null, 5, "ivan.spread@gmail.com",
				null, new Date()), "first.java", null, null, true)), TypeOfFileChanges.ADDED);
		necessarySubscriptions.put(new Subscription(user, new File(null, new Revision(null, 10,
				"ivan.spread@gmail.com", null, new Date()), "second.java", null, null, true)),
				TypeOfFileChanges.MODIFIED);
		
		necessarySubscriptions.put(new Subscription(user2, new File(null, new Revision(null, 8,
				"ivan.spread@gmail.com", null, new Date()), "seco.java", null, null, true)), TypeOfFileChanges.DELETED);

		
		MailConnection mailConnection = new MailConnection("smtp.gmail.com", "sheepdog.svn", "tunisheepdog",
				"src/main/resources/hellouser.vm");

		MailService mailService = new MailServiceImpl(mailConnection);

		long time = System.currentTimeMillis();

		mailService.sendMailBySubscription(necessarySubscriptions);

		System.out.println("COMPLETED!! time: " + (System.currentTimeMillis() - time));
	}

}
