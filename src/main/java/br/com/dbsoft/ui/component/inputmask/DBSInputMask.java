package br.com.dbsoft.ui.component.inputmask;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSInputMask.COMPONENT_TYPE)
public class DBSInputMask extends DBSUIInputText {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.INPUTMASK;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		secret,
		mask,
		stripMask,
		maskEmptyChr;
		
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
	
    public DBSInputMask(){
		setRendererType(DBSInputMask.RENDERER_TYPE);
    }
	
	public void setSecret(java.lang.Boolean pSecret) {
		getStateHelper().put(PropertyKeys.secret, pSecret);
		handleAttribute("secret", pSecret);
	}
	public java.lang.Boolean getSecret() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.secret, false);
	}	

	public void setMask(java.lang.String pMask) {
		getStateHelper().put(PropertyKeys.mask, pMask);
		handleAttribute("mask", pMask);
	}
	public java.lang.String getMask() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.mask, "A");
	}	

	public void setMaskEmptyChr(java.lang.String pMaskEmptyChr) {
		getStateHelper().put(PropertyKeys.maskEmptyChr, pMaskEmptyChr);
		handleAttribute("maskEmptyChr", pMaskEmptyChr);
	}
	public java.lang.String getMaskEmptyChr() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.maskEmptyChr, "0");
	}	

	public void setStripMask(java.lang.Boolean pStripMask) {
		getStateHelper().put(PropertyKeys.stripMask, pStripMask);
		handleAttribute("stripMask", pStripMask);
	}
	public java.lang.Boolean getStripMask() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stripMask, false);
	}	
	
	public String getValueString(){
		if(super.getValue()==null){
			return "";
		}else{
			return super.getValue().toString();
		}
	}
	
}