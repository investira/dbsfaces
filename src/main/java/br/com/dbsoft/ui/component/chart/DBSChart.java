package br.com.dbsoft.ui.component.chart;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSChart.COMPONENT_TYPE)
public class DBSChart extends DBSUIInput {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CHART;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

//	protected enum PropertyKeys {
//		styleClass,
//		style;
//
//		String toString;
//
//		PropertyKeys(String toString) {
//			this.toString = toString;
//		}
//
//		PropertyKeys() {}
//
//		@Override
//		public String toString() {
//			return ((this.toString != null) ? this.toString : super.toString());
//		}
//	}

	public DBSChart(){
		setRendererType(DBSChart.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

	
}
