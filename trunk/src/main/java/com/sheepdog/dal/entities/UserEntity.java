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
import javax.persistence.UniqueConstraint;

/**
 * UserEntity generated by hbm2java
 */
@Entity
@Table(name = "USER_", schema = "PUBLIC", catalog = "PUBLIC", uniqueConstraints = {
		@UniqueConstraint(columnNames = "LOGIN"),
		@UniqueConstraint(columnNames = "EMAIL") })
public class UserEntity implements java.io.Serializable {

	private Integer id;
	private ProjectEntity projectEntity;
	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private boolean admin;
	private Set<SubscriptionEntity> subscriptionEntities = new HashSet<SubscriptionEntity>(
			0);

	public UserEntity() {
	}

	public UserEntity(ProjectEntity projectEntity, String login,
			String firstName, String lastName, String email, String password, boolean admin) {
		this.projectEntity = projectEntity;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.admin = admin;
	}

	public UserEntity(ProjectEntity projectEntity, String login,
			String firstName, String lastName, String email, String password,
			boolean admin, Set<SubscriptionEntity> subscriptionEntities) {
		this.projectEntity = projectEntity;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.admin = admin;
		this.subscriptionEntities = subscriptionEntities;
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

	@Column(name = "LOGIN", unique = true, nullable = false, length = 128)
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "FIRST_NAME", nullable = false, length = 128)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME", nullable = false, length = 256)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "EMAIL", unique = true, nullable = false, length = 128)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "PASSWORD", nullable = false, length = 2048)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "ADMIN", nullable = false)
	public boolean isAdmin() {
		return this.admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userEntity")
	public Set<SubscriptionEntity> getSubscriptionEntities() {
		return this.subscriptionEntities;
	}

	public void setSubscriptionEntities(
			Set<SubscriptionEntity> subscriptionEntities) {
		this.subscriptionEntities = subscriptionEntities;
	}

}
