package com.sheepdog.dal.exceptions;

public class ConstraintViolationDaoException extends DaoException {
	private static final long serialVersionUID = 1380249345567233298L;
	private String constraintField = null;
	public ConstraintViolationDaoException(Throwable cause) {
		super(cause);
	}
	public ConstraintViolationDaoException(String message, Throwable cause) {
		super(message, cause);
	}
	public ConstraintViolationDaoException(String message, Throwable cause, String constraintName) {
		super(message, cause);
		if (constraintName != null) {
			int index = constraintName.lastIndexOf('_');
			if (index != -1) {
				constraintField = constraintName.substring(constraintName.lastIndexOf('_') + 1).toLowerCase();
			} else {
				constraintField = constraintName;
			}
		}
	}
	public String getConstraintField() {
		return constraintField;
	}
}
