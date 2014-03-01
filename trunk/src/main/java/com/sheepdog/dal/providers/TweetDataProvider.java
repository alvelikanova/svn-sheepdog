package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.providers.base.PageableDataProvider;

public interface TweetDataProvider extends PageableDataProvider<TweetEntity, Tweet, Integer>{
}
