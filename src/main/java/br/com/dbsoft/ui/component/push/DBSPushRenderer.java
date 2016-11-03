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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSPush xPush = (DBSPush) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xPush.getClientId(pContext);
		String xClass = CSS.PUSH.MAIN;
		xWriter.startElement("span", xPush);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			if (xPush.getShowStatus()){
				xWriter.startElement("span", xPush);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
				xWriter.endElement("span");
			}
		xWriter.endElement("span");
		DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xPushId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
				     " dbs_push(xPushId,'" + xPush.getUrl() + "'); \n" +
                     "}); \n"; 
		xWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(xWriter);				
	}
}
