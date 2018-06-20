package br.com.dbsoft.ui.component.chartvalue;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class DBSDadosChartValue implements IDBSChartValue{

	private static final long serialVersionUID = -5134495345793538197L;
	
	private String label;
	private String tooltip;
	private Double value;
	private Double displayValue;
	private String color;
	private String style;
	private String styleClass;
	
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String pLabel) {
		label = pLabel;
	}
	@Override
	public String getTooltip() {
		return tooltip;
	}
	@Override
	public void setTooltip(String pTooltip) {
		tooltip = pTooltip;
	}
	@Override
	public Double getValue() {
		return value;
	}
	@Override
	public void setValue(Double pValue) {
		value = pValue;
	}
	@Override
	public Double getDisplayValue() {
		return displayValue;
	}
	@Override
	public void setDisplayValue(Double pDisplayValue) {
		displayValue = pDisplayValue;
	}
	@Override
	public String getColor() {
		return color;
	}
	@Override
	public void setColor(String pColor) {
		color = pColor;
	}
	@Override
	public String getStyle() {
		return style;
	}
	@Override
	public void setStyle(String pStyle) {
		style = pStyle;
	}
	@Override
	public String getStyleClass() {
		return styleClass;
	}
	@Override
	public void setStyleClass(String pStyleClass) {
		styleClass = pStyleClass;
	}
}
