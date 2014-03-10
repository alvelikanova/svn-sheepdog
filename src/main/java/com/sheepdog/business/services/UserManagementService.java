package com.sheepdog.business.services;

import java.util.List;

import com.sheepdog.business.domain.entities.User;

public interface UserManagementService {
	User getUserByLogin(String login);

	List<User> getAllUsers();
	
	void saveUser(User user);
	void deleteUserById(Integer id);
}
