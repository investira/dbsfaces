package br.com.dbsoft.ui.component.charts;

import java.io.IOException;
import java.math.BigDecimal;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chart.DBSChart;
import br.com.dbsoft.ui.component.chart.DBSChart.TYPE;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCharts.RENDERER_TYPE)
public class DBSChartsRenderer extends DBSRenderer {

	private Double 	wMinValue = null;
	private Double 	wMaxValue = null;
	
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
		

		DBSCharts xCharts = (DBSCharts) pComponent;

		//		xCharts.setWidth(xCharts.getWidth());
//		xCharts.setHeight(xCharts.getHeight());
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHARTS.MAIN + " ";
		String xChartsStyle = "width:" + xCharts.getWidth() + "px; height:" + xCharts.getHeight() + "px;";

		if (xCharts.getStyleClass()!=null){
			xClass = xClass + xCharts.getStyleClass() + " ";
		}

		pvCalcularValores(xCharts);


		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xCharts.getStyle(), null);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			encodeClientBehaviors(pContext, xCharts);
			//CONTAINER--------------------------
			xWriter.startElement("span", xCharts);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);

				//CAPTION--------------------------
				if (xCharts.getCaption() !=null){
					xWriter.startElement("span", xCharts);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CAPTION + DBSFaces.CSS.THEME.CAPTION, null);
						xWriter.write(xCharts.getCaption());
					xWriter.endElement("span");
				}
	
				//DATA--------------------------
				xWriter.startElement("span", xCharts);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.DATA, null);
					//CONTAINER--------------------------
					xWriter.startElement("svg", xCharts);
						DBSFaces.setAttribute(xWriter, "xmlns", "http://www.w3.org/2000/svg", null);
						DBSFaces.setAttribute(xWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink", null);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
						DBSFaces.setAttribute(xWriter, "style", xChartsStyle, null);
						//Filters
						pvEncodeFilters(xWriter);
						
						//CONTENT--------------------------
						xWriter.startElement("g", xCharts);
							DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
							//LEFT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LEFT, null);
							xWriter.endElement("g");
							
							//VALUE--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.VALUE, null);
								//Linhas do grid
								xWriter.startElement("g", xCharts);
									DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.GRID, null);
									pvEncodeLines(xCharts, xWriter);
								xWriter.endElement("g");
								//Gráficos--------------------------
								DBSFaces.renderChildren(pContext, xCharts);
							xWriter.endElement("g");
							
							//RIGHT--------------------------
							xWriter.startElement("g", xCharts);
								DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.RIGHT, null);
							xWriter.endElement("g");
						xWriter.endElement("g");
					xWriter.endElement("svg");
				xWriter.endElement("span");

				//FOOTER--------------------------
				if (xCharts.getFooter() !=null){
					xWriter.startElement("span", xCharts);
						DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.FOOTER, null);
						xWriter.write(xCharts.getFooter());
					xWriter.endElement("span");
				}
			xWriter.endElement("span");

			pvEncodeJS(xClientId, xWriter);
			
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_charts(xChartsId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	/**
	 * Calcula valor máximo, mínimo, posição 0 e largura das colunas
	 * @param pCharts
	 */
	private void pvCalcularValores(DBSCharts pCharts){
		BigDecimal 	xX = BigDecimal.ZERO;
		boolean	   	xFound = false;
		TYPE 		xType = null;
		Integer 	xChartIndex = 0;
		
		wMinValue = null;
		wMaxValue = null;
		
		//Loop nos componentes Chart
		for (UIComponent xObject:pCharts.getChildren()){
			if (xObject instanceof DBSChart){
				DBSChart xChart = (DBSChart) xObject;
		        xChart.setTotalValue(0D);
				xType = DBSChart.TYPE.get(xChart.getType());
				//Verifica se será exibido
				if (xChart.isRendered()){
					xChartIndex++;
					xChart.setIndex(xChartIndex);
					// quando por PIE
					if (xType == TYPE.PIE){
						//Não exibe linhas do grid
						pCharts.setShowGrid(false);
					}
					//Se não foi informado DBSResultSet
					if (DBSObject.isEmpty(xChart.getVar())
					 || DBSObject.isEmpty(xChart.getValueExpression("value"))){
						//Loop nos componentes ChartValues filhos do chart
						for (UIComponent xChild:xChart.getChildren()){
							if (xChild instanceof DBSChartValue){
								DBSChartValue xChartValue = (DBSChartValue) xChild;
								if (xChartValue.isRendered()){
									pvCalculaMaxMinAndShowLabel(pCharts, xChart, xChartValue);
									xFound = true;
								}
							}
						}
					}else{
				        int xRowCount = xChart.getRowCount();
				        xChart.setRowIndex(-1);
				        xChart.getFirst();
				        xChart.getRows(); 
						//Loop por todos os registros lidos
				        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
				        	xChart.setRowIndex(xRowIndex);
				        	//Loop no componente filho contendo as definições dos valores
							for (UIComponent xChild : xChart.getChildren()){
								if (xChild instanceof DBSChartValue){
									DBSChartValue xChartValue = (DBSChartValue) xChild;
									if (xChartValue.isRendered()){
										pvCalculaMaxMinAndShowLabel(pCharts, xChart, xChartValue);
										xFound = true;
									}
								}
							}
				        }
				        xChart.setRowIndex(-1);
					}
					//Calcula Largura e Altura Geral
					pCharts.setChartWidthHeight(xType);
	
					//ColumnScale
					xX = BigDecimal.ONE;
					if (xChart.getItensCount() > 1){
						if (xType == TYPE.LINE){
							xX = DBSNumber.divide(pCharts.getChartWidth(), //0,98 para dat espaço nas laterais
												  xChart.getItensCount() - 1); //Para ir até a borda
						}else if (xType == TYPE.BAR){
							xX = DBSNumber.divide(pCharts.getChartWidth(),
												  xChart.getItensCount());
						}
					}
					xChart.setColumnScale(xX.doubleValue());
				}else{
					xChart.setIndex(-1);
				}
			}
		}
		pCharts.setItensCount(xChartIndex); //Aproveita xCharIndex para setar quantidade de gráficos
		if (!xFound){
			pCharts.setMinValue(0D);
			pCharts.setMaxValue(0D);
			pCharts.setRowScale(0D);
			pCharts.setNumberOfGridLines(1);
		}else{
			//Largura da coluna dos valores caso seja para exibi-la
//			if (pCharts.getShowGrid() 
//			 && pCharts.getShowGridValue()){
//				pCharts.setFormatMaskWidth(DBSNumber.multiply(pCharts.getValueFormatMask().length(), 5.5D).intValue());
//			}else{
//				pCharts.setFormatMaskWidth(0);
//			}
			//Valor Mínimo
			pCharts.setMinValue(wMinValue);
			//Valor Máximo
			pCharts.setMaxValue(wMaxValue);
			
			//RowScale
			if (xType !=null){
				if (xType == TYPE.BAR
				|| xType == TYPE.LINE){
					xX = DBSNumber.subtract(wMaxValue, wMinValue);
					if (xX.doubleValue() != 0D){
						xX = DBSNumber.divide(pCharts.getChartHeight(), 
											  xX);
					}
				}else if (xType == TYPE.PIE){
					if (pCharts.getChartWidth() < pCharts.getChartHeight()){
						xX = DBSNumber.divide(pCharts.getChartWidth(),
								  			  14.5);
					}else{
						xX = DBSNumber.divide(pCharts.getChartHeight(),
					  			  			  14.5);
					}
				}
			}
			pCharts.setRowScale(xX.doubleValue());
			
			//Quantidade de linhas. 3 é a quantidade mínima de linhas
			pCharts.setNumberOfGridLines(3 + DBSNumber.divide(pCharts.getChartHeight(), 60).intValue());
		}
	}
	
	/**
	 * Setvalor mínimo e valor máximo
	 * @param pCharts
	 * @param pChartValue
	 */
	private void pvCalculaMaxMinAndShowLabel(DBSCharts pCharts,DBSChart pChart, DBSChartValue pChartValue){
		Double xValue = pChartValue.getValue();
		if (wMinValue == null 
		 || xValue < wMinValue){
			wMinValue = xValue; 
		}
		if (wMaxValue == null
		 || xValue > wMaxValue){
			wMaxValue = xValue; 
		}
		//Verifica se label foi definida e seta indicador que há label a ser exibida
		if (!pCharts.getShowLabel()){
			if (!DBSObject.isEmpty(pChartValue.getLabel())){
				pCharts.setShowLabel(true);
			}
		}
		
		pChart.setItensCount(pChart.getItensCount()+1);
		pChartValue.setIndex(pChart.getItensCount());
		pChartValue.setPreviousValue(pChart.getTotalValue());
		pChart.setTotalValue(pChart.getTotalValue() + DBSObject.getNotNull(pChartValue.getValue(),0D));
	}
	

	private void pvEncodeLines(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		if (pCharts.getCaption()!=null){
			//Linha top
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0D, 0D, pCharts.getWidth().doubleValue(), 0D);
			//Linha bottom
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0D, pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), pCharts.getWidth().doubleValue(), pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2));
		}
		//Linha base
		if (pCharts.getShowGrid()){
			pvEncodeLinhaDeValores(pCharts, pWriter);
		}
	}

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
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, null, 0D, xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 2), xPosicaoInvertida.doubleValue());
			if (pCharts.getShowGridValue()){
				xPosicaoText = xPosicao;
				//Encode do texto do valor
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(pCharts.convertYPxToValue(xPosicaoInvertida), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				DBSFaces.encodeSVGText(pCharts, pWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:end", pCharts.getWidth().doubleValue(), xPosicaoText.doubleValue(), xFormatedValue);
			}
			xPosicao += xIncremento;
		}
		if (pCharts.getMinValue() < 0){
			//Encode da linha ZERO
			xPosicaoInvertida = pCharts.getChartHeight() - pCharts.getZeroPosition() + pCharts.getPadding();
			xFormatedValue = DBSFormat.getFormattedNumber(0, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
			xPosicaoText = xPosicaoInvertida + DBSCharts.FontSize.doubleValue() / 2;
			DBSFaces.encodeSVGLine(pCharts, pWriter, DBSFaces.CSS.MODIFIER.LINE, "stroke-dasharray: 2,5; stroke-width: 0.5px;", 0D, xPosicaoInvertida.doubleValue(), pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 2), xPosicaoInvertida.doubleValue());
//			DBSFaces.encodeSVGText(pCharts, pWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:end", pCharts.getWidth().doubleValue(), xPosicaoText.doubleValue(), xFormatedValue);
		}
	}

	private void pvEncodeFilters(ResponseWriter pWriter) throws IOException{
	    StringBuilder xSB = new StringBuilder();
	    xSB.append("<filter id='blur-filter' x='-1' y='-1'>");
	    xSB.append("<feGaussianBlur in='SourceGraphic' stdDeviation='1' />");
	    xSB.append("</filter>");
	    pWriter.write(xSB.toString());
	}
}
