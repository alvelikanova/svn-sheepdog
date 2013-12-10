package com.sheepdog.business.domain.entities;

public class Subscription extends PersistentEntity<Integer> {

	private static final long serialVersionUID = 2238653836491722724L;
	
	private User user;
	private File file;
	
	public Subscription(User user, File file) {
		this.user = user;
		this.file = file;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

}
