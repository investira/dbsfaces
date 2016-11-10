package br.com.dbsoft.ui.component.button;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSUICommandRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSButton.RENDERER_TYPE)
public class DBSButtonRenderer extends DBSUICommandRenderer {
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSButton xButton = (DBSButton) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xButton.getClientId(pContext);
		String xClass = CSS.BUTTON.MAIN + CSS.THEME.ACTION + getBasicStyleClass(xButton);
		String xOnClick = null;
		String xExecute = "";
		if (xButton.getExecute() == null){
			xExecute = getFormId(pContext, pComponent); 
		}else{
			xExecute = xButton.getExecute();
		}
		if (xButton.getReadOnly()){
			xClass += CSS.MODIFIER.DISABLED;
		}
		
		//if (xButton.getUpdate()!=null){
			xOnClick = DBSFaces.getSubmitString(xButton, DBSFaces.HTML.EVENTS.ONCLICK, xExecute, xButton.getUpdate());
		//}
		
		if (xButton.getReadOnly()){
			xWriter.startElement("div", xButton);
		}else{
			xWriter.startElement("button", xButton);
		}
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xButton.getStyle());
			DBSFaces.encodeAttribute(xWriter, "value", xButton.getValue());
			DBSFaces.encodeAttribute(xWriter, "asid", xButton.getActionSourceClientId());
			if (xButton.getDisabled()
			 || xButton.getReadOnly()){
				DBSFaces.encodeAttribute(xWriter, "disabled", "disabled");
			}
			
			if (!xButton.getReadOnly()){
//Comentado para sempre considerar o onclick criado acima. Forçando uma chamada ajax, desde que tenha sido informado o update.				
//				if (xButton.getActionExpression() != null || 
//					xButton.getonclick() != null){				
//					xWriter.writeAttribute("ontouchstart", "", "ontouchstart"); //Para ipad ativar o css:ACTIVE
					if (xButton.getActionExpression() != null){
						DBSFaces.encodeAttribute(xWriter, "type", "submit");
					}else{
						DBSFaces.encodeAttribute(xWriter, "type", "button");
					}
					if (xButton.getClientBehaviors().isEmpty()){
						DBSFaces.encodeAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick); 
					}else{
						encodeClientBehaviors(pContext, xButton);
					}
					//if (xButton.getUpdate()!=null){
					//}
//				}			
			}
			if (xButton.getIconClass()!=null 
			 || xButton.getLabel()!=null){
				pvEncodeTable(xButton, xWriter);	
			}
			
			DBSFaces.renderChildren(pContext, xButton);
			if (!xButton.getReadOnly()){
				DBSFaces.encodeTooltip(pContext, xButton, xButton.getTooltip());
			}
			if (xButton.getReadOnly()){
				xWriter.endElement("div");
			}else{
				xWriter.endElement("button");
			}
//		if (!xButton.getReadOnly()){
			pvEncodeJS(xButton, xWriter);
//		}
	}
	
	private void pvEncodeTable(DBSButton pButton, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("table", pButton);
//			if (pButton.getReadOnly()){
//				pWriter.writeAttribute("class", CSS.MODIFIER.CENTRALIZED_REL, null);
//			}
			DBSFaces.encodeAttribute(pWriter, "cellspacing", "0px");
			DBSFaces.encodeAttribute(pWriter, "cellpadding", "0px");
			pWriter.startElement("tbody", pButton);
				pWriter.startElement("tr", pButton);
					if (pButton.getIconClass()!=null && pButton.getIconClass()!="") {
						pWriter.startElement("td", pButton);
							pvEncodeIcon(pButton, pWriter);
						pWriter.endElement("td");
					}
					if (pButton.getLabel()!=null && pButton.getLabel()!="") {
						pWriter.startElement("td", pButton);
							pvEncodeLabel(pButton, pWriter);
						pWriter.endElement("td");
					}
				pWriter.endElement("tr");
			pWriter.endElement("tbody");
		pWriter.endElement("table");
	}	

	private void pvEncodeIcon(DBSButton pButton, ResponseWriter pWriter) throws IOException{
		String xClass = CSS.MODIFIER.ICON + pButton.getIconClass();
		pWriter.startElement("div", pButton);
			DBSFaces.encodeAttribute(pWriter, "class", xClass);
		pWriter.endElement("div");
	}

	private void pvEncodeLabel(DBSButton pButton, ResponseWriter pWriter) throws IOException{
		String xClass = CSS.MODIFIER.LABEL;
		pWriter.startElement("div", pButton);
			DBSFaces.encodeAttribute(pWriter, "class", xClass);
			//Adiciona espaço extra entre o icone e o texto
			if (pButton.getIconClass()!=null){ 
				DBSFaces.encodeAttribute(pWriter, "style", "padding-left:2px;"); 
			}
			pWriter.write(pButton.getLabel());
		pWriter.endElement("div");
	}	
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xButtonId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_button(xButtonId); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}

}


