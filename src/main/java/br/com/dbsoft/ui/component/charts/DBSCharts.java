package br.com.dbsoft.ui.component.charts;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSCharts.COMPONENT_TYPE)
public class DBSCharts extends DBSUIInput implements NamingContainer{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTS;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	public static final Integer Padding = 2;
	public static final Integer FontSize = 8;
	
	protected enum PropertyKeys {
		caption,
		footer,
		width,
		height,
		ValueFormatMask,
		lineWidth,
		showGrid,
		showGridValue,
		maxValue,
		minValue,

		zeroPosition,
		whiteSpace,
		formatMaskWidth,
		numberOfGridLines,
		showLabel;

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

	public DBSCharts(){
		setRendererType(DBSCharts.RENDERER_TYPE);
    }
	

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}

	public String getFooter() {
		return (String) getStateHelper().eval(PropertyKeys.footer, null);
	}
	
	public void setFooter(String pFooter) {
		getStateHelper().put(PropertyKeys.footer, pFooter);
		handleAttribute("footer", pFooter);
	}
	
	public Integer getLineWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.lineWidth, 1L);
	}
	public void setLineWidth(Integer pLineWidth) {
		getStateHelper().put(PropertyKeys.lineWidth, pLineWidth);
		handleAttribute("lineWidth", pLineWidth);
	}

	public Boolean getShowGrid() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showGrid, true);
	}
	public void setShowGrid(Boolean pShowGrid) {
		getStateHelper().put(PropertyKeys.showGrid, pShowGrid);
		handleAttribute("showGrid", pShowGrid);
	}

	public Boolean getShowGridValue() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showGridValue, true);
	}
	public void setShowGridValue(Boolean pShowGridValue) {
		getStateHelper().put(PropertyKeys.showGridValue, pShowGridValue);
		handleAttribute("showGridValue", pShowGridValue);
	}


	public Integer getHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.height, 50L);
	}
	public void setHeight(Integer pHeight) {
		getStateHelper().put(PropertyKeys.height, pHeight);
		handleAttribute("hight", pHeight);
	}

	public Integer getWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.width, 50L);
	}
	public void setWidth(Integer pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("width", pWidth);
	}

	public String getValueFormatMask() {
		return (String) getStateHelper().eval(PropertyKeys.ValueFormatMask, "");
	}
	
	public void setValueFormatMask(String pValueFormatMask) {
		getStateHelper().put(PropertyKeys.ValueFormatMask, pValueFormatMask);
		handleAttribute("valueFormatMask", pValueFormatMask);
	}

	//--------------------

	public Double getMaxValue() {
		return (Double) getStateHelper().eval(PropertyKeys.maxValue, null);
	}
	public void setMaxValue(Double pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}

	public Double getMinValue() {
		return (Double) getStateHelper().eval(PropertyKeys.minValue, null);
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
	
	public Double getWhiteSpace() {
		return (Double) getStateHelper().eval(PropertyKeys.whiteSpace, 0D);
	}
	public void setWhiteSpace(Double pWhiteSpace) {
		getStateHelper().put(PropertyKeys.whiteSpace, pWhiteSpace);
		handleAttribute("whiteSpace", pWhiteSpace);
	}

	public Integer getFormatMaskWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.formatMaskWidth, 0);
	}
	public void setFormatMaskWidth(Integer pFormatMaskWidth) {
		getStateHelper().put(PropertyKeys.formatMaskWidth, pFormatMaskWidth);
		handleAttribute("formatMaskWidth", pFormatMaskWidth);
	}
	
	public Integer getNumberOfGridLines() {
		return (Integer) getStateHelper().eval(PropertyKeys.numberOfGridLines, 6);
	}
	public void setNumberOfGridLines(Integer pNumberOfGridLines) {
		getStateHelper().put(PropertyKeys.numberOfGridLines, pNumberOfGridLines);
		handleAttribute("numberOfGridLines", pNumberOfGridLines);
	}
	
	public void setShowLabel(Boolean pShowLabel) {
		getStateHelper().put(PropertyKeys.showLabel, pShowLabel);
		handleAttribute("readOnly", pShowLabel);
	}
	
	public Boolean getShowLabel() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showLabel, false);
	}
	
	public Integer getChartHeight(){
		Integer xChartHeight = getHeight();
		if (getShowLabel()){
			xChartHeight -= DBSCharts.FontSize;
		}
		return xChartHeight;
	}
}
