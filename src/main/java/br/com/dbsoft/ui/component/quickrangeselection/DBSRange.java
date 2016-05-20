package br.com.dbsoft.ui.component.quickrangeselection;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSRange.COMPONENT_TYPE)
public class DBSRange extends DBSUIComponentBase{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.RANGE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		value1,
		value2,
		label,
		tooltip,
		iconClass;

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
    public DBSRange(){
		setRendererType(DBSRange.RENDERER_TYPE);
    }

    public String getValue1() {
		return (String) getStateHelper().eval(PropertyKeys.value1, null);
	}
	
	public void setValue1(String pValue1) {
		getStateHelper().put(PropertyKeys.value1, pValue1);
		handleAttribute("value1", pValue1);
	}
	
	public String getValue2() {
		return (String) getStateHelper().eval(PropertyKeys.value2, null);
	}
	
	public void setValue2(String pValue2) {
		getStateHelper().put(PropertyKeys.value2, pValue2);
		handleAttribute("value2", pValue2);
	}

	public String getLabel() {
		return (String) getStateHelper().eval(PropertyKeys.label, null);
	}
	public void setLabelFor(String pLabel) {
		getStateHelper().put(PropertyKeys.label, pLabel);
		handleAttribute("label", pLabel);
	}

	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, "");
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}
	
	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}

	@Override
    public String getDefaultEventName()
    {
        return "click";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "change", "dblclick", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "valueChange"); 
	}	
}
