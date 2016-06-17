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
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}
	
	@Override //True=Informa que este componente chamará o render dos filhos
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
		DBSMenu xMenu = (DBSMenu) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xMenu.getClientId(pContext);
		TYPE xType = TYPE.get(xMenu.getType());
		String xClass = CSS.MENU.MAIN; 

		if (xMenu.getStyleClass()!=null){
			xClass += xMenu.getStyleClass();
		}
		xWriter.startElement("div", xMenu);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "style", xMenu.getStyle());
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "type", xType.getCode());
			xWriter.startElement("ul", xMenu);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.renderChildren(pContext, xMenu);
			xWriter.endElement("ul");
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xMenuId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_menu(xMenuId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}
