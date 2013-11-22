package com.sample.business.services;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sample.business.exceptions.IllegalCredentialsException;
import com.sample.business.exceptions.UserNotFoundException;
import com.sample.business.services.fakes.UsersDataProviderFake;
import com.sample.utils.TestConstants;
import com.sample.utils.annotations.DefaultProfile;
import com.sample.utils.annotations.UnitTestProfile;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:testApplicationContext.xml" })
@ActiveProfiles("unit-test")
public class UserManagementServiceTest {

	/**
	 * This test uses {@link UsersDataProviderFake} implementation for
	 * {@link UserManagementService} concrete implementation and shows how to
	 * user {@link UnitTestProfile} and {@link DefaultProfile} in conjunction
	 * with {@link ActiveProfiles}
	 */

	@Autowired
	private UserManagementService service;

	@Test(expected = UserNotFoundException.class)
	public void updatePassword_UnknownEmail_ExceptionIsThrown() {
		service.updatePassword(UUID.randomUUID().toString(), "111", "222");
	}

	@Test(expected = IllegalCredentialsException.class)
	public void updatePassword_OldPasswordIncorrect_ExceptionIsThrown() {

		String oldPassword = UUID.randomUUID().toString();

		service.updatePassword(TestConstants.FAKE_USER_EMAIL, oldPassword, UUID.randomUUID().toString());

	}
}
