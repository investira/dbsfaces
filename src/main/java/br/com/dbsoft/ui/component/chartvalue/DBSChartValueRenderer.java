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
import br.com.dbsoft.ui.component.charts.DBSCharts;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSColor;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChartValue.RENDERER_TYPE)
public class DBSChartValueRenderer extends DBSRenderer {
	
//	private static double w2PI = Math.PI * 2;
//	private String wFillColor;
	
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
		String 			xClass = CSS.CHARTVALUE.MAIN;
		String 			xClientId;
		Double 			xPercValue = 0D;
		TYPE			xType;
		//Recupera DBSChart pai
		if (xChartValue.getParent() == null
		|| !(xChartValue.getParent() instanceof DBSChart)){
			return;
		}
		xChart =  (DBSChart) xChartValue.getParent();

		//Recupera DBSCharts avô
		if (xChart.getParent() == null
		|| !(xChart.getParent() instanceof DBSCharts)){
			return;
		}
		xCharts =  (DBSCharts) xChart.getParent();

		//Le tipo de gráfico
		xType =	TYPE.get(xCharts.getType());


		//Configura id a partir do index
		xChartValue.setId("i" + xChartValue.getIndex());
		xClientId = xChartValue.getClientId(pContext);

		//Configura class
		if (xChartValue.getStyleClass()!=null){
			xClass += xChartValue.getStyleClass();
		}
		
		pvSetColor(xCharts, xChart, xChartValue);
		
		xPercValue = pvCalcPercValue(xChart, xChartValue);

		xWriter.startElement("g", xChartValue);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "index", xChartValue.getIndex());
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xChartValue.getStyle());
			DBSFaces.setAttribute(xWriter, "value", DBSNumber.toDouble(xChartValue.getValue(), 0D, Locale.US));
			DBSFaces.setAttribute(xWriter, "perc", DBSNumber.toDouble(xPercValue, 0D, Locale.US));
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
		 && pChartValue.getValue() != 0D
		 && pChart.getTotalValue() != null
		 && pChart.getTotalValue() != 0D){
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
		
		xStroke = "stroke:" + pChartValue.getColor() + ";";

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
				DBSFaces.encodeSVGRect(pChartValue, pWriter, xX.intValue(), xY.doubleValue(), xLineWidth.toString(), xHeight.toString(), CSS.MODIFIER.POINT, xStroke, "fill=" + pChartValue.getColor());
			//Valores negativos
			}else{
				//inverte a posição Yx
				xYTextTooltip = DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition().doubleValue());
				xYTextTooltip =  DBSNumber.add(pCharts.getPadding(), xYTextTooltip);
				DBSFaces.encodeSVGRect(pChartValue, pWriter, xX.intValue(), xYTextTooltip.doubleValue(), xLineWidth.toString(), xHeight.toString(), CSS.MODIFIER.POINT, xStroke, "fill=" + pChartValue.getColor());
			}
		//Encode LINE - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
		}else if (pType == TYPE.LINE){
			//Salva posição do pointo
			xX = DBSNumber.trunc(xX, 0);
			xY = DBSNumber.trunc(xY, 0);
			pChartValue.setPoint(new Point2D.Double(xX.doubleValue(), xY.doubleValue()));
			//Encode do circulo
			//Artifício pois o fcirefox só funciona com valores fixos no transform-origin
			String xStyle = "stroke:currentColor; color:" + pChartValue.getColor() + ";";
			if (pChart.getShowDelta()){
				DBSFaces.encodeSVGEllipse(pChartValue, pWriter, xX.doubleValue(), xY.doubleValue(), ".2em", ".2em", CSS.MODIFIER.POINT, xStyle, "fill=white");
			}else{
				xStyle += "transform: translateX(" + xX.doubleValue() + "px) translateY(" + xY.doubleValue() + "px);";
				DBSFaces.encodeSVGUse(pChartValue, pWriter, pCharts.getClientId() + "_point", CSS.MODIFIER.POINT, xStyle, "cx=" + xX.doubleValue() + "; cy=" + xY.doubleValue());
			}
		}
		//Encode Dados
		pWriter.startElement("g", pChartValue);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.INFO);
			DBSFaces.setAttribute(pWriter, "fill", pChartValue.getColor(), null);
			//Encode do valor da linha ---------------------------------------------------------------------
			pvEncodeText(pChartValue, 
						 DBSFormat.getFormattedNumber(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue()), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()), 
						 pCharts.getWidth().doubleValue() - pCharts.getPadding(), 
						 xYText.doubleValue(), 
						 CSS.MODIFIER.VALUE + "-hide", 
						 null, 
						 null,
						 pWriter);
			//Encode label da coluna ---------------------------------------------------------------------
			if (pCharts.getShowLabel()){
				pWriter.startElement("g", pChartValue);
//					Double xHeight = (pCharts.getHeight().doubleValue() + 2);
					Double xHeight = (pCharts.getHeight().doubleValue() - pCharts.getTopHeight() - pCharts.getBottomHeight() + 2);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LABEL + "-hide");
					 //atributos X e Y somente para informar os valores para posicionamento que será efetivamente efetuado via JS com transform:translate
					DBSFaces.setAttribute(pWriter, "x",  xXText.doubleValue());
					DBSFaces.setAttribute(pWriter, "y",  xHeight);
