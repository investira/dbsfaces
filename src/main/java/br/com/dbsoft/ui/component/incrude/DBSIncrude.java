package br.com.dbsoft.ui.component.incrude;

import javax.faces.component.FacesComponent;

import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent("br.com.dbsoft.ui.component.incrude")
public class DBSIncrude extends DBSUIOutput {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + "incrude";
	public final static String RENDERER_TYPE = "./resources/component/incrude.xhtml";

	//private String wSrc = "aaa";
	
	protected enum PropertyKeys {
		src;

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
	public DBSIncrude(){
		setRendererType(DBSIncrude.RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
		//return DBSFaces.FAMILY;
	}
	
	public String getSrc() {
		System.out.println("INCRUDE Get SRC=" + (java.lang.String) getStateHelper().eval(PropertyKeys.src, ""));
		return (java.lang.String) getStateHelper().eval(PropertyKeys.src, "");
	}
	
	public void setSrc(String pSrc) {
		System.out.println("INCRUDE Set SRC=" + pSrc);
		getStateHelper().put(PropertyKeys.src, pSrc);
		handleAttribute("src", pSrc);
	}
}
