package com.sheepdog.dal.entities;

// Generated 13.12.2013 11:09:05 by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * TweetEntity generated by hbm2java
 */
@Entity
@Table(name = "TWEET")
public class TweetEntity extends GenericDalEntity<Integer> implements java.io.Serializable {
	private static final long serialVersionUID = -3860218096309563019L;
	private RevisionEntity revisionEntity;
	private String author;
	private String tweet;

	public TweetEntity() {
	}

	public TweetEntity(RevisionEntity revisionEntity, String author, String tweet) {
		this.revisionEntity = revisionEntity;
		this.author = author;
		this.tweet = tweet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REVISION_ID", nullable = false)
	public RevisionEntity getRevisionEntity() {
		return this.revisionEntity;
	}

	public void setRevisionEntity(RevisionEntity revisionEntity) {
		this.revisionEntity = revisionEntity;
	}

	@Column(name = "AUTHOR", nullable = false, length = 128)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Column(name = "TWEET", nullable = false, length = 2048)
	public String getTweet() {
		return this.tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

}