//					DBSFaces.setAttribute(pWriter, "transform-origin", xXText.doubleValue() + "px " + xHeight + "px 0px");
//					DBSFaces.setAttribute(pWriter, "style", "transform: translate3d(" + xXText.doubleValue() + "px, " + xHeight + "px, 0px)");
					pvEncodeText(pChartValue, 
								 "<tspan class='-small'>" + pChartValue.getLabel() + "</tspan>" +
								 "<tspan class='-normal'>" + pChartValue.getLabel() + "</tspan>", 
								 null,
								 null,
								 null, 
//								 "-moz-transform-origin:" +  xXText.doubleValue() + "px " + xHeight + "px 0px;", //Artificio para resolver problema no firefox
								 null, 
								 null,
								 pWriter);
				pWriter.endElement("g");
			}
		pWriter.endElement("g");
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(pChartValue, xXText.doubleValue(), xYTextTooltip.doubleValue(), xClientId, pContext, pWriter);
	}
	

	
	private void pvEncodePie(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, Double pPercValue, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		String 			xClass;
		StringBuilder 	xPath;
		String 			xClientId = pChartValue.getClientId(pContext);
		Double 			xPercValue = pPercValue;
		Double 			xPreviousPercValue = DBSNumber.divide(pChartValue.getPreviousValue(), pChart.getTotalValue()).doubleValue() * 100;
		Point2D 		xCentro = pCharts.getCenter();
		Point2D 		x1 = new Point2D.Double();
		Point2D 		x2 = new Point2D.Double();
		Point2D 		x3 = new Point2D.Double();
		Point2D 		x4 = new Point2D.Double();

		String 			xStroke = "stroke:" + pChartValue.getColor() + ";";

		Point2D 		xPoint = new Point2D.Double();

		Integer 		xPercLineWidth =  4;
		Integer			xPositionInverter = 1;
		
		Double			xStartAngle;
		Double			xEndAngle;
		Double			xPointAngle;
		Double			xPneuRaioInterno;
		Double			xPneuRaioExterno;
		
		if (xPercValue == 100){
			xPercValue = 99.99; //Artifício para evitar uma volta completa anulando a exibição de conetúdo
		}
		
		//Angulo inicial e final do arco
		xStartAngle = xPreviousPercValue * DBSNumber.PIDiameterFactor;
		xEndAngle =  xStartAngle + (xPercValue * DBSNumber.PIDiameterFactor);
		
		//Ángulo do ponto no centro do arco para servir de referencia para o label
		xPointAngle = xStartAngle + ((xEndAngle - xStartAngle) / 2);

		xPneuRaioInterno = pChart.getPieChartRelativeRadius(pCharts);
		xPneuRaioExterno = xPneuRaioInterno + pCharts.getPieChartWidth();

		//Calcula as coordenadas do arco 
		//Ponto externo
		x1 = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xStartAngle);
		x2 = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xEndAngle);
		x3 = DBSNumber.circlePoint(xCentro, xPneuRaioInterno, xEndAngle);
		x4 = DBSNumber.circlePoint(xCentro, xPneuRaioInterno, xStartAngle);

		//Ponto no centro e na tangente do arco para servir de referencia para o label
		xPoint = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xPointAngle);

		//Determina orientação horizontal
//		if (xPoint.getX() >= xCentro.getX()){
			xClass = "-right";
