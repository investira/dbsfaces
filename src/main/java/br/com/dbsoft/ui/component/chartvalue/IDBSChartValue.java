package br.com.dbsoft.ui.component.chartvalue;

import java.io.Serializable;

public interface IDBSChartValue extends Serializable {

	public String getLabel();
	public void setLabel(String pLabel);
	
	public String getTooltip();
	public void setTooltip(String pTooltip);
	
	public Double getValue();
	public void setValue(Double pValue);
	
	public Double getDisplayValue();
	public void setDisplayValue(Double pDisplayValue);
}