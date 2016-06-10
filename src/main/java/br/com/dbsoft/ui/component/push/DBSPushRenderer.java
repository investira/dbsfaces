package br.com.dbsoft.ui.component.push;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSPush.RENDERER_TYPE)
public class DBSPushRenderer extends DBSRenderer {
	
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
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSPush xPush = (DBSPush) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xPush.getClientId(pContext);
		String xClass = CSS.PUSH.MAIN;
		xWriter.startElement("span", xPush);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			if (xPush.getShowStatus()){
				xWriter.startElement("span", xPush);
					DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
				xWriter.endElement("span");
			}
		xWriter.endElement("span");
		DBSFaces.encodeJavaScriptTagStart(xWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xPushId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
				     " dbs_push(xPushId,'" + xPush.getUrl() + "'); \n" +
                     "}); \n"; 
		xWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(xWriter);				
	}
}
