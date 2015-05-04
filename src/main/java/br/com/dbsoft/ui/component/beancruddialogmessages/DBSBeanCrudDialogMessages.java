package br.com.dbsoft.ui.component.beancruddialogmessages;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanCrudDialogMessages.COMPONENT_TYPE)
public class DBSBeanCrudDialogMessages extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANCRUDDIALOGMESSAGES;
	public final static String RENDERER_TYPE = "/resources/component/beanCrudDialogMessages.xhtml";
	
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
	
	public DBSBeanCrudDialogMessages(){
		setRendererType(DBSBeanCrudDialogMessages.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Bean que contém as mensagens
	 * @return
	 */
	public DBSBean getCrudBean() {
		return (DBSBean) getStateHelper().eval(PropertyKeys.crudBean, null);
	}

	/**
	 * Bean que contém as mensagens
	 * @param pColumnsWidth
	 */
	public void setCrudBean(DBSBean pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
	}


}