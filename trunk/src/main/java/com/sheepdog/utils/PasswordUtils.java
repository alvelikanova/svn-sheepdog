package com.sheepdog.utils;

import org.apache.shiro.crypto.hash.Sha256Hash;

public class PasswordUtils {

	public static String hashPassword(String password, String salt) {
		return new Sha256Hash(password, salt, 1024).toBase64();
	}
}
