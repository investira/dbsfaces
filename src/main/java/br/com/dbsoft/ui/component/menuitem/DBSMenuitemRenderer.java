package br.com.dbsoft.ui.component.menuitem;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.command.DBSUICommandRenderer;
import br.com.dbsoft.ui.component.menu.DBSMenu;
import br.com.dbsoft.ui.component.menu.DBSMenu.TYPE;
import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.ui.core.DBSFaces.HTML;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSMenuitem.RENDERER_TYPE)
public class DBSMenuitemRenderer extends DBSUICommandRenderer {

	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSMenuitem xMenuitem = (DBSMenuitem) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xMenuitem.getClientId(pContext);
		Boolean xIsSubmenu = true;
		String xOnClick;
		String xExecute = null;
		String xClass = CSS.MENUITEM.MAIN + getBasicStyleClass(xMenuitem);

		//Verifica se é um submenu filho de um outro submenu ou filho do menu principal
		UIComponent xParent = pComponent.getParent();
		if (xParent.getAttributes().get("class") != DBSMenu.class ||
			xParent.getAttributes().get("type") != TYPE.SCROLL.getCode()){
			xIsSubmenu = true;
		}
//		xIsSubmenu = (xParent.getAttributes().get("class") != DBSMenu.class);
		
		//Execute--------
		if (xMenuitem.getActionExpression() != null){
			if (xMenuitem.getExecute() == null){
				xExecute = getFormId(pContext, pComponent); 
			}else{
				xExecute = xMenuitem.getExecute();
			}		
		}
		
		xOnClick = DBSFaces.getSubmitString(xMenuitem, DBSFaces.HTML.EVENTS.ONCLICK, xExecute, xMenuitem.getUpdate());

		//Class----------
		if (xIsSubmenu){
			xClass += CSS.THEME.ACTION;
		}

		if (xMenuitem.getChildCount() == 0){
			if (xMenuitem.getActionExpression() == null
			 && DBSObject.isEmpty(xOnClick)){
				xClass += CSS.MODIFIER.DISABLED;
			}
		}

		//Encode --------
		xWriter.startElement("li", xMenuitem);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xMenuitem.getStyle());
//			DBSFaces.encodeAttribute(xWriter, "asid", xMenuitem.getActionSourceClientId());
			xWriter.startElement("a", xMenuitem);
				writeIdAttribute(xWriter, xMenuitem, xClientId);
				xClass = CSS.MODIFIER.CAPTION;
				DBSFaces.encodeAttribute(xWriter, "class", xClass);
				DBSFaces.encodeAttribute(xWriter, "ontouchstart", "");
				if (!xMenuitem.getReadOnly()){
					if (xMenuitem.getActionExpression() != null
				  	 || !DBSObject.isEmpty(xOnClick)){
						DBSFaces.encodeAttribute(xWriter, "type", "submit"); 
						DBSFaces.encodeAttribute(xWriter, HTML.EVENTS.ONCLICK, xOnClick);
					}
				}
				pvEncodeMenuLine(xMenuitem, xWriter, xMenuitem.getLabel(), xMenuitem.getIconClass(), xMenuitem.getChildCount() > 0);
//				pvEncodeMenuLine(xMenuitem, xWriter, xMenuitem.getLabel(), xMenuitem.getIconClass(), xIsSubmenu & xMenuitem.getChildCount() > 0);
				
			xWriter.endElement("a");
			if (xMenuitem.getChildCount() > 0){
				xWriter.startElement("div", xMenuitem);
					DBSFaces.encodeAttribute(xWriter, "class", "-submenu");
					xWriter.startElement("div", xMenuitem);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
						xWriter.startElement("ul", xMenuitem);
							DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
							DBSFaces.renderChildren(pContext, xMenuitem);
						xWriter.endElement("ul");
					xWriter.endElement("div");
				xWriter.endElement("div");
			}
		xWriter.endElement("li");
	}
	
	public void pvEncodeMenuLine(DBSMenuitem pMenuItem, ResponseWriter pWriter, String pLabel, String pIconClass, Boolean pHasChildren) throws IOException{
		pWriter.startElement("div", pMenuItem);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			if (pIconClass!=null){
				pWriter.startElement("span", pMenuItem);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON + pIconClass);
				pWriter.endElement("span");
			}
			if (pLabel!=null){
				pWriter.startElement("span", pMenuItem);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL + CSS.THEME.INPUT_LABEL);
					pWriter.write(pLabel);
				pWriter.endElement("span");
			}
			if (pHasChildren!=null && pHasChildren){
				pWriter.startElement("span", pMenuItem);
				DBSFaces.encodeAttribute(pWriter, "class", "-childrenIcon"); //Marcação closed="-c", opened="-o". Setado via JS. 
				pWriter.endElement("span");
			}
		pWriter.endElement("div");
	}

}
