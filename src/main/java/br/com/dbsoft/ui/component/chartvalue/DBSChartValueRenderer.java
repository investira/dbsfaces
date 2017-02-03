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
import br.com.dbsoft.util.DBSColor.HSLA;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChartValue.RENDERER_TYPE)
public class DBSChartValueRenderer extends DBSRenderer {
	
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
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "index", xChartValue.getIndex());
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xChartValue.getStyle());
			DBSFaces.encodeAttribute(xWriter, "value", DBSNumber.toDouble(xChartValue.getValue(), 0D, Locale.US));
			DBSFaces.encodeAttribute(xWriter, "perc", DBSNumber.toDouble(xPercValue, 0D, Locale.US));
			DBSFaces.encodeAttribute(xWriter, "label", xChartValue.getLabel(), "");
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
			pvEncodeJS(xChartValue, xWriter);
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
//		wLogger.error("dbschartvaluerendered:\t" + pChartValue.getLabel() + " Valor zerado ou nulo.");
		return 0D;
	}
	
	private void pvEncodeBarAndLine(TYPE pType, DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		BigDecimal	xXText = new BigDecimal(0);
		BigDecimal	xYText = new BigDecimal(0);
		BigDecimal	xYTextTooltip = new BigDecimal(0);
		BigDecimal	xY = new BigDecimal(0);		
		BigDecimal	xX = new BigDecimal(0);
		String 		xClientId = pChartValue.getClientId(pContext);
		String 		xStroke;
		String 		xStyle;
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
		
		xStroke = "stroke:" + pChartValue.getDBSColor().toHSLA() + ";";

		//Encode BAR ---------------------------------------------------------------------------------
		if (pType == TYPE.BAR){
			Double xHeight = DBSNumber.abs(DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition() - pCharts.getPadding(), xY).doubleValue());
			xHeight = DBSNumber.round(xHeight, 4);
			//Centraliza o ponto
			Double xLineWidth = pChart.getColumnScale() * .9;
			if (xLineWidth < 1){
				xLineWidth = 1D;
			}
			xXText = DBSNumber.add(xX, xLineWidth / 2);
			xX = xXText;
			xStyle = xStroke + "stroke-width:" + xLineWidth + "px;";
			//Valore positivos acima
			if (pChartValue.getValue() > 0){
			    xStyle += "transform-origin:" + xX.intValue() + "px " + (xY.doubleValue() + xHeight) + "px;";
//				DBSFaces.encodeSVGLine(pChartValue, pWriter, xX.intValue(), xY.doubleValue(), xX.intValue(), xY.doubleValue() + xHeight,  CSS.MODIFIER.POINT, xStyle, "fill=" + pChartValue.getColor());
				StringBuilder xPath = new StringBuilder();
				xPath.append("M");
				xPath.append(xX.intValue());
				xPath.append(",");
				xPath.append(xY.intValue());
				xPath.append("l");
				xPath.append(0);
				xPath.append(",");
				xPath.append(xHeight);
				DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(), CSS.MODIFIER.POINT, xStyle, null);
			//Valores negativos
			}else{
				//inverte a posição Yx
				xYTextTooltip = DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition().doubleValue());
				xYTextTooltip = DBSNumber.add(pCharts.getPadding(), xYTextTooltip);
			    xStyle += "transform-origin:" + xX.intValue() + "px " + xYTextTooltip + "px;";
