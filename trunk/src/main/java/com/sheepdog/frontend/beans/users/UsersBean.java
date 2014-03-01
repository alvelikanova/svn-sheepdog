package com.sheepdog.frontend.beans.users;

import java.io.Serializable;

import javax.faces.bean.SessionScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.providers.UserDataProvider;

@Component
@SessionScoped
public class UsersBean implements Serializable {
	
	private static final long serialVersionUID = 2819227216048472445L;
	@Autowired
	private UserDataProvider dataProvider;
	private User selectedUser;
	
	public User getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

}
