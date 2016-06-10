package br.com.dbsoft.ui.component.menuitemseparator;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSMenuitemSeparator.RENDERER_TYPE)
public class DBSMenuitemSeparatorRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
//    	if (pComponent.getChildren().size()!=0){
//    	}
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSMenuitemSeparator xMenuitemSeparator = (DBSMenuitemSeparator) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.startElement("span", xMenuitemSeparator);
			DBSFaces.setAttribute(xWriter, "class", CSS.MENUITEMSEPARATOR.MAIN);
		xWriter.endElement("span");
	}

}
