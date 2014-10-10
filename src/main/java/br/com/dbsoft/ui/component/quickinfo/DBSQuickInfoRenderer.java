package br.com.dbsoft.ui.component.quickinfo;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSQuickInfo.RENDERER_TYPE)
public class DBSQuickInfoRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent);
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSQuickInfo xQuickInfo = (DBSQuickInfo) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.QUICKINFO.MAIN + " ";

		if (xQuickInfo.getStyleClass()!=null){
			xClass = xClass + xQuickInfo.getStyleClass();
		}		
		if (xClass.trim().equals("")){xClass = null;}

		xWriter.startElement("div", xQuickInfo);
			DBSFaces.setAttribute(xWriter, "id", xQuickInfo.getClientId(pContext), null);
			DBSFaces.setAttribute(xWriter, "style", xQuickInfo.getStyle(), null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			xWriter.startElement("div", xQuickInfo);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER + DBSFaces.CSS.MODIFIER.ICON + " " +  xQuickInfo.getIconClass(), null);
				encodeClientBehaviors(pContext, xQuickInfo);
				xWriter.startElement("div", xQuickInfo);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
					renderChildren(pContext, xQuickInfo);
				xWriter.endElement("div");
			xWriter.endElement("div");
		xWriter.endElement("div");
	}
	
}
