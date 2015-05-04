package br.com.dbsoft.ui.component.beandialogmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanDialogMessages.COMPONENT_TYPE)
public class DBSBeanDialogMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANDIALOGMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanDialogMessages.xhtml";
	
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
	
	public DBSBeanDialogMessages(){
		setRendererType(DBSBeanDialogMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public DBSBean getUserBean() {
		return (DBSBean) getStateHelper().eval(PropertyKeys.userBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setUserBean(DBSBean pUserBean) {
		getStateHelper().put(PropertyKeys.userBean, pUserBean);
	}


}