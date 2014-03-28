package br.com.dbsoft.ui.component.img;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSImg.COMPONENT_TYPE)
public class DBSImg extends DBSUIOutput {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.IMG;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		src,
		style,
		styleClass,
		tooptip,
		alt;
		
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
    public DBSImg(){
		setRendererType(DBSImg.RENDERER_TYPE);
    }
	
	public java.lang.String getSrc() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.src, null);
	}
	
	public void setSrc(java.lang.String pSrc) {
		getStateHelper().put(PropertyKeys.src, pSrc);
		handleAttribute("src", pSrc);
	}
	
	public java.lang.String getAlt() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.alt, null);
	}
	
	public void setAlt(java.lang.String pAlt) {
		getStateHelper().put(PropertyKeys.alt, pAlt);
		handleAttribute("alt", pAlt);
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
	
	public java.lang.String getTooltip() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tooptip, null);
	}
	
	public void setTooltip(java.lang.String pTooltip) {
		getStateHelper().put(PropertyKeys.tooptip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}
	
}
