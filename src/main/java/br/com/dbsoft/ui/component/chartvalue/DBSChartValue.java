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
		fillColor,
		absoluteX,
		absoluteY;

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
	

	public Long getIndex() {
		return (Long) getStateHelper().eval(PropertyKeys.index, 1L);
	}
	public void setIndex(Long pIndex) {
		if (pIndex != null){
			setId("index_" + pIndex.toString());
		}
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

	public Integer getAbsoluteX() {
		return (Integer) getStateHelper().eval(PropertyKeys.absoluteX, "absoluteX");
	}
	public void setAbsoluteX(Integer pAbsoluteX) {
		getStateHelper().put(PropertyKeys.absoluteX, pAbsoluteX);
		handleAttribute("absoluteX", pAbsoluteX);
	}

	public Integer getAbsoluteY() {
		return (Integer) getStateHelper().eval(PropertyKeys.absoluteY, "absoluteY");
	}
	public void setAbsoluteY(Integer pAbsoluteY) {
		getStateHelper().put(PropertyKeys.absoluteY, pAbsoluteY);
		handleAttribute("absoluteY", pAbsoluteY);
	}
}
