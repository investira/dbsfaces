//TODO SEM USO
package br.com.dbsoft.ui.component.push;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSPush.COMPONENT_TYPE)
public class DBSPush extends DBSUIComponentBase {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.PUSH;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		url,
		showStatus;

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
	
    public DBSPush(){
		setRendererType(DBSPush.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

	public String getUrl() {
		return (String) getStateHelper().eval(PropertyKeys.url, "");
	}
	public void setUrl(String pUrl) {
		getStateHelper().put(PropertyKeys.url, pUrl);
		handleAttribute("url", pUrl);
	}

	public Boolean getShowStatus() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showStatus, false);
	}
	public void setShowStatus(Boolean pShowStatus) {
		getStateHelper().put(PropertyKeys.showStatus, pShowStatus);
		handleAttribute("showStatus", pShowStatus);
	}

}
