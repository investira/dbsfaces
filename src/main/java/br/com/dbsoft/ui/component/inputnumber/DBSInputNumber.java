package br.com.dbsoft.ui.component.inputnumber;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSInputNumber.COMPONENT_TYPE)
public class DBSInputNumber extends DBSUIInputText {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.INPUTNUMBER;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	private static String wMaxValue = "999999999999999"; //trilhões
	private static String wMinValue = "-" + wMaxValue;
	
	protected enum PropertyKeys {
		type,
		secret,
		size,
		decimalPlaces,
		leadingZero,
		separateThousand,
		maxValue,
		minValue,
		autocomplete;

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
	
    public DBSInputNumber(){
		setRendererType(DBSInputNumber.RENDERER_TYPE);
    }
	
	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, "date");
	}
	
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}

	
	public void setSecret(Boolean pSecret) {
		getStateHelper().put(PropertyKeys.secret, pSecret);
		handleAttribute("secret", pSecret);
	}
	public Boolean getSecret() {
		return (Boolean) getStateHelper().eval(PropertyKeys.secret, false);
	}	

	public void setDecimalPlaces(Integer pDecimalPlaces) {
		getStateHelper().put(PropertyKeys.decimalPlaces, pDecimalPlaces);
		handleAttribute("decimalPlaces", pDecimalPlaces);
	}
	public Integer getDecimalPlaces() {
		return (Integer) getStateHelper().eval(PropertyKeys.decimalPlaces, 2);
	}	

	public void setMaxValue(String pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}
	public String getMaxValue() {
		return (String) getStateHelper().eval(PropertyKeys.maxValue, wMaxValue);
	}	

	public void setMinValue(String pMinValue) {
		getStateHelper().put(PropertyKeys.minValue, pMinValue);
		handleAttribute("minValue", pMinValue);
	}
	public String getMinValue() {
		return (String) getStateHelper().eval(PropertyKeys.minValue, wMinValue);
	}	
	
	public void setSize(Integer pSize) {
		getStateHelper().put(PropertyKeys.size, pSize);
		handleAttribute("size", pSize);
	}

	public Integer getSize() {
		return (Integer) getStateHelper().eval(PropertyKeys.size, 18);
	}
	
	public void setLeadingZero(Boolean pLeadingZero) {
		getStateHelper().put(PropertyKeys.leadingZero, pLeadingZero);
		handleAttribute("leadingZero", pLeadingZero);
	}
	public Boolean getLeadingZero() {
		return (Boolean) getStateHelper().eval(PropertyKeys.leadingZero, false);
	}	

	public void setSeparateThousand(Boolean pSeparateThousand) {
		getStateHelper().put(PropertyKeys.separateThousand, pSeparateThousand);
		handleAttribute("separateThousand", pSeparateThousand);
	}
	public Boolean getSeparateThousand() {
		return (Boolean) getStateHelper().eval(PropertyKeys.separateThousand, true);
	}	
	
	public void setAutocomplete(String pAutocomplete) {
		getStateHelper().put(PropertyKeys.autocomplete, pAutocomplete);
		handleAttribute("autocomplete", pAutocomplete);
	}
	public String getAutocomplete() {
		return (String) getStateHelper().eval(PropertyKeys.autocomplete, "off");
	}	

	/**
	 * Retorna o valor convertido para o tipo Double
	 * @return
	 */
	public Double getValueDouble(){
		return DBSNumber.toDouble(super.getValue());
	}
	

}