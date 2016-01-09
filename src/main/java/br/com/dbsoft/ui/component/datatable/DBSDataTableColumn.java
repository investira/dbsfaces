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
		sortable,
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

	@Override
	public boolean getRendersChildren() {
		return super.getRendersChildren();
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

	/**
	 * Retorna a largura individual de cada coluna
	 * @return
	 */
	public String getWidth() {
		return (String) getStateHelper().eval(PropertyKeys.width, "10px");
	}
	/**
	 * Defini a largura individual de cada culuna. Os valores de cada coluna dever set separados por vírgula sem espaço.
	 * Os valores devem ser em pixels(px)
	 * @param pWidth
	 */
	public void setWidth(String pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("columnWidth", pWidth);
	}
	

	public Boolean getSortable() {
		return (Boolean) getStateHelper().eval(PropertyKeys.sortable, false);
	}

	public void setSortable(Boolean pSortable) {
		getStateHelper().put(PropertyKeys.sortable, pSortable);
		handleAttribute("sortable", pSortable);
	}

	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}
	
	


}