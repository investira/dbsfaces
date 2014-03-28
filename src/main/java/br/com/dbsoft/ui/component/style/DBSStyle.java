package br.com.dbsoft.ui.component.style;

import javax.faces.component.FacesComponent;


import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSStyle.COMPONENT_TYPE)
public class DBSStyle extends DBSUIOutput {
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.STYLE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

}


