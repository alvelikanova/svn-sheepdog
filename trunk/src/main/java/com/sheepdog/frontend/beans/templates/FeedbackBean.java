package com.sheepdog.frontend.beans.templates;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class FeedbackBean {

	public void feedback(Severity level, String title, String message) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage(level, title, message));
		RequestContext.getCurrentInstance().update("feedback");

	}

}
