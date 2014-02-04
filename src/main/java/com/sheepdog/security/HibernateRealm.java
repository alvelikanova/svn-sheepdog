package com.sheepdog.security;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.sheepdog.business.domain.entities.User;
import com.sheepdog.dal.providers.UserDataProvider;

public class HibernateRealm extends AuthorizingRealm {

	@Autowired
	UserDataProvider userDataProvider;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if(principals == null) {
		    throw new AuthorizationException("PrincipalCollection can not be null");
		}
		
		if(principals.isEmpty()) {
		    throw new AuthorizationException("PrincipalCollection can not be empty");
		}
		
		String login = (String)principals.getPrimaryPrincipal();
		User user = userDataProvider.getUserByLogin(login);
		if (user == null) {
		    throw new AuthorizationException("Could not find user with specified id");
		}
		// if role based access needed
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//	        for (Role role : user.getRoles()) {
//	            info.addRole(role.getDescription());
//	            for (Permission permission : role.getPermissions()) {
//	                info.addStringPermission(permition.getDescription());
//	            }
//	        }
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken utoken = (UsernamePasswordToken) token;
		String login = utoken.getUsername();
		User user = userDataProvider.getUserByLogin(login);
		if (user == null) {
			throw new UnknownAccountException(String.format("Can't find account info for login=%s", login));
		} else {
			String password = new String(utoken.getPassword());
			if (ObjectUtils.compare(user.getPassword(), password) == 0) {
				SimpleAccount account = new SimpleAccount(login, password, getName());
				return account;
			} else {
				throw new IncorrectCredentialsException();
			}
		}
	}

}
