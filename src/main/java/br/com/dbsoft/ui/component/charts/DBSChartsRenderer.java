package br.com.dbsoft.ui.component.charts;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.google.gson.Gson;
import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.chart.DBSChart;
import br.com.dbsoft.ui.component.chart.IDBSChartDelta;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.div.DBSDiv;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSColor;
import br.com.dbsoft.util.DBSColor.HSLA;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCharts.RENDERER_TYPE)
public class DBSChartsRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		
		DBSCharts 		xCharts = (DBSCharts) pComponent;
		//Tipo de gráficos não informado
		if (xCharts.getType()==null){return;}
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = CSS.CHARTS.MAIN + CSS.MODIFIER.NOT_SELECTABLE;
		boolean			xPreRender = pvIsPreRender(pContext, xCharts);
		TYPE 			xType = TYPE.get(xCharts.getType());
		List<IDBSChartDelta>	xDeltaList = null;

		if (xCharts.getStyleClass()!=null){
			xClass += xCharts.getStyleClass();
		}

		//Configura altura e largura a partir dos valores enviados na chamada ajax do preRender
		if (!xPreRender){
			String xParams = pContext.getExternalContext().getRequestParameterMap().get("params");
			if (xParams != null){
				String[] xDimensions = xParams.split(",");
				xCharts.setWidth(DBSNumber.toInteger(xDimensions[0]));
				xCharts.setHeight(DBSNumber.toInteger(xDimensions[1]));
				xCharts.setFontSize(DBSNumber.toInteger(xDimensions[2]));
//				System.out.println(xCharts.getId() + "\t Reseted");
				if (xCharts.getWidth() == 0
				 || xCharts.getHeight() == 0){
					wLogger.error("Dimensão zerada");
					return;
				}
				//Inicializa valores de controle 
				pvInitializeCharts(xCharts);
			}
		}
//		System.out.println(xCharts.getId() + "\t" + xPreRender + "\t" + xCharts.getWidth() + "\t" + xCharts.getHeight() + "\t" + xCharts.getFontSize());



		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xCharts.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xCharts.getType());
			DBSFaces.encodeAttribute(xWriter, "groupid", xCharts.getGroupId());
			DBSFaces.encodeAttribute(xWriter, "prerender", xPreRender);
			if (xCharts.getShowLabel()){
				DBSFaces.encodeAttribute(xWriter, "showlabel", xCharts.getShowLabel());
			}
			DBSFaces.encodeAttribute(xWriter, "diameter", xCharts.getDiameter());
			DBSFaces.encodeAttribute(xWriter, "cx", xCharts.getCenter().getX());
			DBSFaces.encodeAttribute(xWriter, "cy", xCharts.getCenter().getY());
			//Salva largura e altura para auxiliar refresh no caso de resize
			DBSFaces.encodeAttribute(xWriter, "w", xCharts.getWidth());
			DBSFaces.encodeAttribute(xWriter, "h", xCharts.getHeight());
			DBSFaces.encodeAttribute(xWriter, "pieip", DBSCharts.PieInternalPadding);
			if (xCharts.getValueFormatMask().indexOf("%") > -1){
				DBSFaces.encodeAttribute(xWriter, "perc", "perc");
			}
			if (xType == TYPE.LINE
			 || xType == TYPE.PIE){
				if (xCharts.getShowDelta()){ //Artificio para padronizar o false como não existindo o atributo(comportamento do chrome)
					DBSFaces.encodeAttribute(xWriter, "showdelta", xCharts.getShowDelta());
					if(xCharts.getShowDeltaList()){
						DBSFaces.encodeAttribute(xWriter, "showdeltalist", xCharts.getShowDeltaList());
					}
				}
			}

			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			//Força para que o encode deste componente seja efetuado após, via chamada ajax.
			//para que a altura e largura seja cálculada via js e enviada no request ajax.
