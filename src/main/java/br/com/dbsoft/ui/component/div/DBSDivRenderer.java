package br.com.dbsoft.ui.component.div;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSDiv.RENDERER_TYPE)
public class DBSDivRenderer extends DBSRenderer {
	
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
		DBSDiv xDiv = (DBSDiv) pComponent;
		//xDiv.setTransient(true);
		//System.out.println("RENDER DIV #############################" + xDiv.getClientId(pContext) + ":" + xDiv.getStyleClass());		
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = "";
		if (xDiv.getStyleClass()!=null){
			xClass = xClass + xDiv.getStyleClass() + " ";
		}
		if (xDiv.getSelectable()!=null){
			if (!xDiv.getSelectable()){
				xClass = xClass + DBSFaces.CSS.NOT_SELECTABLE.trim() + " ";
			}
		}
		String xClientId = xDiv.getClientId(pContext);
		xWriter.startElement("div", xDiv);
			if (shouldWriteIdAttribute(xDiv)){
				DBSFaces.setAttribute(xWriter, "id", xClientId, null);
				DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			}
			if (xClass!=""){
				DBSFaces.setAttribute(xWriter, "class", xClass, null);
			}
			DBSFaces.setAttribute(xWriter, "style", xDiv.getStyle(), null);
			
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xDiv, DBSPassThruAttributes.getAttributes(Key.DIV));

			renderChildren(pContext, xDiv);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.endElement("div");
	}
}
