package com.sheepdog.mail;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.RefreshFailedException;
import javax.xml.transform.TransformerException;

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
	 * @throws IOException
	 *             if the message cannot be sent.
	 * @throws TransformerException
	 *             if template merge is failed.
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 * 
	 */
	public void sendMailBySubscription(Map<Subscription, TypeOfFileChanges> necessarySubscriptions)
			throws RefreshFailedException, IOException, TransformerException;

	/**
	 * Send mail, that containing info about comment to revision of required
	 * user.
	 * 
	 * @param tweet
	 *            Tweet object containing revision info.
	 * @param user
	 *            Author of revision.
	 * @throws IOException
	 *             if the message cannot be sent.
	 * @throws TransformerException
	 *             if template merge is failed.
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 */
	public void sendMailByTweet(Tweet tweet, User user) throws RefreshFailedException, IOException,
			TransformerException;

	/**
	 * Reload updated properties and reconfiguring mail service.
	 * 
	 * @throws RefreshFailedException
	 *             if update process is failed. See log for more details.
	 */
	public void resetConfig() throws RefreshFailedException;

}
