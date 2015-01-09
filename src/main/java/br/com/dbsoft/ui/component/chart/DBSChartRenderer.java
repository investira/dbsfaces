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
	private static int wExtraLines = 6;
	
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
//		if (xChart.getStyle()!=null){
//			xStyle += " " + xChart.getStyle();
//		}

		if (xChart.getStyleClass()!=null){
			xClass = xClass + xChart.getStyleClass() + " ";
		}

		calcularValores(xChart);
		
		String xClientId = xChart.getClientId(pContext);
		xWriter.startElement("div", xChart);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChart.getStyle(), null);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);
			//CONTAINER--------------------------
			xWriter.startElement("span", xChart);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);

				//CAPTION--------------------------
				if (xChart.getCaption() !=null){
					xWriter.startElement("span", xChart);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CAPTION, null);
						xWriter.write(xChart.getCaption());
					xWriter.endElement("span");
				}
	
				//DATA--------------------------
				xWriter.startElement("span", xChart);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.DATA, null);
					//CONTAINER--------------------------
					xWriter.startElement("svg", xChart);
						DBSFaces.setAttribute(xWriter, "xmlns", "http://www.w3.org/2000/svg", null);
						DBSFaces.setAttribute(xWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink", null);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
						DBSFaces.setAttribute(xWriter, "style", xStyle, null);
						
						//CONTENT--------------------------
						xWriter.startElement("g", xChart);
							DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							//LEFT--------------------------
							xWriter.startElement("g", xChart);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LEFT, null);
							xWriter.endElement("g");
							
							//DATA--------------------------
							xWriter.startElement("g", xChart);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.VALUE, null);
								//Linhas de marcação
								encodeLines(xChart, xWriter);
		
								renderChildren(pContext, xChart);
							xWriter.endElement("g");
							
							//RIGHT--------------------------
							xWriter.startElement("g", xChart);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.RIGHT, null);
							xWriter.endElement("g");
						xWriter.endElement("g");
					xWriter.endElement("svg");
				xWriter.endElement("span");

				//FOOTER--------------------------
				if (xChart.getFooter() !=null){
					xWriter.startElement("span", xChart);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.FOOTER, null);
						xWriter.write(xChart.getFooter());
					xWriter.endElement("span");
				}
			xWriter.endElement("span");

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
												 				  DBSNumber.multiply(xCount, pChart.getLineWidth()),
												 				  2),
											   xCount-1).intValue();
			}
		}
		//Posição da linha do zero
		pChart.setZeroPosition(xZeroPosition);
		//Espaço entre os valores
		pChart.setWhiteSpace(xWhiteSpace);
		
	}
	private void encodeLines(DBSChart pChart, ResponseWriter pWriter) throws IOException{
		if (pChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
			if (pChart.getCaption()!=null){
				DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, 0, pChart.getWidth().intValue(), 0);
				DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, pChart.getHeight().intValue()-1, pChart.getWidth().intValue(), pChart.getHeight().intValue()-1);
			}
			//Linha base
//			DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, pChart.getZeroPosition(), pChart.getWidth().intValue(), pChart.getZeroPosition());
			encodeLinhaDeValores(pChart, pWriter);
		}
		
	}

	private void encodeLinhaDeValores(DBSChart pChart, ResponseWriter pWriter) throws IOException{
		Double xIncrementoValor = DBSNumber.divide(pChart.getTotalValue(), wExtraLines).doubleValue();
		Double xIncrementoPosicao = DBSNumber.divide(pChart.getHeight() - 4, wExtraLines).doubleValue();
		Double xPosicao = DBSNumber.toBigDecimal(pChart.getZeroPosition()).remainder(DBSNumber.toBigDecimal(xIncrementoPosicao)).doubleValue();
		Double xValor = xIncrementoValor * 2;
		for (int i=0; i <= wExtraLines; i++){
			DBSFaces.encodeSVGLine(pChart, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0, xPosicao.intValue(), pChart.getWidth().intValue(), xPosicao.intValue());
			DBSFaces.encodeSVGText(pChart, pWriter,  DBSFaces.CSS.MODIFIER.LABEL, null, 60, xPosicao.intValue() + 8, xValor.toString());
			xValor -= xIncrementoValor;
			xPosicao += xIncrementoPosicao;
		}
		
	}


//	private void encodeText(DBSChart pChart, ResponseWriter pWriter, String pTexto) throws IOException{
//		if (pChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
//			pWriter.startElement("text", pChart);
//				DBSFaces.setAttribute(pWriter, "x", "50%", null);
//				DBSFaces.setAttribute(pWriter, "y", "12", null);
//				pWriter.write(pTexto);
//			pWriter.endElement("text");
//		}
//
//	}
	
}
