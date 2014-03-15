package com.sheepdog.business.services.impl;

import java.util.List;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.UserDataProvider;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.services.UserManagementService;

@Service
public class UserManagementServiceImpl implements UserManagementService {
	private static final Logger LOG = LoggerFactory.getLogger(UserManagementServiceImpl.class);

	@Autowired
	private UserDataProvider userDataProvider;
	@Override
	public User getUserByLogin(String login) {
		return userDataProvider.getUserByLogin(login);
	}
	
	@Override
	public List<User> getAllUsers() {
		return userDataProvider.findAll(UserEntity.class, User.class);
	}

	@Override
	public void saveUser(User user) {
		String password = user.getPassword();
		String login = user.getLogin();
		if (login==null || password==null) return;
		String hashedPasswordBase64 = new Sha256Hash(password, login, 1024).toBase64();
		user.setPassword(hashedPasswordBase64);
		userDataProvider.save(user, UserEntity.class);
	}

	@Override
	public void deleteUserById(Integer id) {
		userDataProvider.deleteUserById(id);
	}

}
