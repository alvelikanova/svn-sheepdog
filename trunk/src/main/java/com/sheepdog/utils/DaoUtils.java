package com.sheepdog.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DaoUtils {

	public static String getConstraintName(String message) {
		String constraintName = null;
		if (message != null) {
			Pattern PATTERN = Pattern.compile(".*'(UQ_\\S+)'.*",
					Pattern.CASE_INSENSITIVE);
			final Matcher matcher = PATTERN.matcher(message);
			if (matcher.matches()) {
				constraintName = matcher.group(1);
			}
		}
		return constraintName;
	}
}