//			if (pvEncodeLater(pContext, xCharts)){
    		xWriter.startElement("div", xCharts);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.LOADING);
			xWriter.endElement("div");
			if (xPreRender){
			}else{
				xDeltaList = pvEncodeContainer(pContext, xCharts, xType, xWriter);
			}
			
			pvEncodeJS(xCharts, xDeltaList, xWriter, xPreRender);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(UIComponent pComponent, List<IDBSChartDelta> pDeltaList, ResponseWriter pWriter, boolean pPreRender) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		Gson xDeltaListJson = new Gson();
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_charts(xChartsId," + pPreRender + ", " + xDeltaListJson.toJsonTree(pDeltaList, List.class) + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
//		String xJS = "$(document).ready(function() { \n" +
//			     " var xChartId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
//			     " dbs_chart(xChartId, " + xDeltaListJson.toJsonTree(pDeltaList, List.class) + "); \n" +
//                "}); \n"; 
		
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private List<IDBSChartDelta> pvEncodeContainer(FacesContext pContext, DBSCharts pCharts, TYPE pType, ResponseWriter pWriter) throws IOException{
		encodeClientBehaviors(pContext, pCharts);
		List<IDBSChartDelta>	xDeltaList = null;
		//CONTAINER--------------------------
		pWriter.startElement("div", pCharts);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER + "-hide");

			//CAPTION--------------------------
			if (pCharts.getCaption() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.NOT_SELECTABLE);
					pWriter.write(pCharts.getCaption());
				pWriter.endElement("div");
			}

			//DATA--------------------------
			pWriter.startElement("div", pCharts);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.DATA);
				//LABELS QUANDO HOUVER MAIS DE UM GRÁFICO--------------------------
				boolean xHasLabel= pvEncodeChartCaptions(pCharts, pWriter);

				if (pType == TYPE.LINE){
					//Encode dos botões de seleção rápida do delta
//					xDeltaList = pvEncodeDeltaList(pCharts, pContext, pWriter);
				}
				//CONTAINER--------------------------
				pWriter.startElement("svg", pCharts);
					DBSFaces.encodeSVGNamespaces(pWriter);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.encodeAttribute(pWriter, "width", pCharts.getWidth());
					DBSFaces.encodeAttribute(pWriter, "height", pCharts.getHeight() - pCharts.getCaptionHeight() - pCharts.getFooterHeight());
					//Defs
					pvEncodeDefs(pCharts, pWriter);
					
					//CONTENT--------------------------
					pWriter.startElement("g", pCharts);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
						//LEFT--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LEFT);
						pWriter.endElement("g");
						
						//VALUE--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
							//Linhas do grid
							pWriter.startElement("g", pCharts);
								//ATENÇÃO:Não retirar os trim() desta class( CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim())
								DBSFaces.encodeAttribute(pWriter, "class", CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim());
								if (pCharts.getShowDelta()){ 
									DBSFaces.encodeAttribute(pWriter, "showdelta", pCharts.getShowDelta());
								}
								pvEncodeLines(pCharts, pWriter, !xHasLabel);
							pWriter.endElement("g");
							//Gráficos--------------------------
							DBSFaces.renderChildren(pContext, pCharts);
						pWriter.endElement("g");
						
						//RIGHT--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.RIGHT);
						pWriter.endElement("g");
					pWriter.endElement("g");
				pWriter.endElement("svg");
			pWriter.endElement("div");

			//FOOTER--------------------------
			if (pCharts.getFooter() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
					pWriter.write(pCharts.getFooter());
				pWriter.endElement("div");
			}
		pWriter.endElement("div");
		return xDeltaList;
	}
	
	
	/**
	 * Labels para selecionar cada um dos gráficos
	 * @param pCharts
	 * @param pWriter
	 * @return
	 * @throws IOException
	 */
	private Boolean pvEncodeChartCaptions(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		boolean xHasLabels = false;
		if (pCharts.getItensCount() > 1){
			pWriter.startElement("div", pCharts);
			DBSFaces.encodeAttribute(pWriter, "class", "-captions" + CSS.THEME.FLEX);
			for (UIComponent xObject:pCharts.getChildren()){
				if (xObject instanceof DBSChart){
					DBSChart xChart = (DBSChart) xObject;
					if (TYPE.get(pCharts.getType()) == TYPE.LINE){
						pvEncodeCaption(pCharts, xChart, pWriter);
						xHasLabels = true;
					}
				}
			}
			pWriter.endElement("div");
		}
		return xHasLabels;
	}
	
	private void pvEncodeCaption(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
		String xLabel = DBSObject.getNotEmpty(pChart.getCaption(), pChart.getId());
		DBSColor xColor = pChart.getDBSColor();
		HSLA xOriginalColor = xColor.toHSLA();
		HSLA xLightColor = xColor.invertLightness().toHSLA();
		HSLA xTransparentColor = new DBSColor(DBSColor.TYPE.HSLA, xOriginalColor.getHue(), xOriginalColor.getSaturation(), xOriginalColor.getLightness(), 0.5F).toHSLA();
		
		pWriter.startElement("div", pCharts);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.FLEX_COL);
			DBSFaces.encodeAttribute(pWriter, "chartid", pChart.getClientId());
			DBSFaces.encodeAttribute(pWriter, "style", "color: " + xLightColor + "; border-color:" + xTransparentColor + "; background-color:" + xOriginalColor);
			pWriter.startElement("div", pCharts);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION);
				DBSFaces.encodeAttribute(pWriter, "style", "color: " + xOriginalColor);
				pWriter.write(xLabel);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

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
				DBSFaces.encodeSVGLine(pCharts, 
									   pWriter, 
									   0D, 
									   0D, 
									   pCharts.getWidth().doubleValue(), 
									   0D, 
									   CSS.MODIFIER.LINE, 
									   null, 
									   null);
			}
			//Linha bottom
			DBSFaces.encodeSVGLine(pCharts, 
								   pWriter, 
								   0D, 
								   pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), 
								   pCharts.getWidth().doubleValue(), 
								   pCharts.getChartHeight().doubleValue() + (pCharts.getPadding() * 2), 
								   CSS.MODIFIER.LINE, 
								   null, 
								   null);
		}
		//Linha base
		if (TYPE.get(pCharts.getType()) == TYPE.PIE){
//			pvEncodePieBackground(pCharts, pWriter);
		}else{
			if (pCharts.getShowGrid()){
				pvEncodeLinhaDeValores(pCharts, pWriter);
			}
		}
	}
	
