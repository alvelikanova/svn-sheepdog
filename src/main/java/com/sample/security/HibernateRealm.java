package com.sample.security;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;

import com.sample.business.domain.entities.User;
import com.sample.dal.providers.UsersDataProvider;

/**
 * 
 * This is custom implementation of {@link Realm} which uses custom
 * {@link UsersDataProvider} for authentication of users. See
 * spring-shiro-security.xml for details <br>
 * <br>
 * Current implementation supports only {@link UsernamePasswordToken}
 * 
 * @author tillias
 * 
 */
public class HibernateRealm extends AuthenticatingRealm {

	@Autowired
	private UsersDataProvider dataProvider;

	public HibernateRealm() {
		super();
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken utoken = (UsernamePasswordToken) token;

		String email = utoken.getUsername();

		User user = dataProvider.getUserByEmail(email);
		if (user == null) {
			throw new UnknownAccountException(String.format("Can't find account info for email=%s", email));
		} else {
			String password = new String(utoken.getPassword());
			if (ObjectUtils.compare(user.getPassword(), password) == 0) {
				SimpleAccount account = new SimpleAccount(email, password, getName());
				return account;
			} else {
				throw new IncorrectCredentialsException();
			}
		}
	}
}
