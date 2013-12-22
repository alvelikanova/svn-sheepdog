package com.sheepdog.business.services;

import com.sheepdog.business.domain.entities.User;

public interface UserManagementService {
	User getUserByLogin(String login);
}