//	clip-path: circle(60px at center);
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
			DBSFaces.encodeSVGLine(pCharts, 
								   pWriter, 
								   pCharts.getPadding(), 
								   xPosicaoInvertida.doubleValue(), 
								   pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 1), 
								   xPosicaoInvertida.doubleValue(), 
								   CSS.MODIFIER.LINE, 
								   null, 
								   null);
			if (pCharts.getShowGridValue()
			&& !pCharts.getShowDelta()){ 
//			if (pCharts.getShowGridValue()){ 
				xPosicaoText = xPosicao;
				//Encode do texto do valor
				xValorTmp = DBSNumber.toDouble(DBSFormat.getFormattedNumber(pCharts.convertYPxToValue(xPosicaoInvertida), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
				xFormatedValue = DBSFormat.getFormattedNumber(xValorTmp, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				DBSFaces.encodeSVGText(pCharts, 
									   pWriter, 
									   pCharts.getWidth().doubleValue() -pCharts.getPadding(), 
									   xPosicaoText.doubleValue(), 
									   xFormatedValue, 
									   CSS.MODIFIER.LABEL, 
									   null, 
									   null);
			}
			xPosicao += xIncremento;
		}
		if (pCharts.getMinValue() < 0){
			//Encode da linha ZERO
			xPosicaoInvertida = pCharts.getChartHeight() - pCharts.getZeroPosition().doubleValue() + pCharts.getPadding();
			xFormatedValue = DBSFormat.getFormattedNumber(0, NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
			xPosicaoText = xPosicaoInvertida + pCharts.getFontSize() / 2;
			DBSFaces.encodeSVGLine(pCharts, 
								   pWriter, 
								   pCharts.getPadding(), 
								   xPosicaoInvertida.doubleValue(), 
								   pCharts.getChartWidth().doubleValue() + (pCharts.getPadding() * 1), 
								   xPosicaoInvertida.doubleValue(), 
								   CSS.MODIFIER.LINE, 
								   "stroke-dasharray: 2,5; stroke-width: 1px;", 
								   null);
		}
	}

	//Tag padrão onde serão inseridos as definições para posterior reutilização
	private void pvEncodeDefs(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("defs", pCharts);
//			pvEncodeDefsMarker(pCharts, pWriter);
		pWriter.endElement("defs");
	}

	/**
	 * Retorna se é uma chamada que deverá efetivamente efetuar o encode co componente ou se. 
	 * fará uma chamada ajax para que posteriormente seja efetuado o encode.<br/>
	 * 
	 * @param pContext
	 * @param pCharts
	 * @return
	 */
	private boolean pvIsPreRender(FacesContext pContext, DBSCharts pCharts){
		 if (pContext.getPartialViewContext().getRenderIds().contains(pCharts.getClientId())
		  && pContext.getExternalContext().getRequestParameterMap().get("params") != null){
			return false;
		}
		return true;
	}
	
	/**
	 * Calcula valor máximo, mínimo, posição 0 e largura das colunas
	 * @param pCharts
	 */
	private void pvInitializeCharts(DBSCharts pCharts){
		BigDecimal 	xX = BigDecimal.ZERO;
		boolean	   	xFound = false;
		TYPE 		xType = TYPE.get(pCharts.getType());
		Integer 	xChartValueItensCount = 0;
		pCharts.setMinValue(null);
		pCharts.setMaxValue(null);
		pCharts.setLabelMaxHeight(0);
		pCharts.setLabelMaxWidth(0);
		pCharts.setChartValueItensCount(0);
		List<DBSChart> xChartList = new ArrayList<DBSChart>();
		//Cria lista com os graficos filhos(DBSChart) que serão exibidos
		for (UIComponent xObject:pCharts.getChildren()){
			if (xObject instanceof DBSChart){
				DBSChart xChart = (DBSChart) xObject;
				//Verifica se será exibido
				if (xChart.isRendered()){
					xChart.setIndex(xChartList.size() + 1);
					xChartList.add(xChart);
				}else{
					xChart.setIndex(-1);
				}
			}
		}
		//Seta a qualtidade de gráficos filhos(DBSChart)
		pCharts.setItensCount(xChartList.size()); 
		
		//Loop nos componentes Chart
		for (DBSChart xChart: xChartList){
			//Zera totalizadores
	        xChart.setTotalValue(0D);
	        Integer xChartItensCount = 0;
			if (xType == TYPE.LINE
			 || xType == TYPE.PIE){
				if (xType == TYPE.PIE){
					//Não exibe linhas do grid
					pCharts.setShowGrid(false);
				}else{
					//Se possui deltalist
					if (pCharts.getDeltaList() != null
					 && pCharts.getDeltaList().size() > 0
					 && pCharts.getShowDelta()
					 && pCharts.getShowLabel()){
						pCharts.setShowDeltaList(true);
					}
				}
			}
			//Busca DBSChartValue filhos se não foi informado DBSResultSet 
			if (DBSObject.isEmpty(xChart.getVar())
			 || DBSObject.isEmpty(xChart.getValueExpression("value"))){
				//Loop nos componentes ChartValues filhos do chart
				xChartItensCount = pvInitializeChartsValues(pCharts, xChart, 1, xChartValueItensCount);
			//Com DBSResultset
			}else{
		        int xRowCount = xChart.getRowCount();
		        xChart.setRowIndex(-1);
		        xChartItensCount = xRowCount;
				//Loop por todos os registros.
//		        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
	        	for (int xRowIndex = xRowCount - 1; xRowIndex >= 0; xRowIndex--) {	
		        	xChart.setRowIndex(xRowIndex);
		        	pvInitializeChartsValues(pCharts, xChart, xRowIndex + 1, xChartValueItensCount);
		        }
		        xChart.setRowIndex(-1);
			}
			//Quantidade de valores 
	        xChart.setItensCount(xChartItensCount);
	        //Quantidade total de valores
	        xChartValueItensCount += xChartItensCount;
			//Calcula Largura e Altura Geral 
			pCharts.setChartWidthAndHeight(xType); 
			if (xChartItensCount > 0){
				xFound = true;
			}
			//ColumnScale
			xX = BigDecimal.ONE;
			if (xChart.getItensCount() > 1){
				if (xType == TYPE.LINE){
					xX = DBSNumber.divide(pCharts.getChartWidth(), 
										  xChart.getItensCount() - 1); //Para ir até a borda
				}else if (xType == TYPE.BAR){
					xX = DBSNumber.divide(pCharts.getChartWidth(),
										  xChart.getItensCount());
				}
			}
			xChart.setColumnScale(xX.doubleValue());
			if (xChart.getDBSColor() == null){
				xChart.setColor(DBSFaces.calcChartFillcolor(null, pCharts.getItensCount(), xChart.getItensCount(), xChart.getIndex(), 0));
			}
		}
		
		//Zera valores caso não existam valores nos gráficos
		if (!xFound){
			pCharts.setMinValue(0D);
			pCharts.setMaxValue(0D);
			pCharts.setRowScale(0D);
			pCharts.setNumberOfGridLines(1);
		}else{
			pCharts.setChartValueItensCount(xChartValueItensCount);
			//RowScale
			if (xType !=null){
				if (xType == TYPE.BAR
				 || xType == TYPE.LINE){
					xX = DBSNumber.subtract(pCharts.getMaxValue(), pCharts.getMinValue());
					if (xX.doubleValue() != 0D){
						xX = DBSNumber.divide(pCharts.getChartHeight(), 
											  xX);
					}
				}
			}
			pCharts.setRowScale(xX.doubleValue());
			
			//Quantidade de linhas do grid. 3 é a quantidade mínima de linhas
			pCharts.setNumberOfGridLines(3 + DBSNumber.divide(pCharts.getChartHeight(), 60).intValue());
		}
	}

	/**
	 * Configura DBSCharts a partir do conteúdo dos DBSChart e respectivos DBSChartValues
	 * @param pCharts
	 * @param pChart
	 * @param pIndex
	 * @return
	 */
	private Integer pvInitializeChartsValues(DBSCharts pCharts, DBSChart pChart, Integer pIndex, Integer pGlobalIndex){
		Integer xIndex = pIndex - 1;
		TYPE xType = TYPE.get(pCharts.getType());
		//Loop por todos os DBSChartValue de todos os DBSChart deste DBSCharts
		for (UIComponent xChild : pChart.getChildren()){
			if (xChild instanceof DBSChartValue){
				DBSChartValue xChartValue = (DBSChartValue) xChild;
				if (xChartValue.isRendered()){
					xIndex++;
					//Salva valor mínimo
					Double xValue = xChartValue.getValue();
					if (pCharts.getMinValue() == null 
					 || xValue < pCharts.getMinValue()){
						pCharts.setMinValue(xValue);
					}
					//Salva valor máximo
					if (pCharts.getMaxValue() == null
					 || xValue > pCharts.getMaxValue()){
						pCharts.setMaxValue(xValue);
					}
					//Configura valores iniciais
					xChartValue.setIndex(xIndex);
					xChartValue.setGlobalIndex(pGlobalIndex + xIndex);
					xChartValue.setPreviousValue(pChart.getTotalValue());
					if (xType == TYPE.PIE){
						pChart.setTotalValue(pChart.getTotalValue() + DBSObject.getNotNull(DBSNumber.abs(xChartValue.getValue()),0D));
					}else{
						pChart.setTotalValue(pChart.getTotalValue() + DBSObject.getNotNull(xChartValue.getValue(),0D));
					}
					//Salva valores alterados
					xChartValue.setSavedState(xChartValue.saveState(FacesContext.getCurrentInstance()));
					//Salva largura e altura máxima dos labels
//					if (xType.isMatrix()){
//						Integer xMaxWidth = pCharts.getValueFormatMask().length() -1; 
						if (xChartValue.getLabel() != null){
							Integer xMaxWidth = xChartValue.getLabel().length() -1; 
							if (pCharts.getLabelMaxWidth() == null 
							 || xMaxWidth > pCharts.getLabelMaxWidth()){
								pCharts.setLabelMaxWidth(xMaxWidth);
							}
							Integer xMaxHeight = xChartValue.getLabel().length() -1; 
							if (pCharts.getLabelMaxHeight() == null 
							 || xMaxHeight > pCharts.getLabelMaxHeight()){
								pCharts.setLabelMaxHeight(xMaxHeight);
							}
						}
//					}
				}
			}
		}
		return xIndex;
	}

	
	/**
	 * Encodo dos botões de seleção rápida do delta
	 * @param pCharts
	 * @param pChart
	 * @param pContext
	 * @param pWriter
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private List<IDBSChartDelta> pvEncodeDeltaList(DBSCharts pCharts, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		if (!pCharts.getShowDeltaList()){return null;}
		//Lista com os valores dos deltas
		List<IDBSChartDelta> xDeltaList = pCharts.getDeltaList();
		//Define largura e cor dos botões
//		float xWidth = DBSNumber.divide(pCharts.getChartWidth(), xDeltaList.size()).floatValue();
//		String xStyle = "width:" + xWidth + "px; background-color:" + new DBSColor(DBSColor.TYPE.HSLA, xColor.getHue(), xColor.getSaturation(), xColor.getLightness(), 0.3F).toHSLA() + ";";
//		String xStyle = "width:" + xWidth + "px; background-color:" + DBSFaces.calcChartFillcolor(pChart.getDBSColor(), pCharts.getItensCount(), pChart.getItensCount(), pChart.getIndex(), pChart.getItensCount(), 0.3f) + ";";
		//Facet com os botões
		DBSDiv xDeltaListContent = (DBSDiv) pCharts.getFacet("deltalist");
		//Se não informado o facet, cria contendo botões a partir da lista com os valores dos deltas e
		if (xDeltaListContent == null){
			xDeltaListContent = (DBSDiv) pContext.getApplication().createComponent(DBSDiv.COMPONENT_TYPE);
			xDeltaListContent.setId("deltalist");
			xDeltaListContent.setStyleClass("-content");
			pCharts.getFacets().put("deltalist", xDeltaListContent);
			//Adiciona botões com as opções dos deltas pré definidos
			for (IDBSChartDelta xChartDelta: xDeltaList){
				DBSButton xDeltaButton = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
				xDeltaButton.setId(xChartDelta.getId());
				xDeltaButton.setLabel(xChartDelta.getLabel());
				xDeltaButton.setIconClass(xChartDelta.getIconClass());
				xDeltaButton.setTooltip(xChartDelta.getTooltip());
				xDeltaButton.setonclick("null");
//				xDeltaButton.setStyle(xStyle);
				xDeltaListContent.getChildren().add(xDeltaButton);
				//Força que id atribuido ao botão seja efetivamente o gerado/configurado 
				xChartDelta.setId(xDeltaButton.getClientId());
			}
		}
		//Encode do foreignObject que conterá os botões
		pWriter.startElement("div", pCharts);
//			DBSFaces.encodeAttribute(pWriter, "id", pCharts.getClientId() + "_deltalist");
			DBSFaces.encodeAttribute(pWriter, "class", "-deltaList");
//			DBSFaces.encodeAttribute(pWriter, "x", pCharts.getPadding() + "px");
//			DBSFaces.encodeAttribute(pWriter, "y", ((pCharts.getPadding() * 2) + pCharts.getChartHeight()) + "px");
//			DBSFaces.encodeAttribute(pWriter, "width", pCharts.getChartWidth());
//			DBSFaces.encodeAttribute(pWriter, "height", "1.7em");
			pWriter.startElement("div", pCharts);
				DBSFaces.encodeAttribute(pWriter, "class", "-container");
				xDeltaListContent.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("div");
		return xDeltaList;
	}
}
