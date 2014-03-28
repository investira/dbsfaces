package br.com.dbsoft.ui.component.radio;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSRadio.COMPONENT_TYPE)
public class DBSRadio extends DBSUIInput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.RADIO;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		floatLeft,		
		update,
		execute;

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
	
    public DBSRadio(){
		setRendererType(DBSRadio.RENDERER_TYPE);
    }

	public Boolean getFloatLeft() {
		return (Boolean) getStateHelper().eval(PropertyKeys.floatLeft, true);
	}

	public void setFloatLeft(Boolean pFloatLeft) {
		getStateHelper().put(PropertyKeys.floatLeft, pFloatLeft);
		handleAttribute("floatLeft", pFloatLeft);
	}
	
	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}
	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, null);
	}

	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}
	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, "");
	}

}


