package br.com.dbsoft.ui.component.report;


import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.bean.report.DBSReportBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSReport.COMPONENT_TYPE)
public class DBSReport extends DBSUIComponentBase implements NamingContainer { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.REPORT;
	public final static String RENDERER_TYPE = "/resources/component/report.xhtml";
	
	protected enum PropertyKeys {
		reportBean;
		
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
	
	public DBSReport(){
		setRendererType(DBSReport.RENDERER_TYPE);
	}
	
    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	public DBSReportBean getReportBean() {
		return (DBSReportBean) getStateHelper().eval(PropertyKeys.reportBean, null);
	}

	public void setReportBean(DBSReportBean pReportBean) {
		getStateHelper().put(PropertyKeys.reportBean, pReportBean);
//		handleAttribute("reportBean", pReportBean);
	}
	
	public String getForm(){
		return RenderKitUtils.getFormClientId(this, FacesContext.getCurrentInstance());
	}
	
}