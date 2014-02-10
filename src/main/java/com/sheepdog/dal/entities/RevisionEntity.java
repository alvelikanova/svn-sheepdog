package com.sheepdog.dal.entities;

// Generated 13.12.2013 11:09:05 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * RevisionEntity generated by hbm2java
 */
@Entity
@Table(name = "REVISION", schema = "PUBLIC", catalog = "PUBLIC")
public class RevisionEntity implements java.io.Serializable {

	private Integer id;
	private ProjectEntity projectEntity;
	private int revisionNo;
	private String author;
	private String comment;
	private Set<TweetEntity> tweetEntities = new HashSet<TweetEntity>(0);
	private Set<FileEntity> fileEntities = new HashSet<FileEntity>(0);

	public RevisionEntity() {
	}

	public RevisionEntity(ProjectEntity projectEntity, int revisionNo,
			String author, String comment) {
		this.projectEntity = projectEntity;
		this.revisionNo = revisionNo;
		this.author = author;
		this.comment = comment;
	}

	public RevisionEntity(ProjectEntity projectEntity, int revisionNo,
			String author, String comment, Set<TweetEntity> tweetEntities,
			Set<FileEntity> fileEntities) {
		this.projectEntity = projectEntity;
		this.revisionNo = revisionNo;
		this.author = author;
		this.comment = comment;
		this.tweetEntities = tweetEntities;
		this.fileEntities = fileEntities;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	public ProjectEntity getProjectEntity() {
		return this.projectEntity;
	}

	public void setProjectEntity(ProjectEntity projectEntity) {
		this.projectEntity = projectEntity;
	}

	@Column(name = "REVISION_NO", nullable = false)
	public int getRevisionNo() {
		return this.revisionNo;
	}

	public void setRevisionNo(int revisionNo) {
		this.revisionNo = revisionNo;
	}

	@Column(name = "AUTHOR", nullable = false, length = 128)
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Column(name = "COMMENT", nullable = false, length = 2048)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "revisionEntity")
	public Set<TweetEntity> getTweetEntities() {
		return this.tweetEntities;
	}

	public void setTweetEntities(Set<TweetEntity> tweetEntities) {
		this.tweetEntities = tweetEntities;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "revisionEntity")
	public Set<FileEntity> getFileEntities() {
		return this.fileEntities;
	}

	public void setFileEntities(Set<FileEntity> fileEntities) {
		this.fileEntities = fileEntities;
	}

}