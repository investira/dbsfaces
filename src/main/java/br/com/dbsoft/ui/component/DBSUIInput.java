package br.com.dbsoft.ui.component;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;

@ResourceDependencies({
	// Estas libraries serão carregadas junto com o projeto
	@ResourceDependency(library = "css", name = "dbsfaces.min.css", target = "head"),
	@ResourceDependency(library = "js", name = "jquery-3.1.1.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "jquery.actual.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "js.cookie.js", target = "head"),
	@ResourceDependency(library = "js", name = "tinycolor.js", target = "head"),
	@ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces.min.js", target = "head")
//	@ResourceDependency(library = "js", name = "eventsource.js", target = "head")
})
public abstract class DBSUIInput extends UIInput implements IDBSUIComponentBase, ClientBehaviorHolder, SystemEventListener  {
	
	protected enum PropertyKeys {
		label,
		labelWidth,
		rightLabel,
		styleClass,
		style,
		tooltip,
		readOnly,
		placeHolder;

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
	
	public DBSUIInput() {
//		System.out.println("DBSUIInput Constructor\t" + this.getClientId());
		 FacesContext xContext = FacesContext.getCurrentInstance();
//		 xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreValidateEvent.class,this);
		 xContext.getViewRoot().subscribeToViewEvent(PostValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderComponentEvent.class,this);
		// -------------------------------------------------------------------------------
//		 xContext.getViewRoot().subscribeToViewEvent(PostConstructViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostRestoreStateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreDestroyViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRemoveFromViewEvent.class,this);	
	}
	
	
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
//		System.out.println("DBSUIInput ProcessEvent:\t");
		//Força que componente seja atualizado via ajax caso esteja com erro para que se possa verifica novamente se o erro persiste.
		if (event.getSource() instanceof DBSUIInput){
			DBSUIInput xInput = (DBSUIInput) event.getSource();
//			System.out.println("DBSUIInput ProcessEvent:\t" + xInput.getClientId() + "\t" + xInput.isValid() + "\t" + xInput.getValidatorMessage());
			if (!DBSObject.isEmpty(xInput.getValidatorMessage())){
				FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(xInput.getClientId());
			}
		}
	}
	
	
	@Override
	public boolean isListenerForSource(Object pSource) {
//		System.out.println("DBSUIInput isListenerForSource");
		return pSource.equals(this);
	}
	
	@Override
	public String getFamily() {
		return DBSFaces.FAMILY;
	}
	
	@Override
    public String getDefaultEventName(){
        return "change";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("action","click", "blur", "change", "dblclick", "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select", "valueChange"); 
	}	
	
	@Override
	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}
	
	
	@Override
	public void setSubmittedValue(Object pSubmittedValue) {
		super.setSubmittedValue(DBSFaces.castSubmittedValue(this, pSubmittedValue));
	}
	
	public String getLabel() {
		return (String) getStateHelper().eval(PropertyKeys.label, null);
	}
	
	public void setLabel(String pLabel) {
		getStateHelper().put(PropertyKeys.label, pLabel);
		handleAttribute("label", pLabel);
	}
	
	public String getLabelWidth() {
		return (String) getStateHelper().eval(PropertyKeys.labelWidth, "");

	}
	
	public void setLabelWidth(String pLabelWidth) {
		getStateHelper().put(PropertyKeys.labelWidth, pLabelWidth);
		handleAttribute("labelWidth", pLabelWidth);
	}

	public String getRightLabel() {
		return (String) getStateHelper().eval(PropertyKeys.rightLabel, null);
	}
	
	public void setRightLabel(String pRightLabel) {
		getStateHelper().put(PropertyKeys.rightLabel, pRightLabel);
		handleAttribute("rightLabel", pRightLabel);
	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}

	public String getPlaceHolder() {
		return (String) getStateHelper().eval(PropertyKeys.placeHolder, null);
	}
	
	public void setPlaceHolder(String pPlaceHolder) {
		getStateHelper().put(PropertyKeys.placeHolder, pPlaceHolder);
		handleAttribute("placeHolder", pPlaceHolder);
	}
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, null);
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}	
		
	public void setReadOnly(Boolean pReadOnly) {
		getStateHelper().put(PropertyKeys.readOnly, pReadOnly);
		handleAttribute("readOnly", pReadOnly);
	}
	
	public Boolean getReadOnly() {
		return (Boolean) getStateHelper().eval(PropertyKeys.readOnly, false);
	}	

//	System.out.println("Inputtext decode:\t" + xInputText.getClientId() + "\t" + xInputText.isValid());
//	if (xInputText.isValid()){
//		pContext.getPartialViewContext().getRenderIds().add(xInputText.getClientId());
//	}

}
