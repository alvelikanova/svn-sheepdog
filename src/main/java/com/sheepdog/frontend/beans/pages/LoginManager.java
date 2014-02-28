package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean (name = "authenticationBean")
public class LoginManager implements Serializable {
	
	private static final long serialVersionUID = 6003658343558553848L;
	private static final Logger LOG = LoggerFactory.getLogger(LoginManager.class);
    private String username;
    private String password;
    private boolean remember;
    
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
	
    private void facesError(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    public void doLogin() {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(getUsername(), getPassword(), isRemember());

        try {
            subject.login(token);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/svn-sheepdog/");
//            if (subject.hasRole("admin")) {
//                FacesContext.getCurrentInstance().getExternalContext().redirect("admin/index.xhtml");
//            }
//            else {
//                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
//            }
        }
        catch (UnknownAccountException ex) {
            facesError("Unknown account");
        }
        catch (IncorrectCredentialsException ex) {
            facesError("Wrong password");
        }
        catch (AuthenticationException e) {
        	facesError("Authentication failed");
        }
        catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        finally {
            token.clear();
        }
    }
    
	public String doLogout() {
		LOG.trace("doLogout()");
		SecurityUtils.getSubject().logout();
		return "/public/login.xhtml?faces-redirect=true";
	}
}
