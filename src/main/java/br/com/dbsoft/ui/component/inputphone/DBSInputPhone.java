package br.com.dbsoft.ui.component.inputphone;


import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSPhone;

@FacesComponent(DBSInputPhone.COMPONENT_TYPE)
public class DBSInputPhone extends DBSUIInput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.INPUTPHONE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		autocomplete,
		country,
		showDDI,
		showDDD;

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
	
    public DBSInputPhone(){
		setRendererType(DBSInputPhone.RENDERER_TYPE);
    }
	
	public String getCountry() {
		return (String) getStateHelper().eval(PropertyKeys.country,  "BR");
	}
	
	public void setCountry(String pType) {
		getStateHelper().put(PropertyKeys.country, pType);
		handleAttribute("type", pType);
	}

	
	public void setAutocomplete(String pAutocomplete) {
		getStateHelper().put(PropertyKeys.autocomplete, pAutocomplete);
		handleAttribute("autocomplete", pAutocomplete);
	}
	public String getAutocomplete() {
		return (String) getStateHelper().eval(PropertyKeys.autocomplete, "off");
	}	

	public Boolean getShowDDI() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showDDI, false);
	}
	public void setShowDDI(Boolean pShowDDI) {
		getStateHelper().put(PropertyKeys.showDDI, pShowDDI);
		handleAttribute("showDDI", pShowDDI);
	}

	public Boolean getShowDDD() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showDDD, true);
	}
	public void setShowDDD(Boolean pShowDDD) {
		getStateHelper().put(PropertyKeys.showDDD, pShowDDD);
		handleAttribute("showDDD", pShowDDD);
	}
	
	public String getDDI(){
		if (super.getValue()==null){
			return "";
		}
		return DBSPhone.getDDI(getValue().toString());
	}
	public String getDDD(){
		if (super.getValue()==null){
			return "";
		}
		return DBSPhone.getDDD(getValue().toString());
	}
	public String getNumber(){
		if (super.getValue()==null){
			return "";
		}
		return DBSPhone.getNumber(getValue().toString());
	}

	

}