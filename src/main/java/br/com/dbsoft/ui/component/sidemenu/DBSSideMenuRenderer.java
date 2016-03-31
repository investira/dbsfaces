package br.com.dbsoft.ui.component.sidemenu;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSideMenu.RENDERER_TYPE)
public class DBSSideMenuRenderer extends DBSRenderer {

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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSSideMenu xMenu = (DBSSideMenu) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xMenu.getClientId(pContext);
		String xClass = DBSFaces.CSS.SIDEMENU.MAIN + " " + DBSFaces.CSS.BACK_TEXTURE_BLACK_GRADIENT.trim();

		if (xMenu.getStyleClass()!=null){
			xClass += DBSObject.getNotEmpty(xMenu.getStyleClass(), "");
		}
		//BOTAO PRINCIPAL
		DBSButton xBotaoPrincipal = new DBSButton();
		xBotaoPrincipal.setIconClass(xMenu.getIconClass());
		xBotaoPrincipal.setTooltip(xMenu.getTooltip());
		xBotaoPrincipal.setonclick("dbsOpenSideMenu()");
		xBotaoPrincipal.encodeAll(pContext);
		
		//HIDDEN NAV
		xWriter.startElement("div", xMenu);
		xWriter.writeAttribute("id", xClientId+"_hiddennav", "id");
		DBSFaces.setAttribute(xWriter, "class", "dbs_hiddennav", null);
		DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, "dbsCloseSideMenu()", null);
		xWriter.endElement("div");
		
		//SIDEMENU
		xWriter.startElement("div", xMenu);
		xWriter.writeAttribute("id", xClientId, "id");
		DBSFaces.setAttribute(xWriter, "class", xClass, null);
			
			//ICONE CENTRAL
			if(!DBSObject.isEmpty(xMenu.getIconCentral())) {
				xWriter.startElement("span", xMenu);
				DBSFaces.setAttribute(xWriter, "class", "-iconCentral "+xMenu.getIconCentral(), null);
				xWriter.endElement("span");
			}
		
			//BOTAO FECHAR
			DBSButton xBotaoFechar = new DBSButton();
			xBotaoFechar.setId(xClientId+"_closeSideMenu");
			xBotaoFechar.setIconClass("-i_cancel");
			xBotaoFechar.setonclick("dbsCloseSideMenu()");
			xBotaoFechar.encodeAll(pContext);
			
			//MENU
			xWriter.startElement("div", xMenu);
			DBSFaces.setAttribute(xWriter, "class", "dbs_menu dbs_not_selectable ", null);
				xWriter.startElement("ul", xMenu);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
					//MENUITEM FILHOS DO SIDEMENU
					for (UIComponent xMenuItem:pComponent.getChildren()) {
						if (xMenuItem instanceof DBSMenuitem) {
							xMenuItem.encodeAll(pContext);
						}
					}
				xWriter.endElement("ul");
			xWriter.endElement("div");
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = 
		" function dbsOpenSideMenu() { "+
			" document.getElementById('"+ pClientId +"').style.width = '250px'; "+
			" document.getElementById('"+ pClientId+"_hiddennav').style.width = '100vw'; "+
			" document.getElementById('"+ pClientId+"_hiddennav').style.opacity = '0.48'; "+
			" var xMenuItens = document.getElementById('"+ pClientId +"').getElementsByClassName('dbs_menuitem'); "+
		    " for(var i = 0; i < xMenuItens.length; ++i){ "+
		    	" xMenuItens[i].style.opacity = '1'; "+
		    " } "+
		" } "+
		
		" function dbsCloseSideMenu() { "+
			" document.getElementById('"+ pClientId +"').style.width = '0'; "+
			" document.getElementById('"+ pClientId+"_hiddennav').style.width = '0'; "+
			" document.getElementById('"+ pClientId+"_hiddennav').style.opacity = '0'; "+
			" var xMenuItens = document.getElementById('"+ pClientId +"').getElementsByClassName('dbs_menuitem'); "+
		    " for(var i = 0; i < xMenuItens.length; ++i){ "+
		    	" xMenuItens[i].style.opacity = '0.1'; "+
		    " } "+
		" }"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}
