package br.com.dbsoft.ui.component.chart;

import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIData;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSChartX.COMPONENT_TYPE)
public class DBSChartX extends DBSUIData implements ClientBehaviorHolder{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHARTX;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		color,
		caption,
		relationalCaptions;

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

	public DBSChartX(){
		setRendererType(DBSChartX.RENDERER_TYPE);
    }

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}

	public String getColor() {
		return (String) getStateHelper().eval(PropertyKeys.color, null);
	}
	public void setColor(String pColor) {
		getStateHelper().put(PropertyKeys.color, pColor);
		handleAttribute("color", pColor);
	}

	public String getRelationalCaptions() {
		return (String) getStateHelper().eval(PropertyKeys.relationalCaptions, null);
	}
	
	public void setRelationalCaptions(String pRelationalCaptions) {
		getStateHelper().put(PropertyKeys.relationalCaptions, pRelationalCaptions);
		handleAttribute("relationalCaptions", pRelationalCaptions);
	}

//	public String getDimentionsCaptions() {
//		return (String) getStateHelper().eval(PropertyKeys.dimensionsCaptions, null);
//	}
//	
//	public void setDimentionsCaptions(String pDimentionsCaptions) {
//		getStateHelper().put(PropertyKeys.dimensionsCaptions, pDimentionsCaptions);
//		handleAttribute("dimensionsCaptions", pDimentionsCaptions);
//	}

}
