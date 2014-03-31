package com.sheepdog.business.services;

import java.util.List;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Tweet;

public interface TweetManagementService {
	List<Tweet> getTweetsByRevision(Integer revisionID);

	void saveTweet(Tweet tweet);
}
