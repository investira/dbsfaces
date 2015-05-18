package br.com.dbsoft.ui.component.link;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSLink.RENDERER_TYPE)
public class DBSLinkRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
        DBSLink xLink = (DBSLink) pComponent;
		String xClientId = xLink.getClientId(pContext);
		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, xClientId) || /*Chamada Ajax*/
			pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)) { 	/*Chamada Sem Ajax*/
			xLink.queueEvent(new ActionEvent(xLink));
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
	public void encodeEnd(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSLink xLink = (DBSLink) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.NOT_SELECTABLE.trim() + " " + xLink.getStyleClass();
		String xOnClick;

		xOnClick = DBSFaces.getSubmitString(xLink, DBSFaces.HTML.EVENTS.ONCLICK, getFormId(pContext, pComponent), xLink.getUpdate());
		String xClientId = xLink.getClientId(pContext);
		xWriter.startElement("a", xLink);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class"); 
			DBSFaces.setAttribute(xWriter, "style", xLink.getStyle(), null);
			if (xLink.getActionExpression() != null){				
//				xWriter.writeAttribute("ontouchstart", "", "ontouchstart"); //Para ipad ativar o css:ACTIVE
				xWriter.writeAttribute("type", "submit", "type"); 
				//if (xLink.getUpdate()!=null){
					DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick, null); 
				//}
			}
			DBSFaces.renderChildren(pContext, xLink);
		xWriter.endElement("a");
	}

}
