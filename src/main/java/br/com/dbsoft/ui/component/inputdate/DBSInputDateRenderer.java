package br.com.dbsoft.ui.component.inputdate;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSString;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputDate.RENDERER_TYPE)
public class DBSInputDateRenderer extends DBSRenderer {
	
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSInputDate xInputDate = (DBSInputDate) pComponent;
        if(xInputDate.getReadOnly()) {return;}

    	String xDay = "";
    	String xMonth = "";
    	String xYear = "";
    	String xHour = "";
    	String xMinute = "";
    	String xSecond = "";
    	String xDate = "";
    	String xTime = "";

    	decodeBehaviors(pContext, xInputDate);
    	
    	ExternalContext xEC = pContext.getExternalContext();
    	
		String xClientIdAction = getInputDataClientId(xInputDate) ;
		if (xEC.getRequestParameterMap().containsKey(xClientIdAction + DBSFaces.CSS.INPUTDATE.DAY) ||
			xEC.getRequestParameterMap().containsKey(xClientIdAction + DBSFaces.CSS.INPUTDATE.HOUR)){
    		if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE) ||
    			xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
    			xDay = xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTDATE.DAY);
	        	xMonth = xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTDATE.MONTH);
	        	xYear = xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTDATE.YEAR);
	        	if (xDay.length()==0 ||
	        		xMonth.length()==0 ||
	        		xYear.length()==0){
		        	xDate = null;
	        	}else{
		        	xDate = xDay + "/" + xMonth + "/" + xYear;
	        	}
    		}
    		if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME) ||
    			xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES) ||
	    		xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
        		xHour = DBSString.toString(xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTDATE.HOUR), "");
    			xMinute = DBSString.toString(xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTDATE.MINUTE), "");
    			xSecond = DBSString.toString(xEC.getRequestParameterMap().get(xClientIdAction + DBSFaces.CSS.INPUTDATE.SECOND), "00");
	        	if (xHour.length()==0 ||
	        		xMinute.length()==0){
		        	xTime = null;
	        	}else{
	    			xTime = xHour + ":" + xMinute + ":" + xSecond;
	        	}
    		}
    		
    		if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE)){
    			if (xDate == null){
    				xInputDate.setSubmittedValue("");
    			}else{
    				xInputDate.setSubmittedValue(DBSDate.toDate(xDate));
    			}
    		}else if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME)){
    			if (xTime == null){
    				xInputDate.setSubmittedValue("");
    			}else{
    				xInputDate.setSubmittedValue(DBSDate.toTime(xHour, xMinute, "00"));
    			}
    		}else if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES)){
    			if (xTime == null){
    				xInputDate.setSubmittedValue("");
    			}else{
    				xInputDate.setSubmittedValue(DBSDate.toTime(xHour, xMinute, xSecond));
    			}
    		}else if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
	   			if (xDate == null ||
	   				xTime == null){
	   				xInputDate.setSubmittedValue("");	
    			}else{
    				xInputDate.setSubmittedValue(DBSDate.toTimestampYMDHMS(xDate + " " + xTime));
    			}
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
		DBSInputDate xInputDate = (DBSInputDate) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputDate.getClientId(pContext);
		String xClass = DBSFaces.CSS.INPUTDATE.MAIN + " " + DBSFaces.CSS.INPUT.MAIN + " ";
		if (xInputDate.getStyleClass()!=null){
			xClass = xClass + xInputDate.getStyleClass();
		}
		xWriter.startElement("div", xInputDate);
			xWriter.writeAttribute("id", xClientId, "id");
			xWriter.writeAttribute("name", xClientId, "name");
			xWriter.writeAttribute("class", xClass, "class");
			DBSFaces.setAttribute(xWriter, "style", xInputDate.getStyle(), null);
			//Container
			xWriter.startElement("div", xInputDate);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");

				DBSFaces.encodeLabel(pContext, xInputDate, xWriter);
				pvEncodeInput(pContext, xInputDate, xWriter);
				DBSFaces.encodeRightLabel(pContext, xInputDate, xWriter);
				DBSFaces.encodeTooltip(pContext, xInputDate, xInputDate.getTooltip());
