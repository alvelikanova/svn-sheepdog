package com.sample.utils.frontend.beans;

import java.io.Serializable;

import org.primefaces.context.RequestContext;

public abstract class CommonBean implements Serializable {
	private static final long serialVersionUID = 1L;

	protected void showDialog(String dialogName) {

		final RequestContext context = RequestContext.getCurrentInstance();
		context.execute(String.format("%s.show()", dialogName));
	}
}
