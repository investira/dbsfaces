package br.com.dbsoft.ui.component.submenu;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSSubmenu.COMPONENT_TYPE)
public class DBSSubmenu extends DBSMenuitem implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.SUBMENU;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
    public DBSSubmenu(){
		setRendererType(DBSSubmenu.RENDERER_TYPE);
    }

}
