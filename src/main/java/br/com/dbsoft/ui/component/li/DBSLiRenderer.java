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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSLi xLi = (DBSLi) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = "";
		if (xLi.getStyleClass()!=null){
			xClass = xClass + xLi.getStyleClass() + " ";
		}
		String xClientId = xLi.getClientId(pContext);
		xWriter.startElement("li", xLi);
			if (shouldWriteIdAttribute(xLi)){
				DBSFaces.encodeAttribute(xWriter, "id", xClientId);
				DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			}
			if (xClass!=""){
				DBSFaces.encodeAttribute(xWriter, "class", xClass);
			}
			DBSFaces.encodeAttribute(xWriter, "style", xLi.getStyle());
			DBSFaces.renderChildren(pContext, xLi);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.endElement("li");
	}
}
