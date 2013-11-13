package com.sample.dal.exceptions;

public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 8532056393261995809L;

	public DaoException(Throwable cause) {
		super(cause);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}
}
