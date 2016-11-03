package br.com.dbsoft.ui.component.menu;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.menu.DBSMenu.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSMenu.RENDERER_TYPE)
public class DBSMenuRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSMenu xMenu = (DBSMenu) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xMenu.getClientId(pContext);
		TYPE xType = TYPE.get(xMenu.getType());
		String xClass = CSS.MENU.MAIN; 

		xClass += TYPE.get(xMenu.getType()).getStyleClass();
		if (xMenu.getStyleClass()!=null){
			xClass += xMenu.getStyleClass();
		}
		
		xWriter.startElement("div", xMenu);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "style", xMenu.getStyle());
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "type", xType.getCode());
			xWriter.startElement("ul", xMenu);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.renderChildren(pContext, xMenu);
			xWriter.endElement("ul");
			pvEncodeJS(xMenu, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xMenuId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_menu(xMenuId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}
