package br.com.dbsoft.ui.component.nav;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSNavBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSNav.COMPONENT_TYPE)
public class DBSNav extends DBSNavBase{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.NAV;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
    public DBSNav(){
		setRendererType(DBSNav.RENDERER_TYPE);
    }

}
