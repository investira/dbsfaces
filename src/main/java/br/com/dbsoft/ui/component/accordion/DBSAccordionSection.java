package br.com.dbsoft.ui.component.accordion;


import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSAccordionSection.COMPONENT_TYPE)
public class DBSAccordionSection extends DBSUIOutput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.ACCORDIONSECTION;
	//public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		caption,
		iconClass,
//		file,
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
 
   public DBSAccordionSection(String pCaption, String pIconClass, String pStyle, String pStyleClass){
    	this.setCaption(pCaption);
//    	this.setFile(pFile);
    	this.setIconClass(pIconClass);
    	this.setStyle(pStyle);
    	this.setStyleClass(pStyleClass);
    }
	
	public DBSAccordionSection(){
    	setRendererType(null);
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
	
	public java.lang.String getCaption() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(java.lang.String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("styleClass", pCaption);
	}
	
	public java.lang.String getIconClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(java.lang.String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}	

//	public java.lang.String getFile() {
//		return (java.lang.String) getStateHelper().eval(PropertyKeys.file, null);
//	}
//	
//	public void setFile(java.lang.String pFile) {
//		getStateHelper().put(PropertyKeys.file, pFile);
//		handleAttribute("file", pFile);
//	}	
	
}
