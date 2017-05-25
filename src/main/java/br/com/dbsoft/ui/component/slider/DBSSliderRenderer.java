package br.com.dbsoft.ui.component.slider;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.google.gson.Gson;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.slider.DBSSlider;
import br.com.dbsoft.ui.component.slider.DBSSlider.CONTENT_ALIGNMENT;
import br.com.dbsoft.ui.component.slider.DBSSlider.ORIENTATION;
import br.com.dbsoft.ui.component.slider.DBSSlider.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSlider.RENDERER_TYPE)
public class DBSSliderRenderer extends DBSRenderer {
    
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSSlider xSlider = (DBSSlider) pComponent;
        if(xSlider.getReadOnly()) {return;}
        
        decodeBehaviors(pContext, xSlider);
        
        String xValue = DBSFaces.getDecodedComponenteValue(pContext, pvGetInputClientId(xSlider));
        if (xValue != null){
        	xSlider.setSubmittedValue(xValue);
        }
//		String xClientIdAction = getInputDataClientId(xInputNumber);
//		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientIdAction)) {
//			Object xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
//			try {
//				//Primeiramente converte para double para forçar um valor não nulo
//				xSubmittedValue = DBSNumber.toDouble(xSubmittedValue);
//				//Este submittedValue irá converter o valor para o tipo de dado do campo que o receberá
//				xInputNumber.setSubmittedValue(xSubmittedValue);
//			} catch (Exception xE) {
//				wLogger.error("Erro na conversão do inputnumber", xE);
//			}
//		}
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
		String xClass = CSS.SLIDER.MAIN + CSS.THEME.INPUT + CSS.MODIFIER.NOT_SELECTABLE + " -hide ";
		
		TYPE 		xType = TYPE.get(xSlider.getType());
		ORIENTATION xOrientation = ORIENTATION.get(xSlider.getOrientation());

		xClass += xOrientation.getStyleClass();
		
		if (xSlider.getAnimated()){
			xClass += " -ani ";
		}
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
		xWriter.startElement("div", xSlider);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xSlider.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xType.getName());
			DBSFaces.encodeAttribute(xWriter, "dp", xSlider.getDecimalPlaces());
			if (xSlider.getContentAlignment() == null){
				if (xOrientation == ORIENTATION.HORIZONTAL){
					if (xSlider.getInvertValuesListPosition()){
						DBSFaces.encodeAttribute(xWriter, "ca", CONTENT_ALIGNMENT.TOP.getName());
					}else{
						DBSFaces.encodeAttribute(xWriter, "ca", CONTENT_ALIGNMENT.BOTTOM.getName());
					}
				}else{
					if (xSlider.getInvertValuesListPosition()){
						DBSFaces.encodeAttribute(xWriter, "ca", CONTENT_ALIGNMENT.LEFT.getName());
					}else{
						DBSFaces.encodeAttribute(xWriter, "ca", CONTENT_ALIGNMENT.RIGHT.getName());
					}
				}
			}else{
				DBSFaces.encodeAttribute(xWriter, "ca", xSlider.getContentAlignment());
			}
			xWriter.startElement("div", xSlider);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.NOT_SELECTABLE);
				pvEncodeContent(xSlider, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xSlider, xSlider.getTooltip());
			pvEncodeJS(xSlider, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeContent(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pSlider);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			//Label
			if (pSlider.getLabel() != null){
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
					pWriter.write(pSlider.getLabel());
				pWriter.endElement("div");
			}
			if (pSlider.getObs() != null){
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", "-obs");
					pWriter.write(pSlider.getObs());
				pWriter.endElement("div");
			}
			//Slider
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", "-slider");
				pWriter.startElement("div", pSlider);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Handle
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", "-handle");
			pWriter.endElement("div");	
		pWriter.endElement("div");
		pvEncodeInput(pSlider, pWriter);
	}

	private void pvEncodeInput(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		String xTag = (pSlider.getReadOnly() ? "span": "input");
		pWriter.startElement(xTag, pSlider);
			DBSFaces.encodeAttribute(pWriter, "id", pvGetInputClientId(pSlider));
			DBSFaces.encodeAttribute(pWriter, "name", pvGetInputClientId(pSlider));
			DBSFaces.encodeAttribute(pWriter, "type", "hidden");
			DBSFaces.encodeAttribute(pWriter, "class", DBSFaces.getInputDataClass(pSlider));
			DBSFaces.encodeAttribute(pWriter, "value", DBSObject.getNotNull(pSlider.getValue(),0));
		pWriter.endElement(xTag);
	}
	

	private String pvGetInputClientId(DBSSlider pSlider){
		return pSlider.getClientId() + DBSFaces.ID_SEPARATOR + "input";
	}
	
	private void pvEncodeJS(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pSlider, pWriter);
		Gson xListValuesJson = new Gson();
		String xJS = "$(document).ready(function() { \n" +
				     " var xSliderId = dbsfaces.util.jsid('" + pSlider.getClientId() + "'); \n " + 
				     " dbs_slider(xSliderId, " + 
				     			  xListValuesJson.toJsonTree(pSlider.getValuesList(), List.class) + ", " +
				     			  xListValuesJson.toJson(pSlider.getMinValue()) + ", " +
				     			  xListValuesJson.toJson(pSlider.getMaxValue()) +
				     			  "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
//	/**
//	 * Retorna lista com os valores no formato json
//	 * @param pChart
//	 * @return
//	 * @throws IOException
//	 */
//	private String pvGetListRelationalCaptions(DBSSlider pSlider) throws IOException {
//		if (pSlider.getValuesList() == null){return null;}
//		String[] xValuesList = pSlider.getValuesList().split(";");
//		for (int xI=0; xI < xValuesList.length; xI++){
//			xRelationalCaptions[xI] = xRelationalCaptions[xI].trim();
//		}
//		return DBSJson.toJson(xRelationalCaptions).toString();
//	}
}






