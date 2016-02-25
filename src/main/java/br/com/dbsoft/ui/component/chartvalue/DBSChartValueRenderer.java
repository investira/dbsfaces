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
		BigDecimal	xX = new BigDecimal(0);
		BigDecimal	xXText = new BigDecimal(0);
		BigDecimal	xYText = new BigDecimal(0);
		BigDecimal	xY = new BigDecimal(0);		
		String 		xClientId = pChartValue.getClientId(pContext);

		//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
		xY = DBSNumber.subtract(pCharts.getChartHeight(),
								DBSNumber.multiply(pCharts.getRowScale(), 
												   DBSNumber.subtract(pChartValue.getValue(), 
														   			  pCharts.getMinValue())));
		xX = DBSNumber.multiply(pChart.getColumnScale(), pChartValue.getIndex() - 1);
		
		xY = DBSNumber.add(xY, DBSCharts.Padding);
		xX = DBSNumber.add(xX, DBSCharts.Padding);
		
		xXText = xX;
		xYText = DBSNumber.add(xY, (DBSCharts.FontSize / 2));
		//Encode BAR ---------------------------------------------------------------------------------
		if (pType == TYPE.BAR){
			Double xHeight = DBSNumber.abs(DBSNumber.subtract(pCharts.getChartHeight(), pCharts.getZeroPosition() - DBSCharts.Padding, xY).doubleValue());
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
				xIY +=  DBSCharts.Padding;
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
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.DATA, null);
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
		Double 			xPreviousValue = DBSNumber.divide(pChartValue.getPreviousValue(), pChart.getTotalValue()).doubleValue() * 100;
		
		//Diametro do circulo. Utiliza o menor tamanho entre a alrgura a a altura escolhada para não ultrapasar as bordas
		Double xSize;
		if (pCharts.getChartWidth().doubleValue() < pCharts.getChartHeight().doubleValue()){
			xSize = pCharts.getChartWidth().doubleValue();
		}else{
			xSize = pCharts.getChartHeight().doubleValue();
			
		}
		xSize += (DBSCharts.Padding * 2);
		//Largura de cada gráfico
		Double xPneuLargura = DBSNumber.round(xSize / ((pCharts.getItensCount() * 2D) + 3),0); //Quantidades de graficos largura do Pneu
		//Raio da roda central
		Double xRodaRaio = xPneuLargura / 2;
		//Centro da roda
		Double xCentro = xSize / 2;
		//Arco mais externos onde serão exibido dos dados
		Double xArcoExterno = (xSize / 2) - xRodaRaio;
		Double xUnit = (Math.PI *2) / 100;    
		Double xStartangle = (xPreviousValue * xUnit);
		Double xEndangle =  xStartangle + ((xPercValue * xUnit));
		Double xPneuRaioInterno = (xPneuLargura * (pCharts.getItensCount() - pChart.getIndex())) + xRodaRaio;
		Double xPneuRaioExterno = xPneuRaioInterno + xPneuLargura;
		
		//Calcula as coordenadas do arco 
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
		Double xPoint = xCentro + ((xPneuRaioExterno + 0) * Math.sin(xPointCenter));
		Double yPoint = xCentro - ((xPneuRaioExterno + 0) * Math.cos(xPointCenter));

		//Ponto de apoi no centro e acima do arco  para servir de referencia para o label
		Double xLabel = xCentro + (xArcoExterno * Math.sin(xPointCenter));
		Double yLabel = xCentro - (xArcoExterno * Math.cos(xPointCenter));
		
		String xPercLabelStyle =  "text-anchor:";
		Integer xPercLineSize =  4;
		String xPerBoxStyle = "stroke:" + pChartValue.getFillColor() + "; transform: translateY(-54%) ";
		
		if (xLabel >= xCentro){
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
	    if (xEndangle - xStartangle > Math.PI) {
	        xBig = 1;
	    }
		x1 = DBSNumber.round(x1, 2);
		y1 = DBSNumber.round(y1, 2);
		x2 = DBSNumber.round(x2, 2);
		y2 = DBSNumber.round(y2, 2);
		x3 = DBSNumber.round(x3, 2);
		x4 = DBSNumber.round(x4, 2);
		y3 = DBSNumber.round(y3, 2);
		y4 = DBSNumber.round(y4, 2);
		xPoint = DBSNumber.round(xPoint, 2);
		yPoint = DBSNumber.round(yPoint, 2);
		xLabel = DBSNumber.round(xLabel, 2);
		yLabel = DBSNumber.round(yLabel, 2);

		xPath = new StringBuilder();
	    xPath.append("<path ");
	    xPath.append("style='stroke:" + pChartValue.getFillColor() + ";' ");
	    xPath.append("d=");
	    xPath.append("'M " + x1 + "," + y1); //Ponto inicial do arco 
		xPath.append(" A " + xPneuRaioExterno + "," + xPneuRaioExterno + " 0 " + xBig + " 1 " + x2 + "," + y2); //Arco externo até o ponto final 
		xPath.append(" L " + x3 + "," + y3); //Linha do arco externo até o início do arco interno
		xPath.append(" A " + xPneuRaioInterno + "," + xPneuRaioInterno + " 0 " + xBig + " 0 " + x4 + "," + y4); //Arco interno até o ponto incial interno
		xPath.append(" Z' "); //Fecha o path ligando o arco interno ao arco externo  
		xPath.append("fill='" + pChartValue.getFillColor() + "' ");
		xPath.append("></path>");
		pWriter.write(xPath.toString());


		//Encode Dados
		pWriter.startElement("g", pChartValue);
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.DATA, null);
			
			//ENCODE LINHA
			xPath = new StringBuilder();
		    xPath.append("<path ");
		    xPath.append("class='" + DBSFaces.CSS.MODIFIER.LINE + "' ");
		    xPath.append("style='stroke:" + pChartValue.getFillColor() + "; stroke-width:1px;' ");
		    xPath.append("d=");
			xPath.append("'M " + xPoint + "," + yPoint);  
			xPath.append(" L " + xLabel + "," + yLabel); 
			xPath.append(" L " + (xLabel + xPercLineSize) + "," + yLabel);
			xPath.append("' ");  
			xPath.append("fill='none'");
			xPath.append("></path>");
			pWriter.write(xPath.toString());

			//Ponto pequeno no centro e na tangente do arco	
			DBSFaces.encodeSVGCircle(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, null, xPoint, yPoint, 2D, 2D, pChartValue.getFillColor());
			
			//Borda do percentual
			DBSFaces.encodeSVGRect(pChartValue, pWriter, DBSFaces.CSS.MODIFIER.POINT, xPerBoxStyle, (xLabel + xPercLineSize), yLabel, null, null,3,3, "white");

			//Valor do percentual ---------------------------------------------------------------------
			pvEncodeText(pChartValue, DBSFormat.getFormattedNumber(xPercValue, 1) + "%", xLabel + (xPercLineSize * 1.6), yLabel, DBSFaces.CSS.MODIFIER.VALUE, xPercLabelStyle, pWriter);

		pWriter.endElement("g");
		
		//Tooltip -------------------------------------------------------------------------
		pvEncodeTooptip(TYPE.PIE, pChartValue, xLabel, yLabel, xClientId, pContext, pWriter);

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
					if (pType == TYPE.PIE){
						pWriter.writeAttribute("tooltipdelay", "300", null);
					}else{
						pWriter.writeAttribute("tooltipdelay", "300", null);
					}
	//				if (pType == TYPE.BAR
	//				 || pType == TYPE.LINE){
						xExtraInfoStyle += "left:" + pX.intValue() + "px;";
						xExtraInfoStyle += "bottom:-" + (pY.intValue() - 5) + "px;";
	//					xExtraInfoStyle += "color:" + pChartValue.getFillColor() + ";";
	//				}
					pWriter.writeAttribute("style", xExtraInfoStyle, null);
					DBSFaces.encodeTooltip(pContext, pChartValue, pChartValue.getValue().toString(), pClienteId + "_tooltip");
//					DBSFaces.encodeTooltip(pContext, pChartValue, pChartValue.getTooltip(), pClienteId + "_tooltip");
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
}

