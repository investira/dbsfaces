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
//				xValue = DBSNumber.multiply(xCharts.getChartHeight() - (DBSCharts.Padding * 2),
//						 					DBSNumber.divide(xChartValue.getValue(), 
//						 									 xCharts.getTotalValue())).intValue();
				
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
					DBSFaces.encodeSVGCircle(xChartValue, xWriter, DBSFaces.CSS.MODIFIER.VALUE, null, xX.doubleValue(), xY.doubleValue(), 2D, 2D, "transparent");
				}else if (xType == TYPE.PIE){
					StringBuilder xPath = new StringBuilder();
					Double xValue = DBSNumber.divide(xChartValue.getValue(), xChart.getTotalValue()).doubleValue() * 100;
					Double xPreviousValue = DBSNumber.divide(xChartValue.getPreviousValue(), xChart.getTotalValue()).doubleValue() * 100;
//					Double xValue = xChartValue.getValue();
//					Double xPreviousValue = xChartValue.getPreviousValue();
					
					//Diametro do circulo. Utiliza o menor tamanho entre a alrgura a a altura escolhada para não ultrapasar as bordas
					Double xSize;
					if (xCharts.getChartWidth().doubleValue() < xCharts.getChartHeight().doubleValue()){
						xSize = xCharts.getChartWidth().doubleValue();
					}else{
						xSize = xCharts.getChartHeight().doubleValue();
						
					}
					xSize += (DBSCharts.Padding * 2);
					//
					Double xPneuLargura = xSize / ((xCharts.getItensCount() * 2D) + 1); //Quantidades de graficos largura do Pneu
					Double xRodaRaio = xPneuLargura / 2;
					Double xCentro = xSize / 2; //xCharts.getChartWidth() / 2;
					Double xUnit = (Math.PI *2) / 100;    
					Double xStartangle = (xPreviousValue * xUnit) - 0.001;
					Double xEndangle =  xStartangle + ((xValue * xUnit) - 0.001);
					Double xPneuRaioInterno = (xPneuLargura * (xChart.getIndex() - 1)) + xRodaRaio;
					Double xPneuRaioExterno = xPneuRaioInterno + xPneuLargura;
					Double x1 = xCentro + (xPneuRaioExterno * Math.sin(xStartangle));
					Double y1 = xCentro - (xPneuRaioExterno * Math.cos(xStartangle));
					Double x2 = xCentro + (xPneuRaioExterno * Math.sin(xEndangle));
					Double y2 = xCentro - (xPneuRaioExterno * Math.cos(xEndangle));
					
					Double x3 = xCentro + (xPneuRaioInterno * Math.sin(xEndangle));
					Double y3 = xCentro - (xPneuRaioInterno * Math.cos(xEndangle));
					Double x4 = xCentro + (xPneuRaioInterno * Math.sin(xStartangle));
					Double y4 = xCentro - (xPneuRaioInterno * Math.cos(xStartangle)); 

//					DBSFaces.encodeSVGCircle(xChartValue, xWriter, null, "stroke:#333333; stroke-width: 1px;", xCentro.doubleValue(), xCentro.doubleValue(), xSize / 2, xSize / 2, "transparent");
//					DBSFaces.encodeSVGCircle(xChartValue, xWriter, null, "stroke:#333333; stroke-width: 1px;", xCentro.doubleValue(), xCentro.doubleValue(), xSize / 6, xSize / 6, "transparent");
					
//					Double x2 = (xSize / 2) + (xSize / 2) * Math.sin(xEndangle);
//					Double y2 = (xSize / 2) - (xSize / 2) * Math.cos(xEndangle);
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
					xPath.append("fill='" + xChartValue.getFillColor() + "' ");
//					xPath.append("stroke='#333333' style='stroke-width: 0px;'");
					xPath.append("></path>");
					
//					xPath = new StringBuilder();
//
//					//					StringBuilder xPath = new StringBuilder();
//					Integer xPerc = 50;
//					Integer xSize = xCharts.getChartHeight();
//					Double xLargura = xSize / (xChart.getSize() * 2) - (xSize * 0.2);
//					Integer xCX = 134; //xCharts.getChartWidth() / 2;
//					Integer xCY = 134; //xCharts.getChartHeight() / 2;
//					Double xUnit = (Math.PI *2) / 100;    
//					Double xStartangle = (10 * xUnit) - 0.001;
//					Double xEndangle =  xStartangle + ((xPerc * xUnit) - 0.001);
//					Double x1 = (xSize / 2) + (xSize / 2) * Math.sin(xStartangle);
//					Double y1 = (xSize / 2) - (xSize / 2) * Math.cos(xStartangle);
//					Double x2 = (xSize / 2) + (xSize / 2) * Math.sin(xEndangle);
//					Double y2 = (xSize / 2) - (xSize / 2) * Math.cos(xEndangle);
//					Integer xBig = 0;
//				    if (xEndangle - xStartangle > Math.PI) {
//				        xBig = 1;
//				    }
//					xPath.append("<path ");
//					xPath.append("d='M " + (xSize / 2) + "," + (xSize / 2) +  // Start at circle center
//					        " L " + x1 + "," + y1 +     // Draw line to (x1,y1)
//					        " A " + (xSize / 2) + "," + (xSize / 2) +       // Draw an arc of radius r
//					        " 0 " + xBig + " 1 " +       // Arc details...
//					        x2 + "," + y2 +             // Arc goes to to (x2,y2)
//					        " Z' ");   
//					xPath.append("fill='#4d00ff' stroke='#333333' data-id='s0' transform='matrix(1,0,0,1,0,0)' style='stroke-width: 1px;'>");
//					xPath.append("</path>");


					//<path data-cx="134" data-cy="134" d="M134,20.10000000000001 A113.89999999999999,113.89999999999999,0,0,1,228.27377689006983,197.91920674477535 L171.70951075602795,159.56768269791013 A45.56,45.56,0,0,0,134,88.44 z" fill="#4d00ff" stroke="#333333" data-id="s0" transform="matrix(1,0,0,1,0,0)" style="stroke-width: 0px;"></path>

//					xPath.append("<path ");
//					xPath.append("data-cx='" + xCX + "' data-cy='" + xCY + "' ");
//					xPath.append("d='M" + xCY + ",20.10000000000001 A113.89999999999999,113.89999999999999,0,0,1,228.27377689006983,197.91920674477535 L171.70951075602795,159.56768269791013 A45.56,45.56,0,0,0," + xCY + ",88.44 z' ");
//					xPath.append("fill='#4d00ff' stroke='#333333' data-id='s0' transform='matrix(1,0,0,1,0,0)' style='stroke-width: 0px;'>");
//					xPath.append("</path>");
					xWriter.write(xPath.toString());
					
				}
				//Encode do valor da linha ---------------------------------------------------------------------
				DBSFaces.encodeSVGText(xChartValue, 
									   xWriter,  
									   "-ylabel -hide", 
									   "text-anchor:end;" +"fill:" + xChartValue.getFillColor(), 
									   xCharts.getWidth().doubleValue(), 
									   xYText.doubleValue(), 
									   DBSFormat.getFormattedNumber(xChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, xCharts.getValueFormatMask()));
				//Encode label da coluna ---------------------------------------------------------------------
				if (!DBSObject.isEmpty(xChartValue.getLabel())){
					DBSFaces.encodeSVGText(xChartValue, 
										   xWriter,  
										   "-xlabel -hide", 
										   "text-anchor:middle;" + "fill:" + xChartValue.getFillColor(), 
										   xXText.doubleValue(), 
										   xCharts.getHeight().doubleValue(), 
										   xChartValue.getLabel());
				}
			}
			//Tooltip -------------------------------------------------------------------------
			String xExtraInfoStyle = "";
			xWriter.startElement("foreignObject", xChartValue);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
