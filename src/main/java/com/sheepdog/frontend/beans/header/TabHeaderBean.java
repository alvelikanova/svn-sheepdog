package com.sheepdog.frontend.beans.header;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "tabHeaderBean")
@SessionScoped
public class TabHeaderBean {

	private int activeIndex = 0;

	public String toChangelog() {
		activeIndex = 0;

		return "changelog";
	}

	public String toFiles() {
		activeIndex = 1;

		return "files";
	}

	public String toSubscriptions() {
		activeIndex = 2;

		return "subscriptions";
	}

	public String toProfile() {
		activeIndex = 3;

		return "profile";
	}

	public String toLogout() {
		activeIndex = 4;

		return "/";
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

}