//		}else{
//			xClass = "-left";
//			xPositionInverter = -1;
//		}
		//Direção da linha auxiliar do label
		xPercLineWidth *= xPositionInverter;

		//Encode PIE----------------------------------
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
		DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(), CSS.MODIFIER.POINT, xStroke, "fill=" + pChartValue.getColor());


		//Encode Dados
		if (pCharts.getShowLabel()){
			pWriter.startElement("g", pChartValue);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.INFO + xClass);
				DBSFaces.setAttribute(pWriter, "globalindex", pChartValue.getGlobalIndex());
				
				//Ponto pequeno no centro e na tangente do arco	
				DBSFaces.encodeSVGEllipse(pChartValue, pWriter, xPoint.getX(), xPoint.getY(), "2px", "2px", CSS.MODIFIER.POINT, null, "fill=" + pChartValue.getColor());

				//Valor do percentual, label e valor ---------------------------------------------------------------------
				pvEncodePieLabel(pCharts, pChartValue, pPercValue, xPoint, pWriter);
				
			pWriter.endElement("g");
		}
		
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(pChartValue, pChartValue.getPoint().getX(), pChartValue.getPoint().getY(), xClientId, pContext, pWriter);

	}
	private void pvEncodePieLabel(DBSCharts pCharts, DBSChartValue pChartValue, Double pPercValue, Point2D pPoint, ResponseWriter pWriter) throws IOException{
		StringBuilder 	xPath;

		Point2D 		xI1 = new Point2D.Double();
		Point2D 		xI2 = new Point2D.Double();
		Point2D 		xI3 = new Point2D.Double();
		Point2D 		xI4 = new Point2D.Double();
		Point2D 		xPointLabel = new Point2D.Double();
		Point2D 		xPointAnchor = new Point2D.Double();
		Double			xPneuRaioInterno;
		Double			xPneuRaioExterno;
		Integer 		xBig = 0;
		Double			xAlturaUnitaria = DBSNumber.divide(pCharts.getDiameter() - pCharts.getPadding(), pCharts.getChartValueItensCount()).doubleValue();
		Double			xAlturaTotal = pCharts.getPieChartRadius() - (xAlturaUnitaria * (pCharts.getChartValueItensCount() - pChartValue.getGlobalIndex())); 
		//Tamanho do font é 70% da altura da linha quando a altura for menos que o tamanho do fonte padrão 
		Double			xFontSize = Math.min(xAlturaUnitaria * .7, pCharts.getFontSize()); 
		Double			xLegendaWidth = (xFontSize * 5D);
		Double			xLegendaY = xAlturaTotal - (xAlturaUnitaria / 2) + 1;
//		Double			xTextX = pCharts.getCenter().getX() + 

		xPneuRaioInterno = pCharts.getPieChartRadius() + (pCharts.getPieChartWidth().intValue() / 3D) ;
		xPneuRaioExterno = xPneuRaioInterno + xLegendaWidth;
		
		//Legenda em cor(arcos)
		//Y (Altura)		
		xI1.setLocation(0, xAlturaTotal);
		xI4.setLocation(0, xI1.getY());
		xI2.setLocation(0, xAlturaTotal - xAlturaUnitaria);
		xI3.setLocation(0, xI2.getY());
		
		//X
		xI1.setLocation(getCateto(xPneuRaioExterno, xI1.getY()) + pCharts.getCenter().getX(), xI1.getY() + pCharts.getCenter().getY());
		xI2.setLocation(getCateto(xPneuRaioExterno, xI2.getY()) + pCharts.getCenter().getX(), xI2.getY() + pCharts.getCenter().getY());
		xI3.setLocation(getCateto(xPneuRaioInterno, xI3.getY()) + pCharts.getCenter().getX(), xI3.getY() + pCharts.getCenter().getY());
		xI4.setLocation(getCateto(xPneuRaioInterno, xI4.getY()) + pCharts.getCenter().getX(), xI4.getY() + pCharts.getCenter().getY());

		pChartValue.setPoint(xI4);

		xPath = new StringBuilder();
	    xPath.append("M" + xI1.getX() + "," + xI1.getY()); //Ponto inicial do arco 
		xPath.append("A" + xPneuRaioExterno + "," + xPneuRaioExterno + " 0 " + xBig + " 0 " + xI2.getX() + "," + xI2.getY()); //Arco externo até o ponto final 
		xPath.append("L" + xI3.getX() + "," + xI3.getY()); //Linha do arco externo até o início do arco interno
		xPath.append("A" + xPneuRaioInterno + "," + xPneuRaioInterno + " 0 " + xBig + " 1 " + xI4.getX() + "," + xI4.getY()); //Arco interno até o ponto incial interno
		xPath.append("Z"); //Fecha o path ligando o arco interno ao arco externo  
		DBSFaces.encodeSVGPath(pChartValue, 
							   pWriter, 
							   xPath.toString(), 
							   "-box", 
							   null, 
							   "fill=" + pChartValue.getColor());

		//LINHA DA PAUTA
		xPath = new StringBuilder();
		xPath.append("M" + xI4.getX() + "," + xI4.getY());  
		xPath.append("L" + pCharts.getChartWidth() + ", " + xI4.getY());
		DBSFaces.encodeSVGPath(pChartValue, 
							   pWriter, 
							   xPath.toString(), 
							   "-underline", 
							   "stroke:" + pChartValue.getColor() + ";", 
							   null);

		//LINHA DE CONEXÃO
		xPointAnchor.setLocation(0, xLegendaY); //Centro da linha
		xPointAnchor.setLocation(getCateto(xPneuRaioInterno, xPointAnchor.getY()) + pCharts.getCenter().getX(), xPointAnchor.getY() + pCharts.getCenter().getY());
		xPath = new StringBuilder();
		xPath.append("M" + pPoint.getX() + "," + pPoint.getY());  
		xPath.append("L" + xPointAnchor.getX() + "," + xPointAnchor.getY());
		DBSFaces.encodeSVGPath(pChartValue, 
							   pWriter, 
							   xPath.toString(), 
							   CSS.MODIFIER.LINE, 
							   "stroke:" + pChartValue.getColor() + "; stroke-width:1px; ", 
							   "fill=none");

		//TEXT
		xPointLabel.setLocation(0, xLegendaY); //Centro da linha
		xPointLabel.setLocation(getCateto(xPneuRaioInterno + (xLegendaWidth / 8), xPointLabel.getY()) + pCharts.getCenter().getX(), xPointLabel.getY() + pCharts.getCenter().getY());
		

		//TEXT VALUE
		StringBuilder xText = new StringBuilder();
		String xLabelPerc = DBSFormat.getFormattedNumber(pPercValue, 2) + "%";
		
		String xLabelValue = DBSFormat.numberSimplify(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue())).toString();
