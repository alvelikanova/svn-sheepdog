package com.sheepdog.frontend.beans.users;

import java.io.IOException;

import javax.faces.application.FacesMessage;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.ProjectManagementService;
import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.dal.exceptions.DaoException;
import com.sheepdog.frontend.beans.pages.LoginManager;
import com.sheepdog.frontend.beans.templates.FeedbackBean;
import com.sheepdog.security.HibernateRealm;
import com.sheepdog.utils.PasswordUtils;

@Component(value = "profileBean")
@Scope("session")
public class ProfileBean {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileBean.class);
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

	@Autowired
	LoginManager loginBean;
	
	public void loadFields(String principal) {
		try {
			User user = userManagementService.getUserByLogin(principal);
			if (user != null) {
				login = user.getLogin();
				firstName = user.getFirstName();
				lastName = user.getLastName();
				email = user.getEmail();
			}
		} catch (DaoException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Failed to load profile data");
			LOG.error("Data access error occured while loading fields");
		} catch (Exception ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Failed to load profile data");
			LOG.error("Error occured while loading fields");
		}
	}

	public void verifySVN() {
		User user = userManagementService.getUserByLogin(login);
		try {
			if (svnProjectFacade.addSVNProjectConnection(user)) {
				feedback.feedback(FacesMessage.SEVERITY_INFO, "Verify SVN",
						"Your profile was successfully verified");
			} else {
				feedback.feedback(FacesMessage.SEVERITY_WARN, "Verify SVN",
						"Could not verify your profile. Please contact your administrator");
			}
		} catch (InvalidURLException | IllegalArgumentException
				| RepositoryAuthenticationExceptoin | IOException e) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Verify SVN",
					"An error occured. Please contact your administrator");
			LOG.error("Error occured while verifying SVN");
		}
	}

	public void changeUserInfo() {
		try {
			User user = userManagementService.getUserByLogin(login);
			user.setEmail(email);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			userManagementService.updateUser(user);
			User currentUser = loginBean.getCurrentUser();
			if (currentUser!=null) {
				currentUser.setEmail(email);
				currentUser.setFirstName(firstName);
				currentUser.setLastName(lastName);
			}
			feedback.feedback(FacesMessage.SEVERITY_INFO, "Update",
					"Profile data was updated");
		} catch (DaoException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Error occured while updating profile data");
			LOG.error("Data access error occured while updating profile data");
		} catch (Exception ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Error occured while updating profile data");
			LOG.error("Error occured while updating profile data");
		}
	}

	public void changePassword() {
		try {
			User user = userManagementService.getUserByLogin(login);
			// check if entered old password matches password in database
			if (!checkPassword(user, oldPassword)) {
				feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
						"You have entered wrong old password");
				return;
			}
			// hash new password and save in database
			String hashed_password = PasswordUtils.hashPassword(newPassword,
					login);
			user.setPassword(hashed_password);
			userManagementService.updateUser(user);
			User currentUser = loginBean.getCurrentUser();
			if (currentUser!=null) {
				currentUser.setPassword(hashed_password);
			}
			feedback.feedback(FacesMessage.SEVERITY_INFO, "Update",
					"Password was changed");
		} catch (DaoException ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Error occured while changing password");
			LOG.error("Data access error occured while changing password");
		} catch (Exception ex) {
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Error",
					"Error occured while changing password");
			LOG.error("Error occured while changing password");
		}
	}

	public boolean checkPassword(User user, String credential) {
		String hashedPass = user.getPassword();
		AuthenticationToken token = new UsernamePasswordToken(user.getLogin(),
				credential);
		AuthenticationInfo info = new SimpleAuthenticationInfo(user.getLogin(),
				hashedPass, ByteSource.Util.bytes(user.getLogin()),
				HibernateRealm.class.getName());

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
