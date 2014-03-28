package br.com.dbsoft.ui.component.li;

import javax.faces.component.FacesComponent;


import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 * Componente necessário pois a respectiva tag html padrão é ignorada quando utilizada dentro de um <f:facet> 
 */
@FacesComponent(DBSLi.COMPONENT_TYPE)
public class DBSLi extends DBSUIComponentBase {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.LI;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		styleClass,
		style;

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
    public DBSLi(){
		setRendererType(DBSLi.RENDERER_TYPE);
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
	
}
