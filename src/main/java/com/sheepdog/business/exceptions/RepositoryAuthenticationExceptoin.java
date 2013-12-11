package com.sheepdog.business.exceptions;

import com.sheepdog.business.domain.entities.User;

public class RepositoryAuthenticationExceptoin extends RuntimeException {

	private static final long serialVersionUID = -3297150548011538458L;

	private User user;

	public RepositoryAuthenticationExceptoin(User user) {
		super();
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
