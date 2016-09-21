package br.com.dbsoft.ui.component.beannavcrudmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.beandialogcrudmessages.IDBSBeanDialogCrudMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanNavCrudMessages.COMPONENT_TYPE)
public class DBSBeanNavCrudMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANNAVCRUDMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanNavCrudMessages.xhtml";
	
	protected enum PropertyKeys {
		crudBean;
		
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
	
	public DBSBeanNavCrudMessages(){
		setRendererType(DBSBeanNavCrudMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public IDBSBeanDialogCrudMessages getCrudBean() {
		return (IDBSBeanDialogCrudMessages) getStateHelper().eval(PropertyKeys.crudBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setCrudBean(IDBSBeanDialogCrudMessages pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
	}


}