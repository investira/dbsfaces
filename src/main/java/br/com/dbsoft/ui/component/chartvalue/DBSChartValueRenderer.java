package br.com.dbsoft.ui.component.chartvalue;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.chart.DBSChart;
import br.com.dbsoft.ui.component.chart.DBSChart.TYPE;
import br.com.dbsoft.ui.component.charts.DBSCharts;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChartValue.RENDERER_TYPE)
public class DBSChartValueRenderer extends DBSRenderer {
	
//	private static double w2PI = Math.PI * 2;
	private String wFillColor;
	
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChartValue 	xChartValue = (DBSChartValue) pComponent;
		DBSChart 		xChart;
		DBSCharts 		xCharts;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = DBSFaces.CSS.CHARTVALUE.MAIN + " ";
		String 			xClientId;
		Double 			xPercValue = 0D;
		TYPE			xType;
		//Recupera DBSChart pai
		if (xChartValue.getParent() == null
		|| !(xChartValue.getParent() instanceof DBSChart)){
			return;
		}
		xChart =  (DBSChart) xChartValue.getParent();
		//Le tipo do chart pai
		xType =	DBSChart.TYPE.get(xChart.getType());

		//Recupera DBSCharts avô
		if (xChart.getParent() == null
		|| !(xChart.getParent() instanceof DBSCharts)){
			return;
		}
		xCharts =  (DBSCharts) xChart.getParent();

		//Configura id a partir do index
		xChartValue.setId("i" + xChartValue.getIndex());
		xClientId = xChartValue.getClientId(pContext);

		//Configura class
		if (xChartValue.getStyleClass()!=null){
			xClass = xClass.trim()  + " " + xChartValue.getStyleClass().trim();
		}
		
		pvSetFillcolor(xCharts, xChart, xChartValue);
		
		xPercValue = pvCalcPercValue(xChart, xChartValue);

