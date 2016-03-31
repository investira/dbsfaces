package br.com.dbsoft.ui.component.sidemenu;


import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSSideMenu.COMPONENT_TYPE)
public class DBSSideMenu extends DBSUIOutput implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.SIDEMENU;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	 
	protected enum PropertyKeys {
		iconClass,
		iconCentral,
		tooltip;
		
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
    public DBSSideMenu(){
		setRendererType(DBSSideMenu.RENDERER_TYPE);
    }
	
  	public String getIconClass() {
 		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
 	}

 	public void setIconClass(String pIconClass) {
 		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
 		handleAttribute("iconClass", pIconClass);
 	}
 	
 	public String getIconCentral() {
 		return (String) getStateHelper().eval(PropertyKeys.iconCentral, null);
 	}

 	public void setIconCentral(String pIconCentral) {
 		getStateHelper().put(PropertyKeys.iconCentral, pIconCentral);
 		handleAttribute("iconCentral", pIconCentral);
 	}

 	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, null);
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}
}
