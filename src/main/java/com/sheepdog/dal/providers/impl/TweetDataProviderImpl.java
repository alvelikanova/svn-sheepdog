package com.sheepdog.dal.providers.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.dal.providers.TweetDataProvider;

@Repository
public class TweetDataProviderImpl extends BaseDataProviderImpl<TweetEntity, Tweet, Integer> implements
		TweetDataProvider {

	@Transactional
	@Override
	public List<Tweet> getTweetsByRevision(Revision revision) throws DaoException {
		List<Tweet> tweets = new ArrayList<Tweet>();
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria crtweets = session.createCriteria(TweetEntity.class).add(
					Restrictions.eq("revisionEntity.id", revision.getId()));
			List<TweetEntity> te_list = crtweets.list();
			for (TweetEntity te : te_list) {
				Tweet tweet = mappingService.map(te, Tweet.class);
				tweets.add(tweet);
			}
		} catch (HibernateException ex) {
			LOG.error("Hibernate error occured while getting tweets by revision", ex.getMessage());
			throw new DaoException(ex);
		} catch (Exception ex) {
			LOG.error("Unknown error occured while getting tweets by revision", ex.getMessage());
			throw new DaoException(ex);
		}
		return tweets;
	}

}
