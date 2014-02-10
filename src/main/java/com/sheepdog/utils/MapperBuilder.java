package com.sheepdog.utils;

import org.dozer.loader.api.BeanMappingBuilder;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.Tweet;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.FileEntity;
import com.sheepdog.dal.entities.RevisionEntity;
import com.sheepdog.dal.entities.SubscriptionEntity;
import com.sheepdog.dal.entities.TweetEntity;
import com.sheepdog.dal.entities.UserEntity;

public class MapperBuilder extends BeanMappingBuilder {

	@Override
	protected void configure() {
		mapping(UserEntity.class, User.class).fields("projectEntity", "project");
		mapping(SubscriptionEntity.class, Subscription.class).fields("userEntity", "user")
															 .fields("fileEntity", "file");
		mapping(FileEntity.class, File.class).fields("projectEntity", "project")
		 									 .fields("revisionEntity", "revision");
		mapping(RevisionEntity.class, Revision.class).fields("projectEntity", "project");
		mapping(TweetEntity.class, Tweet.class).fields("revisionEntity", "revision");
	}
}
