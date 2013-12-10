package com.sheepdog.dal.providers;

import java.util.List;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.entities.UserEntity;
import com.sheepdog.dal.providers.pagination.LoadOptions;
import com.sheepdog.dal.providers.pagination.PagedList;

public interface UserDataProvider {
	UserEntity findUserById(Integer id);
	UserEntity findUserByLogin(String login);
	UserEntity findUserByEmail(String email);
	void createUser(UserEntity userEntity);
	void deleteUserById(Integer userId);
	List<UserEntity> findAllUsers();
	void changeUserPassword(UserEntity userEntity, String pass);
	PagedList<User> getUserBusinessObjects(LoadOptions loadOptions);
}
