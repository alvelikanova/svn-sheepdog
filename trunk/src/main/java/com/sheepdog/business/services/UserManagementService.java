package com.sheepdog.business.services;

import java.util.List;

import com.sheepdog.business.domain.entities.User;

public interface UserManagementService {
	User getUserByLogin(String login);
	User getUserByEmail(String email);
	
	List<User> getAllUsers();
	
	void saveUser(User user);
	void deleteUserById(Integer id);
	void updateUser(User user);
}
