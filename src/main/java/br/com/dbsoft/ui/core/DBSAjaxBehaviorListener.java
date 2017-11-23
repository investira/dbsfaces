package br.com.dbsoft.ui.core;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

/**
 * Implementação para AjaxBehaviorListener. 
 * Esta classe é usada na criação de AjaxBehavior programáticamente (no DBSPagedSearch por ex),
 * e recebe um MethodExpression que é o método que será executado no listener do ajax. 
 * @author jose.avila@dbsoft.com.br
 *
 */
//@SuppressWarnings("rawtypes")
public class DBSAjaxBehaviorListener implements AjaxBehaviorListener, Serializable{
	
	private static final long serialVersionUID = 6078090650536329330L;
	
	private MethodExpression 	wListener;
	
	public DBSAjaxBehaviorListener(MethodExpression pListener) {
		this.wListener = pListener;
	}

	@Override
	public void processAjaxBehavior(AjaxBehaviorEvent pEvent) throws AbortProcessingException {
		FacesContext xContext = FacesContext.getCurrentInstance();
		final ELContext xELContext= xContext.getELContext();
		wListener.invoke(xELContext, new Object[]{});
	}

}
