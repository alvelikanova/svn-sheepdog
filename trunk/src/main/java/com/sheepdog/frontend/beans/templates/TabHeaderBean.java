package com.sheepdog.frontend.beans.templates;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "tabHeaderBean")
@SessionScoped
public class TabHeaderBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4481996656447052376L;
	private int activeIndex = 2;

	public TabHeaderBean() {

	}

	public String updateIndex(Long index, String url) {
		setActiveIndex(index.intValue());

		// return url + "?faces-redirect=true"; TODO
		return url;
	}

	public int getActiveIndex() {

		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

}
