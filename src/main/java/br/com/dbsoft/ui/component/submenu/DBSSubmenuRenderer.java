package br.com.dbsoft.ui.component.submenu;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.menu.DBSMenu;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSubmenu.RENDERER_TYPE)
public class DBSSubmenuRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}
	
	@Override 
	public boolean getRendersChildren() {
		return true;//True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
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
		DBSSubmenu xSubmenu = (DBSSubmenu) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xSubmenu.getClientId(pContext);
		Boolean xIsSubmenu = true;
		Boolean xIsRoot = false;

		String xClass = DBSFaces.CSS.NOT_SELECTABLE;
		String xClassMenuitem = DBSFaces.CSS.NOT_SELECTABLE;
		
		if (xSubmenu.getStyleClass()!=null){
			xClass += xSubmenu.getStyleClass(); 
		}
		
		//Ignora o style padrão caso o pai/avô seja um DBSMenu
		UIComponent xParent = pComponent.getParent();
		xIsSubmenu = (xParent.getAttributes().get("class") != DBSMenu.class);
		if (xIsSubmenu){
			xClass += DBSFaces.CSS.SUBMENU.MAIN; 
			xClassMenuitem += DBSFaces.CSS.MENUITEM.MAIN; 
			xIsRoot = (xParent.getAttributes().get("class") != DBSSubmenu.class);
		}		

		if (!xIsRoot){
			xWriter.startElement("li", xSubmenu);
				DBSFaces.setAttribute(xWriter, "class", xClassMenuitem, null);
				DBSFaces.setAttribute(xWriter, "style", xSubmenu.getStyle(), null);
				xWriter.startElement("a", xSubmenu);
	//				DBSFaces.setAttribute(xWriter, xSubmenu, "href", "javascript:void(0)", null);
					DBSFaces.setAttribute(xWriter, "ontouchstart", "", null);
					xWriter.startElement("span", xSubmenu);
					xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT, "class");
						if (xSubmenu.getIconClass()!=null){
							xWriter.startElement("span", xSubmenu);
								xWriter.writeAttribute("class", xSubmenu.getIconClass() + " " + DBSFaces.CSS.MODIFIER.ICON, "class");
							xWriter.endElement("span");
						}
						if (xSubmenu.getLabel()!=null){
							xWriter.startElement("span", xSubmenu);
								xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.LABEL + " " + DBSFaces.CSS.INPUT.LABEL, "class");
								xWriter.write(xSubmenu.getLabel());
							xWriter.endElement("span");
						}
						if (xIsSubmenu && !xIsRoot){
							xWriter.startElement("span", xSubmenu);
								xWriter.writeAttribute("class", DBSFaces.CSS.ICONSMALL + " " + DBSFaces.CSS.MENUITEM.PLUS, "class");
							xWriter.endElement("span");
						}
					xWriter.endElement("span");
				xWriter.endElement("a");
		}

		xWriter.startElement("ul", xSubmenu);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			renderChildren(pContext, xSubmenu);
		xWriter.endElement("ul");
		
		if (!xIsRoot){
			xWriter.endElement("li");
		}
	}
}
