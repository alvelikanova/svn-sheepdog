package com.sheepdog.mail.impl;

import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;

/**
 * MessageBuilder containing complete message, which will be sent to subscriber.
 * That message complete by new subscriptions info.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public class MessageBuilder {

	/**
	 * Containing prepare message for subscriber.
	 */
	private StringBuilder finalMessage;

	public MessageBuilder() {
		finalMessage = new StringBuilder("");
	}

	public MessageBuilder(String message) {
		if (message == null) {
			finalMessage = new StringBuilder("");
		}
		finalMessage.append(message);
	}

	public String getFinalMessage() {
		return finalMessage.toString();
	}

	/**
	 * Completing final message by changes info.
	 * 
	 * @param subscription
	 *            Containing changed File object.
	 * @param type
	 *            Type of changes.
	 */
	void addSubscriptionUpdate(Subscription subscription, TypeOfFileChanges type) {

		try {
			finalMessage.append("File ");
			finalMessage.append(subscription.getFile().getName());
			finalMessage.append(type.toString());
			finalMessage.append(" in revision ");
			finalMessage.append(subscription.getFile().getRevision().getRevisionNo());
			finalMessage.append(" by ");
			finalMessage.append(subscription.getFile().getRevision().getAuthor());
			finalMessage.append(".");
			finalMessage.append("\n");
		} catch (NullPointerException e) {
			// TODO
		}
	}
}
