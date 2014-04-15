package com.sheepdog.dal.exceptions;

public class ConstraintViolationDaoException extends DaoException {
	private static final long serialVersionUID = 1380249345567233298L;
	private String constraintField;
	public ConstraintViolationDaoException(Throwable cause) {
		super(cause);
		constraintField = null;
	}
	public ConstraintViolationDaoException(String message, Throwable cause) {
		super(message, cause);
		constraintField = null;
	}
	public ConstraintViolationDaoException(String message, Throwable cause, String constraintName) {
		super(message, cause);
		constraintField = constraintName.substring(constraintName.lastIndexOf('_') + 1).toLowerCase();
	}
	public String getConstraintField() {
		return constraintField;
	}
}
