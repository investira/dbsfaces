package br.com.dbsoft.ui.component.link;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;


import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSLink.COMPONENT_TYPE)
public class DBSLink extends DBSUICommand {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.LINK;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
    public DBSLink(){
		setRendererType(DBSLink.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

	
}
