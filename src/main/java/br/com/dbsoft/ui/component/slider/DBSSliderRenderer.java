package br.com.dbsoft.ui.component.slider;

import java.io.IOException;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.google.gson.Gson;
import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.inputnumber.DBSInputNumber;
import br.com.dbsoft.ui.component.slider.DBSSlider;
import br.com.dbsoft.ui.component.slider.DBSSlider.ORIENTATION;
import br.com.dbsoft.ui.component.slider.DBSSlider.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSlider.RENDERER_TYPE)
public class DBSSliderRenderer extends DBSRenderer {
    
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSSlider xSlider = (DBSSlider) pComponent;
        if(xSlider.getReadOnly()) {return;}
        
        decodeBehaviors(pContext, xSlider);

		Object xSubmittedValue = null;
        
		TYPE xType = TYPE.get(xSlider.getType());
		if (xType == TYPE.RANGE){
			Double xValueBegin, xValueEnd, xValueTmp;
			ValueExpression xVEBegin, xVEEnd;
			xValueBegin = DBSNumber.round(DBSNumber.toDouble(DBSFaces.getDecodedComponenteValue(pContext, pvGetInputClientId(xSlider, "_begin"))), xSlider.getDecimalPlaces());
			xValueEnd = DBSNumber.round(DBSNumber.toDouble(DBSFaces.getDecodedComponenteValue(pContext, pvGetInputClientId(xSlider, "_end"))), xSlider.getDecimalPlaces());
	        //Troca o menor pelo maior se seleção foi digitada de forma invertida pelo usuário
			if (xValueBegin != null && xValueEnd != null){
	        	if (xValueBegin > xValueEnd){
	        		xValueTmp = xValueBegin;
	        		xValueBegin = xValueEnd;
	        		xValueEnd = xValueTmp;
	        	}
	        }
	        if (xValueBegin != null){
	        	xVEBegin =  pComponent.getValueExpression("beginValue");
			 	if (xVEBegin != null){
			 		xVEBegin.setValue(pContext.getELContext(), xValueBegin);
			 	}
	        }
	        if (xValueEnd != null){
	        	xVEEnd =  pComponent.getValueExpression("endValue");
			 	if (xVEEnd != null){
			 		xVEEnd.setValue(pContext.getELContext(), xValueEnd);
			 	}
	        }
		}else{
			xSubmittedValue = DBSFaces.getDecodedComponenteValue(pContext, pvGetInputClientId(xSlider,  ""));
			if (xType == TYPE.VALUES){
				Double xValue = DBSNumber.round(DBSNumber.toDouble(xSubmittedValue), xSlider.getDecimalPlaces());
				xSubmittedValue = xValue;
			}
		}
        if (xSubmittedValue != null){
        	xSlider.setSubmittedValue(xSubmittedValue);
        }
    }
    
    @Override
	public boolean getRendersChildren() {
		return false; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSSlider xSlider = (DBSSlider) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xSlider.getClientId(pContext);
		String xClass = CSS.SLIDER.MAIN + CSS.THEME.INPUT + CSS.MODIFIER.NOT_SELECTABLE + " -hide ";// + " -editing";
		
		TYPE 		xType = TYPE.get(xSlider.getType());
		ORIENTATION xOrientation = ORIENTATION.get(xSlider.getOrientation());


		xClass += xOrientation.getStyleClass();
		
		if (xSlider.getInvertValuesListPosition()){
			xClass += CSS.MODIFIER.INVERT;
		}
		if (xSlider.getStyleClass()!=null){
			xClass += xSlider.getStyleClass();
		}
		if (xSlider.getReadOnly()){
			xClass += CSS.MODIFIER.READONLY;
		}
		if (!xSlider.isValid()){
			xClass += CSS.MODIFIER.INVALID;
		}
		if (xSlider.getPlaceHolder() != null){
			xClass += " -ph ";
		}
		if (xSlider.getShowValues()){
			xClass += " -sv ";
		}
		
		xWriter.startElement("div", xSlider);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xSlider.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xType.getName());
			DBSFaces.encodeAttribute(xWriter, "dp", xSlider.getDecimalPlaces());
			if (xSlider.getReadOnly()){
				DBSFaces.encodeAttribute(xWriter, "disabled", "disabled");
			}
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xSlider, DBSPassThruAttributes.getAttributes(Key.SLIDER));
			xWriter.startElement("div", xSlider);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.NOT_SELECTABLE);
				pvEncodeContent(pContext, xSlider, xWriter, xType, xOrientation);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xSlider, xSlider.getTooltip());
			pvEncodeJS(xSlider, xWriter);
		xWriter.endElement("div");
	}

	private void pvEncodeContent(FacesContext pContext, DBSSlider pSlider, ResponseWriter pWriter, TYPE pType, ORIENTATION pOrientation) throws IOException{
		pWriter.startElement("div", pSlider);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.FLEX);
			//Label
			if (pSlider.getLabel() != null){
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
					pWriter.write(pSlider.getLabel());
				pWriter.endElement("div");
			}
			//Obs
			if (pOrientation == ORIENTATION.HORIZONTAL){
				pvEncodeContentObs(pSlider, pWriter);
			}
			//Slider
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", "-sub_container");
				//Handle
				if (!pSlider.getInvertValuesListPosition()){
					pvEncodeContentHandle(pSlider, pWriter, pType);
				}
				//slider value
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", "-slider" + CSS.THEME.FC);
					DBSFaces.encodeAttribute(pWriter, "pr", pSlider.getPlaceHolder());
					pWriter.startElement("div", pSlider);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
						pWriter.startElement("div", pSlider);
