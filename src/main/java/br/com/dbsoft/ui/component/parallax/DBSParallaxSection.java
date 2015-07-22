package br.com.dbsoft.ui.component.parallax;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSParallaxSection.COMPONENT_TYPE)
public class DBSParallaxSection extends DBSUIComponentBase {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.PARALLAXSECTION;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		styleClass,
		style,
		a,
		centerAttraction;

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
    public DBSParallaxSection(){
		setRendererType(DBSParallaxSection.RENDERER_TYPE);
    }
	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public void setA(Integer pA) {
		getStateHelper().put(PropertyKeys.a, pA);
		handleAttribute("a", pA);
	}
	
	public Integer getA() {
		return (Integer) getStateHelper().eval(PropertyKeys.a, null);
	}		
	
	public void setCenterAttraction(Boolean pCenterAttraction) {
		getStateHelper().put(PropertyKeys.centerAttraction, pCenterAttraction);
		handleAttribute("centerAttraction", pCenterAttraction);
	}
	
	public Boolean getCenterAttraction() {
		return (Boolean) getStateHelper().eval(PropertyKeys.centerAttraction, null);
	}		
}
