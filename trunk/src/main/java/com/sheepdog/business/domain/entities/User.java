package com.sheepdog.business.domain.entities;

public class User extends PersistentEntity<Integer> {
	
	private static final long serialVersionUID = 5292401382106106765L;
	
	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	
	public User(){
	}
	
	/**
	 * Required fields
	 * 
	 * @param login
	 * @param email
	 * @param password
	 */
	public User(String login, String email, String password) {
		this.login = login;
		this.email = email;
		this.password = password;
	}
	
	/**
	 * Set all fields
	 * 
	 * @param login
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 */
	public User(String login, String firstName, String lastName, String email,
			String password) {
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}



	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
