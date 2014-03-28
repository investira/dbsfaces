package br.com.dbsoft.ui.component.menuitem;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.img.DBSImg;
import br.com.dbsoft.ui.component.menu.DBSMenu;
import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;
import br.com.dbsoft.ui.core.DBSFaces;

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
		String xClass = DBSFaces.CSS.NOT_SELECTABLE;
		String xOnClick;
		String xExecute = "";
		if (xMenuitem.getExecute() == null){
			xExecute = getFormId(pContext, pComponent); 
		}else{
			xExecute = xMenuitem.getExecute();
		}		
		
		xOnClick = DBSFaces.getSubmitString(xMenuitem, DBSFaces.HTML.EVENTS.ONCLICK, xExecute, xMenuitem.getUpdate());

		if (xMenuitem.getStyleClass()!=null){
			xClass += xMenuitem.getStyleClass(); 
		}
		
		if (xMenuitem.getActionExpression() == null){
			xClass += DBSFaces.CSS.MODIFIER.DISABLED;
		}
		
		UIComponent xParent = pComponent.getParent();
		if (xParent.getAttributes().get("class") != DBSMenu.class){
			xClass += " " + DBSFaces.CSS.MENUITEM.MAIN; 
		}
		if (xMenuitem.getReadOnly()){
			xClass += " " + DBSFaces.CSS.MODIFIER.READONLY;
		}
		
		xWriter.startElement("li", xMenuitem);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);

			if (xMenuitem.getReadOnly()){
				xWriter.startElement("span", xMenuitem);
			}else{
				xWriter.startElement("a", xMenuitem);
			}	
				writeIdAttribute(xWriter, xMenuitem, xClientId);
				DBSFaces.setAttribute(xWriter, "style", xMenuitem.getStyle(), null);
				if (!xMenuitem.getReadOnly()){
					if (xMenuitem.getActionExpression() != null){
						xWriter.writeAttribute("type", "submit", "type"); 
//						xWriter.writeAttribute("ontouchstart", "", "ontouchstart"); //Para ipad ativar o css:ACTIVE
						DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick, null);
//						xWriter.writeAttribute("href", "#", "href");

//				        Collection<ClientBehaviorContext.Parameter> xParams = getBehaviorParameters(xMenuitem);
//				        RenderKitUtils.renderOnclick(pContext, 
//				        							 xMenuitem,
//				                                     xParams,
//				                                     "",
//				                                     true);
					}
				}
				//encodeClientBehaviors(pContext, xMenuitem);
				if (xMenuitem.getIconClass()!=null){
					DBSImg xImg = new DBSImg();
						xImg.setStyleClass(xMenuitem.getIconClass() + " " + DBSFaces.CSS.MODIFIER.ICON);
					xImg.encodeAll(pContext);
				}
				//Encode Behavior
				
				//Encode Label
				if (xMenuitem.getLabel()!=null){
					xWriter.startElement("span", xMenuitem);
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.LABEL + " " + DBSFaces.CSS.INPUT.LABEL, "class");
						xWriter.write(xMenuitem.getLabel());
					xWriter.endElement("span");
				}
			if (xMenuitem.getReadOnly()){
				xWriter.endElement("span");
			}else{
				xWriter.endElement("a");
			}
		xWriter.endElement("li");
	}

}
