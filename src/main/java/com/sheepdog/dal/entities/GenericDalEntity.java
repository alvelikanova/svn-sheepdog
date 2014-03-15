package com.sheepdog.dal.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base class for persistent entity having unique identifier
 * 
 * @author iternovy
 * 
 * @param <T>
 */
@MappedSuperclass
public abstract class GenericDalEntity<ID extends Serializable> implements
		Serializable {
	private static final long serialVersionUID = -8974048124636154613L;
	private ID id;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}
}
