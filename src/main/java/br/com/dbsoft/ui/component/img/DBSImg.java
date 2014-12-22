package br.com.dbsoft.ui.component.img;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSImg.COMPONENT_TYPE)
public class DBSImg extends DBSUIOutput {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.IMG;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		src,
		tooptip,
		alt;
		
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
    public DBSImg(){
		setRendererType(DBSImg.RENDERER_TYPE);
    }
	
	public String getSrc() {
		return (String) getStateHelper().eval(PropertyKeys.src, null);
	}
	
	public void setSrc(String pSrc) {
		getStateHelper().put(PropertyKeys.src, pSrc);
		handleAttribute("src", pSrc);
	}
	
	public String getAlt() {
		return (String) getStateHelper().eval(PropertyKeys.alt, null);
	}
	
	public void setAlt(String pAlt) {
		getStateHelper().put(PropertyKeys.alt, pAlt);
		handleAttribute("alt", pAlt);
	}
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooptip, null);
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooptip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}
	
}
