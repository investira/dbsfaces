package br.com.dbsoft.ui.component.beandialogmessage;


import java.util.ArrayList;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSBeanDialogMessage.COMPONENT_TYPE)
public class DBSBeanDialogMessage extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.BEANDIALOGMESSAGE;
	public final static String RENDERER_TYPE = "/resources/component/beanDialogMessage.xhtml";
	
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
	
	ArrayList<String> 	wColumnsWidths = new ArrayList<String>();
	String				wColumnsWidth = "";
	Integer				wWidth = 0;
	
	public DBSBeanDialogMessage(){
		setRendererType(DBSBeanDialogMessage.RENDERER_TYPE);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	
	/**
	 * Retorna a largura individual de cada coluna
	 * @return
	 */
	public DBSBean getUserBean() {
		return (DBSBean) getStateHelper().eval(PropertyKeys.userBean, null);
	}

	/**
	 * Defini a largura individual de cada culuna. Os valores de cada coluna dever set separados por vírgula sem espaço.
	 * Os valores devem ser em pixels(px)
	 * @param pColumnsWidth
	 */
	public void setUserBean(DBSBean pUserBean) {
		getStateHelper().put(PropertyKeys.userBean, pUserBean);
	}


}