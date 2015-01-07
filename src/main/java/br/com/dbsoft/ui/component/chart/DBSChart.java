package br.com.dbsoft.ui.component.chart;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSChart.COMPONENT_TYPE)
public class DBSChart extends DBSUIInput implements NamingContainer{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHART;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	public static class TYPE{
		public static String BAR = "bar";
		public static String LINE = "line";
		public static String PIE = "pie";
	}
	protected enum PropertyKeys {
		type,
		width,
		height,
		lineWidth,

		maxValue,
		minValue,
		zeroPosition,
		whiteSpace;

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

	public DBSChart(){
		setRendererType(DBSChart.RENDERER_TYPE);
    }
	

	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, TYPE.BAR);
	}
	
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}
	
	public Long getLineWidth() {
		return (Long) getStateHelper().eval(PropertyKeys.lineWidth, 1L);
	}
	public void setLineWidth(Long pLineWidth) {
		getStateHelper().put(PropertyKeys.lineWidth, pLineWidth);
		handleAttribute("lineWidth", pLineWidth);
	}

	public Long getHeight() {
		return (Long) getStateHelper().eval(PropertyKeys.height, 50L);
	}
	public void setHeight(Long pHeight) {
		getStateHelper().put(PropertyKeys.height, pHeight);
		handleAttribute("hight", pHeight);
	}

	public Long getWidth() {
		return (Long) getStateHelper().eval(PropertyKeys.width, 50L);
	}
	public void setWidth(Long pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("width", pWidth);
	}

	public Double getMaxValue() {
		return (Double) getStateHelper().eval(PropertyKeys.maxValue, 50D);
	}
	public void setMaxValue(Double pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}

	public Double getMinValue() {
		return (Double) getStateHelper().eval(PropertyKeys.minValue, 50D);
	}
	public void setMinValue(Double pMinValue) {
		getStateHelper().put(PropertyKeys.minValue, pMinValue);
		handleAttribute("minValue", pMinValue);
	}

	public Double getTotalValue() {
		return DBSNumber.subtract(getMaxValue(), getMinValue()).doubleValue();
	}

	public Integer getZeroPosition() {
		return (Integer) getStateHelper().eval(PropertyKeys.zeroPosition, 0);
	}
	public void setZeroPosition(Integer pZeroPosition) {
		getStateHelper().put(PropertyKeys.zeroPosition, pZeroPosition);
		handleAttribute("zeroPosition", pZeroPosition);
	}
	
	public Integer getWhiteSpace() {
		return (Integer) getStateHelper().eval(PropertyKeys.whiteSpace, 0);
	}
	public void setWhiteSpace(Integer pWhiteSpace) {
		getStateHelper().put(PropertyKeys.whiteSpace, pWhiteSpace);
		handleAttribute("whiteSpace", pWhiteSpace);
	}
	
}
