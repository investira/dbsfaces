package br.com.dbsoft.ui.component.chart;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSChart.COMPONENT_TYPE)
public class DBSChart extends DBSUIInput implements NamingContainer{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHART;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public static class TYPE{
		public static String BAR = "bar";
		public static String LINE = "line";
		public static String PIE = "pie";
	}
	
	protected enum PropertyKeys {
		type;

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

	public DBSChart(){
		setRendererType(DBSChart.RENDERER_TYPE);
    }
	

	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, null);
	}
	
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}


}
