package com.sheepdog.dal.entities;

// Generated 10.12.2013 11:02:02 by Hibernate Tools 4.0.0

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
import javax.persistence.UniqueConstraint;

/**
 * ProjectEntity generated by hbm2java
 */
@Entity
@Table(name = "PROJECT", schema = "PUBLIC", catalog = "PUBLIC", uniqueConstraints = {
		@UniqueConstraint(columnNames = "NAME"),
		@UniqueConstraint(columnNames = "URL") })
public class ProjectEntity implements java.io.Serializable {

	private Integer id;
	private UserEntity userEntity;
	private String name;
	private String url;
	private Set<FileEntity> fileEntities = new HashSet<FileEntity>(0);
	private Set<RevisionEntity> revisionEntities = new HashSet<RevisionEntity>(
			0);

	public ProjectEntity() {
	}

	public ProjectEntity(UserEntity userEntity, String name, String url) {
		this.userEntity = userEntity;
		this.name = name;
		this.url = url;
	}

	public ProjectEntity(UserEntity userEntity, String name, String url,
			Set<FileEntity> fileEntities, Set<RevisionEntity> revisionEntities) {
		this.userEntity = userEntity;
		this.name = name;
		this.url = url;
		this.fileEntities = fileEntities;
		this.revisionEntities = revisionEntities;
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
	@JoinColumn(name = "USER_ID", nullable = false)
	public UserEntity getUserEntity() {
		return this.userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Column(name = "NAME", unique = true, nullable = false)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "URL", unique = true, nullable = false)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "projectEntity")
	public Set<FileEntity> getFileEntities() {
		return this.fileEntities;
	}

	public void setFileEntities(Set<FileEntity> fileEntities) {
		this.fileEntities = fileEntities;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "projectEntity")
	public Set<RevisionEntity> getRevisionEntities() {
		return this.revisionEntities;
	}

	public void setRevisionEntities(Set<RevisionEntity> revisionEntities) {
		this.revisionEntities = revisionEntities;
	}

}
