package br.com.dbsoft.ui.component.inputdate;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSString;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSInputDate.RENDERER_TYPE)
public class DBSInputDateRenderer extends DBSRenderer {
	
	private static String wWidth1 = "width:0.4em;";
	private static String wWidth2 = "width:1.3em;";
	private static String wWidth4 = "width:2.5em;";
	
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
		if (xEC.getRequestParameterMap().containsKey(xClientIdAction + CSS.INPUTDATE.DAY.trim()) ||
			xEC.getRequestParameterMap().containsKey(xClientIdAction + CSS.INPUTDATE.HOUR.trim())){
    		if (xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE) ||
    			xInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
    			xDay = xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTDATE.DAY.trim());
	        	xMonth = xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTDATE.MONTH.trim());
	        	xYear = xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTDATE.YEAR.trim());
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
        		xHour = DBSString.toString(xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTDATE.HOUR.trim()), "");
    			xMinute = DBSString.toString(xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTDATE.MINUTE.trim()), "");
    			xSecond = DBSString.toString(xEC.getRequestParameterMap().get(xClientIdAction + CSS.INPUTDATE.SECOND.trim()), "00");
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSInputDate xInputDate = (DBSInputDate) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xInputDate.getClientId(pContext);
		String xClass = CSS.INPUTDATE.MAIN + CSS.THEME.INPUT;
		if (xInputDate.getStyleClass()!=null){
			xClass += xInputDate.getStyleClass();
		}
		xWriter.startElement("div", xInputDate);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xInputDate.getStyle());
			//Container
			xWriter.startElement("div", xInputDate);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);

				DBSFaces.encodeLabel(pContext, xInputDate, xWriter);
				pvEncodeInput(pContext, xInputDate, xWriter);
				DBSFaces.encodeRightLabel(pContext, xInputDate, xWriter);
