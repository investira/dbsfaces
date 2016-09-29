package br.com.dbsoft.ui.component.charts;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chart.DBSChart;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
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
		//Tipo de gráficos não informado
		if (xCharts.getType()==null){return;}
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = CSS.CHARTS.MAIN + CSS.MODIFIER.NOT_SELECTABLE;
		boolean			xPreRender = pvIsPreRender(pContext, xCharts);

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
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xCharts.getStyle());
			DBSFaces.setAttribute(xWriter, "type", xCharts.getType());
			DBSFaces.setAttribute(xWriter, "groupid", xCharts.getGroupId());
			DBSFaces.setAttribute(xWriter, "prerender", xPreRender);
			if (xCharts.getShowLabel()){
				DBSFaces.setAttribute(xWriter, "showlabel", xCharts.getShowLabel());
			}
			DBSFaces.setAttribute(xWriter, "diameter", xCharts.getDiameter());
			DBSFaces.setAttribute(xWriter, "cx", xCharts.getCenter().getX());
			DBSFaces.setAttribute(xWriter, "cy", xCharts.getCenter().getY());
			//Salva largura e altura para auxiliar refresh no caso de resize
			DBSFaces.setAttribute(xWriter, "w", xCharts.getWidth());
			DBSFaces.setAttribute(xWriter, "h", xCharts.getHeight());
			DBSFaces.setAttribute(xWriter, "pieip", DBSCharts.PieInternalPadding);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));
			
			//Força para que o encode deste componente seja efetuado após, via chamada ajax.
			//para que a altura e largura seja cálculada via js e enviada no request ajax.
//			if (pvEncodeLater(pContext, xCharts)){
    		xWriter.startElement("div", xCharts);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.LOADING);
			xWriter.endElement("div");
			if (xPreRender){
			}else{
				pvEncodeContainer(pContext, xCharts, xWriter);
			}
			pvEncodeJS(xClientId, xWriter, xPreRender);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter, boolean pPreRender) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_charts(xChartsId," + pPreRender + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private void pvEncodeContainer(FacesContext pContext, DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		encodeClientBehaviors(pContext, pCharts);
		//CONTAINER--------------------------
		pWriter.startElement("div", pCharts);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER + "-hide");

			//CAPTION--------------------------
			if (pCharts.getCaption() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.THEME.CAPTION + CSS.NOT_SELECTABLE);
					pWriter.write(pCharts.getCaption());
				pWriter.endElement("div");
			}

			//DATA--------------------------
			pWriter.startElement("div", pCharts);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.DATA);
				//CONTAINER--------------------------
				pWriter.startElement("svg", pCharts);
					DBSFaces.setAttribute(pWriter, "xmlns", "http://www.w3.org/2000/svg");
					DBSFaces.setAttribute(pWriter, "xmlns:xlink", "http://www.w3.org/1999/xlink");
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
					DBSFaces.setAttribute(pWriter, "width", pCharts.getWidth());
					DBSFaces.setAttribute(pWriter, "height", pCharts.getHeight() - pCharts.getCaptionHeight() - pCharts.getFooterHeight());
					//Defs
					pvEncodeDefs(pCharts, pWriter);
					
					//CONTENT--------------------------
					pWriter.startElement("g", pCharts);
						DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
						//LABELS QUANDO HOUVER MAIS DE UM GRÁFICO--------------------------
						boolean xHasLabel= pvEncodeChartCaptions(pCharts, pWriter);
						//LEFT--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LEFT);
						pWriter.endElement("g");
						
						//VALUE--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
							//Linhas do grid
							pWriter.startElement("g", pCharts);
								//ATENÇÃO:Não retirar os trim() desta class( CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim())
								DBSFaces.setAttribute(pWriter, "class", CSS.CHARTS.MAIN.trim() + CSS.MODIFIER.GRID.trim());
								if (pCharts.getShowDelta()){ 
									DBSFaces.setAttribute(pWriter, "showdelta", pCharts.getShowDelta());
								}
								pvEncodeLines(pCharts, pWriter, !xHasLabel);
							pWriter.endElement("g");
							//Gráficos--------------------------
							DBSFaces.renderChildren(pContext, pCharts);
						pWriter.endElement("g");
						
						//RIGHT--------------------------
						pWriter.startElement("g", pCharts);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.RIGHT);
						pWriter.endElement("g");
					pWriter.endElement("g");
				pWriter.endElement("svg");
			pWriter.endElement("div");

			//FOOTER--------------------------
			if (pCharts.getFooter() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
					pWriter.write(pCharts.getFooter());
				pWriter.endElement("div");
			}
		pWriter.endElement("div");
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
			pWriter.startElement("g", pCharts);
			DBSFaces.setAttribute(pWriter, "class", "-captions");
			for (UIComponent xObject:pCharts.getChildren()){
				if (xObject instanceof DBSChart){
					DBSChart xChart = (DBSChart) xObject;
					if (TYPE.get(pCharts.getType()) == TYPE.LINE){
						pvEncodeCaption(pCharts, xChart, pWriter);
						xHasLabels = true;
					}
				}
			}
			pWriter.endElement("g");
		}
		return xHasLabels;
	}
	
	private void pvEncodeCaption(DBSCharts pCharts, DBSChart pChart, ResponseWriter pWriter) throws IOException{
		String xLabel = DBSObject.getNotEmpty(pChart.getCaption(), pChart.getId());
		Double xWidth = DBSNumber.divide(pCharts.getWidth(), pCharts.getChildCount()).doubleValue();
		Double xX = xWidth * (pChart.getIndex() - 1);
		pWriter.startElement("g", pCharts);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.FC);
			DBSFaces.setAttribute(pWriter, "chartid", pChart.getClientId());
				DBSFaces.encodeSVGRect(pCharts, 
									   pWriter, 
									   xX.toString(), 
									   "0", 
									   xWidth.toString(), 
									   "1.7em", 
									   null, 
									   null, 
									   "fill=url(#" + pChart.getClientId() + "_linestroke); stroke=url(#" + pChart.getClientId() + "_linestroke)");
				DBSFaces.encodeSVGRect(pCharts, 
									   pWriter, 
									   xX.toString(), 
									   "0", 
									   ".4em", 
									   "1.7em", 
									   "-legend", 
									   "stroke-wdith:0px", 
									   "fill=" + DBSFaces.calcChartFillcolor(pChart.getDBSColor(), pCharts.getItensCount(), pChart.getItensCount(), pChart.getIndex(), pChart.getItensCount()));
				DBSFaces.encodeSVGText(pCharts, 
									   pWriter, 
									   xX.toString(), 
									   "1.2em", 
									   xLabel, 
									   null, 
									   null, 
									   "transform:translateY(1.7em)");
		pWriter.endElement("g");
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
				// Exibição dos delta
				if (!pCharts.getShowDelta()
				 && xChart.getShowDelta()){
					pCharts.setShowDelta(true);
				}
				if (xType == TYPE.PIE){
					//Não exibe linhas do grid
					pCharts.setShowGrid(false);
				}else{
					//Se possui deltalist
					if (xChart.getDeltaList() != null
					 && xChart.getDeltaList().size() > 0
					 && pCharts.getShowDelta()
					 && pCharts.getShowLabel()){
						xChart.setShowDeltaList(true);
						pCharts.setShowDeltaList(true);
					}
				}
			}
			//Se não foi informado DBSResultSet
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
		        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
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
//		TYPE xType = TYPE.get(pCharts.getType());
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
					pChart.setTotalValue(pChart.getTotalValue() + DBSObject.getNotNull(xChartValue.getValue(),0D));
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

}
