package com.sample.dal.providers.pagination;

import java.io.Serializable;

public class FilterOption implements Serializable {
	private static final long serialVersionUID = 7187601491809166374L;
	private String field;
	private Serializable value;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

}
