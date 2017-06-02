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
	
	public String getColor();
	public void setColor(String pColor);

	public String getStyle();
	public void setStyle(String pStyle);

	public String getStyleClass();
	public void setStyleClass(String pStyleClass);
}
