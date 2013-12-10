package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface TweetDataProvider {
	TweetEntity findTweetById(Integer id);
	void createTweet(TweetEntity tweetEntity);
	void deleteTweetById(Integer tweetId);
	List<TweetEntity> findAllTweets();
	void changeTweetText(TweetEntity tweetEntity, String newText);//
	PagedList<Tweet> getTweetBusinessObjects(LoadOptions loadOptions);
}
