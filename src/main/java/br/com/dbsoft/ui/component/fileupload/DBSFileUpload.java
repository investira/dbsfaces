package br.com.dbsoft.ui.component.fileupload;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSFileUpload.COMPONENT_TYPE)
public class DBSFileUpload extends DBSUIInput implements NamingContainer{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.FILEUPLOAD;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		fileUploadServletPath,
		multiple,
		maxSize,
		accept;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}

    public DBSFileUpload(){
		setRendererType(DBSFileUpload.RENDERER_TYPE);
		FacesContext xContext = FacesContext.getCurrentInstance();
		xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
    }


	public String getFileUploadServletPath() {
		return (String) getStateHelper().eval(PropertyKeys.fileUploadServletPath, null);
	}


	public void setFileUploadServletPath(String pFileUploadServletPath) {
		getStateHelper().put(PropertyKeys.fileUploadServletPath, pFileUploadServletPath);
	}

	public void setMultiple(Boolean pMultiple) {
		getStateHelper().put(PropertyKeys.multiple, pMultiple);
		handleAttribute("multiple", pMultiple);
	}
	
	public Boolean getMultiple() {
		return (Boolean) getStateHelper().eval(PropertyKeys.multiple, true);
	}
	
	public String getAccept() {
		return (String) getStateHelper().eval(PropertyKeys.accept, null);
	}
	public void setAccept(String pAccept) {
		getStateHelper().put(PropertyKeys.accept, pAccept);
	}
	
	public String getMaxSize() {
		return (String) getStateHelper().eval(PropertyKeys.maxSize, null);
	}
	public void setMaxSize(String pMaxSize) {
		getStateHelper().put(PropertyKeys.maxSize, pMaxSize);
	}
	
	@Override
    public String getDefaultEventName(){
        return "load";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("action","click", "blur", "load", "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "abort", "error", "loadstart", "timeout", "cancel"); 
	}


	@Override
	public void processEvent(SystemEvent pEvent) throws AbortProcessingException {
		//Adiciona os botões de start e cancel
		DBSFaces.createFileUploadButtons(this);
		super.processEvent(pEvent);
	}


	@Override
	public boolean isListenerForSource(Object pSource) {
		return pSource.equals(this);
	}
	
	
}