//		String xLabelValue = DBSFormat.getFormattedNumber(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue()), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask());
		String xLabelText = "";
		if (!DBSObject.isEmpty(pChartValue.getLabel())){
			xLabelText = pChartValue.getLabel();
		}

		DBSColor xInvertColor = pChartValue.getDBSColor().invertLightness();
		Double xLegendaX = (pCharts.getCenter().getX() + xPneuRaioExterno) * 1.01;
		
		xText.append("<tspan style='fill:" + xInvertColor.toHSLA() + "'>");
			xText.append(xLabelPerc);
		xText.append("</tspan>");
 		xText.append("<tspan x='" + xLegendaX + "'>");
 			xText.append(xLabelText);
		xText.append("</tspan>");
//		xText.append("<tspan dx='" + pCharts.getLabelMaxWidth() + "'>");
 		xText.append("<tspan x='" + ((xLegendaX * 1.01 ) + (pCharts.getLabelMaxWidth() * xFontSize.intValue())) + "'>");
			xText.append(xLabelValue);
		xText.append("</tspan>");
//		xText.append("<tspan x='" + (pCharts.getCenter().getX() + xPneuRaioExterno + 50) + "'>");
//		xText.append("<tspan style='text-anchor: end;'>");
// 		xText.append("<tspan x='" + (pCharts.getCenter().getX() + xPneuRaioInterno + 10) + "' dx='5em" + ""  + "'>");

		//Valor do percentual ---------------------------------------------------------------------
		pvEncodeText(pChartValue, 
					 xText.toString().trim(), 
					 xPointLabel.getX(), 
					 xPointLabel.getY(), 
					 CSS.MODIFIER.VALUE, 
					 "font-size:" + xFontSize.intValue() + "px", 
					 null,
					 pWriter);

	}
	
	private Double getCateto(Double pRaio, Double pAltura){
		Double xCateto;
		pRaio = Math.pow(pRaio, 2D);
		pAltura = Math.pow(pAltura, 2D);
		xCateto = pRaio - pAltura;
		xCateto = DBSNumber.round(Math.sqrt(xCateto),4);
		return xCateto;
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
			DBSFaces.setAttribute(pWriter, "xmlns","http://www.w3.org/1999/xhtml");
			DBSFaces.setAttribute(pWriter, "id", pClienteId + "_tooltip");
			DBSFaces.setAttribute(pWriter, "class", "-foreignobject");
			DBSFaces.setAttribute(pWriter, "x", pX + "px");
			DBSFaces.setAttribute(pWriter, "y", pY + "px");
			DBSFaces.setAttribute(pWriter, "width", ".5");
			DBSFaces.setAttribute(pWriter, "height", ".5");
			DBSFaces.encodeTooltip(pContext, pChartValue, 1, pChartValue.getTooltip(), pClienteId + "_tooltip", null);
		pWriter.endElement("foreignObject");
	}

	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartValueId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
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
	private void pvSetColor(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue){
		//Usa cor informada pelo usuário
		if (pChartValue.getColor() == null){
			pChartValue.setColor(DBSFaces.calcChartFillcolor(pChart.getDBSColor(), pCharts.getItensCount(), pChart.getItensCount(), pChart.getIndex(), pChartValue.getIndex()));
		}
	}
}

