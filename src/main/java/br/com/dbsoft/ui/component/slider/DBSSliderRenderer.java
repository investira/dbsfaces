package br.com.dbsoft.ui.component.slider;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.slider.DBSSlider;
import br.com.dbsoft.ui.component.slider.DBSSlider.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSlider.RENDERER_TYPE)
public class DBSSliderRenderer extends DBSRenderer {
	
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
		String xClass = CSS.SLIDER.MAIN + " -hide ";
		
		TYPE xType = TYPE.get(xSlider.getType());
		
		xClass += xType.getStyleClass();
		
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
			DBSFaces.encodeAttribute(xWriter, "v", pvGetPercent(xSlider));
			xWriter.startElement("div", xSlider);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.NOT_SELECTABLE);
				pvEncodeContent(xSlider, xWriter);
			xWriter.endElement("div");
			DBSFaces.encodeTooltip(pContext, xSlider, xSlider.getTooltip().toString());
			pvEncodeJS(xSlider, xWriter);
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

	private Double pvGetPercent(DBSSlider pSlider) throws IOException{
		Double xM = DBSNumber.toDouble(pSlider.getMaxValue());
		Double xV = DBSNumber.toDouble(pSlider.getValue());
		Double xFator = 0D;
		//Exibe já preenchido se não houver valor máximo
		if (xM == 0D){
			xFator = 1D; 
		//Exibe já vázio se valor atual for zero
		}else if (xV == 0D){
			xFator = 0D;
		}else{
			xFator = (xV / xM); //Calcula fator
		}
		xFator *= 100D; //Calcula percentual
		return xFator;
	}
	
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xSliderId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_slider(xSliderId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}