//				xWriter.writeAttribute("requiredExtensions", "http://www.w3.org/1999/xhtml", null);
				xWriter.writeAttribute("xmlns","http://www.w3.org/1999/xhtml", null);
//				xmlns="http://www.w3.org/1999/xhtml"
				xWriter.writeAttribute("height", "1px", null);
				xWriter.writeAttribute("width", "1px", null);
				xWriter.writeAttribute("x", "0px", null);
				xWriter.writeAttribute("y", "0px", null);
				xWriter.startElement("span", xChartValue);
//					xWriter.writeAttribute("xmlns","http://www.w3.org/1999/xhtml", null);
					xWriter.writeAttribute("id", xClientId + "_tooltip", null);
					xWriter.writeAttribute("tooltipdelay", "200", null);
					if (xType == TYPE.BAR
					 || xType == TYPE.LINE){
						xExtraInfoStyle += "left:" + xXText.intValue() + "px;";
						xExtraInfoStyle += "bottom:-" + (xYText.intValue() - 5) + "px;";
						xExtraInfoStyle += "color:" + xChartValue.getFillColor() + ";";
					}
					xWriter.writeAttribute("style", xExtraInfoStyle, null);
					DBSFaces.encodeTooltip(pContext, xChartValue, xChartValue.getTooltip(), xClientId + "_tooltip");
				xWriter.endElement("span");
			xWriter.endElement("foreignObject");
			
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChartValue, DBSPassThruAttributes.getAttributes(Key.DIV));
			encodeClientBehaviors(pContext, xChartValue);
			pvEncodeJS(xClientId, xWriter);
		
		xWriter.endElement("g");
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

