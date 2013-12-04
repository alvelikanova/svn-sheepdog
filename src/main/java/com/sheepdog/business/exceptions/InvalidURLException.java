package com.sheepdog.business.exceptions;

public class InvalidURLException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9155914788711525733L;

	private String url;

	public InvalidURLException(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
