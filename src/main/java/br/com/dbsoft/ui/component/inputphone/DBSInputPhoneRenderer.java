package br.com.dbsoft.ui.component.inputphone;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputPhone.RENDERER_TYPE)
public class DBSInputPhoneRenderer extends DBSRenderer {
	
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
		if (xEC.getRequestParameterMap().containsKey(xClientIdAction + DBSFaces.CSS.INPUTPHONE.NUMBER)){
			if (xEC.getRequestParameterMap().containsKey(xClientIdAction + DBSFaces.CSS.INPUTPHONE.DDI)){
    			xDDI = xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTPHONE.DDI);
    		}
			if (xEC.getRequestParameterMap().containsKey(xClientIdAction + DBSFaces.CSS.INPUTPHONE.DDD)){
    			xDDD = xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTPHONE.DDD);
    		}
   			xNumber = xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTPHONE.NUMBER);
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
		String xClass = DBSFaces.CSS.INPUTPHONE.MAIN + " " + DBSFaces.CSS.INPUT.MAIN + " ";
		if (xInputPhone.getStyleClass()!=null){
			xClass = xClass + xInputPhone.getStyleClass();
		}
		xWriter.startElement("div", xInputPhone);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xInputPhone.getStyle(), null);
			//Container
			xWriter.startElement("div", xInputPhone);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");

				DBSFaces.encodeLabel(pContext, xInputPhone, xWriter);
				pvEncodeInput(pContext, xInputPhone, xWriter);
				DBSFaces.encodeRightLabel(pContext, xInputPhone, xWriter);
				DBSFaces.encodeTooltip(pContext, xInputPhone, xInputPhone.getTooltip());
//				encodeClientBehaviors(pContext, xInputPhone);

			xWriter.endElement("div");
		xWriter.endElement("div");
		DBSFaces.encodeJavaScriptTagStart(xWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xInputPhoneId = '#' + dbsfaces.util.jsid('" + xClientId + "'); \n " + 
				     " dbs_inputPhone(xInputPhoneId); \n" +
                     "}); \n"; 
		xWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(xWriter);		
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
//				xStyleClass = DBSFaces.CSS.MODIFIER.ERROR;
//			}
//		}		
		if (pInputPhone.getReadOnly()){
			if (pInputPhone.getValue()==null){
				if (pInputPhone.getShowDDI()){
					xSize = 19;
					xStyle =  DBSFaces.getStyleWidthFromInputSize(19);
				}else if (pInputPhone.getShowDDD()){
					xSize = 14;
					xStyle =  DBSFaces.getStyleWidthFromInputSize(14);
				}else{
					xSize = 10;
					xStyle =  DBSFaces.getStyleWidthFromInputSize(10);
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
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputPhone) + xStyleClass, null);
				DBSFaces.setSizeAttributes(pWriter, xSize, null);
				//Define a largura do campo
				pvEncodeInputPhone(pContext, pInputPhone, pWriter);
			pWriter.endElement("span");
		}
	}

	private void pvEncodeInputPhone(FacesContext pContext, DBSInputPhone pInputPhone, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputPhone);
		//Se for somente leitura, gera código como <Span>
		if (pInputPhone.getShowDDI()){
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
				DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTPHONE.DDI, null);
				pWriter.write("(");
			pWriter.endElement("label");
			pWriter.startElement("input", pInputPhone);
				DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTPHONE.DDI, null);
				DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTPHONE.DDI, null);
				DBSFaces.setAttribute(pWriter, "type", "text", null);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTPHONE.DDI, null);
				DBSFaces.setAttribute(pWriter, "style", DBSFaces.getStyleWidthFromInputSizeInternal(3), null);
				DBSFaces.setAttribute(pWriter, "size", "3", null);
				DBSFaces.setAttribute(pWriter, "maxlength", "3", null);
				DBSFaces.setAttribute(pWriter, "value", pInputPhone.getDDI(), "");
				pvEncodeAutocompleteAttribute(pInputPhone, pWriter);			
				encodeClientBehaviors(pContext, pInputPhone);
			pWriter.endElement("input");
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
				DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTPHONE.DDD, null);
				pWriter.write(")");
			pWriter.endElement("label");
		}
		if (pInputPhone.getShowDDD()){
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
				DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTPHONE.DDD, null);
				pWriter.write("(");
			pWriter.endElement("label");
			pWriter.startElement("input", pInputPhone);
				DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTPHONE.DDD, null);
				DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTPHONE.DDD, null);
				DBSFaces.setAttribute(pWriter, "type", "text", null);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTPHONE.DDD, null);
				DBSFaces.setAttribute(pWriter, "style", DBSFaces.getStyleWidthFromInputSizeInternal(2), null);
				DBSFaces.setAttribute(pWriter, "size", "2", null);
				DBSFaces.setAttribute(pWriter, "maxlength", "2", null);
				DBSFaces.setAttribute(pWriter, "value", pInputPhone.getDDD(), "");
				pvEncodeAutocompleteAttribute(pInputPhone, pWriter);
				encodeClientBehaviors(pContext, pInputPhone);
			pWriter.endElement("input");
			pWriter.startElement("label", pInputPhone);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
				DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTPHONE.NUMBER, null);
				pWriter.write(")");
			pWriter.endElement("label");
		}
		pWriter.startElement("input", pInputPhone);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTPHONE.NUMBER, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTPHONE.NUMBER, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTPHONE.NUMBER, null);
			DBSFaces.setAttribute(pWriter, "style", DBSFaces.getStyleWidthFromInputSizeInternal(10), null);
			DBSFaces.setAttribute(pWriter, "size", "9", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "9", null);
			DBSFaces.setAttribute(pWriter, "value", pInputPhone.getNumber(), "");
			pvEncodeAutocompleteAttribute(pInputPhone, pWriter);
			encodeClientBehaviors(pContext, pInputPhone);
		pWriter.endElement("input");
	}
	
	
	private void pvEncodeAutocompleteAttribute(DBSInputPhone pInputPhone, ResponseWriter pWriter) throws IOException{
		if (pInputPhone.getAutocomplete().equalsIgnoreCase("off") ||
			pInputPhone.getAutocomplete().equalsIgnoreCase("false")){
			DBSFaces.setAttribute(pWriter, "autocomplete", "off", null);
		}			
	}
}






