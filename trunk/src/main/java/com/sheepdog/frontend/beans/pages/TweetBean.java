package com.sheepdog.frontend.beans.pages;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.services.TweetManagementService;
import com.sheepdog.frontend.beans.templates.FeedbackBean;

@Component(value = "tweetBean")
@Scope("session")
public class TweetBean {

	@Autowired
	private TweetManagementService tms;

	@Autowired
	private LoginManager lm;

	@Autowired
	private FeedbackBean feedback;

	private String tweetMessage;

	private List<Tweet> tweets;

	public void loadTweets(Revision revision) {
		List<Tweet> list = new ArrayList<>();
		// tms.getTweetsByRevision(Revision revision); TODO

		if (list == null) {
			tweets = new ArrayList<Tweet>(0);
		}
		tweets = list;
	}

	public void saveTweet(Revision revision) {

		feedback.feedback(FacesMessage.SEVERITY_INFO, "SEND", tweetMessage);

		Tweet tweet = new Tweet(revision, lm.getCurrentUser().getLogin(), tweetMessage);

		tweetMessage = "";

		// tms.saveTweet(tweet);

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
