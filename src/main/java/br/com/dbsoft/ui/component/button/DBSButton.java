package br.com.dbsoft.ui.component.button;


import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSButton.COMPONENT_TYPE)
public class DBSButton extends DBSUICommand {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BUTTON;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		label,
		iconClass,
		execute,
		tooptip,
		readOnly,
		disabled,
		timerVerify;

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
	
    public DBSButton(){
		setRendererType(DBSButton.RENDERER_TYPE);
    }
	
	@Override
    public String getDefaultEventName()
    {
        return "action";
    }

	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("action","click", "blur", "change", "click", "dblclick", "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select", "valueChange"); 
	}
    

	public String getLabel() {
		return (String) getStateHelper().eval(PropertyKeys.label, null);
	}
	
	public void setLabel(String pLabel) {
		getStateHelper().put(PropertyKeys.label, pLabel);
		handleAttribute("label", pLabel);
	}


	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}	
	
	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}

	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, null);
	}

	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooptip, null);
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooptip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}	
	
	public void setReadOnly(Boolean pReadOnly) {
		getStateHelper().put(PropertyKeys.readOnly, pReadOnly);
		handleAttribute("readOnly", pReadOnly);
	}
	
	public Boolean getReadOnly() {
		return (Boolean) getStateHelper().eval(PropertyKeys.readOnly, false);
	}	

	public void setDisabled(Boolean pDisabled) {
		getStateHelper().put(PropertyKeys.disabled, pDisabled);
		handleAttribute("disabled", pDisabled);
	}
	
	public Boolean getDisabled() {
		return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}
	
	public void setTimerVerify(Boolean pTimerVerify) {
		getStateHelper().put(PropertyKeys.timerVerify, pTimerVerify);
		handleAttribute("timerVerify", pTimerVerify);
	}
	
	public Boolean getTimerVerify() {
		return (Boolean) getStateHelper().eval(PropertyKeys.timerVerify, false);
	}

}
