package com.sheepdog.utils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

	@Override
	public void validate(FacesContext facesContext, UIComponent component, Object value)
			throws ValidatorException {
		String email = value.toString();
		email = email.trim();
		Pattern pattern = Pattern.compile("[a-zA-Z]+[-0-9a-zA-Z._]+@[a-zA-Z]+[-0-9a-zA-Z._]+\\.[a-zA-Z]{2,4}");
		Matcher matcher = pattern.matcher(email);
        
        //Check whether match is found
        boolean matchFound = matcher.matches();
        
        if (!matchFound) {
            FacesMessage message = new FacesMessage();
            message.setDetail("You should provide an email that contains letters, digits, hyphen and underscore only");
            message.setSummary("Email not valid");
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            throw new ValidatorException(message);
        }
	}

}
