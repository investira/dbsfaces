package br.com.dbsoft.ui.component.link;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;


import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSLink.COMPONENT_TYPE)
public class DBSLink extends DBSUICommand {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.LINK;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		execute,
		tooltip,
		readOnly;

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
	
    public DBSLink(){
		setRendererType(DBSLink.RENDERER_TYPE);
    }
	

	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}

	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, null);
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


	@Override
    public String getDefaultEventName()
    {
        return "action";
    }

	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("action","click", "blur", "change", "click", "dblclick", "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select", "valueChange"); 
	}
    

}
