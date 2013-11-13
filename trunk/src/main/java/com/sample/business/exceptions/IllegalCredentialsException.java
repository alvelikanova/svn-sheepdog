package com.sample.business.exceptions;

public class IllegalCredentialsException extends RuntimeException {

	private static final long serialVersionUID = -1585501641473843112L;

	private String email;
	private String password;

	public IllegalCredentialsException(String email, String password) {
		super();

		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
