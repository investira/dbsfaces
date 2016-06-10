package br.com.dbsoft.ui.component.charts;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chart.DBSChart;
import br.com.dbsoft.ui.component.chart.DBSChart.TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCharts.RENDERER_TYPE)
public class DBSChartsRenderer extends DBSRenderer {

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
		

		DBSCharts 		xCharts = (DBSCharts) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = CSS.CHARTS.MAIN + CSS.MODIFIER.NOT_SELECTABLE;
//		String xChartsStyle =  "width:" + xCharts.getWidth() + "px; height:" + xCharts.getHeight() + "px;";

		if (xCharts.getStyleClass()!=null){
			xClass = xClass + xCharts.getStyleClass() + " ";
		}

		//Inicializa valores de controle 
		DBSFaces.initializeChartsValues(xCharts);


		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xCharts.getStyle());
			DBSFaces.setAttribute(xWriter, "groupid", xCharts.getGroupId());
			if (xCharts.getShowLabel()){
				DBSFaces.setAttribute(xWriter, "showlabel", xCharts.getShowLabel());
			}
			DBSFaces.setAttribute(xWriter, "diameter", xCharts.getDiameter());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			encodeClientBehaviors(pContext, xCharts);
			//CONTAINER--------------------------
			xWriter.startElement("div", xCharts);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);

				//CAPTION--------------------------
				if (xCharts.getCaption() !=null){
					xWriter.startElement("div", xCharts);
						DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CAPTION + CSS.THEME.CAPTION + CSS.NOT_SELECTABLE);
						xWriter.write(xCharts.getCaption());
					xWriter.endElement("div");
				}
	
				//DATA--------------------------
				xWriter.startElement("div", xCharts);
					DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.DATA);
					//CONTAINER--------------------------
					xWriter.startElement("svg", xCharts);
						DBSFaces.setAttribute(xWriter, "xmlns", "http://www.w3.org/2000/svg");
						DBSFaces.setAttribute(xWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink");
						DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
						DBSFaces.setAttribute(xWriter, "width", xCharts.getWidth());
						DBSFaces.setAttribute(xWriter, "height", xCharts.getHeight());
						//Defs
						pvEncodeDefs(xCharts, xWriter);
						
						//CONTENT--------------------------
						xWriter.startElement("g", xCharts);
							DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
							//LABELS QUANDO HOUVER MAIS DE UM GRÁFICO--------------------------
							boolean xHasLabel= pvEncodeLabels(xCharts, xWriter);
							//LEFT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.LEFT);
							xWriter.endElement("g");
							
							//VALUE--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.VALUE);
								//Linhas do grid
								xWriter.startElement("g", xCharts);
									//ATENÇÃO:Não retirar os trim() desta class( CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim())
									DBSFaces.setAttribute(xWriter, "class", CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim());
									if (xCharts.getShowDelta()){ 
										DBSFaces.setAttribute(xWriter, "showdelta", xCharts.getShowDelta());
									}
									pvEncodeLines(xCharts, xWriter, !xHasLabel);
								xWriter.endElement("g");
								//Gráficos--------------------------
								DBSFaces.renderChildren(pContext, xCharts);
							xWriter.endElement("g");
							
							//RIGHT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.RIGHT);
							xWriter.endElement("g");
						xWriter.endElement("g");
					xWriter.endElement("svg");
				xWriter.endElement("div");

				//FOOTER--------------------------
				if (xCharts.getFooter() !=null){
					xWriter.startElement("div", xCharts);
						DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.FOOTER);
						xWriter.write(xCharts.getFooter());
					xWriter.endElement("div");
				}
			xWriter.endElement("div");

			pvEncodeJS(xClientId, xWriter);
			
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_charts(xChartsId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	/**
	 * Labels para selecionar cada um dos gráficos
	 * @param pCharts
	 * @param pWriter
	 * @return
	 * @throws IOException
	 */
	private Boolean pvEncodeLabels(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		boolean xHasLabels = false;
		if (pCharts.getItensCount() > 1){
			pWriter.startElement("g", pCharts);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
			for (UIComponent xObject:pCharts.getChildren()){
				if (xObject instanceof DBSChart){
					DBSChart xChart = (DBSChart) xObject;
					if (TYPE.get(xChart.getType()) == TYPE.LINE){
						pvEncodeLabel(pCharts, xChart, pWriter);
						xHasLabels = true;
					}
				}
			}
			pWriter.endElement("g");
		}
		return xHasLabels;
	}
	
	private void pvEncodeLabel(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
		String xLabel = DBSObject.getNotEmpty(pChart.getLabel(), pChart.getId());
		Double xWidth = DBSNumber.divide(pCharts.getWidth(), pCharts.getChildCount()).doubleValue();
		Double xX = xWidth * (pChart.getIndex() - 1);
		pWriter.startElement("g", pCharts);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
			DBSFaces.setAttribute(pWriter, "chartid", pChart.getClientId());
				DBSFaces.encodeSVGRect(pCharts, pWriter, xX.toString(), "0", xWidth.toString(), null, null, null, "fill=url(#" + pChart.getClientId() + "_linestroke); stroke=url(#" + pChart.getClientId() + "_linestroke)");
				DBSFaces.encodeSVGText(pCharts, pWriter, xX.toString(), "1.2em", xLabel, null, null, null);
		pWriter.endElement("g");
	}
	
//	/**
//	 * Espaço que conterá os botões para selação rápida dos deltas
//	 * @param pCharts
//	 * @param pChart
//	 * @param pWriter
//	 * @throws IOException
//	 */
//	private void pvEncodeDeltaList(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
//		String xLabel = DBSObject.getNotEmpty(pChart.getLabel(), pChart.getId());
//		Double xWidth = DBSNumber.divide(pCharts.getWidth(), pCharts.getChildCount()).doubleValue();
//		Double xX = xWidth * (pChart.getIndex() - 1);
//		pWriter.startElement("g", pCharts);
//			DBSFaces.setAttribute(pWriter, "class", "-deltaList", "class");
////				DBSFaces.encodeSVGRect(pCharts, pWriter, "0", pCharts.getChartHeight().toString(), pCharts.getChartWidth().toString(), "1em", null, null, "fill=url(#" + pChart.getClientId() + "_linestroke); stroke=url(#" + pChart.getClientId() + "_linestroke)");
//		pWriter.endElement("g");
//	}

	/**
	 * Linhas horizontais dos valores
	 * @param pCharts
	 * @param pWriter
	 * @param pEncodeTopLine
	 * @throws IOException
	 */
	private void pvEncodeLines(DBSCharts pCharts, ResponseWriter pWriter, boolean pEncodeTopLine) throws IOException{
		if (pCharts.getCaption()!=null){
			//Linha top
			if (pEncodeTopLine){
				DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, 0D, pCharts.getWidth().doubleValue(), 0D, CSS.MODIFIER.LINE, null, null);
			}
			//Linha bottom
			DBSFaces.encodeSVGLine(pCharts, pWriter, 0D, pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), pCharts.getWidth().doubleValue(), pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), CSS.MODIFIER.LINE, null, null);
		}
		//Linha base
		if (pCharts.getShowGrid()){
			pvEncodeLinhaDeValores(pCharts, pWriter);
		}
	}
	
	/**
	 * Linhas horizontais dos valores
	 * @param pCharts
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeLinhaDeValores(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		Double xIncremento = DBSNumber.divide(pCharts.getChartHeight(), pCharts.getNumberOfGridLines()).doubleValue();
		Double xPosicao = (xIncremento / 2);
		Double xPosicaoText;
		Double xPosicaoInvertida;
		Double xValorTmp;
		String xFormatedValue;
		for (int i=0; i < pCharts.getNumberOfGridLines(); i++){
			xPosicaoInvertida = pCharts.getChartHeight() - xPosicao + pCharts.getPadding();
			//Encode da linha do grid até o inicio do texto do valor
			DBSFaces.encodeSVGLine(pCharts, pWriter, pCharts.getPadding(), xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 1), xPosicaoInvertida.doubleValue(), CSS.MODIFIER.LINE, null, null);
			if (pCharts.getShowGridValue()
			&& !pCharts.getShowDelta()){ 
//			if (pCharts.getShowGridValue()){ 
				xPosicaoText = xPosicao;
				//Encode do texto do valor
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(pCharts.convertYPxToValue(xPosicaoInvertida), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				DBSFaces.encodeSVGText(pCharts, pWriter, pCharts.getWidth().doubleValue() -pCharts.getPadding(), xPosicaoText.doubleValue(), xFormatedValue, CSS.MODIFIER.LABEL, null, null);
			}
			xPosicao += xIncremento;
		}
		if (pCharts.getMinValue() < 0){
			//Encode da linha ZERO
			xPosicaoInvertida = pCharts.getChartHeight() - pCharts.getZeroPosition().doubleValue() + pCharts.getPadding();
			xFormatedValue = DBSFormat.getFormattedNumber(0, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
			xPosicaoText = xPosicaoInvertida + DBSCharts.FontSize.doubleValue() / 2;
			DBSFaces.encodeSVGLine(pCharts, pWriter, pCharts.getPadding(), xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 1), xPosicaoInvertida.doubleValue(), CSS.MODIFIER.LINE, "stroke-dasharray: 2,5; stroke-width: 1px;", null);
		}
	}

	//Tag padrão onde serão inseridos as definições para posterior reutilização
	private void pvEncodeDefs(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("defs", pCharts);
//			pvEncodeDefsMarker(pCharts, pWriter);
		pWriter.endElement("defs");
	}
//	private void pvEncodeDefsMarker(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
//		pWriter.startElement("g", pCharts);
//			DBSFaces.setAttribute(pWriter, "id", "svgchartmarker" , null);
//			DBSFaces.encodeSVGRect(pCharts, pWriter, 10D, 10D, "2em", "2em", null, null, "black");
//			DBSFaces.encodeSVGRect(pCharts, pWriter, 10D, 10D, "1em", "1em", null, null, "blue");
//		pWriter.endElement("g");
//	}
	
	
//	private void pvEncodeFilters(ResponseWriter pWriter) throws IOException{
//	    StringBuilder xSB = new StringBuilder();
//	    xSB.append("<filter id='blur-filter' x='-1' y='-1'>");
//	    xSB.append("<feGaussianBlur in='SourceGraphic' stdDeviation='1' />");
//	    xSB.append("</filter>");
//	    pWriter.write(xSB.toString());
//	}
}
