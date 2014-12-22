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
