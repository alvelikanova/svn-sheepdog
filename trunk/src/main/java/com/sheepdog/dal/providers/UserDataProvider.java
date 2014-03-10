package com.sheepdog.dal.providers;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.base.PageableDataProvider;

public interface UserDataProvider extends PageableDataProvider<UserEntity,User,Integer> {
	User getUserByLogin(String login);
	User getUserByEmail(String email);
	void changePassword(User user, String password);
	void deleteUserById(Integer id);
}
