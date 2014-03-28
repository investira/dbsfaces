package br.com.dbsoft.ui.component.include;

import br.com.dbsoft.ui.component.DBSUIComponentBase;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSInclude.COMPONENT_TYPE)
public class DBSInclude extends DBSUIComponentBase {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.INCLUDE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
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
    public DBSInclude(){
		setRendererType(DBSInclude.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

	public java.lang.String getSrc() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.src, null);
	}
	
	public void setSrc(java.lang.String pSrc) {
		getStateHelper().put(PropertyKeys.src, pSrc);
		handleAttribute("src", pSrc);
	}

}
