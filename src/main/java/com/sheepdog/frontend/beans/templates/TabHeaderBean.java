package com.sheepdog.frontend.beans.templates;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.frontend.beans.pages.LoginManager;

@Component(value = "tabHeaderBean")
@Scope("session")
public class TabHeaderBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4481996656447052376L;

	@Autowired
	private LoginManager lm;

	private int activeIndex = 2;

	private boolean admin = true;

	@PostConstruct
	public void init() {
		if (!lm.isAdmin()) {
			admin = false;
			activeIndex--;
		}

	}

	public String updateIndex(Long index, String url) {
		if (!admin) {
			index--;
		}

		setActiveIndex(index.intValue());

		return url + "?faces-redirect=true";
		// return url;TODO
	}

	public int getActiveIndex() {

		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}
