package br.com.dbsoft.ui.component.inputphone;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSFormat;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputPhone.RENDERER_TYPE)
public class DBSInputPhoneRenderer extends DBSRenderer {
	
	private static String wWidth2 = "width:1.3em;";
	private static String wWidth3 = "width:2em;";
	private static String wWidth10 = "width:6em;";
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSInputPhone xInputPhone = (DBSInputPhone) pComponent;
        if(xInputPhone.getReadOnly()) {return;}

    	String xDDI = "";
    	String xDDD= "";
    	String xNumber = "";

    	decodeBehaviors(pContext, xInputPhone);
    	
    	
		String xClientIdAction = getInputDataClientId(xInputPhone);
		ExternalContext xEC = pContext.getExternalContext();
		if (xEC.getRequestParameterMap().containsKey(xClientIdAction + CSS.INPUTPHONE.NUMBER.trim())){
			if (xEC.getRequestParameterMap().containsKey(xClientIdAction + CSS.INPUTPHONE.DDI.trim())){
    			xDDI = xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTPHONE.DDI.trim());
    		}
			if (xEC.getRequestParameterMap().containsKey(xClientIdAction + CSS.INPUTPHONE.DDD.trim())){
    			xDDD = xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTPHONE.DDD.trim());
    		}
   			xNumber = xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTPHONE.NUMBER.trim());
   			String xValue = DBSFormat.getPhoneNumber(xDDI, xDDD, xNumber);
			if (xValue == null){
				xInputPhone.setSubmittedValue("");
			}else{
				xInputPhone.setSubmittedValue(xValue);
			}
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
		DBSInputPhone xInputPhone = (DBSInputPhone) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputPhone.getClientId(pContext);
		String xClass = CSS.INPUTPHONE.MAIN + " " + CSS.INPUT.MAIN + " ";
		if (xInputPhone.getStyleClass()!=null){
			xClass = xClass + xInputPhone.getStyleClass();
		}
		xWriter.startElement("div", xInputPhone);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xInputPhone.getStyle());
			//Container
			xWriter.startElement("div", xInputPhone);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);

				DBSFaces.encodeLabel(pContext, xInputPhone, xWriter);
				pvEncodeInput(pContext, xInputPhone, xWriter);
				DBSFaces.encodeRightLabel(pContext, xInputPhone, xWriter);
