package br.com.dbsoft.ui.component.modalcrudmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.modalcrudmessages.IDBSModalCrudMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSModalCrudMessages.COMPONENT_TYPE)
public class DBSModalCrudMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MODALCRUDMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/modalCrudMessages.xhtml";
	
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
	
	public DBSModalCrudMessages(){
		setRendererType(DBSModalCrudMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public IDBSModalCrudMessages getCrudBean() {
		return (IDBSModalCrudMessages) getStateHelper().eval(PropertyKeys.crudBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setCrudBean(IDBSModalCrudMessages pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
	}


}