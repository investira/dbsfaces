package br.com.dbsoft.ui.component.chartvalue;

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
		BigDecimal 		xX = new BigDecimal(0);
		BigDecimal 		xXText = new BigDecimal(0);
		BigDecimal		xYText = new BigDecimal(0);
		BigDecimal		xY = new BigDecimal(0);		
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
			
			//Grafico
			if (xType != null){
				//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
				xY = DBSNumber.subtract(xCharts.getChartHeight(),
										DBSNumber.multiply(xCharts.getRowScale(), 
														   DBSNumber.subtract(xChartValue.getValue(), 
																   			  xCharts.getMinValue())));
				xX = DBSNumber.multiply(xChart.getColumnScale(), xChartValue.getIndex() - 1);
				
				xY = DBSNumber.add(xY, DBSCharts.Padding);
				xX = DBSNumber.add(xX, DBSCharts.Padding);
				
				xXText = xX;
				xYText = DBSNumber.add(xY, (DBSCharts.FontSize / 2));
				//Encode bar ---------------------------------------------------------------------------------
				if (xType == TYPE.BAR){
					Double xHeight = DBSNumber.abs(DBSNumber.subtract(xCharts.getChartHeight(), xCharts.getZeroPosition() - DBSCharts.Padding, xY).doubleValue());
					//Centraliza o ponto
					Double xLineWidth = xChart.getColumnScale() * .9;
					if (xLineWidth < 2){
						xLineWidth = 2D;
					}
					xXText = DBSNumber.add(xX,xChart.getColumnScale() / 2);
					xX = DBSNumber.add(xX,
							   		   DBSNumber.divide(xChart.getColumnScale() - xLineWidth, 2));
					//Valore positivos acima
					if (xChartValue.getValue() > 0){
						DBSFaces.encodeSVGRect(xChartValue, xWriter, null, null, xX.doubleValue(), xY.doubleValue(), xHeight, xLineWidth, xChartValue.getFillColor());
					//Valore negativos
					}else{
						//inverte a posição Yx
						Double xIY = DBSNumber.subtract(xCharts.getChartHeight(), xCharts.getZeroPosition().doubleValue()).doubleValue();
						xIY +=  DBSCharts.Padding;
						DBSFaces.encodeSVGRect(xChartValue, xWriter, null, null, xX.doubleValue(), xIY, xHeight, xLineWidth, xChartValue.getFillColor());
						//Configura posição do texto para a linha do zero
						xYText = DBSNumber.add(xIY, (DBSCharts.FontSize / 2));
					}
				//Encode line - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
				}else if (xType == TYPE.LINE){
					DBSFaces.encodeSVGCircle(xChartValue, xWriter, DBSFaces.CSS.MODIFIER.POINT, null, xX.doubleValue(), xY.doubleValue(), 2D, 2D, null);
				}else if (xType == TYPE.PIE){
					pvEncodePie(xCharts, xChart, xChartValue, xWriter);
				}
				if (xType == TYPE.LINE
				 || xType == TYPE.BAR){
					//Encode do valor da linha ---------------------------------------------------------------------
					pvEncodeValor(xCharts, xChartValue, xCharts.getWidth().doubleValue(), xYText.doubleValue(), "-hide", "text-anchor:end;", xWriter);
					//Encode label da coluna ---------------------------------------------------------------------
					pvEncodeLabel(xChartValue, xXText.doubleValue(), xCharts.getHeight().doubleValue(), "-hide", "text-anchor:middle;", xWriter);
				}
			}
			//Tooltip -------------------------------------------------------------------------
			pvEncodeTooptip(xChartValue, xXText.doubleValue(), xYText.doubleValue(), xType, xClientId, pContext, xWriter);
			
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChartValue, DBSPassThruAttributes.getAttributes(Key.DIV));
			encodeClientBehaviors(pContext, xChartValue);
			pvEncodeJS(xClientId, xWriter);
		
		xWriter.endElement("g");
	}

	private void pvEncodeValor(DBSCharts pCharts, DBSChartValue pChartValue, Double pX, Double pY, String pStyleClass, String pStyle, ResponseWriter pWriter) throws IOException{
		//Encode do valor da linha ---------------------------------------------------------------------
		DBSFaces.encodeSVGText(pChartValue, 
							   pWriter,  
							   DBSFaces.CSS.MODIFIER.VALUE + pStyleClass, 
							   pStyle + "fill:" + pChartValue.getFillColor(), 
							   pX, 
							   pY, 
							   DBSFormat.getFormattedNumber(pChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, pCharts.getValueFormatMask()));
	}
	
	private void pvEncodeLabel(DBSChartValue pChartValue, Double pX, Double pY, String pStyleClass, String pStyle, ResponseWriter pWriter) throws IOException{
		//Encode label da coluna ---------------------------------------------------------------------
		if (!DBSObject.isEmpty(pChartValue.getLabel())){
			DBSFaces.encodeSVGText(pChartValue, 
								   pWriter,  
								   DBSFaces.CSS.MODIFIER.LABEL + pStyleClass, 
								   pStyle + "fill:" + pChartValue.getFillColor(), 
								   pX, 
								   pY, 
								   pChartValue.getLabel());
		}
	}
	
	private void pvEncodeTooptip(DBSChartValue pChartValue, Double pX, Double pY, TYPE pType, String pClienteId, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		String xExtraInfoStyle = "";
		pWriter.startElement("foreignObject", pChartValue);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
//			xWriter.writeAttribute("requiredExtensions", "http://www.w3.org/1999/xhtml", null);
			pWriter.writeAttribute("xmlns","http://www.w3.org/1999/xhtml", null);
			pWriter.writeAttribute("height", "1px", null);
			pWriter.writeAttribute("width", "1px", null);
			pWriter.writeAttribute("x", "0px", null);
			pWriter.writeAttribute("y", "0px", null);
			pWriter.startElement("span", pChartValue);
				pWriter.writeAttribute("id", pClienteId + "_tooltip", null);
				pWriter.writeAttribute("tooltipdelay", "200", null);
				if (pType == TYPE.BAR
				 || pType == TYPE.LINE){
					xExtraInfoStyle += "left:" + pX.intValue() + "px;";
					xExtraInfoStyle += "bottom:-" + (pY.intValue() - 5) + "px;";
					xExtraInfoStyle += "color:" + pChartValue.getFillColor() + ";";
				}
				pWriter.writeAttribute("style", xExtraInfoStyle, null);
				DBSFaces.encodeTooltip(pContext, pChartValue, pChartValue.getTooltip(), pClienteId + "_tooltip");
			pWriter.endElement("span");
		pWriter.endElement("foreignObject");
	}
	
	private void pvEncodePie(DBSCharts pCharts, DBSChart pChart, DBSChartValue pChartValue, ResponseWriter pWriter) throws IOException{
		StringBuilder xPath = new StringBuilder();
		Double xValue = DBSNumber.divide(pChartValue.getValue(), pChart.getTotalValue()).doubleValue() * 100;
		Double xPreviousValue = DBSNumber.divide(pChartValue.getPreviousValue(), pChart.getTotalValue()).doubleValue() * 100;
//		Double xValue = xChartValue.getValue();
//		Double xPreviousValue = xChartValue.getPreviousValue();
		
		//Diametro do circulo. Utiliza o menor tamanho entre a alrgura a a altura escolhada para não ultrapasar as bordas
		Double xSize;
		if (pCharts.getChartWidth().doubleValue() < pCharts.getChartHeight().doubleValue()){
			xSize = pCharts.getChartWidth().doubleValue();
		}else{
			xSize = pCharts.getChartHeight().doubleValue();
			
		}
		xSize += (DBSCharts.Padding * 2);
		//
		Double xPneuLargura = xSize / ((pCharts.getItensCount() * 2D) + 3); //Quantidades de graficos largura do Pneu
		Double xRodaRaio = xPneuLargura / 2;
		Double xArcoExterno = (xSize / 2) - xRodaRaio;
		Double xCentro = xSize / 2; //xCharts.getChartWidth() / 2;
		Double xUnit = (Math.PI *2) / 100;    
		Double xStartangle = (xPreviousValue * xUnit) - 0.001;
		Double xEndangle =  xStartangle + ((xValue * xUnit) - 0.001);
		Double xPneuRaioInterno = (xPneuLargura * (pCharts.getItensCount() - pChart.getIndex())) + xRodaRaio;
		Double xPneuRaioExterno = xPneuRaioInterno + xPneuLargura;
		Double x1 = xCentro + (xPneuRaioExterno * Math.sin(xStartangle));
		Double y1 = xCentro - (xPneuRaioExterno * Math.cos(xStartangle));
		Double x2 = xCentro + (xPneuRaioExterno * Math.sin(xEndangle));
		Double y2 = xCentro - (xPneuRaioExterno * Math.cos(xEndangle));
		
		Double x3 = xCentro + (xPneuRaioInterno * Math.sin(xEndangle));
		Double y3 = xCentro - (xPneuRaioInterno * Math.cos(xEndangle));
		Double x4 = xCentro + (xPneuRaioInterno * Math.sin(xStartangle));
		Double y4 = xCentro - (xPneuRaioInterno * Math.cos(xStartangle)); 

		//Ponto de apoi no centro do arco para servir de referencia para o label
		Double xPointCenter = xStartangle + ((xEndangle - xStartangle) / 2);
		Double xPoint = xCentro + ((xPneuRaioExterno + 1) * Math.sin(xPointCenter));
		Double yPoint = xCentro - ((xPneuRaioExterno + 1) * Math.cos(xPointCenter));
		DBSFaces.encodeSVGCircle(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, "fill:" + pChartValue.getFillColor(), xPoint, yPoint, 2D, 2D, null);

		//Ponto de apoi no centro e acima do arco  para servir de referencia para o label
		Double xLabel = xCentro + (xArcoExterno * Math.sin(xPointCenter));
		Double yLabel = xCentro - (xArcoExterno * Math.cos(xPointCenter));
		DBSFaces.encodeSVGCircle(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, null, xLabel, yLabel, 2D, 2D, null);

		
		DBSFaces.encodeSVGLine(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.LINE, "stroke:" + pChartValue.getFillColor() + "; stroke-width:1px;", xPoint, yPoint, xLabel, yLabel);
		
		String xStyle =  "text-anchor:";
		if (xLabel >= xCentro){
			xStyle += "start;";
		}else{
			xStyle += "end;";
		}
		xStyle += "dominant-baseline:";
		xStyle += "middle;";
//		if (yLabel >= xCentro){
//			xStyle += "text-before-edge;";
//		}else{
//			xStyle += "text-after-edge;";
//		}
		//Encode do valor da linha ---------------------------------------------------------------------
		pvEncodeValor(pCharts, pChartValue, xLabel, yLabel, "", xStyle, pWriter);
		//Encode label da coluna ---------------------------------------------------------------------
//		pvEncodeLabel(pChartValue, xLabel, yLabel, xStyle, pWriter);

		//Se curva por mais de 180º
		Integer xBig = 0;
	    if (xEndangle - xStartangle > Math.PI) {
	        xBig = 1;
	    }
		x1 = DBSNumber.round(x1, 2);
		x2 = DBSNumber.round(x2, 2);
		x3 = DBSNumber.round(x3, 2);
		x4 = DBSNumber.round(x4, 2);
		y1 = DBSNumber.round(y1, 2);
		y2 = DBSNumber.round(y2, 2);
		y3 = DBSNumber.round(y3, 2);
		y4 = DBSNumber.round(y4, 2);

	    xPath.append("<path d=");
		xPath.append("'M " + x1 + "," + y1 + //Ponto inicial do arco 
        " A " + xPneuRaioExterno + "," + xPneuRaioExterno + " 0 " + xBig + " 1 " + x2 + "," + y2 + //Arco externo até o ponto final 
        " L " + x3 + "," + y3 + //Linha do arco externo até o início do arco interno
        " A " + xPneuRaioInterno + "," + xPneuRaioInterno + " 0 " + xBig + " 0 " + x4 + "," + y4 + //Arco interno até o ponto incial interno
        " Z' "); //Fecha o path ligando o arco interno ao arco externo  
		xPath.append("fill='" + pChartValue.getFillColor() + "' ");
//		xPath.append("stroke='#333333' style='stroke-width: 0px;'");
		xPath.append("></path>");
		pWriter.write(xPath.toString());
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
}

