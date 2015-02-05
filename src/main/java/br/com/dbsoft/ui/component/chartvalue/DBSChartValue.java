package br.com.dbsoft.ui.component.chartvalue;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSChartValue.COMPONENT_TYPE)
public class DBSChartValue extends DBSUIInput implements NamingContainer {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTVALUE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		index,
		fillColor;

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

	public DBSChartValue(){
		setRendererType(DBSChartValue.RENDERER_TYPE);
    }
	

	public Integer getIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.index, 1);
	}
	public void setIndex(Integer pIndex) {
		getStateHelper().put(PropertyKeys.index, pIndex);
		handleAttribute("index", pIndex);
	}

	public String getFillColor() {
		return (String) getStateHelper().eval(PropertyKeys.fillColor, "gray");
	}
	public void setFillColor(String pFillColor) {
		getStateHelper().put(PropertyKeys.fillColor, pFillColor);
		handleAttribute("fillColor", pFillColor);
	}


	@Override
	public Double getValue() {
		return DBSNumber.toDouble(super.getValue());
	}
}
