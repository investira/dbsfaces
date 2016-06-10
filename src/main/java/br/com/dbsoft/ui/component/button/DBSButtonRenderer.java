package br.com.dbsoft.ui.component.button;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

import com.sun.faces.renderkit.RenderKitUtils;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSButton.RENDERER_TYPE)
public class DBSButtonRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
        DBSButton 	xButton = (DBSButton) pComponent;
		String 		xClientId = xButton.getClientId(); //xButton.getClientId(pContext);
        if(xButton.getReadOnly()) {return;}
        
//        if(xButton.isDisabled() || xButton.isReadonly()) {
//            return;
//        }
        
        decodeBehaviors(pContext, xButton);
        
//        String xSourceId = DBSString.toString(pContext.getExternalContext().getRequestParameterMap().get(DBSFaces.PARTIAL_SOURCE_PARAM),"");

		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, xClientId) || /*Chamada Ajax*/
			pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)) { 	/*Chamada Sem Ajax*/
			xButton.queueEvent(new ActionEvent(xButton));
		}
//        if(xButton.isDisabled()) {
//            return;
//        }

		/*
		ExternalContext external = context.getExternalContext();
           Map<String, String> params = external.getRequestParameterMap();
           String behaviorEvent = params.get("javax.faces.behavior.event");
   
           if (behaviorEvent != null) {
               List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);
   
               if (behaviors.size() > 0) {
                   String behaviorSource = params.get("javax.faces.source");
                  String clientId = getClientId(context);
                  if (behaviorSource != null && behaviorSource.equals(clientId)) {
                      for (ClientBehavior behavior: behaviorsForEvent) {
                          behavior.decode(context, this);
                      }
                  }
               }
           }	
		*/
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
		DBSButton xButton = (DBSButton) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xButton.getClientId(pContext);
		String xClass = CSS.BUTTON.MAIN + CSS.THEME.ACTION;
		String xOnClick = null;
		String xExecute = "";
		if (xButton.getExecute() == null){
			xExecute = getFormId(pContext, pComponent); 
		}else{
			xExecute = xButton.getExecute();
		}
		if (!DBSObject.isEmpty(xButton.getStyleClass())){
			xClass += xButton.getStyleClass();
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
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xButton.getStyle());
			DBSFaces.setAttribute(xWriter, "value", xButton.getValue());
			if (xButton.getDisabled()
			 || xButton.getReadOnly()){
				DBSFaces.setAttribute(xWriter, "disabled", "disabled");
			}
			
			if (!xButton.getReadOnly()){
//Comentado para sempre considerar o onclick criado acima. Forçando uma chamada ajax, desde que tenha sido informado o update.				
//				if (xButton.getActionExpression() != null || 
//					xButton.getonclick() != null){				
//					xWriter.writeAttribute("ontouchstart", "", "ontouchstart"); //Para ipad ativar o css:ACTIVE
					if (xButton.getActionExpression() != null){
						DBSFaces.setAttribute(xWriter, "type", "submit", null);
					}else{
						DBSFaces.setAttribute(xWriter, "type", "button", null);
					}
					if (xButton.getClientBehaviors().isEmpty()){
						DBSFaces.setAttribute(xWriter, DBSFaces.HTML.EVENTS.ONCLICK, xOnClick, null); 
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
			pvEncodeJS(xWriter, xClientId);
//		}
	}
	
	private void pvEncodeTable(DBSButton pButton, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("table", pButton);
//			if (pButton.getReadOnly()){
//				pWriter.writeAttribute("class", CSS.MODIFIER.CENTRALIZED_REL, null);
//			}
			DBSFaces.setAttribute(pWriter, "cellspacing", "0px");
			DBSFaces.setAttribute(pWriter, "cellpadding", "0px");
			pWriter.startElement("tbody", pButton);
				pWriter.startElement("tr", pButton);
					if (pButton.getIconClass()!=null && pButton.getIconClass()!="") {
						pWriter.startElement("td", pButton);
							DBSFaces.setAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
							pvEncodeIcon(pButton, pWriter);
						pWriter.endElement("td");
					}
					if (pButton.getLabel()!=null && pButton.getLabel()!="") {
						pWriter.startElement("td", pButton);
							DBSFaces.setAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
							pvEncodeLabel(pButton, pWriter);
						pWriter.endElement("td");
					}
				pWriter.endElement("tr");
			pWriter.endElement("tbody");
		pWriter.endElement("table");
	}	

	private void pvEncodeIcon(DBSButton pButton, ResponseWriter pWriter) throws IOException{
		String xClass = CSS.NOT_SELECTABLE + CSS.MODIFIER.ICON + pButton.getIconClass();
		pWriter.startElement("div", pButton);
			DBSFaces.setAttribute(pWriter, "class", xClass);
		pWriter.endElement("div");
	}

	private void pvEncodeLabel(DBSButton pButton, ResponseWriter pWriter) throws IOException{
		String xClass = CSS.NOT_SELECTABLE + CSS.MODIFIER.LABEL;
		pWriter.startElement("div", pButton);
			DBSFaces.setAttribute(pWriter, "class", xClass);
			//Adiciona espaço extra entre o icone e o texto
			if (pButton.getIconClass()!=null){ 
				DBSFaces.setAttribute(pWriter, "style", "padding-left:2px;"); 
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
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xButtonId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_button(xButtonId); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}

}


