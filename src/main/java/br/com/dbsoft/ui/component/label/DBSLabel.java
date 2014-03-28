package br.com.dbsoft.ui.component.label;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSLabel.COMPONENT_TYPE)
public class DBSLabel extends DBSUIOutput implements ClientBehaviorHolder {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.LABEL;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
//		value,
		labelFor,
		labelWidth,
		style,
		tooltip,
		styleClass,
		selectable;

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
    public DBSLabel(){
		setRendererType(DBSLabel.RENDERER_TYPE);
    }
	
//	public String getValue() {
//		return (String) getStateHelper().eval(PropertyKeys.value, null);
//	}
//	
//	public void setValue(String pValue) {
//		getStateHelper().put(PropertyKeys.value, pValue);
//		handleAttribute("value", pValue);
//	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, "");
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public String getLabelFor() {
		return (String) getStateHelper().eval(PropertyKeys.labelFor, null);
	}
	
	public String getLabelWidth() {
		return (String) getStateHelper().eval(PropertyKeys.labelWidth, null);
	}
	
	public void setLabelWidth(String pLabelWidth) {
		getStateHelper().put(PropertyKeys.labelWidth, pLabelWidth);
		handleAttribute("labelWidth", pLabelWidth);
	}
	
	public void setLabelFor(String pLabelFor) {
		getStateHelper().put(PropertyKeys.labelFor, pLabelFor);
		handleAttribute("labelFor", pLabelFor);
	}
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, "");
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}
	
	public void setSelectable(Boolean pSelectable) {
		getStateHelper().put(PropertyKeys.selectable, pSelectable);
		handleAttribute("selectable", pSelectable);
	}
	
	public Boolean getSelectable() {
		return (Boolean) getStateHelper().eval(PropertyKeys.selectable, true);
	}	

	@Override
    public String getDefaultEventName()
    {
        return "change";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "change", "dblclick", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "valueChange"); 
	}	
}
