package com.sheepdog.dal.providers.impl;

import org.springframework.stereotype.Repository;

import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.providers.TweetDataProvider;

@Repository
public class TweetDataProviderImpl extends BaseDataProviderImpl<TweetEntity,Tweet,Integer> implements TweetDataProvider{

}
