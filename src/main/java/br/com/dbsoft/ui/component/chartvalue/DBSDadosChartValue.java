package br.com.dbsoft.ui.component.chartvalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class DBSDadosChartValue implements IDBSChartValue{

	private static final long serialVersionUID = -5134495345793538197L;
	
	@JsonProperty("label")
	private String wLabel;
	@JsonProperty("tooltip")
	private String wTooltip;
	@JsonProperty("value")
	private Double wValue;
	@JsonProperty("displayValue")
	private Double wDisplayValue;
	@JsonProperty("color")
	private String wColor;
	@JsonProperty("style")
	private String wStyle;
	@JsonProperty("styleClass")
	private String wStyleClass;
	
	@Override
	public String getLabel() {
		return wLabel;
	}
	@Override
	public void setLabel(String pLabel) {
		wLabel = pLabel;
	}
	@Override
	public String getTooltip() {
		return wTooltip;
	}
	@Override
	public void setTooltip(String pTooltip) {
		wTooltip = pTooltip;
	}
	@Override
	public Double getValue() {
		return wValue;
	}
	@Override
	public void setValue(Double pValue) {
		wValue = pValue;
	}
	@Override
	public Double getDisplayValue() {
		return wDisplayValue;
	}
	@Override
	public void setDisplayValue(Double pDisplayValue) {
		wDisplayValue = pDisplayValue;
	}
	@Override
	public String getColor() {
		return wColor;
	}
	@Override
	public void setColor(String pColor) {
		wColor = pColor;
	}
	@Override
	public String getStyle() {
		return wStyle;
	}
	@Override
	public void setStyle(String pStyle) {
		wStyle = pStyle;
	}
	@Override
	public String getStyleClass() {
		return wStyleClass;
	}
	@Override
	public void setStyleClass(String pStyleClass) {
		wStyleClass = pStyleClass;
	}
}
