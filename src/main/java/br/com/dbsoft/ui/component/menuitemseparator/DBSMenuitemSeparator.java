package br.com.dbsoft.ui.component.menuitemseparator;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;


import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 * Componente necessário pois a respectiva tag html padrão é ignorada quando utilizada dentro de um <f:facet> 
 */
@FacesComponent(DBSMenuitemSeparator.COMPONENT_TYPE)
public class DBSMenuitemSeparator extends DBSUIComponentBase {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MENUITEMSEPARATOR;
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
//		public String toString() {
//			return ((this.toString != null) ? this.toString : super.toString());
//		}
//	}

	public DBSMenuitemSeparator(){
		setRendererType(DBSMenuitemSeparator.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

}
