package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.providers.base.GenericDataProvider;

public interface TweetDataProvider extends GenericDataProvider<TweetEntity, Tweet, Integer>{
}
