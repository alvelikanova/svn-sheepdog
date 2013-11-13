package com.sample.business.exceptions;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3392945729002894599L;

	private String email;

	public UserNotFoundException(String email) {
		super();

		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
