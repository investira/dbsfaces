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
		BigDecimal		xY = new BigDecimal(0);		
		TYPE			xType;
		//Recupera DBSChart pai
		if (xChartValue.getParent() == null
		|| !(xChartValue.getParent() instanceof DBSChart)){
			return;
		}
		xChart =  (DBSChart) xChartValue.getParent();
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
			if (xType == TYPE.BAR
			 || xType == TYPE.LINE){
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
				//Encode bar ---------------------------------------------------------------------------------
				if (xType == TYPE.BAR){
					Double xHeight = DBSNumber.abs(DBSNumber.subtract(xCharts.getChartHeight(), xCharts.getZeroPosition() - DBSCharts.Padding, xY).doubleValue());
					//Centraliza o ponto
					Double xLineWidth = xChart.getColumnScale() * .9;
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
					}
				//Encode line - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
				}else if (xType == TYPE.LINE){
					//Centraliza texto
					DBSFaces.encodeSVGCircle(xChartValue, xWriter, DBSFaces.CSS.MODIFIER.VALUE, null, xX.doubleValue(), xY.doubleValue(), 2D, 2D, "transparent");
				}
				//Encode do valor da linha ---------------------------------------------------------------------
				DBSFaces.encodeSVGText(xChartValue, 
									   xWriter,  
									   "-ylabel -hide", 
									   "text-anchor:end;" +"fill:" + xChartValue.getFillColor(), 
									   xCharts.getWidth().doubleValue(), 
									   xY.doubleValue() + (DBSCharts.FontSize / 2), 
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
			//EXTRAINFO -------------------------------------------------------------------------
//			UIComponent xExtraInfo = xChartValue.getFacet("extrainfo");
//			String xExtraInfoStyle;
			xWriter.startElement("foreignObject", xChartValue);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
				//Encode do label da coluna do hover (X)
//				if (!DBSObject.isEmpty(xChartValue.getLabel())){
//					xExtraInfoStyle = "position:absolute;";
//					xWriter.startElement("span", xChartValue);
//						DBSFaces.setAttribute(xWriter, "class", "-x", null);
//						xExtraInfoStyle += "top:" + (xCharts.getHeight().intValue() - DBSCharts.FontSize) + "px;";
//						DBSFaces.setAttribute(xWriter, "style", xExtraInfoStyle, null);
//						xWriter.write(xChartValue.getLabel());
//					xWriter.endElement("span");
//				}
//				//Encode do valor da linha (Y)
//				xExtraInfoStyle = "position:absolute;";
//				xWriter.startElement("span", xChartValue);
//					DBSFaces.setAttribute(xWriter, "class", "-y", null);
//					if (xType == TYPE.BAR
//					 || xType == TYPE.LINE){
//						
//						xExtraInfoStyle += "right:-" + (xCharts.getWidth() + 3) + "px;";// + (wAbsoluteX + DBSNumber.divide(xCharts.getLineWidth() + xCharts.getWhiteSpace(),2).intValue()) + "px;";
//						xExtraInfoStyle += "bottom:-" + (xY.intValue() + (DBSCharts.FontSize/2)) + "px;";
//						xExtraInfoStyle += "color:" + xChartValue.getFillColor() + ";";
//						
//					}
//					
//					DBSFaces.setAttribute(xWriter, "style", xExtraInfoStyle, null);
//					//Se existir o facet Extrainfo
//					if (xExtraInfo != null){
//						xExtraInfo.encodeAll(pContext);
//					//Se não existir, encode o valor como extrainfo
//					}else{
//						xWriter.write(DBSFormat.getFormattedNumber(xChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, xCharts.getValueFormatMask()));
//					}
//				xWriter.endElement("span");
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