		xWriter.startElement("g", xChartValue);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "index", xChartValue.getIndex(), null);
			DBSFaces.setAttribute(xWriter, "class", xClass.trim(), null);
			DBSFaces.setAttribute(xWriter, "style", xChartValue.getStyle(), null);
			DBSFaces.setAttribute(xWriter, "value", DBSNumber.toDouble(xChartValue.getValue(), 0D, Locale.US), null);
			DBSFaces.setAttribute(xWriter, "perc", DBSNumber.toDouble(xPercValue, 0D, Locale.US), null);
			DBSFaces.setAttribute(xWriter, "label", xChartValue.getLabel(), "");
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChartValue, DBSPassThruAttributes.getAttributes(Key.DIV));
			//Grafico
			if (xType != null){
				if (xType == TYPE.LINE
				 || xType == TYPE.BAR){
					pvEncodeBarAndLine(xType, xCharts, xChart, xChartValue, pContext, xWriter);
				}else if (xType == TYPE.PIE){
					pvEncodePie(xCharts, xChart, xChartValue, xPercValue, pContext, xWriter);
				}
			}
			encodeClientBehaviors(pContext, xChartValue);
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("g");
//		xChartValue.setSavedState(xChartValue.saveState(pContext));
	}

	/**
	 * Calcula valor percentual do valor em relação ao total
	 * @param pChart
	 * @param pChartValue
	 * @return
	 */
	private Double pvCalcPercValue(DBSChart pChart, DBSChartValue pChartValue){
		if (pChartValue.getValue() != null
		 || pChartValue.getValue() != 0D
		 || pChart.getTotalValue() != null
		 || pChart.getTotalValue() != 0D){
			return DBSNumber.divide(pChartValue.getValue(), pChart.getTotalValue()).doubleValue() * 100;
		}
		return null;
	}
	
	private void pvEncodeBarAndLine(TYPE pType, DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		BigDecimal	xXText = new BigDecimal(0);
		BigDecimal	xYText = new BigDecimal(0);
		BigDecimal	xYTextTooltip = new BigDecimal(0);
		BigDecimal	xY = new BigDecimal(0);		
		BigDecimal	xX = new BigDecimal(0);
		String 		xClientId = pChartValue.getClientId(pContext);
		String 		xStroke; 
		//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
		xY = DBSNumber.subtract(pCharts.getChartHeight(),
								DBSNumber.multiply(pCharts.getRowScale(), 
												   DBSNumber.subtract(pChartValue.getValue(), 
														   			  pCharts.getMinValue())));
		xX = DBSNumber.multiply(pChart.getColumnScale(), pChartValue.getIndex() - 1);
		
		xY = DBSNumber.add(xY, pCharts.getPadding());
		xX = DBSNumber.add(xX, pCharts.getPadding());
		
		xXText = xX;
		xYText = xY;
		xYTextTooltip = xY;
		
		xStroke = "stroke:" + wFillColor + ";";

		//Encode BAR ---------------------------------------------------------------------------------
		if (pType == TYPE.BAR){
			Double xHeight = DBSNumber.abs(DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition() - pCharts.getPadding(), xY).doubleValue());
			xHeight = DBSNumber.round(xHeight, 4);
			//Centraliza o ponto
			Double xLineWidth = pChart.getColumnScale() * .9;
			if (xLineWidth < 1){
				xLineWidth = 1D;
			}
			xXText = DBSNumber.add(xX,pChart.getColumnScale() / 2);
			xX = DBSNumber.add(xX,
					   		   DBSNumber.divide(pChart.getColumnScale() - xLineWidth, 2));
			//Valore positivos acima
			if (pChartValue.getValue() > 0){
				DBSFaces.encodeSVGRect(pChartValue, pWriter, xX.intValue(), xY.doubleValue(), xLineWidth.toString(), xHeight.toString(), DBSFaces.CSS.MODIFIER.POINT, xStroke, "fill=" + wFillColor);
			//Valore negativos
			}else{
				//inverte a posição Yx
				xYTextTooltip = DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition().doubleValue());
				xYTextTooltip =  DBSNumber.add(pCharts.getPadding(), xYTextTooltip);
				DBSFaces.encodeSVGRect(pChartValue, pWriter, xX.intValue(), xYTextTooltip.doubleValue(), xLineWidth.toString(), xHeight.toString(), DBSFaces.CSS.MODIFIER.POINT, xStroke, "fill=" + wFillColor);
			}
		//Encode LINE - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
		}else if (pType == TYPE.LINE){
			//Salva posição do pointo
			xX = DBSNumber.trunc(xX, 0);
			xY = DBSNumber.trunc(xY, 0);
			pChartValue.setPoint(new Point2D.Double(xX.doubleValue(), xY.doubleValue()));
			//Encode do circulo
			//Artifício pois o fcirefox só funciona com valores fixos no transform-origin
			String xStyle = "stroke:currentColor; color:" + wFillColor + ";";
			if (pChart.getShowDelta()){
				DBSFaces.encodeSVGEllipse(pChartValue, pWriter, xX.doubleValue(), xY.doubleValue(), ".2em", ".2em", DBSFaces.CSS.MODIFIER.POINT, xStyle, "fill=white");
			}else{
				xStyle += "transform: translateX(" + xX.doubleValue() + "px) translateY(" + xY.doubleValue() + "px);";
				DBSFaces.encodeSVGUse(pChartValue, pWriter, pCharts.getClientId() + "_point", DBSFaces.CSS.MODIFIER.POINT, xStyle, "cx=" + xX.doubleValue() + "; cy=" + xY.doubleValue());
			}
		}
		//Encode Dados
		pWriter.startElement("g", pChartValue);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.INFO, null);
			DBSFaces.setAttribute(pWriter, "fill", wFillColor, null);
			//Encode do valor da linha ---------------------------------------------------------------------
			pvEncodeText(pChartValue, 
						 DBSFormat.getFormattedNumber(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue()), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()), 
						 pCharts.getWidth().doubleValue() - pCharts.getPadding(), 
						 xYText.doubleValue(), 
						 DBSFaces.CSS.MODIFIER.VALUE + "-hide", 
						 null, 
						 null,
						 pWriter);
			//Encode label da coluna ---------------------------------------------------------------------
			if (pCharts.getShowLabel()){
				pWriter.startElement("g", pChartValue);
					DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.LABEL + "-hide", null);
					Double xHeight = (pCharts.getHeight().doubleValue() -5);
					DBSFaces.setAttribute(pWriter, "transform-origin", xXText.doubleValue() + "px " + xHeight + "px 0px", null);
					pvEncodeText(pChartValue, 
								 "<tspan class='-small'>" + pChartValue.getLabel() + "</tspan>" +
								 "<tspan class='-normal'>" + pChartValue.getLabel() + "</tspan>", 
								 xXText.doubleValue(), 
								 xHeight, 
								 null, 
								 "-moz-transform-origin:" +  xXText.doubleValue() + "px " + xHeight + "px 0px;", //Artificio para resolver problema no firefox
								 null,
								 pWriter);
				pWriter.endElement("g");
			}
		pWriter.endElement("g");
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(pChartValue, xXText.doubleValue(), xYTextTooltip.doubleValue(), xClientId, pContext, pWriter);
	}
	

	
	private void pvEncodePie(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, Double pPercValue, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		StringBuilder 	xPath;
		String 			xClientId = pChartValue.getClientId(pContext);
		Double 			xPercValue = pPercValue;
		Double 			xPreviousPercValue = DBSNumber.divide(pChartValue.getPreviousValue(), pChart.getTotalValue()).doubleValue() * 100;
		Point2D 		xCentro = pCharts.getCenter();
		Point2D 		x1 = new Point2D.Double();
		Point2D 		x2 = new Point2D.Double();
		Point2D 		x3 = new Point2D.Double();
		Point2D 		x4 = new Point2D.Double();
		String 			xStroke = "stroke:" + wFillColor + ";";

		Point2D 		xPoint = new Point2D.Double();

		String 			xPercLabelStyle =  "fill:white; text-anchor:";
		Integer 		xPercLineWidth =  4;
		String 			xPerBoxStyle = "fill:" + wFillColor + ";";
		Integer			xPositionInverter = 1;
		Point2D			xPointLabel = new Point2D.Double();
		
		Double			xArcoExterno;
		Double			xStartAngle;
		Double			xEndAngle;
		Double			xPointAngle;
		Double			xPneuRaioInterno;
		Double			xPneuRaioExterno;
		Double 			xRaio;
		
		if (xPercValue == 100){
			xPercValue = 99.99;
		}
		//Diametro do circulo. Utiliza o menor tamanho entre a alrgura e a altura escolhada para não ultrapasar as bordas
		xRaio = pCharts.getPieChartRadius();
		
		//Arco mais externos onde serão exibido dos dados
		xArcoExterno = xRaio - (DBSCharts.PieLabelPadding / 2);

		//Angulo inicial e final do arco
		xStartAngle = xPreviousPercValue * DBSNumber.PIDiameterFactor;
		xEndAngle =  xStartAngle + (xPercValue * DBSNumber.PIDiameterFactor);
		
		//Angulo do ponto de apoi no centro do arco para servir de referencia para o label
		xPointAngle = xStartAngle + ((xEndAngle - xStartAngle) / 2);

		xPneuRaioInterno = pChart.getPieChartRelativeRadius(pCharts, pCharts.getPieChartWidth());
		xPneuRaioExterno = xPneuRaioInterno + pCharts.getPieChartWidth();

		//Calcula as coordenadas do arco 
		//Ponto externo
		x1 = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xStartAngle);
		x2 = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xEndAngle);
		x3 = DBSNumber.circlePoint(xCentro, xPneuRaioInterno, xEndAngle);
		x4 = DBSNumber.circlePoint(xCentro, xPneuRaioInterno, xStartAngle);

		//Ponto de apoi no centro e na tangente do arco para servir de referencia para o label
		xPoint = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xPointAngle);


		//Verifica sobreposição dos valores
		pvSetLabelPoint(pCharts, pChart, pChartValue, xCentro, xPointAngle, xArcoExterno);
		
		//Determina orientação horizontal
		if (pChartValue.getPoint().getX() >= xCentro.getX()){
			xPercLabelStyle += "start;";
		}else{
			xPercLabelStyle += "end;";
			xPositionInverter = -1;
//			xPerBoxStyle += "transform: translateX(-100%);";
//			xPerBoxStyle += "-moz-transform-origin:" + xPointLabel.getX() + "px " + xPointLabel.getY() + "px;";
		}
		//Direção da linha auxiliar do label
		xPercLineWidth *= xPositionInverter;

		//Define ponto de exibição do label;
		xPointLabel.setLocation(pChartValue.getPoint().getX() + xPercLineWidth, pChartValue.getPoint().getY());

		//Encode PIE
		//Se curva por mais de 180º
		Integer xBig = 0;
	    if (xEndAngle - xStartAngle > Math.PI) {
	        xBig = 1;
	    }

		xPath = new StringBuilder();
	    xPath.append("M" + x1.getX() + "," + x1.getY()); //Ponto inicial do arco 
		xPath.append("A" + xPneuRaioExterno + "," + xPneuRaioExterno + " 0 " + xBig + " 1 " + x2.getX() + "," + x2.getY()); //Arco externo até o ponto final 
		xPath.append("L" + x3.getX() + "," + x3.getY()); //Linha do arco externo até o início do arco interno
		xPath.append("A" + xPneuRaioInterno + "," + xPneuRaioInterno + " 0 " + xBig + " 0 " + x4.getX() + "," + x4.getY()); //Arco interno até o ponto incial interno
		xPath.append("Z"); //Fecha o path ligando o arco interno ao arco externo  
		DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(), DBSFaces.CSS.MODIFIER.POINT, xStroke, "fill=" + wFillColor);


		//Encode Dados
		if (pCharts.getShowLabel()){
			pWriter.startElement("g", pChartValue);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.INFO, null);
				
				//ENCODE LINHA
				xPath = new StringBuilder();
				xPath.append("M" + xPoint.getX() + "," + xPoint.getY());  
				xPath.append("L" + pChartValue.getPoint().getX() + "," + pChartValue.getPoint().getY()); 
				xPath.append("L" + xPointLabel.getX() + "," + xPointLabel.getY());
				DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(),DBSFaces.CSS.MODIFIER.LINE, xStroke + " stroke-width:1px; ", "fill=none");
	
				//Ponto pequeno no centro e na tangente do arco	
				DBSFaces.encodeSVGEllipse(pChartValue, pWriter, xPoint.getX(), xPoint.getY(), "2px", "2px", DBSFaces.CSS.MODIFIER.POINT, null, "fill=" + wFillColor);
				
				//Valor do percentual, label e valor ---------------------------------------------------------------------
				StringBuilder xText = new StringBuilder();
				String xLabelPerc = DBSFormat.getFormattedNumber(pPercValue, 2) + "%";
				
				String xLabelValue = DBSFormat.numberSimplify(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue())).toString();
