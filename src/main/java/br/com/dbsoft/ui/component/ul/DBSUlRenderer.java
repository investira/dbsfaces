package br.com.dbsoft.ui.component.ul;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSUl.RENDERER_TYPE)
public class DBSUlRenderer extends DBSRenderer {
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSUl xUl = (DBSUl) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = "";
		if (xUl.getStyleClass()!=null){
			xClass = xClass + xUl.getStyleClass() + " ";
		}
//		String xClientId = xUl.getClientId(pContext);
		xWriter.startElement("ul", xUl);
//			DBSFaces.setAttribute(xWriter, xUl, "id", xClientId, null);
//			xWriter.writeAttribute("name", xClientId, "name");
			if (xClass!=""){
				DBSFaces.encodeAttribute(xWriter, "class", xClass);
			}
			DBSFaces.encodeAttribute(xWriter, "style", xUl.getStyle());
			DBSFaces.renderChildren(pContext, xUl);
	} 
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.endElement("ul");
	}
}
