package com.sheepdog.business.domain.entities;

public class Project extends PersistentEntity<Integer> {

	private static final long serialVersionUID = -2704065688508963585L;
	
	private User user;
	private String name;
	private String url;
	
	public Project(User user, String name, String url) {
		this.user = user;
		this.name = name;
		this.url = url;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	
}
