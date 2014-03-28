package br.com.dbsoft.ui.component.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class EmptyValidator implements Validator {

	@Override
	public void validate(FacesContext pContext, UIComponent pComponent, Object pValue)
			throws ValidatorException {
		if (pValue == null || "".equals(pValue.toString().trim())) {
			FacesMessage message = new FacesMessage();
			String messageStr = (String)pComponent.getAttributes().get("message");
			if (messageStr == null) {
				messageStr = "Please enter data";
			}
			message.setDetail(messageStr);
			message.setSummary(messageStr);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}

	}

}
