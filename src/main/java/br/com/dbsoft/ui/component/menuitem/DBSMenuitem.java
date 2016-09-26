package br.com.dbsoft.ui.component.menuitem;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSMenuitem.COMPONENT_TYPE)
public class DBSMenuitem extends DBSUICommand implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MENUITEM;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		label,
		iconClass,
		execute,
		open,
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
	
    public DBSMenuitem(){
		setRendererType(DBSMenuitem.RENDERER_TYPE);
    }
    
    //ClientBehaviorHolder default: action
    @Override
	public String getDefaultEventName()
    {
        return "click";
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

	public void setReadOnly(Boolean pReadOnly) {
		getStateHelper().put(PropertyKeys.readOnly, pReadOnly);
		handleAttribute("readOnly", pReadOnly);
	}
	
	public Boolean getReadOnly() {
		return (Boolean) getStateHelper().eval(PropertyKeys.readOnly, false);
	}	
	
	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}

	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, null);
	}

	public void setOpen(Boolean pOpen) {
		getStateHelper().put(PropertyKeys.open, pOpen);
		handleAttribute("open", pOpen);
	}
	
	public Boolean getOpen() {
		return (Boolean) getStateHelper().eval(PropertyKeys.open, false);
	}	
	

}
