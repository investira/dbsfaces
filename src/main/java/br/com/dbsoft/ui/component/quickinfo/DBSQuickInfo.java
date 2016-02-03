package br.com.dbsoft.ui.component.quickinfo;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSQuickInfo.COMPONENT_TYPE)
public class DBSQuickInfo extends DBSUIOutput implements ClientBehaviorHolder {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.QUICKINFO;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		iconClass,
		showOnHover,
		tooltip,
		defaultLocation;
		
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
    public DBSQuickInfo(){
		setRendererType(DBSQuickInfo.RENDERER_TYPE);
    }

	
	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, "");
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}	
	
	public void setShowOnHover(Boolean pShowOnHover) {
		getStateHelper().put(PropertyKeys.showOnHover, pShowOnHover);
		handleAttribute("showOnHover", pShowOnHover);
	}
	
	public Boolean getShowOnHover() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showOnHover, true);
	}

	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, null);
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}	

	public Integer getDefaultLocation() {
		return (Integer) getStateHelper().eval(PropertyKeys.defaultLocation, 2);
	}
	
	public void setDefaultLocation(Integer pDefaultLocation) {
		if (pDefaultLocation == null
		 || pDefaultLocation < 1
		 || pDefaultLocation > 4){
			pDefaultLocation = 2;
		}
		getStateHelper().put(PropertyKeys.defaultLocation, pDefaultLocation);
		handleAttribute("defaultLocation", pDefaultLocation);
	}	
	
	@Override
    public String getDefaultEventName()
    {
        return "mouseover";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "dblclick", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup"); 
	}	
	


}
