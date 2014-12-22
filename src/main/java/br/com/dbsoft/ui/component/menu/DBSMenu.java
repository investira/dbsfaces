package br.com.dbsoft.ui.component.menu;


import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSMenu.COMPONENT_TYPE)
public class DBSMenu extends DBSUIOutput implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MENU;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	 
	protected enum PropertyKeys {
		label,
		iconClass;
		
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
    public DBSMenu(){
		setRendererType(DBSMenu.RENDERER_TYPE);
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
	
}
