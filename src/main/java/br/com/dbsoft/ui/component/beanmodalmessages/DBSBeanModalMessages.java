package br.com.dbsoft.ui.component.beanmodalmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.beanmodalmessages.IDBSBeanModalMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanModalMessages.COMPONENT_TYPE)
public class DBSBeanModalMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANMODALMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanModalMessages.xhtml";
	
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
	
	public DBSBeanModalMessages(){
		setRendererType(DBSBeanModalMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public IDBSBeanModalMessages getUserBean() {
		return (IDBSBeanModalMessages) getStateHelper().eval(PropertyKeys.userBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setUserBean(IDBSBeanModalMessages pUserBean) {
		getStateHelper().put(PropertyKeys.userBean, pUserBean);
	}


}