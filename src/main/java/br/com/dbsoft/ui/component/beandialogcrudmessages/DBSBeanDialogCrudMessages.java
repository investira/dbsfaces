package br.com.dbsoft.ui.component.beandialogcrudmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanDialogCrudMessages.COMPONENT_TYPE)
public class DBSBeanDialogCrudMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANDIALOGCRUDCRUDMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanDialogCrudMessages.xhtml";
	
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
	
	public DBSBeanDialogCrudMessages(){
		setRendererType(DBSBeanDialogCrudMessages.RENDERER_TYPE);
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