package br.com.dbsoft.ui.component.chart;

public class DBSChartDelta implements IDBSChartDelta {

	private String Id = null;
	private String Label;
	private String StartLabel;
	private String EndLabel;
	private String Tooltip;
	private String IconClass;
	
	@Override
	public String getId() {
		return Id;
	}
	@Override
	public void setId(String pId) {
		Id = pId;
	}
	
	@Override
	public String getLabel() {
		return Label;
	}
	@Override
	public void setLabel(String pLabel) {
		Label = pLabel;
	}
	
	@Override
	public String getStartLabel() {
		return StartLabel;
	}
	@Override
	public void setStartLabel(String pStartLabel) {
		StartLabel = pStartLabel;
	}
	@Override
	public String getEndLabel() {
		return EndLabel;
	}
	@Override
	public void setEndLabel(String pEndLabel) {
		EndLabel = pEndLabel;
	}
	@Override
	public String getTooltip() {
		return Tooltip;
	}
	@Override
	public void setTooltip(String pTooltip) {
		Tooltip = pTooltip;
		
	}
	@Override
	public String getIconClass() {
		return IconClass;
	}
	@Override
	public void setIconClass(String pIconClass) {
		IconClass = pIconClass;
	}

	
}
