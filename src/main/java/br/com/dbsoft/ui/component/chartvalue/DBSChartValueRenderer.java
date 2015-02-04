package br.com.dbsoft.ui.component.chartvalue;

import java.io.IOException;

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
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChartValue.RENDERER_TYPE)
public class DBSChartValueRenderer extends DBSRenderer {
	
	private Integer wAbsoluteX;
	private Integer wAbsoluteY;
	
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
		DBSChartValue xChartValue = (DBSChartValue) pComponent;
		DBSChart xChart;
		DBSCharts xCharts;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHARTVALUE.MAIN + " ";
		Integer xValue = 0;
		String xClientId = xChartValue.getClientId(pContext);
		
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

		//Configura id a partir do index
		xChartValue.setId("index_" + xChartValue.getIndex());

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
			if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)
			 || xChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
				//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
				xValue = DBSNumber.multiply(xCharts.getChartHeight() - (DBSCharts.Padding * 2),
						 					DBSNumber.divide(xChartValue.getValue(), 
						 									 xCharts.getTotalValue())).intValue();
				
				//Seta valor absolute dentro do gráfico
				wAbsoluteY = (DBSNumber.subtract(xCharts.getZeroPosition(), xValue).intValue());
				//Seta valor absolute dentro do gráfico
				wAbsoluteX = (DBSNumber.multiply(xChartValue.getIndex() - 1,
													 		xCharts.getLineWidth() + xCharts.getWhiteSpace()).intValue());
				
				//Encode label da coluna
				if (xChartValue.getLabel() != null){
					DBSFaces.encodeSVGText(xChartValue, xWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:middle", wAbsoluteX + (xCharts.getLineWidth().intValue()/2), xCharts.getHeight().intValue(), xChartValue.getLabel());
				}

				//Encode bar
				if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
					//Força tamanho mínimo quando valor for zero
					if (xValue.equals(0)){
						xValue = 3;
						wAbsoluteY =(xCharts.getZeroPosition() -2);		
					}
					//Utliza valor absolute
					xValue = DBSNumber.abs(xValue);
					if (wAbsoluteY < xCharts.getZeroPosition()){
						DBSFaces.encodeSVGRect(xChartValue, xWriter, null, null, wAbsoluteX, wAbsoluteY, xValue, xCharts.getLineWidth().intValue(), xChartValue.getFillColor());
					}else{
						DBSFaces.encodeSVGRect(xChartValue, xWriter, null, null, wAbsoluteX, xCharts.getZeroPosition(), xValue, xCharts.getLineWidth().intValue(), xChartValue.getFillColor());
					}
				//Encode line - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
				}else if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
					wAbsoluteX = (wAbsoluteX + DBSNumber.divide(xCharts.getLineWidth(), 2).intValue());
					DBSFaces.encodeSVGCircle(xChartValue, xWriter, DBSFaces.CSS.MODIFIER.VALUE, null, wAbsoluteX, wAbsoluteY, 2, 2, xChartValue.getFillColor());
				}
			}
			
			UIComponent xExtraInfo = xChartValue.getFacet("extrainfo");
			//Extrainfo
			xWriter.startElement("foreignObject", xChartValue);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
				xWriter.startElement("span", xChartValue);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
					String xExtraInfoStyle = "position:absolute;";
					
					if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)
					 || xChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
						xExtraInfoStyle += "left:" + (wAbsoluteX + DBSNumber.divide(xCharts.getLineWidth() + xCharts.getWhiteSpace(),2).intValue()) + "px;";
						if (xChartValue.getValue() - DBSCharts.FontSize < 0D){
							xExtraInfoStyle += "bottom:-" + wAbsoluteY + "px;";
						}else{
							xExtraInfoStyle += "top:" + wAbsoluteY + "px;";
						}
					}
					
					DBSFaces.setAttribute(xWriter, "style", xExtraInfoStyle, null);
					//Se existir o facet Extrainfo
					if (xExtraInfo != null){
						xExtraInfo.encodeAll(pContext);
					//Se não existir, encode o valor como extrainfo
					}else{
						xWriter.write(DBSFormat.getFormattedNumber(xChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, xCharts.getValueFormatMask()));
					}
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
