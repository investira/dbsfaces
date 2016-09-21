package br.com.dbsoft.ui.component.beannavmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.beandialogmessages.IDBSBeanDialogMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanNavMessages.COMPONENT_TYPE)
public class DBSBeanNavMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANNAVMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanNavMessages.xhtml";
	
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
	
	public DBSBeanNavMessages(){
		setRendererType(DBSBeanNavMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public IDBSBeanDialogMessages getUserBean() {
		return (IDBSBeanDialogMessages) getStateHelper().eval(PropertyKeys.userBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setUserBean(IDBSBeanDialogMessages pUserBean) {
		getStateHelper().put(PropertyKeys.userBean, pUserBean);
	}


}