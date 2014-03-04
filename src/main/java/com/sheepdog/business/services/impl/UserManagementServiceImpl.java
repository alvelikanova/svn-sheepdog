package com.sheepdog.business.services.impl;

import java.util.List;

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

}
