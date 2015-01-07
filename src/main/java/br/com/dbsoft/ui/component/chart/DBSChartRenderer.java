package br.com.dbsoft.ui.component.chart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChart.RENDERER_TYPE)
public class DBSChartRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChart xChart = (DBSChart) pComponent;
		
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHART.MAIN + " ";
		String xStyle = "width:" + xChart.getWidth() + "px; height:" + xChart.getHeight() + "px;";
		if (xChart.getStyle()!=null){
			xStyle += " " + xChart.getStyle();
		}

		if (xChart.getStyleClass()!=null){
			xClass = xClass + xChart.getStyleClass() + " ";
		}

		calcularValores(xChart);
		
		String xClientId = xChart.getClientId(pContext);
		xWriter.startElement("div", xChart);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xStyle, null);
			
			encodeClientBehaviors(pContext, xChart);
			
			xWriter.startElement("g", xChart);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
				xWriter.startElement("svg", xChart);
					DBSFaces.setAttribute(xWriter, "xmlns", "http://www.w3.org/2000/svg", null);
					DBSFaces.setAttribute(xWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink", null);
//					DBSFaces.setAttribute(xWriter, "viewBox", "0 0 widthOfContainer heightOfContainer", null);
					
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
					RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.DIV));
					encodeZeroPosition(xChart, xWriter);
					renderChildren(pContext, xChart);
				xWriter.endElement("svg");
			xWriter.endElement("g");
			pvEncodeJS(xClientId, xWriter);
		xWriter.startElement("div", xChart);
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_chart(xChartId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private void calcularValores(DBSChart pChart){
		Double xMinValue = 0D;
		Double xMaxValue = 0D;
		Integer xZeroPosition = 0;
		Integer xCount = 0;
		Integer xWhiteSpace = 0;
		for (UIComponent xChild:pChart.getChildren()){
			if (xChild instanceof DBSChartValue){
				xCount++;
				Double xValue = ((DBSChartValue) xChild).getValue();
				if (xValue < 0 
				 && xValue < xMinValue){
					xMinValue = xValue; 
				}
				if (xValue > 0 
				 && xValue > xMaxValue){
					xMaxValue = xValue; 
				}
			}
		}
		//Valor Mínimo
		pChart.setMinValue(xMinValue);
		//Valor Máximo
		pChart.setMaxValue(xMaxValue);
		if (pChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
			xZeroPosition = DBSNumber.multiply(pChart.getHeight(),
					   						   DBSNumber.divide(DBSNumber.abs(xMaxValue), 
					   								   			pChart.getTotalValue())).intValue();
			//Distribui o espaço que sobra total, entre as cada coluna
			if (xCount>1){
				xWhiteSpace = DBSNumber.divide(DBSNumber.subtract(pChart.getWidth(), 
												 				  DBSNumber.multiply(xCount, pChart.getLineWidth())),
											   xCount-1).intValue();
			}
		}
		//Posição da linha do zero
		pChart.setZeroPosition(xZeroPosition);
		//Espaço entre os valores
		pChart.setWhiteSpace(xWhiteSpace);
		
	}
	
	private void encodeZeroPosition(DBSChart pChart, ResponseWriter pWriter) throws IOException{
		if (pChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
			pWriter.startElement("rect", pChart);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.CHART.BASELINE, null);
				DBSFaces.setAttribute(pWriter, "x", 	0, null);
				DBSFaces.setAttribute(pWriter, "y", 	pChart.getZeroPosition(), null);
				DBSFaces.setAttribute(pWriter, "width", pChart.getWidth() + "px", null);
				DBSFaces.setAttribute(pWriter, "height", "0.5px", null);
				DBSFaces.setAttribute(pWriter, "stroke", "none", null);
//				DBSFaces.setAttribute(xWriter, "stroke-width", "0", null);
//				DBSFaces.setAttribute(pWriter, "rx", 	"0", null);
//				DBSFaces.setAttribute(pWriter, "ry", 	"0", null);
			pWriter.endElement("rect");
		}

	}

}
