package com.sheepdog.frontend.beans.users;

import java.io.IOException;

import javax.faces.application.FacesMessage;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.ProjectManagementService;
import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.frontend.beans.templates.FeedbackBean;
import com.sheepdog.security.HibernateRealm;
import com.sheepdog.utils.PasswordUtils;

@Component(value = "profileBean")
@Scope("session")
public class ProfileBean {
	@Autowired
	private UserManagementService userManagementService;
	
	@Autowired
	private ProjectManagementService projectManagementService;
	
	@Autowired
	private FeedbackBean feedback;
	
	@Autowired
	private CredentialsMatcher credentialsMatcher;
	
	@Autowired
	private SVNProjectFacade svnProjectFacade;
	
	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private String oldPassword;
	private String newPassword;
	
	public void loadFields(String principal) {
		try {
			User user = userManagementService.getUserByLogin(principal);
			if (user!=null) {
				login = user.getLogin();
				firstName = user.getFirstName();
				lastName = user.getLastName();
				email = user.getEmail();
			} else {
				feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error", "User was not found");
			}
		} catch (DaoException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Data access error");
		} catch (Exception ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Unknown error");
		}
	}
	
	public void verifySVN() {
		User user = userManagementService.getUserByLogin(login);
		try {
			if (svnProjectFacade.addSVNProjectConnection(user)) {
				feedback.feedback(FacesMessage.SEVERITY_INFO, "Verify SVN", "Your profile was successfully verified");
			} else {
				feedback.feedback(FacesMessage.SEVERITY_WARN, "Verify SVN", "Could not verify your profile. Please contact your administrator");
			}
		} catch (InvalidURLException | IllegalArgumentException
				| RepositoryAuthenticationExceptoin | IOException e) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Verify SVN", "An error occured. Please contact your administrator");
		}
	}
	public void changeUserInfo() {
		try {
			if (!checkPassword(login, oldPassword)) {
				feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error", "You have entered wrong old password");
				return;
			}
			User user = userManagementService.getUserByLogin(login);
			Project project = projectManagementService.getCurrentProject();
			user.setEmail(email);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setProject(project);
			String hashed_password = PasswordUtils.hashPassword(newPassword, login);
			user.setPassword(hashed_password);
			userManagementService.updateUser(user);
			feedback.feedback(FacesMessage.SEVERITY_INFO, "Update", "Profile data was updated");
		} catch (DaoException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Data access error");
		} catch (Exception ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Unknown error");
		}
	}

	public boolean checkPassword(String principal, String credential) {
		User user = userManagementService.getUserByLogin(principal);
		String hashedPass = user.getPassword();
		AuthenticationToken token = new UsernamePasswordToken(principal, credential);
		AuthenticationInfo info = new SimpleAuthenticationInfo(
				principal, hashedPass, 
				ByteSource.Util.bytes(user.getLogin()), HibernateRealm.class.getName());
		
		return credentialsMatcher.doCredentialsMatch(token, info);
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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
}
