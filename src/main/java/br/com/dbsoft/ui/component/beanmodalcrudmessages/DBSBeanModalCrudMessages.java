package br.com.dbsoft.ui.component.beanmodalcrudmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.beanmodalcrudmessages.IDBSBeanModalCrudMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanModalCrudMessages.COMPONENT_TYPE)
public class DBSBeanModalCrudMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANMODALCRUDMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanModalCrudMessages.xhtml";
	
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
	
	public DBSBeanModalCrudMessages(){
		setRendererType(DBSBeanModalCrudMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public IDBSBeanModalCrudMessages getCrudBean() {
		return (IDBSBeanModalCrudMessages) getStateHelper().eval(PropertyKeys.crudBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setCrudBean(IDBSBeanModalCrudMessages pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
	}


}