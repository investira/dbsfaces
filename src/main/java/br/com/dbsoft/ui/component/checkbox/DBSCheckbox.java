package br.com.dbsoft.ui.component.checkbox;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSCheckbox.COMPONENT_TYPE)
public class DBSCheckbox extends DBSUIInput{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHECKBOX;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		update,
		execute,
		invertLabel;

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

    public DBSCheckbox(){
		setRendererType(DBSCheckbox.RENDERER_TYPE);
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
	
	
	public void setInvertLabel(Boolean pInvertLabel) {
		getStateHelper().put(PropertyKeys.invertLabel, pInvertLabel);
		handleAttribute("invertLabel", pInvertLabel);
	}
	
	public Boolean getInvertLabel() {
		return (Boolean) getStateHelper().eval(PropertyKeys.invertLabel, false);
	}

}
