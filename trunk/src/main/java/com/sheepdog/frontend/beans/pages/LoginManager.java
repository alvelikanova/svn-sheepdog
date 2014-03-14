package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.frontend.beans.templates.FeedbackBean;

@Component(value = "authenticationBean")
@Scope("session")
public class LoginManager implements Serializable {

	private static final long serialVersionUID = 6003658343558553848L;
	private static final Logger LOG = LoggerFactory.getLogger(LoginManager.class);
	private String username;
	private String password;
	private boolean remember;

	@Autowired
	private UserManagementService ums;
	@Autowired
	private FeedbackBean feedback;
	private User currentUser = null;
	private boolean isAdmin = false;

	public User getCurrentUser() {
		return currentUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRemember() {
		return remember;
	}

	public void setRemember(boolean remember) {
		this.remember = remember;
	}

	public void doLogin() {
		Subject subject = SecurityUtils.getSubject();

		UsernamePasswordToken token = new UsernamePasswordToken(getUsername(), getPassword(), isRemember());

		try {
			subject.login(token);
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.redirect(ec.getRequestContextPath());
			// if (subject.hasRole("admin")) {
			// FacesContext.getCurrentInstance().getExternalContext().redirect("admin/index.xhtml");
			// }
			// else {
			// FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
			// }

			currentUser = ums.getUserByLogin(getUsername());

			isAdmin = "admin".equals(currentUser.getRole());

		} catch (UnknownAccountException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Authentication error", "Unknown account");
		} catch (IncorrectCredentialsException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Authentication error", "Wrong password");
		} catch (AuthenticationException e) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Authentication error", "Authentication failed");
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			token.clear();
		}

	}

	public String doLogout() {
		LOG.trace("doLogout()");
		SecurityUtils.getSubject().logout();
		return "/public/login.xhtml?faces-redirect=true";
	}

	public boolean isAdmin() {
		return isAdmin;
	}
}