//				encodeClientBehaviors(pContext, xInputDate);

			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xInputDate, xInputDate.getTooltip());

			if (!xInputDate.getReadOnly()){
				DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
				String xJS = "$(document).ready(function() { \n" +
						     " var xInputDateId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
						     " dbs_inputDate(xInputDateId); \n" +
		                     "}); \n"; 
				xWriter.write(xJS);
				DBSFaces.encodeJavaScriptTagEnd(xWriter);
			}
			xWriter.endElement("div");
	}
	
	private void pvEncodeInput(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String 	xClientId = getInputDataClientId(pInputDate);
		String 	xStyle = "";
		String 	xStyleClass = "";
		String 	xValue = "";
		Integer xSize = 0;
		if (pInputDate.getDate() != null){
			if ((pInputDate.getDateMin() != null 
			  && pInputDate.getDate().before(pInputDate.getDateMin()))
			 || (pInputDate.getDateMax() != null 
			  && pInputDate.getDate().after(pInputDate.getDateMax()))){
				xStyleClass = CSS.MODIFIER.ERROR;
			}
		}		
		if (pInputDate.getReadOnly()){
			if (pInputDate.getDate()==null){
				if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE)){
					xSize = 10;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(10);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME)){
					xSize = 5;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(5);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES)){
					xSize = 8;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(8);
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
					xSize = 16;
					xStyle =  DBSFaces.getCSSStyleWidthFromInputSize(16);
				}else{
					wLogger.error(xClientId + ":type inválido " + pInputDate.getType());
				}
			}else{
				if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATE)){
					xValue = DBSFormat.getFormattedDate(pInputDate.getDate());
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIME)){
					xValue = DBSFormat.getFormattedTime(pInputDate.getDate());
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.TIMES)){
					xValue = DBSFormat.getFormattedTimes(pInputDate.getDate());
				}else if (pInputDate.getType().equalsIgnoreCase(DBSInputDate.TYPE.DATETIME)){
					xValue = DBSFormat.getFormattedDateTimes(pInputDate.getDate());
				}else{
					wLogger.error(xClientId + ":type inválido " + pInputDate.getType());
				}
				xSize = xValue.length();
			}
			
			DBSFaces.encodeInputDataReadOnly(pInputDate, pWriter, xClientId, false, xValue, xSize, null, xStyle);
		}else{
			pWriter.startElement("div", pInputDate);
				DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pInputDate) + xStyleClass);
				DBSFaces.setSizeAttributes(pWriter, xSize, null);
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
			pWriter.endElement("div");
		}
	}

	private void pvEncodeInputDate(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputDate);
		//Se for somente leitura, gera código como <Span>
		pWriter.startElement("input", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "id", xClientId + CSS.INPUTDATE.DAY.trim());
			DBSFaces.encodeAttribute(pWriter, "name", xClientId + CSS.INPUTDATE.DAY.trim());
			DBSFaces.encodeAttribute(pWriter, "placeHolder", pInputDate.getPlaceHolder());
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
			DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
			DBSFaces.encodeAttribute(pWriter, "class", CSS.INPUTDATE.DAY);
			DBSFaces.encodeAttribute(pWriter, "style", wWidth2);
			DBSFaces.encodeAttribute(pWriter, "size", "2");
			DBSFaces.encodeAttribute(pWriter, "maxlength", "2");
			DBSFaces.encodeAttribute(pWriter, "value", pInputDate.getDay(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);			
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
		pWriter.startElement("label", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
			DBSFaces.encodeAttribute(pWriter, "for", xClientId + CSS.INPUTDATE.MONTH.trim());
			pWriter.write("/");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "id", xClientId + CSS.INPUTDATE.MONTH.trim());
			DBSFaces.encodeAttribute(pWriter, "name", xClientId + CSS.INPUTDATE.MONTH.trim());
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
			DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
			DBSFaces.encodeAttribute(pWriter, "class", CSS.INPUTDATE.MONTH);
			DBSFaces.encodeAttribute(pWriter, "style", wWidth2);
			DBSFaces.encodeAttribute(pWriter, "size", "2");
			DBSFaces.encodeAttribute(pWriter, "maxlength", "2");
			DBSFaces.encodeAttribute(pWriter, "value", pInputDate.getMonth(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
		pWriter.startElement("label", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
			DBSFaces.encodeAttribute(pWriter, "for", xClientId + CSS.INPUTDATE.YEAR.trim());
			pWriter.write("/");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "id", xClientId + CSS.INPUTDATE.YEAR.trim());
			DBSFaces.encodeAttribute(pWriter, "name", xClientId + CSS.INPUTDATE.YEAR.trim());
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
			DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
			DBSFaces.encodeAttribute(pWriter, "class", CSS.INPUTDATE.YEAR);
			DBSFaces.encodeAttribute(pWriter, "style", wWidth4);
			DBSFaces.encodeAttribute(pWriter, "size", "4");
			DBSFaces.encodeAttribute(pWriter, "maxlength", "4");
			DBSFaces.encodeAttribute(pWriter, "value", pInputDate.getYear(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
	}
	
	private void pvEncodeInputTime(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputDate);
		//HORA
		pWriter.startElement("input", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "id", xClientId + CSS.INPUTDATE.HOUR.trim());
			DBSFaces.encodeAttribute(pWriter, "name", xClientId + CSS.INPUTDATE.HOUR.trim());
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
			DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
			DBSFaces.encodeAttribute(pWriter, "class", CSS.INPUTDATE.HOUR);
			DBSFaces.encodeAttribute(pWriter, "style", wWidth2);
			DBSFaces.encodeAttribute(pWriter, "size", "2");
			DBSFaces.encodeAttribute(pWriter, "maxlength", "2");
			DBSFaces.encodeAttribute(pWriter, "value", pInputDate.getHour(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");
		
		//Minutos
		pWriter.startElement("label", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
			DBSFaces.encodeAttribute(pWriter, "for", xClientId + CSS.INPUTDATE.MINUTE.trim());
			pWriter.write(":");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "id", xClientId + CSS.INPUTDATE.MINUTE.trim());
			DBSFaces.encodeAttribute(pWriter, "name", xClientId + CSS.INPUTDATE.MINUTE.trim());
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
			DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
			DBSFaces.encodeAttribute(pWriter, "class", CSS.INPUTDATE.MINUTE);
			DBSFaces.encodeAttribute(pWriter, "style", wWidth2);
			DBSFaces.encodeAttribute(pWriter, "size", "2");
			DBSFaces.encodeAttribute(pWriter, "maxlength", "2");
			DBSFaces.encodeAttribute(pWriter, "value", pInputDate.getMinute(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");

	}

	private void pvEncodeInputTimes(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		pvEncodeInputTime(pContext, pInputDate, pWriter);
		String xClientId = getInputDataClientId(pInputDate);
		//Segundos
		pWriter.startElement("label", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
			DBSFaces.encodeAttribute(pWriter, "for", xClientId + CSS.INPUTDATE.SECOND.trim());
			pWriter.write(":");
		pWriter.endElement("label");
		pWriter.startElement("input", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "id", xClientId + CSS.INPUTDATE.SECOND.trim());
			DBSFaces.encodeAttribute(pWriter, "name", xClientId + CSS.INPUTDATE.SECOND.trim());
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "pattern", "[0-9]*");
			DBSFaces.encodeAttribute(pWriter, "inputmode", "numeric");
			DBSFaces.encodeAttribute(pWriter, "class", CSS.INPUTDATE.SECOND);
			DBSFaces.encodeAttribute(pWriter, "style", wWidth2);
			DBSFaces.encodeAttribute(pWriter, "size", "2");
			DBSFaces.encodeAttribute(pWriter, "maxlength", "2");
			DBSFaces.encodeAttribute(pWriter, "value", pInputDate.getSecond(), "");
			pvEncodeAutocompleteAttribute(pInputDate, pWriter);
			encodeClientBehaviors(pContext, pInputDate);
		pWriter.endElement("input");

	}

	private void pvEncodeInputDateTime(FacesContext pContext, DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		String xClientId = getInputDataClientId(pInputDate);
		pvEncodeInputDate(pContext, pInputDate, pWriter);
		pWriter.startElement("label", pInputDate);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.NOT_SELECTABLE);
			DBSFaces.encodeAttribute(pWriter, "for", xClientId + CSS.INPUTDATE.HOUR.trim());
			DBSFaces.encodeAttribute(pWriter, "style", wWidth1);
			pWriter.write(" ");
		pWriter.endElement("label");
		pvEncodeInputTime(pContext, pInputDate, pWriter);
	}
	
	private void pvEncodeAutocompleteAttribute(DBSInputDate pInputDate, ResponseWriter pWriter) throws IOException{
		if (pInputDate.getAutocomplete().equalsIgnoreCase("off") ||
			pInputDate.getAutocomplete().equalsIgnoreCase("false")){
			DBSFaces.encodeAttribute(pWriter, "autocomplete", "off");
		}			
	}
}






