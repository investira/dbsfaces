package br.com.dbsoft.ui.component.datatable;


import javax.faces.component.FacesComponent;
import javax.faces.component.UIColumn;

import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSDataTableColumn.COMPONENT_TYPE)
public class DBSDataTableColumn extends UIColumn{ 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DATATABLECOLUMN;
	//public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		style,
		styleClass,
		width;
		
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

//	public DBSDataTableColumn(){
//		super();
//		//setRendererType(null);
//	}

	@Override
	public boolean getRendersChildren() {
		// TODO Auto-generated method stub
		return super.getRendersChildren();
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

	/**
	 * Retorna a largura individual de cada coluna
	 * @return
	 */
	public java.lang.String getWidth() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.width, "10px");
	}
	/**
	 * Defini a largura individual de cada culuna. Os valores de cada coluna dever set separados por vírgula sem espaço.
	 * Os valores devem ser em pixels(px)
	 * @param pWidth
	 */
	public void setWidth(java.lang.String pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("columnWidth", pWidth);
	}
	

	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}

}