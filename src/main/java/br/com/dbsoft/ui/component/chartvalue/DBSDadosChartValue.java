package br.com.dbsoft.ui.component.chartvalue;

public class DBSDadosChartValue implements IDBSChartValue{

	private static final long serialVersionUID = -5134495345793538197L;
	
	private String wLabel;
	private String wTooltip;
	private Double wValue;
	private Double wDisplayValue;
	
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
}
