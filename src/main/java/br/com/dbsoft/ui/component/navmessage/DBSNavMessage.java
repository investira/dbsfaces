package br.com.dbsoft.ui.component.navmessage;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.nav.DBSNav;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSNavMessage.COMPONENT_TYPE)
public class DBSNavMessage extends DBSNav {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.NAVMESSAGE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public DBSNavMessage() {
		setRendererType(DBSNavMessage.RENDERER_TYPE);
	}
	
	protected enum PropertyKeys {
		forParent;

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
	
	public String getFor() {
		return (String) getStateHelper().eval(PropertyKeys.forParent, null);
	}
	
	public void setFor(String pForParent) {
		getStateHelper().put(PropertyKeys.forParent, pForParent);
		handleAttribute("for", pForParent);
	}
}
