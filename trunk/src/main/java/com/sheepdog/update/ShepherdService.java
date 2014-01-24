package com.sheepdog.update;

import org.springframework.stereotype.Service;

/**
 * ShepherdService check updates from repository, update DB and invoke methods
 * of MailService to send notification emails.
 * 
 * @author Ivan Arkhipov
 * 
 */
@Service
public interface ShepherdService {

	/**
	 * Method look on repository changes and make required updates and
	 * notifications.
	 */
	public void lookOn();

}
