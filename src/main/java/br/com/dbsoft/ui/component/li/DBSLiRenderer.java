package br.com.dbsoft.ui.component.li;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSLi.RENDERER_TYPE)
public class DBSLiRenderer extends DBSRenderer {
	
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
		DBSLi xLi = (DBSLi) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = "";
		if (xLi.getStyleClass()!=null){
			xClass = xClass + xLi.getStyleClass() + " ";
		}
//		String xClientId = xLi.getClientId(pContext);
		xWriter.startElement("li", xLi);
//			DBSFaces.setAttribute(xWriter, xLi, "id", xClientId, null);
//			xWriter.writeAttribute("name", xClientId, "name");
			if (xClass!=""){
				DBSFaces.setAttribute(xWriter, "class", xClass, null);
			}
			DBSFaces.setAttribute(xWriter, "style", xLi.getStyle(), null);
			renderChildren(pContext, xLi);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.endElement("li");
	}
}