//							DBSFaces.encodeAttribute(pWriter, "class", "-background" + CSS.THEME.BC + CSS.THEME.FC + CSS.THEME.INVERT);
							DBSFaces.encodeAttribute(pWriter, "class", "-background" + CSS.THEME.FC + CSS.THEME.INVERT);
							DBSFaces.encodeAttribute(pWriter, "pr", pSlider.getPlaceHolder());
						pWriter.endElement("div");
					pWriter.endElement("div");
				pWriter.endElement("div");
				//Inputs
				if (pType == TYPE.RANGE){
					pvEncodeInput(pContext, pSlider, pWriter, pType, pSlider.getBeginValue(), "begin");
					pvEncodeInput(pContext, pSlider, pWriter, pType, pSlider.getEndValue(), "end");
				}else{
					pvEncodeInput(pContext, pSlider, pWriter, pType, pSlider.getValue(), "");
				}					
				//Handle
				if (pSlider.getInvertValuesListPosition()){
					pvEncodeContentHandle(pSlider, pWriter, pType);
				}
			pWriter.endElement("div");
			//Obs
			if (pOrientation == ORIENTATION.VERTICAL){
				pvEncodeContentObs(pSlider, pWriter);
			}
		pWriter.endElement("div");
		

	}
	
	private void pvEncodeContentObs(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		if (pSlider.getObs() != null){
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", "-obs");
				pWriter.write(pSlider.getObs());
			pWriter.endElement("div");
		}
	}
	
	private void pvEncodeContentHandle(DBSSlider pSlider, ResponseWriter pWriter, TYPE pType) throws IOException{
		String xClass = " -handle ";
		String xClassLabel = " -label ";
		if (pType == TYPE.RANGE || pType == TYPE.VALUES){
			xClassLabel += "-th_action";
		}
		if (pType == TYPE.RANGE){
			//Handle
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", xClass + " -begin");
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", xClassLabel);
				pWriter.endElement("div");	
			pWriter.endElement("div");	
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", xClass + " -end");
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", xClassLabel);
				pWriter.endElement("div");	
			pWriter.endElement("div");	
		}else{
			//Handle
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", xClass);
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", xClassLabel);
				pWriter.endElement("div");	
			pWriter.endElement("div");	
		}
	}

	private void pvEncodeInput(FacesContext pContext, DBSSlider pSlider, ResponseWriter pWriter, TYPE pType, Object pValue, String pSuffix) throws IOException{
		String xId = "input";
		String xClass = null;
		if (pSuffix != ""){
			xId += "_" + pSuffix;
			xClass = "-" + pSuffix;
		}
		if (pType == TYPE.VALUES
		 || pType == TYPE.RANGE){
			DBSInputNumber xInput = (DBSInputNumber) pSlider.getFacet(xId);
			if (xInput == null){
				xInput = (DBSInputNumber) pContext.getApplication().createComponent(DBSInputNumber.COMPONENT_TYPE);
				xInput.setId(xId);
				xInput.setOnFocusSelectAll(true);
				xInput.setDecimalPlaces(pSlider.getDecimalPlaces());
				xInput.setStyleClass(xClass);
				pSlider.getFacets().put(xId, xInput);
			}
			if (pSlider.getValuesList() !=null){
				@SuppressWarnings("unchecked")
				List<String> xList = (List<String>) pSlider.getValuesList();
				if (xList.size() > 0){
					xInput.setMinValue(DBSNumber.toDouble(xList.get(0)));
					xInput.setMaxValue(DBSNumber.toDouble(xList.get(xList.size()-1)));
//					System.out.println(xList.get(xList.size()-1));
				}
			}
//			xInput.setReadOnly(pSlider.getReadOnly());
			xInput.setValue(DBSObject.getNotNull(pValue,0));
			xInput.encodeAll(pContext);
		}else{
//			String xTag = (pSlider.getReadOnly() ? "span": "input");
			String xTag = "input";
			xId = pvGetInputClientId(pSlider, "");
			if (pSuffix == null){
				pSuffix = "";
			}else{
				xId += "_" + pSuffix;
			}
			pWriter.startElement(xTag, pSlider);
				DBSFaces.encodeAttribute(pWriter, "id", xId);
				DBSFaces.encodeAttribute(pWriter, "name", xId);
				DBSFaces.encodeAttribute(pWriter, "type", "hidden");
				DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pSlider) + xClass);
				DBSFaces.encodeAttribute(pWriter, "value", DBSObject.getNotNull(pValue,0));
			pWriter.endElement(xTag);
		}
	}
	

	private String pvGetInputClientId(DBSSlider pSlider, String pSuffix){
		return pSlider.getClientId() + DBSFaces.ID_SEPARATOR + "input" + pSuffix + "-data";
	}

	
	private void pvEncodeJS(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pSlider, pWriter);
		Gson xJson = new Gson();
		String xJS = "$(document).ready(function() { \n" +
				     " var xSliderId = dbsfaces.util.jsid('" + pSlider.getClientId() + "'); \n " + 
				     " dbs_slider(xSliderId, " + 
				     			  xJson.toJsonTree(pSlider.getValuesList(), List.class) + ", " +
				     			  xJson.toJsonTree(pSlider.getLabelsList(), List.class) + ", " +
				     			  xJson.toJson(pSlider.getMinValue()) + ", " +
				     			  xJson.toJson(pSlider.getMaxValue()) + ", " +
				     			  getLocale() +
				     			  "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
}






