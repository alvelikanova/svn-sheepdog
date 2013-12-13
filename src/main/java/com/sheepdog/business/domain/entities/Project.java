package com.sheepdog.business.domain.entities;

public class Project extends PersistentEntity<Integer> {

	private static final long serialVersionUID = -2704065688508963585L;
	
	private String name;
	private String url;
	
	public Project() {
	}

	public Project(String name, String url) {
		this.name = name;
		this.url = url;
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
