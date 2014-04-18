package com.sheepdog.dal.exceptions;

public class ConstraintViolationDaoException extends DaoException {
	
	private static final long serialVersionUID = 6304771815180108689L;
	
	public ConstraintViolationDaoException(Throwable cause) {
		super(cause);
	}
	
	public ConstraintViolationDaoException(String message, Throwable cause) {
		super(message, cause);
	}
}
