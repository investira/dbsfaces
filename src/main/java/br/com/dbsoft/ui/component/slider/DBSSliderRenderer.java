package br.com.dbsoft.ui.component.slider;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.slider.DBSSlider;
import br.com.dbsoft.ui.component.slider.DBSSlider.ORIENTATION;
import br.com.dbsoft.ui.component.slider.DBSSlider.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSlider.RENDERER_TYPE)
public class DBSSliderRenderer extends DBSRenderer {
    
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSSlider xSlider = (DBSSlider) pComponent;
        if(xSlider.getReadOnly()) {return;}
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
		String xClass = CSS.SLIDER.MAIN + CSS.MODIFIER.NOT_SELECTABLE + " -hide ";
		
		TYPE 		xType = TYPE.get(xSlider.getType());
		ORIENTATION xOrientation = ORIENTATION.get(xSlider.getOrientation());
		
		xClass += xOrientation.getStyleClass();
		
		
		if (xSlider.getAnimated()){
			xClass += " -ani ";
		}
		if (xSlider.getStyleClass()!=null){
			xClass += xSlider.getStyleClass();
		}
		xWriter.startElement("div", xSlider);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xSlider.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xType.getName());
			DBSFaces.encodeAttribute(xWriter, "v", xSlider.getValue());
			DBSFaces.encodeAttribute(xWriter, "max", xSlider.getMaxValue());
			DBSFaces.encodeAttribute(xWriter, "min", xSlider.getMinValue());
			xWriter.startElement("div", xSlider);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.NOT_SELECTABLE);
				pvEncodeContent(xSlider, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xSlider, xSlider.getTooltip());
			if (!xSlider.getReadOnly()){
				pvEncodeJS(xSlider, xWriter);
			}
		xWriter.endElement("div");
	}
	
	
	private void pvEncodeContent(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pSlider);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			pWriter.startElement("div", pSlider);
				DBSFaces.encodeAttribute(pWriter, "class", "-slider");
//				pvEncodeHorizontalVerticalLabel(pSlider, pWriter);
			pWriter.endElement("div");
		pWriter.endElement("div");
		pWriter.startElement("div", pSlider);
			DBSFaces.encodeAttribute(pWriter, "class", "-handle");
//			pWriter.startElement("div", pSlider);
//				DBSFaces.encodeAttribute(pWriter, "class", "-i_dot");
//			pWriter.endElement("div");	
		pWriter.endElement("div");	
	}

//	private void pvEncodeHorizontalVerticalLabel(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
//		pWriter.startElement("div", pSlider);
//			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
//			pWriter.startElement("div", pSlider);
//				DBSFaces.encodeAttribute(pWriter, "class", "-value");
//			pWriter.endElement("div");
//			pWriter.startElement("div", pSlider);
//				DBSFaces.encodeAttribute(pWriter, "class", "-sufix");
//			pWriter.endElement("div");
//		pWriter.endElement("div");
//	}

//	private Double pvGetPercent(DBSSlider pSlider) throws IOException{
////		Double xM = DBSNumber.toDouble(pSlider.getMaxValue());
//		Double xMin = DBSNumber.toDouble(pSlider.getMinValue());
//		Double xMax = DBSNumber.toDouble(pSlider.getMaxValue());
//		Double xV = DBSNumber.toDouble(pSlider.getValue());
//		Double xFator = 0D;
//		//Exibe já preenchido se não houver valor máximo
//		if (xMax == 0D){
//			xFator = 1D; 
//		//Exibe já vázio se valor atual for zero
//		}else if (xV == 0D){
//			xFator = 0D;
//		}else{
//			xFator = (xV / (xMax - xMin)); //Calcula fator
//		}
//		xFator *= 100D; //Calcula percentual
//		return xFator;
//	}
	
	private void pvEncodeJS(DBSSlider pSlider, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pSlider, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xSliderId = dbsfaces.util.jsid('" + pSlider.getClientId() + "'); \n " + 
				     " dbs_slider(xSliderId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}






