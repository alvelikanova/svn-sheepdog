package com.sample.business.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sample.business.domain.entities.User;
import com.sample.business.exceptions.IllegalCredentialsException;
import com.sample.business.exceptions.UserNotFoundException;
import com.sample.business.services.UserManagementService;
import com.sample.dal.exceptions.DaoException;
import com.sample.dal.providers.UsersDataProvider;

@Service
public class UserManagementServiceImpl implements UserManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagementServiceImpl.class);

	@Autowired
	private UsersDataProvider dataProvider;

	@Override
	public void updatePassword(String email, String oldPassword, String newPassword) throws UserNotFoundException,
			IllegalCredentialsException, DaoException {
		User user = dataProvider.getUserByEmail(email);
		if (user == null)
			throw new UserNotFoundException(email);

		if (oldPassword == null)
			throw new IllegalCredentialsException(email, oldPassword);

		if (!oldPassword.equals(user.getPassword()))
			throw new IllegalCredentialsException(email, oldPassword);

		user.setPassword(newPassword);
		dataProvider.saveUser(user);
	}

	@Override
	public List<User> getAllUsers() {
		return dataProvider.getUsers();
	}

}