//				encodeClientBehaviors(pContext, xInputPhone);

			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xInputPhone, xInputPhone.getTooltip());
		xWriter.endElement("div");

		if (!xInputPhone.getReadOnly()){
			DBSFaces.encodeJavaScriptTagStart(xWriter);
			String xJS = "$(document).ready(function() { \n" +
					     " var xInputPhoneId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
					     " dbs_inputPhone(xInputPhoneId); \n" +
	                     "}); \n"; 
			xWriter.write(xJS);
			DBSFaces.encodeJavaScriptTagEnd(xWriter);		
		}
	}
	
	private void pvEncodeInput(FacesContext pContext, DBSInputPhone pInputPhone, ResponseWriter pWriter) throws IOException{
		String 	xClientId = getInputDataClientId(pInputPhone);
		String 	xStyle = "";
		String 	xStyleClass = "";
		String 	xValue = "";
		Integer xSize = 0;
//		if (pInputPhone.getDate() != null){
//			if ((pInputPhone.getDateMin() != null 
//			  && pInputPhone.getDate().before(pInputPhone.getDateMin()))
//			 || (pInputPhone.getDateMax() != null 
//			  && pInputPhone.getDate().after(pInputPhone.getDateMax()))){
//				xStyleClass = CSS.MODIFIER.ERROR;
//			}
//		}		
		if (pInputPhone.getReadOnly()){
			if (pInputPhone.getValue()==null){
				if (pInputPhone.getShowDDI()){
					xSize = 19;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(19);
				}else if (pInputPhone.getShowDDD()){
					xSize = 14;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(14);
				}else{
					xSize = 10;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(10);
				}
			}else{
				if (pInputPhone.getShowDDI()){
					xValue = DBSFormat.getPhoneNumber(pInputPhone.getDDI(), pInputPhone.getDDD(), pInputPhone.getNumber());
				}else if (pInputPhone.getShowDDD()){
					xValue = DBSFormat.getPhoneNumber(null, pInputPhone.getDDD(), pInputPhone.getNumber());
				}else{
					xValue = DBSFormat.getPhoneNumber(null, null, pInputPhone.getNumber());
				}
				xSize = xValue.length();
			}
			
			DBSFaces.encodeInputDataReadOnly(pInputPhone, pWriter, xClientId, false, xValue, xSize, null, xStyle);
		}else{
			pWriter.startElement("span", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputPhone) + xStyleClass);
				//Define a largura do campo
				DBSFaces.setSizeAttributes(pWriter, xSize, null);
				pvEncodeInputPhone(pContext, pInputPhone, pWriter);
			pWriter.endElement("span");
		}
	}

	private void pvEncodeInputPhone(FacesContext pContext, DBSInputPhone pInputPhone, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputPhone);
		//Se for somente leitura, gera código como <Span>
		if (pInputPhone.getShowDDI()){
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
				DBSFaces.setAttribute(pWriter, "for", xClientId + CSS.INPUTPHONE.DDI.trim());
				pWriter.write("(");
			pWriter.endElement("label");
			pWriter.startElement("input", pInputPhone);
				DBSFaces.setAttribute(pWriter, "id", xClientId + CSS.INPUTPHONE.DDI.trim());
				DBSFaces.setAttribute(pWriter, "name", xClientId + CSS.INPUTPHONE.DDI.trim());
				DBSFaces.setAttribute(pWriter, "type", "tel");
				DBSFaces.setAttribute(pWriter, "class", CSS.INPUTPHONE.DDI);
				DBSFaces.setAttribute(pWriter, "style", wWidth3);
				DBSFaces.setAttribute(pWriter, "size", "3");
				DBSFaces.setAttribute(pWriter, "maxlength", "3");
				DBSFaces.setAttribute(pWriter, "value", pInputPhone.getDDI(), "");
				pvEncodeAutocompleteAttribute(pInputPhone, pWriter);			
				encodeClientBehaviors(pContext, pInputPhone);
			pWriter.endElement("input");
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
				DBSFaces.setAttribute(pWriter, "for", xClientId + CSS.INPUTPHONE.DDD.trim());
				pWriter.write(")");
			pWriter.endElement("label");
		}
		if (pInputPhone.getShowDDD()){
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
				DBSFaces.setAttribute(pWriter, "for", xClientId + CSS.INPUTPHONE.DDD.trim());
				pWriter.write("(");
			pWriter.endElement("label");
			pWriter.startElement("input", pInputPhone);
				DBSFaces.setAttribute(pWriter, "id", xClientId + CSS.INPUTPHONE.DDD.trim());
				DBSFaces.setAttribute(pWriter, "name", xClientId + CSS.INPUTPHONE.DDD.trim());
				DBSFaces.setAttribute(pWriter, "type", "tel");
				DBSFaces.setAttribute(pWriter, "class", CSS.INPUTPHONE.DDD);
				DBSFaces.setAttribute(pWriter, "style", wWidth2);
				DBSFaces.setAttribute(pWriter, "size", "2");
				DBSFaces.setAttribute(pWriter, "maxlength", "2");
				DBSFaces.setAttribute(pWriter, "value", pInputPhone.getDDD(), "");
				pvEncodeAutocompleteAttribute(pInputPhone, pWriter);
				encodeClientBehaviors(pContext, pInputPhone);
			pWriter.endElement("input");
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
				DBSFaces.setAttribute(pWriter, "for", xClientId + CSS.INPUTPHONE.NUMBER.trim());
				pWriter.write(")");
			pWriter.endElement("label");
		}
		pWriter.startElement("input", pInputPhone);
			DBSFaces.setAttribute(pWriter, "id", xClientId + CSS.INPUTPHONE.NUMBER.trim());
			DBSFaces.setAttribute(pWriter, "name", xClientId + CSS.INPUTPHONE.NUMBER.trim());
			DBSFaces.setAttribute(pWriter, "type", "tel");
			DBSFaces.setAttribute(pWriter, "class", CSS.INPUTPHONE.NUMBER);
			DBSFaces.setAttribute(pWriter, "style", wWidth10);
			DBSFaces.setAttribute(pWriter, "size", "9");
			DBSFaces.setAttribute(pWriter, "maxlength", "9");
			DBSFaces.setAttribute(pWriter, "value", pInputPhone.getNumber(), "");
			pvEncodeAutocompleteAttribute(pInputPhone, pWriter);
			encodeClientBehaviors(pContext, pInputPhone);
		pWriter.endElement("input");
	}
	
	
	private void pvEncodeAutocompleteAttribute(DBSInputPhone pInputPhone, ResponseWriter pWriter) throws IOException{
		if (pInputPhone.getAutocomplete().equalsIgnoreCase("off") ||
			pInputPhone.getAutocomplete().equalsIgnoreCase("false")){
			DBSFaces.setAttribute(pWriter, "autocomplete", "off");
		}			
	}
}






