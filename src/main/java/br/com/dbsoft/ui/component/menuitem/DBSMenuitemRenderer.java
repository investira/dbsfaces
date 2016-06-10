package br.com.dbsoft.ui.component.menuitem;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.menu.DBSMenu;
import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSMenuitem.RENDERER_TYPE)
public class DBSMenuitemRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSMenuitem xMenuitem = (DBSMenuitem) pComponent;
		String xClientId = xMenuitem.getClientId(pContext);
		//System.out.println("DECODE Menuitem");
		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, xClientId) || /*Chamada Ajax*/
			pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)) { /*Chamada Sem Ajax*/ 
			//System.out.println("DECODE Menuitem achou");	
			xMenuitem.queueEvent(new ActionEvent(xMenuitem));
			decodeBehaviors(pContext, pComponent);
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSMenuitem xMenuitem = (DBSMenuitem) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xMenuitem.getClientId(pContext);
		Boolean xIsSubmenu = true;
		String xOnClick;
		String xExecute;
		String xClass = CSS.MENUITEM.MAIN + CSS.NOT_SELECTABLE;

		//Verifica se é um submenu filho de um outro submenu ou filho do menu principal
		UIComponent xParent = pComponent.getParent();
		xIsSubmenu = (xParent.getAttributes().get("class") != DBSMenu.class);
		
		//Execute--------
		if (xMenuitem.getExecute() == null){
			xExecute = getFormId(pContext, pComponent); 
		}else{
			xExecute = xMenuitem.getExecute();
		}		
		
		xOnClick = DBSFaces.getSubmitString(xMenuitem, DBSFaces.HTML.EVENTS.ONCLICK, xExecute, xMenuitem.getUpdate());

		//Class----------
		if (xIsSubmenu){
			xClass += CSS.THEME.ACTION;
		}
		if (xMenuitem.getStyleClass()!=null){
			xClass += xMenuitem.getStyleClass();
		}
		if (xMenuitem.getChildCount() == 0){
			if (xMenuitem.getActionExpression() == null){
				xClass += CSS.MODIFIER.DISABLED;
			}
		}
		
		//Encode --------
		xWriter.startElement("li", xMenuitem);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xMenuitem.getStyle());
			xWriter.startElement("a", xMenuitem);
				writeIdAttribute(xWriter, xMenuitem, xClientId);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.setAttribute(xWriter, "ontouchstart", "", null);
				if (!xMenuitem.getReadOnly()){
					if (xMenuitem.getActionExpression() != null){
						DBSFaces.setAttribute(xWriter, "type", "submit"); 
						DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick);
					}
				}
				encodeMenuLine(xMenuitem, xWriter, xMenuitem.getLabel(), xMenuitem.getIconClass(), xIsSubmenu & xMenuitem.getChildCount() > 0);
				
			xWriter.endElement("a");
			if (xMenuitem.getChildCount() > 0){
				xWriter.startElement("ul", xMenuitem);
					DBSFaces.renderChildren(pContext, xMenuitem);
				xWriter.endElement("ul");
			}
		xWriter.endElement("li");
	}
	
	public void encodeMenuLine(UIComponent pComponent, ResponseWriter pWriter, String pLabel, String pIconClass, Boolean pHasChildren) throws IOException{
		if (pIconClass!=null){
			pWriter.startElement("span", pComponent);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + pIconClass);
			pWriter.endElement("span");
		}
		if (pLabel!=null){
			pWriter.startElement("span", pComponent);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LABEL + CSS.INPUT.LABEL);
				pWriter.write(pLabel);
			pWriter.endElement("span");
		}
		if (pHasChildren!=null && pHasChildren){
			pWriter.startElement("span", pComponent);
				DBSFaces.setAttribute(pWriter, "class",  "-i_add");
			pWriter.endElement("span");
		}
		
	}

}
