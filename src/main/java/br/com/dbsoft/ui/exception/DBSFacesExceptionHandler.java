package br.com.dbsoft.ui.exception;

import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.FacesException;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.log4j.Logger;

import br.com.dbsoft.ui.core.DBSFaces;


public class DBSFacesExceptionHandler  extends ExceptionHandlerWrapper {
	
	private Logger wLogger = Logger.getLogger(this.getClass());

	private final ExceptionHandler wWrapped;

	public DBSFacesExceptionHandler(ExceptionHandler wrapped) {
		wWrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
	    return wWrapped;
	}

	@Override
	public void handle() throws FacesException {
		final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();         

	    while (i.hasNext()) {             
			ExceptionQueuedEvent xEvent = i.next();             
			ExceptionQueuedEventContext xContext = (ExceptionQueuedEventContext) xEvent.getSource();               
			// get the exception from context             
			Throwable xThrowable = xContext.getException();
			Boolean xRemove = true;
			final FacesContext xFC = FacesContext.getCurrentInstance();   
			
			final Map<String, Object> xRequestMap = xFC.getExternalContext().getRequestMap();            
			final ConfigurableNavigationHandler xNav = (ConfigurableNavigationHandler) xFC.getApplication().getNavigationHandler();               
			try {                   
				//Não encontrou a conversação (CoversationScoped)
				if (xThrowable instanceof NonexistentConversationException){
					//Atualiza integralmente a página atual
					xNav.performNavigation(DBSFaces.getCurrentViewRefresh());
//					xFC.renderResponse();       
				//View Expirada
				}else if (xThrowable instanceof ViewExpiredException){
					//Direciona para a pasta raiz do sistema(normalmente o sistema irá redirecionar para a páginal index.xhtml)
//					xNav.performNavigation(xFC.getExternalContext().getRequestContextPath());
//					xFC.renderResponse();       
					xNav.performNavigation("/");
				}else if (xThrowable instanceof javax.el.PropertyNotFoundException){
					wLogger.error("Erro no encode do componente - Propriedade não encontrada", xThrowable); 
				}else if (xThrowable instanceof javax.faces.FacesException){ 
					if (xThrowable.getCause() instanceof com.sun.faces.context.FacesFileNotFoundException){
						wLogger.error("Página não encontrada. " + xThrowable.getMessage()); 
//						FacesMessage xM = new FacesMessage("aaaaa");
//						xFC.addMessage(clientId, message);
					}
					xRemove = false;
				}else{
					wLogger.error("Severe Exception Occured", xThrowable);
					xRequestMap.put("exceptionMessage", xThrowable.getMessage());                 
					xNav.performNavigation(xFC.getExternalContext().getRequestContextPath());
					xFC.renderResponse();                   
					// remove the comment below if you want to report the error in a jsf error message                 
//					JsfUtil.addErrorMessage(t.getMessage());     
				}
			} finally {     
				if (xRemove){
				    i.remove();             
				}
			}         
	   }         
	    //parent hanle         
	    getWrapped().handle(); 

	}

}
