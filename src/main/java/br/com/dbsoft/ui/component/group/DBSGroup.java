package br.com.dbsoft.ui.component.group;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;


import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSGroup.COMPONENT_TYPE)
public class DBSGroup extends DBSUIComponentBase {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.GROUP;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		styleClass,
		floatLeft,
		style,
		label;

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
    public DBSGroup(){
		setRendererType(DBSGroup.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
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
	
	public void setLabel(java.lang.String pLabel) {
		getStateHelper().put(PropertyKeys.label, pLabel);
		handleAttribute("label", pLabel);
	}
	
	public java.lang.String getLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.label, "");
	}	
	
	public Boolean getFloatLeft() {
		return (Boolean) getStateHelper().eval(PropertyKeys.floatLeft, false);
	}

	public void setFloatLeft(Boolean pFloatLeft) {
		getStateHelper().put(PropertyKeys.floatLeft, pFloatLeft);
		handleAttribute("floatLeft", pFloatLeft);
	}

	
}
