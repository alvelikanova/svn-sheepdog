package com.sheepdog.mail;

import java.util.Map;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;

/**
 * MailService provides building and sending new messages.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public interface MailService {

	/**
	 * Send mail to user,which was subscribed to changed files.
	 * 
	 * @param necessarySubscriptions
	 *            Containing User objects, files and type of their changes.
	 */
	public void sendMailBySubscription(Map<Subscription, TypeOfFileChanges> necessarySubscriptions);

	/**
	 * Send mail, that containing info about comment to revision of required
	 * user.
	 * 
	 * @param tweet
	 *            Tweet object containing revision info.
	 * @param user
	 *            Author of revision.
	 */
	public void sendMailByTweet(Tweet tweet, User user);

}