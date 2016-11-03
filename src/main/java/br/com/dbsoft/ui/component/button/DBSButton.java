package br.com.dbsoft.ui.component.button;


import javax.faces.component.FacesComponent;


import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSButton.COMPONENT_TYPE)
public class DBSButton extends DBSUICommand {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BUTTON;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		label,
		iconClass,
		disabled;

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
	
    public DBSButton(){
		setRendererType(DBSButton.RENDERER_TYPE);
    }
	

	public String getLabel() {
		return (String) getStateHelper().eval(PropertyKeys.label, null);
	}
	
	public void setLabel(String pLabel) {
		getStateHelper().put(PropertyKeys.label, pLabel);
		handleAttribute("label", pLabel);
	}


	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}	
	
	public void setDisabled(Boolean pDisabled) {
		getStateHelper().put(PropertyKeys.disabled, pDisabled);
		handleAttribute("disabled", pDisabled);
	}
	
	public Boolean getDisabled() {
		return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
	}





}
