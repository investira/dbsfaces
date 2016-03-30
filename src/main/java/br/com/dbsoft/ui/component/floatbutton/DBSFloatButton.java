package br.com.dbsoft.ui.component.floatbutton;


import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * Botão de menu flutuante. 
 * Os botões do menu devem ser do tipo dbs:menuitem como filhos do floatButton.
 * Ex:
 * <dbs:floatButton id="floatButton" iconClass="-i_compass">
 * 		<dbs:menuitem iconClass="-i_sun" action="#"/>
 * 		<dbs:menuitem iconClass="-i_moon" action="#"/>
 *		<dbs:menuitem iconClass="-i_earth" action="#"/>
 * </dbs:floatButton>
 * 
 * @author jose.avila@dbsoft.com.br
 *
 */
@FacesComponent(DBSFloatButton.COMPONENT_TYPE)
public class DBSFloatButton extends DBSUICommand {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.FLOATBUTTON;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		iconClass,
		tooltip,
		horizontal,
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
	
    public DBSFloatButton(){
		setRendererType(DBSFloatButton.RENDERER_TYPE);
    }
	
	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}	
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, null);
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}	
	
	public void setHorizontal(Boolean pHorizontal) {
		getStateHelper().put(PropertyKeys.horizontal, pHorizontal);
		handleAttribute("horizontal", pHorizontal);
	}
	
	public Boolean getHorizontal() {
		return (Boolean) getStateHelper().eval(PropertyKeys.horizontal, false);
	}
	
	public Integer getDefaultLocation() {
		return (Integer) getStateHelper().eval(PropertyKeys.defaultLocation, 3);
	}
	
	public void setDefaultLocation(Integer pDefaultLocation) {
		Integer xDefaultLocation = pDefaultLocation;
		if (pDefaultLocation == null
		 || pDefaultLocation < 1
		 || pDefaultLocation > 4){
			xDefaultLocation = 3;
		}
		getStateHelper().put(PropertyKeys.defaultLocation, xDefaultLocation);
		handleAttribute("defaultLocation", xDefaultLocation);
	}

}
