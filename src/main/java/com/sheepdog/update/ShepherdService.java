package com.sheepdog.update;


/**
 * ShepherdService check updates from repository, update DB and invoke methods
 * of MailService to send notification emails.
 * 
 * @author Ivan Arkhipov
 * 
 */

public interface ShepherdService {

	/**
	 * Method look on repository changes and make required updates and
	 * notifications.
	 */
	public void lookOn();

}
