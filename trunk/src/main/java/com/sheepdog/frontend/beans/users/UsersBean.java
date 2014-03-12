package com.sheepdog.frontend.beans.users;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.frontend.beans.templates.FeedbackBean;

@Component
@Scope("session")
public class UsersBean implements Serializable {
	
	private static final long serialVersionUID = 2819227216048472445L;
	@Autowired
	private UserManagementService ums;
	@Autowired
	private FeedbackBean feedback;
	private String login;
	private String role;
	private String firstName;
	private String lastName;
	private String email;
	private String oldPassword;
	private String newPassword;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void saveUser() throws IOException {
		feedback.feedback(FacesMessage.SEVERITY_INFO, "Save User", "Method was called");
	}
	
	public void deleteUser(Integer id) {
		try {
			ums.deleteUserById(id);
			RequestContext.getCurrentInstance().update("form");
			feedback.feedback(FacesMessage.SEVERITY_INFO, "Delete User", "User was deleted");
		} catch (DaoException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error", "Data access error");
		} catch (Exception ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error", "Unknown error");
		}
	}
}
