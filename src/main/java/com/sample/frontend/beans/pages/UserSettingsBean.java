package com.sample.frontend.beans.pages;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@SessionScoped
public class UserSettingsBean implements Serializable {

	private static final long serialVersionUID = 5750063835740897932L;
	private static final Logger LOG = LoggerFactory.getLogger(UserSettingsBean.class);

	public String getGreeting() {
		return "Greetings from JSF Managed bean!";
	}

	public String doLogout() {
		LOG.trace("doLogout()");

		SecurityUtils.getSubject().logout();

		return null;
	}
}
