package br.com.dbsoft.ui.component.modalmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSModalMessages.COMPONENT_TYPE)
public class DBSModalMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MODALMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/modalMessages.xhtml";
	
	protected enum PropertyKeys {
		userBean;
		
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
	
	public DBSModalMessages(){
		setRendererType(DBSModalMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public IDBSModalMessages getUserBean() {
		return (IDBSModalMessages) getStateHelper().eval(PropertyKeys.userBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setUserBean(IDBSModalMessages pUserBean) {
		getStateHelper().put(PropertyKeys.userBean, pUserBean);
	}


}