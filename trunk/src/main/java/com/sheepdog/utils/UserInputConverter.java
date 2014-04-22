package com.sheepdog.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("userInputConverter")
public class UserInputConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		String field = value.trim();
		if ("".equals(field)) {
			field = null;
			FacesMessage message = new FacesMessage();
			String messageBundle = context.getApplication().getMessageBundle();
			Locale locale = context.getViewRoot().getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(messageBundle, locale);
            message.setDetail(component.getId() + ": " + bundle.getString("emptyFieldValidation_detail"));
            message.setSummary(bundle.getString("emptyFieldValidation"));
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            throw new ConverterException(message);
		}
		return field;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}

}