//				String xLabelValue = DBSFormat.getFormattedNumber(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue()), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
				String xLabelSpacesX = "&#124;"; //DBSString.repeat("&#160;",7 - xLabelPerc.length());
				String xLabelSpaces1 = "";
				String xLabelText = "";
				if (!DBSObject.isEmpty(pChartValue.getLabel())){
					xLabelSpaces1 = "&#124;";
					xLabelText = pChartValue.getLabel();
				}
				//Textos a esquerda: Investe ordem do texto
				if (xPositionInverter == -1){
					xText.append(xLabelText);
					xText.append(xLabelSpaces1);
					xText.append(xLabelValue);
					xText.append(xLabelSpacesX);
					xText.append(xLabelPerc);
				//Textos a direita
				}else{
					xText.append(xLabelPerc);
					xText.append(xLabelSpacesX);
					xText.append(xLabelValue);
					xText.append(xLabelSpaces1);
					xText.append(xLabelText);
				}

				//Borda do texto:  largura será cofigurada via JS
//				DBSFaces.encodeSVGRect(pChartValue, pWriter, xPointLabel.getX(), xPointLabel.getY(), ((xText.toString().trim().length() - 10) * .7) + "em", "1.3em", 3, 3, "-box", xPerBoxStyle, null);
				DBSFaces.encodeSVGRect(pChartValue, pWriter, xPointLabel.getX(), xPointLabel.getY(), null, "1.3em", 3, 3, "-box", xPerBoxStyle, null);
				
				//Valor do percentual ---------------------------------------------------------------------
				pvEncodeText(pChartValue, 
							 xText.toString().trim(), 
							 xPointLabel.getX() + (3 * xPositionInverter), 
							 xPointLabel.getY(), 
							 DBSFaces.CSS.MODIFIER.VALUE, 
							 xPercLabelStyle, 
							 null,
							 pWriter);
			pWriter.endElement("g");
		}
		
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(pChartValue, pChartValue.getPoint().getX(), pChartValue.getPoint().getY(), xClientId, pContext, pWriter);

	}

	/**
	 * Procura pointo do label que não tenha sobreposição
	 * @param pCharts
	 * @param pChart
	 * @param pChartValue
	 * @param pCentro
	 * @param pPointAngle
	 * @param pArcoExterno
	 */
	private void pvSetLabelPoint(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, Point2D pCentro, Double pPointAngle, Double pArcoExterno){
		Point2D xLabel = new Point2D.Double();
		Double 	xIncrement = 0D;
		boolean xOk = false;
		boolean xDireita = pPointAngle < Math.PI;//xPoint.getX() >= xCentro.getX();
		//Determina a direção do incremento para buscar a nova posição em caso de sob reposição
		if (xDireita){
			xIncrement = .05;
		}else{
			xIncrement = -.05;
		}
		pChartValue.setPoint(null);
		while (!xOk){
			//Ponto de apoi no centro e acima do arco  para servir de referencia para o label
			xLabel = DBSNumber.circlePoint(pCentro, pArcoExterno, pPointAngle);
//			xLabel.setLocation(DBSNumber.round(pCentro.getX() + (pArcoExterno * Math.sin(pPointAngle)),2),
//							   DBSNumber.round(pCentro.getY() - (pArcoExterno * Math.cos(pPointAngle)),2));
			xOk = pvValidateLabelPoint(pCharts, pChart, pChartValue, xLabel, pCentro.getX());
			if (!xOk){
				pPointAngle += xIncrement;
				if (xDireita){
					//Ultrapassou o meio
					if (pPointAngle >= Math.PI){
						//Inicia a partir do meio
						pPointAngle = Math.PI;
						//Procura espaço de tras para frente
						xIncrement *= -1;
					//Ultrapassou o inicio
					}else if (pPointAngle < 0){
						//Procura espaço de tras para frente
						xIncrement *= -1;
						pPointAngle = 0D;
						if (pCharts.getRowScale() > 12){
							pCharts.setRowScale(pCharts.getRowScale() - 1);
						}else{
							break;
						}
					}
				}else{
					//Ultrapassou o meio
					if (pPointAngle <= Math.PI){
						//Inicia a partir do meio
						pPointAngle = Math.PI;
						//Procura espaço de tras para frente
						xIncrement *= -1;
					//Ultrapassou o fim
					}else if (pPointAngle > DBSNumber.PIDiameter){
						//Inicia a partir do fim
						pPointAngle = DBSNumber.PIDiameter;
						//Procura espaço de tras para frente
						xIncrement *= -1;
						if (pCharts.getRowScale() > 12){
							pCharts.setRowScale(pCharts.getRowScale() - 1);
						}else{
							break;
						}
					}
				}
			}
		}
		//Salva pointo do label
		pChartValue.setPoint(xLabel);
	}
	/**
	 * Verifica a ha sobreposição em outros pontos
	 * @param pCharts
	 * @param pChart
	 * @param pChartValue
	 * @param pPoint
	 * @param pMiddle
	 * @return
	 */
	private boolean pvValidateLabelPoint(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, Point2D pPoint, Double pMiddle){
		for (UIComponent xObject:pCharts.getChildren()){
			if (xObject instanceof DBSChart){
				DBSChart xChart = (DBSChart) xObject;
				if (xChart.getIndex() > pChart.getIndex()){
					return true;
				}
				//Verifica se será exibido
				if (xChart.isRendered()){
					//Se não foi informado DBSResultSet
					if (DBSObject.isEmpty(xChart.getVar())
					 || DBSObject.isEmpty(xChart.getValueExpression("value"))){
						//Loop nos componentes ChartValues filhos do chart
						for (UIComponent xChild:xChart.getChildren()){
							if (xChild instanceof DBSChartValue){
								DBSChartValue xChartValue = (DBSChartValue) xChild;
								if (pChart == xChart 
								 && xChartValue.getIndex() > pChartValue.getIndex()){
									return true;
								}
								if (xChartValue.isRendered()){
									boolean xOk = pvVerifyLabelPoint(xChartValue.getPoint(), pPoint, pMiddle, pCharts.getRowScale());
									if (!xOk){
										return xOk;
									}
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
									if (xChartValue.getIndex() > pChartValue.getIndex()){
										return true;
									}
									if (xChartValue.isRendered()){
										boolean xOk = pvVerifyLabelPoint(xChartValue.getPoint(), pPoint, pMiddle, pChart.getColumnScale());
										if (!xOk){
											return xOk;
										}
									}
								}
							}
				        }
				        xChart.setRowIndex(-1);
					}
				}else{
					 xChart.setIndex(-1);
				}
			}
		}
		return true;
	}
	
	/**
	 * Verifica se a distância entre os pontos é suficiente
	 * @param pOtherChartValuePoint
	 * @param pChartValuePoint
	 * @param pMiddle
	 * @param pRowScale
	 * @return
	 */
	private boolean pvVerifyLabelPoint(Point2D pOtherChartValuePoint, Point2D pChartValuePoint, Double pMiddle, Double pRowScale){
		if (pOtherChartValuePoint == null){return true;}
		boolean xOtherDireita = pOtherChartValuePoint.getX() >= pMiddle;
		boolean xDireita = pChartValuePoint.getX() >= pMiddle;
		//Se não estiverem do mesmo lado
		if (xOtherDireita != xDireita){
			return true;
		}
		Double xDif = Math.abs(pChartValuePoint.getY() - pOtherChartValuePoint.getY());
		return (Math.abs(xDif) > pRowScale);
	}

	private void pvEncodeText(DBSChartValue pChartValue, String pText, Double pX, Double pY, String pStyleClass, String pStyle, String pAttrs, ResponseWriter pWriter) throws IOException{
		//Encode label da coluna ---------------------------------------------------------------------
		if (DBSObject.isEmpty(pText)){return;}
		DBSFaces.encodeSVGText(pChartValue, 
							   pWriter,  
							   pX, 
							   pY,
							   pText,
							   pStyleClass, 
							   DBSObject.getNotNull(pStyle, ""),
							   pAttrs);
	}

	private void pvEncodeTooptip(DBSChartValue pChartValue, Double pX, Double pY, String pClienteId, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		if (DBSObject.isEmpty(pChartValue.getTooltip())){return;}
		pWriter.startElement("foreignObject", pChartValue);
			pWriter.writeAttribute("xmlns","http://www.w3.org/1999/xhtml", null);
			pWriter.writeAttribute("id", pClienteId + "_tooltip", null);
			DBSFaces.setAttribute(pWriter, "class", "-foreignobject", null);
			DBSFaces.setAttribute(pWriter, "x", pX + "px", null);
			DBSFaces.setAttribute(pWriter, "y", pY + "px", null);
			DBSFaces.setAttribute(pWriter, "width", ".5", null);
			DBSFaces.setAttribute(pWriter, "height", ".5", null);
			DBSFaces.encodeTooltip(pContext, pChartValue, 1, pChartValue.getTooltip(), pClienteId + "_tooltip", null);
		pWriter.endElement("foreignObject");
	}

	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartValueId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_chartValue(xChartValueId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	/**
	 * Determina a cor que será utilizada
	 * @param pCharts
	 * @param pChart
	 * @param pChartValue
	 */
	private void pvSetFillcolor(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue){
		//Usa cor informada pelo usuário
		if (pChartValue.getFillColor() != null){
			wFillColor = pChartValue.getFillColor(); 
			return;
		}
		//Calcula próxima cor
		wFillColor = DBSFaces.calcChartFillcolor(pChart.getColorHue(), pChart.getColorBrightness(), pCharts.getItensCount(), pChart.getItensCount(), pChart.getIndex(), pChartValue.getIndex());
	}
}

