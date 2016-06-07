package br.com.dbsoft.ui.component.chart;

public interface IDBSChartDelta {

	public String getId();
	public void setId(String pId);

	public String getLabel();
	public void setLabel(String pLabel);

	public String getStartLabel();
	public void setStartLabel(String pStartLabel);

	public String getEndLabel();
	public void setEndLabel(String pEndLabel);

	public String getTooltip();
	public void setTooltip(String pTooltip);

	public String getIconClass();
	public void setIconClass(String pIconClass);
}
