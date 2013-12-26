package com.sheepdog.business.domain.entities;

public class User extends PersistentEntity<Integer> {

	private static final long serialVersionUID = 5292401382106106765L;

	private Project project;

	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private String password;

	private boolean isAdmin = Boolean.FALSE;

	public User() {
	}

	/**
	 * Required fields
	 * 
	 * @param login
	 * @param email
	 * @param password
	 */
	public User(Project project, String login, String email, String password) {
		super();
		this.project = project;
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
	public User(Project project, String login, String firstName, String lastName, String email, String password) {
		super();
		this.project = project;
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	/**
	 * Get user for update service.
	 * 
	 * @return 
	 */
	public static User getUpdateUser() {
		User updateUser = new User();
		updateUser.setLogin("UPDATE_USER");
		return updateUser;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
