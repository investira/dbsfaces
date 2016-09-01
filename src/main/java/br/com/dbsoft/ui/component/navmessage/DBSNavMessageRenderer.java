package br.com.dbsoft.ui.component.navmessage;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.nav.DBSNav;
import br.com.dbsoft.ui.component.nav.DBSNav.LOCATION;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSNavMessage.RENDERER_TYPE)
public class DBSNavMessageRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSNavMessage 	xNavMessage = (DBSNavMessage) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		LOCATION 		xLocation = LOCATION.get(xNavMessage.getLocation(), xNavMessage.getContentVerticalAlign(), xNavMessage.getContentHorizontalAlign());
		String 			xClass = CSS.NAVMESSAGE.MAIN + CSS.THEME.FC + xLocation.getCSS();
		if (!xNavMessage.getOpened()){
			xClass += CSS.MODIFIER.CLOSED; //Indica que esta fechado
		}
		if (xNavMessage.getStyleClass()!=null){
			xClass += xNavMessage.getStyleClass();
		}
		
		String xClientId = xNavMessage.getClientId(pContext);
		xWriter.startElement("div", xNavMessage);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xNavMessage.getStyle());
			//ENCODE DO NAV
			DBSNav xNav = new DBSNav();
			xNav.setContentHorizontalAlign(xNavMessage.getContentHorizontalAlign());
			xNav.setContentVerticalAlign(xNavMessage.getContentVerticalAlign());
			xNav.setContentStyleClass(xNavMessage.getContentStyleClass());
			xNav.setCloseTimeout(xNavMessage.getCloseTimeout());
			xNav.setLocation(xNavMessage.getLocation());
//			xNav.setId(xClientId+":msgPanel");
			xNav.encodeBegin(pContext);
				pvEncodeMessage(xNavMessage, xWriter);
			xNav.encodeEnd(pContext);
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("div");
	}

	private void pvEncodeMessage(DBSNavMessage pNavMessage, ResponseWriter pWriter) throws IOException {
		if (!DBSObject.isEmpty(pNavMessage.getMessage())) {
			pWriter.startElement("span", pNavMessage);
				pWriter.write(pNavMessage.getMessage());
			pWriter.endElement("span");
		}
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xNavMessageId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_navMessage(xNavMessageId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

}
