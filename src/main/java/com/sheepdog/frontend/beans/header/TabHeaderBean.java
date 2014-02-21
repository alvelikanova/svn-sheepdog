package com.sheepdog.frontend.beans.header;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "tabHeaderBean")
@SessionScoped
public class TabHeaderBean {

	private int activeIndex = 2;

	
	public TabHeaderBean(){
		
	}
	
	public String updateIndex(Long index, String url) {
		setActiveIndex(index.intValue());
		
//		return url + "?faces-redirect=true"; TODO
		return url;
	}

	public int getActiveIndex() {
		
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

}
