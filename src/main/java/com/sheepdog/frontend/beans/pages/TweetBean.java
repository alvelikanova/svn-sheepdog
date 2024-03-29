package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.RefreshFailedException;
import javax.xml.transform.TransformerException;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.TweetManagementService;
import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.frontend.beans.templates.FeedbackBean;
import com.sheepdog.mail.MailService;

@Component(value = "tweetBean")
@Scope("session")
public class TweetBean {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(TweetBean.class);

	@Autowired
	private TweetManagementService tms;

	@Autowired
	private LoginManager lm;

	@Autowired
	private FeedbackBean feedback;

	@Autowired
	private MailService mailService;

	@Autowired
	private UserManagementService ums;

	private String tweetMessage;

	private List<Tweet> tweets;

	private Map<Tweet, User> notificationTweets = new HashMap<>(0);

	public void loadTweets(Integer revisionID) {

		List<Tweet> list = tms.getTweetsByRevision(revisionID);

		if (list == null) {
			tweets = new ArrayList<Tweet>(0);
		}
		tweets = list;
	}

	public void saveTweet(Revision revision) {
		Tweet tweet = new Tweet(revision, lm.getCurrentUser().getLogin(), tweetMessage);

		tweetMessage = "";
		tweets.add(tweet);
		RequestContext.getCurrentInstance().update("changelog_form:changelog:tweets");

		tms.saveTweet(tweet);

		User user = ums.getUserByLogin(revision.getAuthor());

		if (user == null) {
			user = ums.getUserByEmail(revision.getAuthor());
		}
		if (user != null) {

			notificationTweets.put(tweet, user);

		}
	}

	public void sendTweetNotification() {

		for (Map.Entry<Tweet, User> entry : notificationTweets.entrySet()) {

			try {
				mailService.sendMailByTweet(entry.getKey(), entry.getValue());
				notificationTweets.remove(entry.getKey());
			} catch (RefreshFailedException e) {
				LOG.warn("Failed to send tweet notification to user : " + entry.getValue().getLogin());
			} catch (IOException e) {
				LOG.warn("Failed to send tweet notification to user : " + entry.getValue().getLogin()
						+ ". Connection problem.");
			} catch (TransformerException e) {
				LOG.warn("Failed to send tweet notification to user : " + entry.getValue().getLogin()
						+ ". Template merging is failed.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<Tweet> getTweets() {

		return tweets;
	}

	public String getTweetMessage() {
		return tweetMessage;
	}

	public void setTweetMessage(String tweetMessage) {
		this.tweetMessage = tweetMessage;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
}
