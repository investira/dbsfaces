package br.com.dbsoft.ui.component.div;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSDivNC.COMPONENT_TYPE)
public class DBSDivNC extends DBSDiv implements NamingContainer  {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIVNC;

}