//				encodeClientBehaviors(pContext, xInputDate);

			xWriter.endElement("div");
		xWriter.endElement("div");
		DBSFaces.encodeJavaScriptTagStart(xWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xInputDateId = '#' + dbsfaces.util.jsid('" + xClientId + "'); \n " + 
				     " dbs_inputDate(xInputDateId); \n" +
                     "}); \n"; 
		xWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(xWriter);		
	}
	
	private void pvEncodeInput(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputDate);
		String xStyle = "";
		String xStyleClass = "";
		String xValue = "";
		if (pInputDate.getDate() != null){
			if ((pInputDate.getDateMin() != null 
			  && pInputDate.getDate().before(pInputDate.getDateMin()))
			 || (pInputDate.getDateMax() != null 
			  && pInputDate.getDate().after(pInputDate.getDateMax()))){
				xStyleClass = DBSFaces.CSS.MODIFIER.ERROR;
			}
		}		
		if (pInputDate.getReadOnly()){
			if (pInputDate.getDate()==null){
				if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE)){
					xStyle =  DBSFaces.getStyleWidthFromInputSize(10);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME)){
					xStyle =  DBSFaces.getStyleWidthFromInputSize(5);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES)){
					xStyle =  DBSFaces.getStyleWidthFromInputSize(8);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
					xStyle =  DBSFaces.getStyleWidthFromInputSize(16);
				}else{
					wLogger.error(xClientId + ":type inválido " + pInputDate.getType());
				}
			}else{
				if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE)){
					xValue = DBSFormat.getFormattedDate(pInputDate.getDate());
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME)){
					xValue = DBSFormat.getFormattedTime(pInputDate.getDate());
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES)){
					xValue = DBSFormat.getFormattedTime(pInputDate.getDate());
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
					xValue = DBSFormat.getFormattedDateTime(pInputDate.getDate());
				}else{
					wLogger.error(xClientId + ":type inválido " + pInputDate.getType());
				}
			}
			
			DBSFaces.encodeInputDataReadOnly(pInputDate, pWriter, xClientId, xStyle, false, xValue);
		}else{
			pWriter.startElement("span", pInputDate);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUT.DATA + xStyleClass, null);
				//Define a largura do campo
				if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE)){
					pvEncodeInputDate(pContext, pInputDate, pWriter);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME)){
					pvEncodeInputTime(pContext, pInputDate, pWriter);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES)){
					pvEncodeInputTimes(pContext, pInputDate, pWriter);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
					pvEncodeInputDateTime(pContext, pInputDate, pWriter);
				}else{
					wLogger.error(xClientId + ":type inválido [" + pInputDate.getType() + "]");
				}
			pWriter.endElement("span");
		}
	}

	private void pvEncodeInputDate(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputDate);
		//Se for somente leitura, gera código como <Span>
		pWriter.startElement("input", pInputDate);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTDATE.DAY, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTDATE.DAY, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTDATE.DAY, null);
			DBSFaces.setAttribute(pWriter, "style", "width: 14px;", null);
			DBSFaces.setAttribute(pWriter, "size", "2", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "2", null);
			DBSFaces.setAttribute(pWriter, "value", pInputDate.getDay(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);			
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
		pWriter.startElement("label", pInputDate);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
			DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTDATE.MONTH, null);
			pWriter.write("/");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTDATE.MONTH, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTDATE.MONTH, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTDATE.MONTH, null);
			DBSFaces.setAttribute(pWriter, "style", "width: 14px;", null);
			DBSFaces.setAttribute(pWriter, "size", "2", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "2", null);
			DBSFaces.setAttribute(pWriter, "value", pInputDate.getMonth(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
		pWriter.startElement("label", pInputDate);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
			DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTDATE.YEAR, null);
			pWriter.write("/");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTDATE.YEAR, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTDATE.YEAR, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTDATE.YEAR, null);
			DBSFaces.setAttribute(pWriter, "style", "width: 30px;", null);
			DBSFaces.setAttribute(pWriter, "size", "4", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "4", null);
			DBSFaces.setAttribute(pWriter, "value", pInputDate.getYear(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
	}
	
	private void pvEncodeInputTime(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputDate);
		//HORA
		pWriter.startElement("label", pInputDate);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
			DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTDATE.HOUR, null);
			pWriter.write(" ");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTDATE.HOUR, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTDATE.HOUR, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTDATE.HOUR, null);
			DBSFaces.setAttribute(pWriter, "style", "width: 14px;", null);
			DBSFaces.setAttribute(pWriter, "size", "2", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "2", null);
			DBSFaces.setAttribute(pWriter, "value", pInputDate.getHour(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
		
		//Minutos
		pWriter.startElement("label", pInputDate);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
			DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTDATE.MINUTE, null);
			pWriter.write(":");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTDATE.MINUTE, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTDATE.MINUTE, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTDATE.MINUTE, null);
			DBSFaces.setAttribute(pWriter, "style", "width: 14px;", null);
			DBSFaces.setAttribute(pWriter, "size", "2", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "2", null);
			DBSFaces.setAttribute(pWriter, "value", pInputDate.getMinute(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");

	}

	private void pvEncodeInputTimes(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		pvEncodeInputTime(pContext, pInputDate, pWriter);
		String xClientId = getInputDataClientId(pInputDate);
		//Segundos
		pWriter.startElement("label", pInputDate);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.NOT_SELECTABLE, null);
			DBSFaces.setAttribute(pWriter, "for", xClientId + DBSFaces.CSS.INPUTDATE.SECOND, null);
			pWriter.write(":");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.setAttribute(pWriter, "id", xClientId + DBSFaces.CSS.INPUTDATE.SECOND, null);
			DBSFaces.setAttribute(pWriter, "name", xClientId + DBSFaces.CSS.INPUTDATE.SECOND, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.INPUTDATE.SECOND, null);
			DBSFaces.setAttribute(pWriter, "style", "width: 14px;", null);
			DBSFaces.setAttribute(pWriter, "size", "2", null);
			DBSFaces.setAttribute(pWriter, "maxlength", "2", null);
			DBSFaces.setAttribute(pWriter, "value", pInputDate.getSecond(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");

	}

	private void pvEncodeInputDateTime(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		pvEncodeInputDate(pContext, pInputDate, pWriter);
		pvEncodeInputTime(pContext, pInputDate, pWriter);
	}
	
	private void pvEncodeAutocompleteAttribute(DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		if (pInputDate.getAutocomplete().equalsIgnoreCase("off") ||
			pInputDate.getAutocomplete().equalsIgnoreCase("false")){
			DBSFaces.setAttribute(pWriter, "autocomplete", "off", null);
		}			
	}
}






