package br.com.dbsoft.ui.component.sidenav;


import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSString;

@FacesComponent(DBSSideNav.COMPONENT_TYPE)
public class DBSSideNav extends DBSUIOutput implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.SIDENAV;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	 
	protected enum PropertyKeys {
		caption,
		iconClass,
		iconCaption,
		width,
		height,
		defaultLocation;;
		
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
    public DBSSideNav(){
		setRendererType(DBSSideNav.RENDERER_TYPE);
    }

    public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}
	
  	public String getIconClass() {
 		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
 	}

 	public void setIconClass(String pIconClass) {
 		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
 		handleAttribute("iconClass", pIconClass);
 	}
 	
 	public String getIconCaption() {
 		return (String) getStateHelper().eval(PropertyKeys.iconCaption, null);
 	}

 	public void setIconCaption(String pIconCaption) {
 		getStateHelper().put(PropertyKeys.iconCaption, pIconCaption);
 		handleAttribute("iconCaption", pIconCaption);
 	}

 	public String getWidth() {
 		return DBSString.toString(getStateHelper().eval(PropertyKeys.width, null), "250px");
 	}

 	public void setWidth(String pWidth) {
 		getStateHelper().put(PropertyKeys.width, pWidth);
 		handleAttribute("width", pWidth);
 	}
 	
 	public String getHeight() {
 		return DBSString.toString(getStateHelper().eval(PropertyKeys.height, null), "250px");
 	}

 	public void setHeight(String pHeight) {
 		getStateHelper().put(PropertyKeys.height, pHeight);
 		handleAttribute("height", pHeight);
 	}
	
	public Integer getDefaultLocation() {
		return (Integer) getStateHelper().eval(PropertyKeys.defaultLocation, 1);
	}
	
	public void setDefaultLocation(Integer pDefaultLocation) {
		Integer xDefaultLocation = pDefaultLocation;
		if (pDefaultLocation == null
		 || pDefaultLocation < 1
		 || pDefaultLocation > 4){
			xDefaultLocation = 1;
		}
		getStateHelper().put(PropertyKeys.defaultLocation, xDefaultLocation);
		handleAttribute("defaultLocation", xDefaultLocation);
	}
}
