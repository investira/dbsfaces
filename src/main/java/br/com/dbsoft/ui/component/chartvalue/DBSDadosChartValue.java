package br.com.dbsoft.ui.component.chartvalue;

public class DBSDadosChartValue implements IDBSChartValue{

	private static final long serialVersionUID = -5134495345793538197L;
	
	private String label;
	private String tooltip;
	private Double value;
	private Double displayValue;
	private String color;
	
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
}
