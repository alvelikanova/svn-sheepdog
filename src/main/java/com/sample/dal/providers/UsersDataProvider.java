package com.sample.dal.providers;

import java.util.List;

import com.sample.business.domain.entities.User;
import com.sample.dal.exceptions.DaoException;

public interface UsersDataProvider {

	List<User> getUsers();

	User getUserByEmail(String email);

	void saveUser(User user) throws DaoException;
}