//				DBSFaces.encodeSVGLine(pChartValue, pWriter, xX.intValue(), xYTextTooltip, xX.intValue(), xYTextTooltip.doubleValue() + xHeight,  CSS.MODIFIER.POINT, xStyle, "fill=" + pChartValue.getColor());
				StringBuilder xPath = new StringBuilder();
				xPath.append("M");
				xPath.append(xX.intValue());
				xPath.append(",");
				xPath.append(xYTextTooltip);
				xPath.append("l");
				xPath.append(0);
				xPath.append(",");
				xPath.append(xHeight);
				DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(), CSS.MODIFIER.POINT, xStyle, null);
			}
		//Encode LINE - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
		}else if (pType == TYPE.LINE){
			//Salva posição do pointo
			xX = DBSNumber.trunc(xX, 0);
			xY = DBSNumber.trunc(xY, 0);
			pChartValue.setPoint(new Point2D.Double(xX.doubleValue(), xY.doubleValue()));
			//Encode do circulo
			//Artifício pois o fcirefox só funciona com valores fixos no transform-origin
			xStyle = "stroke:currentColor; color:" + pChartValue.getDBSColor().toHSLA() + ";";
			if (pChart.getShowDelta()){
				DBSFaces.encodeSVGEllipse(pChartValue, pWriter, xX.doubleValue(), xY.doubleValue(), ".2em", ".2em", CSS.MODIFIER.POINT, xStyle, "fill=white");
			}else{
				xStyle += "transform: translateX(" + xX.doubleValue() + "px) translateY(" + xY.doubleValue() + "px);";
				DBSFaces.encodeSVGUse(pChartValue, pWriter, pCharts.getClientId() + "_point", CSS.MODIFIER.POINT, xStyle, "cx=" + xX.doubleValue() + "; cy=" + xY.doubleValue());
			}
		}
		//Encode Dados
		pWriter.startElement("g", pChartValue);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.INFO);
			DBSFaces.encodeAttribute(pWriter, "fill", pChartValue.getDBSColor().toHSLA());
			//Encode do valor da linha ---------------------------------------------------------------------
			pvEncodeText(pChartValue, 
						 DBSFormat.getFormattedNumber(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue()), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()), 
						 (pCharts.getWidth().doubleValue() - pCharts.getPadding()) + "px", 
						 xYText.doubleValue() + "px", 
						 CSS.MODIFIER.VALUE + "-hide", 
						 null, 
						 null,
						 pWriter);
			//Encode label da coluna ---------------------------------------------------------------------
			if (pCharts.getShowLabel()){
				pWriter.startElement("g", pChartValue);
//					Double xHeight = (pCharts.getHeight().doubleValue() + 2);
					Double xHeight = (pCharts.getHeight().doubleValue() - pCharts.getTopHeight() - pCharts.getBottomHeight() + 2);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL + "-hide");
					 //atributos X e Y somente para informar os valores para posicionamento que será efetivamente efetuado via JS com transform:translate
					DBSFaces.encodeAttribute(pWriter, "x",  xXText.doubleValue());
					DBSFaces.encodeAttribute(pWriter, "y",  xHeight);
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

		String 			xStyle = "stroke:" + pChartValue.getDBSColor().toHSLA() + "; stroke-width: " + pCharts.getPieChartWidth() + "px;";

		Point2D 		xPoint = new Point2D.Double();
		Point2D 		xPointCenter = new Point2D.Double();

		Integer 		xPercLineWidth =  4;
		Integer			xPositionInverter = 1;
		
		Double			xStartAngle;
		Double			xEndAngle;
		Double			xPointAngle;
		Double			xPneuRaioExterno;
		Double			xPneuRaioCentro;
		
		if (xPercValue == 100){
			xPercValue = 99.99; //Artifício para evitar uma volta completa anulando a exibição de conetúdo
		}
		
		//Angulo inicial e final do arco
		xStartAngle = DBSNumber.round(xPreviousPercValue * DBSNumber.PIDiameterFactor, 4);
		xEndAngle =  DBSNumber.round(xStartAngle + (xPercValue * DBSNumber.PIDiameterFactor), 4);
		
		//Ángulo do ponto no centro do arco para servir de referencia para o label
		xPointAngle = xStartAngle + ((xEndAngle - xStartAngle) / 2);


		//Metade da largura, pois o stroke terá a a largura integral.
		xPneuRaioExterno = pChart.getPieChartRelativeRadius(pCharts) + pCharts.getPieChartWidth();
		xPneuRaioCentro = xPneuRaioExterno - (pCharts.getPieChartWidth() / 2);

		//Calcula as coordenadas do arco 
		//Ponto externo
		x1 = DBSNumber.circlePoint(xCentro, xPneuRaioCentro, xStartAngle);
		x2 = DBSNumber.circlePoint(xCentro, xPneuRaioCentro, xEndAngle);


		//Ponto no centro e na tangente do arco para servir de referencia para o label
		xPoint = DBSNumber.circlePoint(xCentro, xPneuRaioExterno, xPointAngle);

		//Ponto no centro para servir no transform-origin
		xPointCenter = DBSNumber.circlePoint(xCentro, xPneuRaioCentro, xPointAngle);
	    xStyle += "transform-origin:" + xPointCenter.getX() + "px " + xPointCenter.getY() + "px;";

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
	    xPath.append("M" + DBSNumber.round(x1.getX(),2) + "," + DBSNumber.round(x1.getY(), 2)); //Ponto inicial do arco 
		xPath.append("A" + xPneuRaioCentro + "," + xPneuRaioCentro + " 0 " + xBig + " 1 " + DBSNumber.round(x2.getX(), 2) + "," + DBSNumber.round(x2.getY(),2)); //Arco externo até o ponto final 
		DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(), CSS.MODIFIER.POINT, xStyle, null);
	    

		//Encode Dados
		if (pCharts.getShowLabel()){
			Double xAlturaUnitaria = DBSNumber.divide(pCharts.getDiameter() - pCharts.getPadding(), pCharts.getChartValueItensCount()).doubleValue();
			Double xFontSize = Math.min(xAlturaUnitaria * .8D, 20); //Tamanho será 80% da altura da linha, com o máximo 20px;
			pWriter.startElement("g", pChartValue);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.INFO + xClass);
				DBSFaces.encodeAttribute(pWriter, "globalindex", pChartValue.getGlobalIndex());
				DBSFaces.encodeAttribute(pWriter, "style", "stroke:" + pChartValue.getDBSColor().toHSLA() + "; font-size:" + xFontSize + "px");
				
				//Ponto pequeno no centro e na tangente do arco	
				DBSFaces.encodeSVGEllipse(pChartValue, pWriter, xPoint.getX(), xPoint.getY(), "2px", "2px", CSS.MODIFIER.POINT, null, "fill=" + pChartValue.getDBSColor().toHSLA());

				//Valor do percentual, label e valor ---------------------------------------------------------------------
				pvEncodePieLabel(pCharts, pChartValue, pPercValue, xPoint, xAlturaUnitaria, pWriter);
				
			pWriter.endElement("g");
		}
		
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(pChartValue, pChartValue.getPoint().getX(), pChartValue.getPoint().getY(), xClientId, pContext, pWriter);

	}
	private void pvEncodePieLabel(DBSCharts pCharts, DBSChartValue pChartValue, Double pPercValue, Point2D pPoint, Double pAlturaUnitario, ResponseWriter pWriter) throws IOException{
		StringBuilder 	xPath;

		Point2D 		xPointLabel = new Point2D.Double();
		Double			xAlturaTotal = pCharts.getDiameter() - (pAlturaUnitario * (pCharts.getChartValueItensCount() - pChartValue.getGlobalIndex()) ); 
		Double			xLegendaY = xAlturaTotal - (pAlturaUnitario / 2) + 1; //Centraliza verticalmente dentro da linha
		Double 			xLegendaX = pCharts.getCenter().getX() + (pCharts.getDiameter() / 1.9); 
		HSLA 			xFillColor = pChartValue.getDBSColor().toHSLA();

		xPointLabel.setLocation(DBSNumber.round(xLegendaX,2), DBSNumber.round(xLegendaY,2));
		

		//LINHA DE CONEXÃO
		xPath = new StringBuilder();
		xPath.append("M" + pPoint.getX() + "," + pPoint.getY());  
		xPath.append("L" + xPointLabel.getX() + "," + xPointLabel.getY());
		DBSFaces.encodeSVGPath(pChartValue, 
							   pWriter, 
							   xPath.toString(), 
							   CSS.MODIFIER.LINE, 
							   null, 
							   null);

		//TEXT
		pChartValue.setPoint(xPointLabel);
		

		//TEXT VALUE
		StringBuilder xText = new StringBuilder();
		Double xParcValueInt = DBSNumber.toInteger(pPercValue).doubleValue();
		Double xParcValueDec = DBSNumber.round(pPercValue - xParcValueInt,2);
		String xLabelPerc = DBSFormat.getFormattedNumber(xParcValueInt, 0);
		String xLabelValue = DBSFormat.numberSimplify(DBSObject.getNotNull(pChartValue.getDisplayValue(), pChartValue.getValue())).toString().trim();
		String xLabelText = "";
		if (!DBSObject.isEmpty(pChartValue.getLabel())){
			xLabelText = pChartValue.getLabel();
		}

		DBSColor xInvertColor = pChartValue.getDBSColor().invertLightness();
		pWriter.startElement("g", pChartValue);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
			DBSFaces.encodeAttribute(pWriter, "x", xPointLabel.getX());
			DBSFaces.encodeAttribute(pWriter, "y", xPointLabel.getY());
			DBSFaces.encodeAttribute(pWriter, "style", "transform: translate(" + xPointLabel.getX() + "px," + xPointLabel.getY() + "px)");
			DBSFaces.encodeAttribute(pWriter, "fill", xInvertColor.toHSLA());
			DBSFaces.encodeAttribute(pWriter, "dy", ".35em");
			//Background do percentual. Dimensão será calculada via JS para considerar o tamanho do fonte.
			xPath = new StringBuilder();
			DBSFaces.encodeSVGPath(pChartValue, pWriter, xPath.toString(), "-box", null, "fill=" + xFillColor);
			
			//Percentual
			xText = new StringBuilder();
			xText.append("<tspan>");
				xText.append(xLabelPerc);
			xText.append("</tspan>");
			xText.append("<tspan style='font-size:.5em;'>");
				xText.append(DBSString.getSubString(xParcValueDec.toString(), 2, 3) + "%");
			xText.append("</tspan>");
			pvEncodeText(pChartValue, 
						 xText.toString().trim(), 
						 null, 
						 null, 
						 "-perc", 
						 null, 
						 "fill=" + xInvertColor.toHSLA() +";dx=3em;dy=.35em", //Centraliza base do texto--base. Lembrando que dominant-baseline não funciona no IE.
						 pWriter);
			//Texto do label
			xText = new StringBuilder();
			xText.append("<tspan>");
				xText.append(xLabelText);
			xText.append("</tspan>");
			pvEncodeText(pChartValue, 
						 xText.toString().trim(), 
						 "3.5em", 
						 "0em", 
						 "-label", 
						 null, 
						 "fill=" + xFillColor, //Centraliza base do texto--base. Lembrando que dominant-baseline não funciona no IE.
						 pWriter);
			//Valor nominal
			xText = new StringBuilder();
			xText.append("<tspan dy= '1em'>");
				xText.append(xLabelValue);
			xText.append("</tspan>");
			pvEncodeText(pChartValue, 
						 xText.toString().trim(), 
						 "3.5em", 
						 "0", 
						 "-value", 
						 null, 
						 "fill=" + xFillColor, //Centraliza base do texto--base. Lembrando que dominant-baseline não funciona no IE.
						 pWriter);
		pWriter.endElement("g");
	}
	

	private void pvEncodeText(DBSChartValue pChartValue, String pText, String pX, String pY, String pStyleClass, String pStyle, String pAttrs, ResponseWriter pWriter) throws IOException{
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
			DBSFaces.encodeAttribute(pWriter, "xmlns","http://www.w3.org/1999/xhtml");
			DBSFaces.encodeAttribute(pWriter, "id", pClienteId + "_tooltip");
			DBSFaces.encodeAttribute(pWriter, "class", "-foreignobject");
			DBSFaces.encodeAttribute(pWriter, "x", pX + "px");
			DBSFaces.encodeAttribute(pWriter, "y", pY + "px");
			DBSFaces.encodeAttribute(pWriter, "width", ".5");
			DBSFaces.encodeAttribute(pWriter, "height", ".5");
			DBSFaces.encodeTooltip(pContext, pChartValue, 1, pChartValue.getTooltip(), pClienteId + "_tooltip", null);
		pWriter.endElement("foreignObject");
	}

	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartValueId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
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
			pChartValue.setDBSColor(DBSColor.fromString(DBSFaces.calcChartFillcolor(pChart.getDBSColor(), pCharts.getItensCount(), pChart.getItensCount(), pChart.getIndex(), pChartValue.getIndex())));
		}
	}
}
