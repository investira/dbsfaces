package br.com.dbsoft.ui.component.floatbutton;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;

/**
 * Renderer do DBSFloatButton
 * @author jose.avila@dbsoft.com.br
 *
 */
@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSFloatButton.RENDERER_TYPE)
public class DBSFloatButtonRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
        DBSFloatButton 	xFloatButton = (DBSFloatButton) pComponent;
		String 			xClientId = xFloatButton.getClientId();
        
        decodeBehaviors(pContext, xFloatButton);
        
		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, xClientId) || /*Chamada Ajax*/
			pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)) { 	/*Chamada Sem Ajax*/
			xFloatButton.queueEvent(new ActionEvent(xFloatButton));
		}
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSFloatButton xFloatButton = (DBSFloatButton) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xFloatButton.getClientId(pContext);
		String xClass = DBSFaces.CSS.FLOATBUTTON.MAIN;
		xClass += " dbs_floatbutton_location"+ xFloatButton.getDefaultLocation();
		
		if (!DBSObject.isEmpty(xFloatButton.getStyleClass())){
			xClass += DBSObject.getNotEmpty(xFloatButton.getStyleClass(), "");
		}
		
		//DIV PRINCIPAL
		xWriter.startElement("div", xFloatButton);
		xWriter.writeAttribute("id", xClientId, "id");
		xWriter.writeAttribute("name", xClientId, "name");
		DBSFaces.setAttribute(xWriter, "class", xClass, null);
		DBSFaces.setAttribute(xWriter, "style",xFloatButton.getStyle(), null);
		
		//BOTÃO FLUTUANTE PRINCIPAL
		DBSButton xBotaoPrincipal = new DBSButton();
		xBotaoPrincipal.setIconClass(xFloatButton.getIconClass());
		xBotaoPrincipal.setTooltip(xFloatButton.getTooltip());
		xBotaoPrincipal.setonclick("dbsOpenCloseFloatMenu()");
		xBotaoPrincipal.encodeAll(pContext);
		
		//MENU FLUTUANTE
		xWriter.startElement("div", xFloatButton);
		xWriter.writeAttribute("id", "floatMenu", "id");
		DBSFaces.setAttribute(xWriter, "class", "dbs_menu dbs_not_selectable dbs_floatMenu_location"+xFloatButton.getDefaultLocation(), null);
			xWriter.startElement("ul", xFloatButton);
			if (xFloatButton.getHorizontal()) {
				//Menu na Horizontal
				DBSFaces.setAttribute(xWriter, "class", "-content dbs_floatMenu_horizontal dbs_floatMenu_horizontal_location"+xFloatButton.getDefaultLocation(), null); 
			} else {
				//Menu na Vertical
				DBSFaces.setAttribute(xWriter, "class", "-content dbs_floatMenu_vertical", null);
			}
				//MENUITEM FILHOS DO FLOATBUTTON
				for (UIComponent xMenuItem:pComponent.getChildren()) {
					if (xMenuItem instanceof DBSMenuitem) {
						((DBSMenuitem) xMenuItem).setStyleClass(" dbs_floatmenuitem");
						xMenuItem.encodeAll(pContext);
					}
				}
			xWriter.endElement("ul");
		xWriter.endElement("div");
		
		DBSFaces.encodeTooltip(pContext, xFloatButton, xFloatButton.getTooltip());
		xWriter.endElement("div");
		pvEncodeJS(xWriter, xClientId);
	}
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = 
		" function dbsOpenCloseFloatMenu() { "+
			" var xButtons = document.getElementsByClassName('dbs_floatmenuitem'); "+
			" if (document.getElementById('floatMenu').style.visibility == 'hidden' "+
			 " || document.getElementById('floatMenu').style.visibility == '') { "+
			 	" document.getElementById('floatMenu').style.visibility = 'visible'; "+
				" for(var i = 0; i < xButtons.length; ++i){ "+
					" xButtons[i].style.opacity = '1'; "+
					" xButtons[i].style.transform = 'scale(1,1)'; "+
				" } "+
			" } else { "+
				" document.getElementById('floatMenu').style.visibility = 'hidden'; "+
				" for(var i = 0; i < xButtons.length; ++i){ "+
					" xButtons[i].style.opacity = '0'; "+
					" xButtons[i].style.transform = 'scale(0,0)'; "+
				" } "+
			" } "+
		" }";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}

}
