package br.com.dbsoft.ui.component.submenu;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSSubmenu.COMPONENT_TYPE)
public class DBSSubmenu extends DBSUIOutput implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.SUBMENU;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		label,
		iconClass,
		style,
		styleClass;

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
    public DBSSubmenu(){
		setRendererType(DBSSubmenu.RENDERER_TYPE);
    }

    public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
	}
	
	public void setLabel(java.lang.String pLabel) {
		getStateHelper().put(PropertyKeys.label, pLabel);
		handleAttribute("label", pLabel);
	}
	
	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(java.lang.String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(java.lang.String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}

	public java.lang.String getIconClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}

	public void setIconClass(java.lang.String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}

}
