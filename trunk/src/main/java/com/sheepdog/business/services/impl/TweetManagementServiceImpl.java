package com.sheepdog.business.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.services.TweetManagementService;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.providers.TweetDataProvider;

@Service
public class TweetManagementServiceImpl implements TweetManagementService {

	@Autowired
	TweetDataProvider tweetDataProvider;

	@Override
	public List<Tweet> getTweetsByRevision(Integer revisionID) {
		return tweetDataProvider.getTweetsByRevision(revisionID);
	}

	@Override
	public void saveTweet(Tweet tweet) {
		tweetDataProvider.save(tweet, TweetEntity.class);
	}

}
