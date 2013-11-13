package com.sample.business.domain.entities;

import java.io.Serializable;

/**
 * Base class for persistent entity having unique identifier
 * 
 * @author iternovy
 * 
 * @param <T>
 */
public abstract class PersistentEntity<T extends Serializable> implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private T id;

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}
}
