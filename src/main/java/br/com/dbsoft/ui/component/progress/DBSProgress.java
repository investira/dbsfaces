package br.com.dbsoft.ui.component.progress;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSProgress.COMPONENT_TYPE)
public class DBSProgress extends DBSUIOutput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.PROGRESS;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
//		label,
		maxValue,
		width,
		tooltip;
		
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
	
    public DBSProgress(){
		setRendererType(DBSProgress.RENDERER_TYPE);
    }
	
//	public String getLabel() {
//		return (String) getStateHelper().eval(PropertyKeys.label, null);
//	}
//	
//	public void setLabel(String pLabel) {
//		getStateHelper().put(PropertyKeys.label, pLabel);
//		handleAttribute("label", pLabel);
//	}
//	
	
	public Double getMaxValue() {
		return (Double) getStateHelper().eval(PropertyKeys.maxValue, 100D);
	}
	
	public void setMaxValue(Double pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}

	public Double getWidth() {
		return (Double) getStateHelper().eval(PropertyKeys.width, 16D);
	}
	public void setWidth(Double pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("width", pWidth);
	}
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, "");
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}

	@Override
	public Double getValue() {
		return DBSNumber.toDouble(super.getValue());
	}
	

}
