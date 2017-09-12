package br.com.dbsoft.ui.component.menuitemseparator;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 * Componente necessário pois a respectiva tag html padrão é ignorada quando utilizada dentro de um <f:facet> 
 */
@FacesComponent(DBSMenuitemSeparator.COMPONENT_TYPE)
public class DBSMenuitemSeparator extends DBSUIOutput {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MENUITEMSEPARATOR;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	public DBSMenuitemSeparator(){
		setRendererType(DBSMenuitemSeparator.RENDERER_TYPE);
    }
	
}
