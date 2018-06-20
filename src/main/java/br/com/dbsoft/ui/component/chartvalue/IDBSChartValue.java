package br.com.dbsoft.ui.component.chartvalue;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="@class", defaultImpl=DBSDadosChartValue.class)
@JsonSubTypes({
      @JsonSubTypes.Type(value=DBSDadosChartValue.class)
  })
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
