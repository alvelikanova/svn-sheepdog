package com.sample.business.services.fakes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sample.business.domain.entities.User;
import com.sample.dal.exceptions.DaoException;
import com.sample.dal.providers.UsersDataProvider;
import com.sample.utils.TestConstants;
import com.sample.utils.annotations.UnitTestProfile;

@Repository
@UnitTestProfile
public class UsersDataProviderFake implements UsersDataProvider {

	private User user;

	public UsersDataProviderFake() {
		user = new User();
		user.setEmail(TestConstants.FAKE_USER_EMAIL);
		user.setPassword(TestConstants.FAKE_USER_PASSWORD);
	}

	@Override
	public List<User> getUsers() {
		List<User> result = new ArrayList<>();

		return result;
	}

	@Override
	public User getUserByEmail(String email) {
		if (user.getEmail().equals(email))
			return user;

		return null;
	}

	@Override
	public void saveUser(User user) throws DaoException {
		// TODO Auto-generated method stub

	}
}
