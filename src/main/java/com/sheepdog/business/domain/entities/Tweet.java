package com.sheepdog.business.domain.entities;

public class Tweet extends PersistentEntity<Integer> {

	private static final long serialVersionUID = 6514450289850592987L;

	private Revision revision;
	private String author;
	private String tweet;
	
	public Tweet() {
	}

	public Tweet(Revision revision, String author, String tweet) {
		this.revision = revision;
		this.author = author;
		this.tweet = tweet;
	}
	
	public Revision getRevision() {
		return revision;
	}
	public void setRevision(Revision revision) {
		this.revision = revision;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	
	
}
