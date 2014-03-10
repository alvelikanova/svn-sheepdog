package com.sheepdog.security;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
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
		    throw new AuthorizationException("Could not find user with specified login");
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRole(user.getRole());
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
			SimpleAuthenticationInfo saInfo = new SimpleAuthenticationInfo(login, user.getPassword(), getName());
            saInfo.setCredentialsSalt(ByteSource.Util.bytes(login));
			return saInfo;
		}
	}

}
