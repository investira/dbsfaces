package br.com.dbsoft.ui.component.reportform;


import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import br.com.dbsoft.ui.bean.report.DBSReportBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSReportForm.COMPONENT_TYPE)
public class DBSReportForm extends DBSUIComponentBase { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.REPORTFORM;
	public final static String RENDERER_TYPE = "/resources/component/reportForm.xhtml";
	
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
	
	public DBSReportForm(){
		setRendererType(DBSReportForm.RENDERER_TYPE);
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
		handleAttribute("reportBean", pReportBean);
	}
	
}