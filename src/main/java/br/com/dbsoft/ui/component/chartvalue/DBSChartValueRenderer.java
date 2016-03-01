package br.com.dbsoft.ui.component.chartvalue;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.math.BigDecimal;

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
	
	private static double w2PI = Math.PI * 2;
	
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
			xClass = xClass + xChartValue.getStyleClass() + " ";
		}
	
		xWriter.startElement("g", xChartValue);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "index", xChartValue.getIndex(), null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChartValue.getStyle(), null);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChartValue, DBSPassThruAttributes.getAttributes(Key.DIV));
			pvSetFillcolor(xCharts, xChart, xChartValue);
			//Grafico
			if (xType != null){
				if (xType == TYPE.LINE
				 || xType == TYPE.BAR){
					pvEncodeBarAndLine(xType, xCharts, xChart, xChartValue, pContext, xWriter);
				}else if (xType == TYPE.PIE){
					pvEncodePie(xCharts, xChart, xChartValue, pContext, xWriter);
				}
			}
			
			encodeClientBehaviors(pContext, xChartValue);
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("g");
	}

	private void pvEncodeBarAndLine(TYPE pType, DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		BigDecimal	xXText = new BigDecimal(0);
		BigDecimal	xYText = new BigDecimal(0);
		BigDecimal	xY = new BigDecimal(0);		
		BigDecimal	xX = new BigDecimal(0);
		String 		xClientId = pChartValue.getClientId(pContext);

		//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
		xY = DBSNumber.subtract(pCharts.getChartHeight(),
								DBSNumber.multiply(pCharts.getRowScale(), 
												   DBSNumber.subtract(pChartValue.getValue(), 
														   			  pCharts.getMinValue())));
		xX = DBSNumber.multiply(pChart.getColumnScale(), pChartValue.getIndex() - 1);
		
		xY = DBSNumber.add(xY, pCharts.getPadding());
		xX = DBSNumber.add(xX, pCharts.getPadding());
		
		xXText = xX;
		xYText = DBSNumber.add(xY, (DBSCharts.FontSize / 2));
		//Encode BAR ---------------------------------------------------------------------------------
		if (pType == TYPE.BAR){
			Double xHeight = DBSNumber.abs(DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition() - pCharts.getPadding(), xY).doubleValue());
			//Centraliza o ponto
			Double xLineWidth = pChart.getColumnScale() * .9;
			if (xLineWidth < 2){
				xLineWidth = 2D;
			}
			xXText = DBSNumber.add(xX,pChart.getColumnScale() / 2);
			xX = DBSNumber.add(xX,
					   		   DBSNumber.divide(pChart.getColumnScale() - xLineWidth, 2));
			//Valore positivos acima
			if (pChartValue.getValue() > 0){
				DBSFaces.encodeSVGRect(pChartValue, pWriter, null, null, xX.doubleValue(), xY.doubleValue(), xHeight, xLineWidth, pChartValue.getFillColor());
			//Valore negativos
			}else{
				//inverte a posição Yx
				Double xIY = DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition().doubleValue()).doubleValue();
				xIY +=  pCharts.getPadding();
				DBSFaces.encodeSVGRect(pChartValue, pWriter, null, null, xX.doubleValue(), xIY, xHeight, xLineWidth, pChartValue.getFillColor());
				//Configura posição do texto para a linha do zero
				xYText = DBSNumber.add(xIY, (DBSCharts.FontSize / 2));
			}
		//Encode LINE - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
		}else if (pType == TYPE.LINE){
			DBSFaces.encodeSVGCircle(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, null, xX.doubleValue(), xY.doubleValue(), 2D, 2D, null);
		}
		//Encode Dados
		pWriter.startElement("g", pChartValue);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.INFO, null);
			//Encode do valor da linha ---------------------------------------------------------------------
			pvEncodeText(pChartValue, 
						 DBSFormat.getFormattedNumber(pChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()), 
						 pCharts.getWidth().doubleValue(), 
						 xYText.doubleValue(), 
						 DBSFaces.CSS.MODIFIER.VALUE + "-hide", 
						 "text-anchor:end;", 
						 pWriter);
			//Encode label da coluna ---------------------------------------------------------------------
			pvEncodeText(pChartValue, 
						 pChartValue.getLabel(), 
						 xXText.doubleValue(), 
						 pCharts.getHeight().doubleValue(), 
						 DBSFaces.CSS.MODIFIER.LABEL + "-hide", 
						 "text-anchor:middle;", 
						 pWriter);
		pWriter.endElement("g");
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(pType, pChartValue, xXText.doubleValue(), xYText.doubleValue(), xClientId, pContext, pWriter);
	}
	
	private void pvEncodePie(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		StringBuilder 	xPath;
		String 			xClientId = pChartValue.getClientId(pContext);
		Double 			xPercValue = DBSNumber.divide(pChartValue.getValue(), pChart.getTotalValue()).doubleValue() * 100;
		Double 			xPreviousPercValue = DBSNumber.divide(pChartValue.getPreviousValue(), pChart.getTotalValue()).doubleValue() * 100;
		Point2D 		xCentro = new Point2D.Double();
		Point2D 		x1 = new Point2D.Double();
		Point2D 		x2 = new Point2D.Double();
		Point2D 		x3 = new Point2D.Double();
		Point2D 		x4 = new Point2D.Double();
		Point2D 		xLabel = new Point2D.Double();
		Point2D 		xPoint = new Point2D.Double();

		String 			xPercLabelStyle =  "text-anchor:";
		Integer 		xPercLineSize =  4;
		String 			xPerBoxStyle = "stroke:" + pChartValue.getFillColor() + "; transform: translateY(-54%) ";

		Integer 		xPneuInternalPadding = 2;
		Integer 		xLabelPadding = DBSNumber.toInteger(DBSCharts.FontSize * 1.5);
		Double			xUnit = w2PI / 100;

		Double 			xPneuLargura;
		Double 			xRodaRaio;
		Double			xArcoExterno;
		Double			xStartAngle;
		Double			xEndAngle;
		Double			xPointAngle;
		Double			xPointAngleBkp;
		Double			xPneuRaioInterno;
		Double			xPneuRaioExterno;
		Double 			xDiametro;
		
		//Diametro do circulo. Utiliza o menor tamanho entre a alrgura a a altura escolhada para não ultrapasar as bordas
		if (pCharts.getChartWidth().doubleValue() < pCharts.getChartHeight().doubleValue()){
			xDiametro = pCharts.getChartWidth().doubleValue();
		}else{
			xDiametro = pCharts.getChartHeight().doubleValue();
		}
		xDiametro = (xDiametro / 2) - pCharts.getPadding();
		
		//Centro do círculo
		xCentro.setLocation((pCharts.getChartWidth().doubleValue() / 2) + pCharts.getPadding(),
						    (pCharts.getChartHeight().doubleValue() / 2) + pCharts.getPadding());
		
		//Largura de cada gráfico
		xPneuLargura = xDiametro - xLabelPadding; //Retirna padding principal
		xPneuLargura -= (xPneuInternalPadding * (pCharts.getItensCount() - 1)); //Retina padding de cada pneu
		xPneuLargura /= (pCharts.getItensCount() + 0.5); // 0.5 = metado da roda

		//Raio da roda central
		xRodaRaio = xPneuLargura / 2;
		
		//Arco mais externos onde serão exibido dos dados
		xArcoExterno = xDiametro - (xLabelPadding / 2);
		//Angulo inicial e final do arco
		xStartAngle = (xPreviousPercValue * xUnit);
		xEndAngle =  xStartAngle + ((xPercValue * xUnit));
		//Angulo do ponto de apoi no centro do arco para servir de referencia para o label
		xPointAngle = xStartAngle + ((xEndAngle - xStartAngle) / 2);

		xPneuRaioInterno = xRodaRaio + ((xPneuLargura + xPneuInternalPadding) * (pCharts.getItensCount() - pChart.getIndex()));
		xPneuRaioExterno = xPneuRaioInterno + xPneuLargura;

		//Calcula as coordenadas do arco 
		x1.setLocation(DBSNumber.round(xCentro.getX() + (xPneuRaioExterno * Math.sin(xStartAngle)), 2), 
					   DBSNumber.round(xCentro.getY() - (xPneuRaioExterno * Math.cos(xStartAngle)), 2));

		x2.setLocation(DBSNumber.round(xCentro.getX() + (xPneuRaioExterno * Math.sin(xEndAngle)), 2), 
					   DBSNumber.round(xCentro.getY() - (xPneuRaioExterno * Math.cos(xEndAngle)), 2));
		
		x3.setLocation(DBSNumber.round(xCentro.getX() + (xPneuRaioInterno * Math.sin(xEndAngle)), 2), 
					   DBSNumber.round(xCentro.getY() - (xPneuRaioInterno * Math.cos(xEndAngle)), 2));

		x4.setLocation(DBSNumber.round( xCentro.getX() + (xPneuRaioInterno * Math.sin(xStartAngle)), 2), 
					   DBSNumber.round(xCentro.getY() - (xPneuRaioInterno * Math.cos(xStartAngle)), 2));

		//Ponto de apoi no centro e na tangente do arco para servir de referencia para o label
		xPoint.setLocation(DBSNumber.round(xCentro.getX() + ((xPneuRaioExterno + 0) * Math.sin(xPointAngle)),2),
						   DBSNumber.round(xCentro.getY() - ((xPneuRaioExterno + 0) * Math.cos(xPointAngle)),2));

		//Verifica sobreposição dos valores
		pChartValue.setPoint(null);
		xPointAngleBkp = xPointAngle;
		Integer xLoop = 0;
		boolean xOk = false;
		Double 	xIncrement = 0D;
		boolean xDireita = xPointAngle < Math.PI;//xPoint.getX() >= xCentro.getX();
		//Determina a direção do incremento para buscar a nova posição em caso de sob reposição
		if (xDireita){
			xIncrement = .05;
		}else{
			xIncrement = -.05;
		}
		while (!xOk){
			//Ponto de apoi no centro e acima do arco  para servir de referencia para o label
			xLabel.setLocation(DBSNumber.round(xCentro.getX() + (xArcoExterno * Math.sin(xPointAngle)),2),
							   DBSNumber.round(xCentro.getY() - (xArcoExterno * Math.cos(xPointAngle)),2));
			xOk = pvValidateLabelPoint(pCharts, pChart, pChartValue, xLabel, xCentro.getX());
			if (!xOk){
				xPointAngle += xIncrement;
				if (xDireita){
					//Ultrapassou o meio
					if (xPointAngle >= Math.PI){
						//Inicia a partir do meio
						xPointAngle = Math.PI;
						//Procura espaço de tras para frente
						xIncrement *= -1;
					//Ultrapassou o inicio
					}else if (xPointAngle < 0){
						//Procura espaço de tras para frente
						xIncrement *= -1;
						xPointAngle = 0D;
						if (pCharts.getRowScale() > 12){
							pCharts.setRowScale(pCharts.getRowScale() - 1);
						}else{
							break;
						}
					}
				}else{
					//Ultrapassou o meio
					if (xPointAngle <= Math.PI){
						//Inicia a partir do meio
						xPointAngle = Math.PI;
						//Procura espaço de tras para frente
						xIncrement *= -1;
					//Ultrapassou o fim
					}else if (xPointAngle > w2PI){
						//Inicia a partir do fim
						xPointAngle = w2PI;
						//Procura espaço de tras para frente
						xIncrement *= -1;
						if (pCharts.getRowScale() > 12){
							pCharts.setRowScale(pCharts.getRowScale() - 1);
						}else{
							break;
						}
					}
				}
				
//				if ((xDireita
//				 && xPointAngle >= Math.PI)
//				|| (!xDireita
//				 && xPointAngle < Math.PI)){
//					xLoop++;
//					xIncrement *= -1;
//					if (xDireita){
//						xPointAngle = 0D;
//					}else{
//						xPointAngle = w2PI;
//					}
//				}
//				if (xDireita
//				 && xPointAngle >= Math.PI){
//					xPointAngle -= Math.PI;
//					xIncrement *= -1;
//				}
//				if (!xDireita
//				 && xPointAngle < 0){
//					xPointAngle += Math.PI;
//					xIncrement *= -1;
//				}
				//Tesde de deu volta completa
//				if (xLoop > 0){
//					
//				}
//				if (xPointAngle > w2PI
//				 || xPointAngle < 0){
//					xLoop++;
//					if (xDireita){
//						xPointAngle = 0D;
////						xIncrement = .05;
//					}else{
//						xPointAngle = w2PI;
////						xIncrement = -.05;
//					}
//					break;
//				}
//			}
				//Sai caso já tenha dado um volta completa sem encontrar uma localização válida
//				if (xLoop > 1
//				&& xPointAngle < xPointAngleBkp){
//					break;
//				}
			}
		}
		//Salva pointo do label
		pChartValue.setPoint(xLabel);

		//Determina orientação horizontal
		if (xLabel.getX() >= xCentro.getX()){
			xPercLabelStyle += "start;";
		}else{
			xPercLabelStyle += "end;";
			xPercLineSize *= -1;
			xPerBoxStyle += " translateX(-100%);";
		}
		xPercLabelStyle += "dominant-baseline:";
		xPercLabelStyle += "middle;";

		//Encode PIE
		//Se curva por mais de 180º
		Integer xBig = 0;
	    if (xEndAngle - xStartAngle > Math.PI) {
	        xBig = 1;
	    }

		xPath = new StringBuilder();
	    xPath.append("<path ");
	    xPath.append("style='stroke:" + pChartValue.getFillColor() + ";' ");
	    xPath.append("d=");
	    xPath.append("'M " + x1.getX() + "," + x1.getY()); //Ponto inicial do arco 
		xPath.append(" A " + xPneuRaioExterno + "," + xPneuRaioExterno + " 0 " + xBig + " 1 " + x2.getX() + "," + x2.getY()); //Arco externo até o ponto final 
		xPath.append(" L " + x3.getX() + "," + x3.getY()); //Linha do arco externo até o início do arco interno
		xPath.append(" A " + xPneuRaioInterno + "," + xPneuRaioInterno + " 0 " + xBig + " 0 " + x4.getX() + "," + x4.getY()); //Arco interno até o ponto incial interno
		xPath.append(" Z' "); //Fecha o path ligando o arco interno ao arco externo  
		xPath.append("fill='" + pChartValue.getFillColor() + "' ");
		xPath.append("></path>");
		pWriter.write(xPath.toString());


		if (pCharts.getShowLabel()){
			//Encode Dados
			pWriter.startElement("g", pChartValue);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.INFO, null);
				
				//ENCODE LINHA
				xPath = new StringBuilder();
			    xPath.append("<path ");
			    xPath.append("class='" + DBSFaces.CSS.MODIFIER.LINE + "' ");
			    xPath.append("style='stroke:" + pChartValue.getFillColor() + "; stroke-width:1px;' ");
			    xPath.append("d=");
				xPath.append("'M " + xPoint.getX() + "," + xPoint.getY());  
				xPath.append(" L " + xLabel.getX() + "," + xLabel.getY()); 
				xPath.append(" L " + (xLabel.getX() + xPercLineSize) + "," + xLabel.getY());
				xPath.append("' ");  
				xPath.append("fill='none'");
				xPath.append("></path>");
				pWriter.write(xPath.toString());
	
				//Ponto pequeno no centro e na tangente do arco	
				DBSFaces.encodeSVGCircle(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, null, xPoint.getX(), xPoint.getY(), 2D, 2D, pChartValue.getFillColor());
				
				//Borda do percentual
				DBSFaces.encodeSVGRect(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, xPerBoxStyle, (xLabel.getX() + xPercLineSize), xLabel.getY(), null, null,3,3, "white");
//				pvEncodeText(pChartValue, DBSFormat.getFormattedNumber(xPointAngle, 2), xLabel.getX() + (xPercLineSize * 1.6), xLabel.getY(), DBSFaces.CSS.MODIFIER.VALUE, xPercLabelStyle, pWriter);
				//Valor do percentual ---------------------------------------------------------------------
				pvEncodeText(pChartValue, DBSFormat.getFormattedNumber(xPercValue, 1) + "%", xLabel.getX() + (xPercLineSize * 1.6), xLabel.getY(), DBSFaces.CSS.MODIFIER.VALUE, xPercLabelStyle, pWriter);
	
			pWriter.endElement("g");
		}
		
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(TYPE.PIE, pChartValue, xLabel.getX(), xLabel.getY(), xClientId, pContext, pWriter);

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
//									if (pChartValue.getLabel().equals("a10")){
//										System.out.println(pChartValue.getLabel() + "\t" + xChartValue.getLabel());
//									}
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

	private void pvEncodeText(DBSChartValue pChartValue, String pText, Double pX, Double pY, String pStyleClass, String pStyle, ResponseWriter pWriter) throws IOException{
		//Encode label da coluna ---------------------------------------------------------------------
		if (DBSObject.isEmpty(pText)){return;}
		DBSFaces.encodeSVGText(pChartValue, 
							   pWriter,  
							   pStyleClass, 
							   DBSObject.getNotNull(pStyle, "") + " fill:" + pChartValue.getFillColor(), 
							   pX, 
							   pY, 
							   pText);
	}

	private void pvEncodeTooptip(TYPE pType, DBSChartValue pChartValue, Double pX, Double pY, String pClienteId, FacesContext pContext, ResponseWriter pWriter) throws IOException{
			String xStyle = "";
			pWriter.startElement("foreignObject", pChartValue);
				pWriter.writeAttribute("xmlns","http://www.w3.org/1999/xhtml", null);
				pWriter.writeAttribute("height", "1px", null);
				pWriter.writeAttribute("width", "1px", null);
				pWriter.writeAttribute("x", "0px", null);
				pWriter.writeAttribute("y", "0px", null);
				pWriter.startElement("span", pChartValue);
					pWriter.writeAttribute("id", pClienteId + "_tooltip", null);
					if (pType == TYPE.PIE){
						pWriter.writeAttribute("tooltipdelay", "300", null);
					}else{
						pWriter.writeAttribute("tooltipdelay", "300", null);
					}
					xStyle += "left:" + pX.intValue() + "px;";
					xStyle += "bottom:-" + (pY.intValue() - 5) + "px;";
					pWriter.writeAttribute("style", xStyle, null);
//					DBSFaces.encodeTooltip(pContext, pChartValue, pChartValue.getValue().toString(), pClienteId + "_tooltip");
					DBSFaces.encodeTooltip(pContext, pChartValue, pChartValue.getTooltip(), pClienteId + "_tooltip");
				pWriter.endElement("span");
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
	
	private void pvSetFillcolor(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue){
		//Usa cor informada pelo usuário
		if (pChartValue.getFillColor() != null){return;}

		Float xChartFator = DBSNumber.divide(pChart.getIndex(), pCharts.getItensCount()).floatValue();
		Float xChartValueFator = DBSNumber.divide(pChartValue.getIndex(), pChart.getItensCount()).floatValue();
		Float xColorH;
		Float xColorS = DBSNumber.multiply(1, xChartValueFator).floatValue();
		Float xColorB = DBSNumber.multiply(.8, xChartValueFator).floatValue();
		if (pChart.getHue() != null){
			xColorH = pChart.getHue();
		}else{
			xColorH = DBSNumber.multiply(pChart.getHue(), xChartFator).floatValue();
		}
		xColorH += 0F;
		xColorS = 1F;
		xColorB += .2F;
		Color xColor = Color.getHSBColor(xColorH, xColorS, xColorB);
		StringBuilder xSB = new StringBuilder();
		xSB.append("rgb(");
		xSB.append(xColor.getRed());
		xSB.append(",");
		xSB.append(xColor.getGreen());
		xSB.append(",");
		xSB.append(xColor.getBlue());
		xSB.append(")");
		pChartValue.setFillColor(xSB.toString());
//		Color.HSBtoRGB(0, xColor.3Increment, .9F))
	}
}

