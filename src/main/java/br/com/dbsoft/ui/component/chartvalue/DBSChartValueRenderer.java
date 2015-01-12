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
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSFormat;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSFormat.NUMBER_SIGN;


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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChartValue xChartValue = (DBSChartValue) pComponent;
		//Recupera o componente pai
		if (pComponent.getParent() == null
		|| !(pComponent.getParent() instanceof DBSChart)){
			return;
		}
		DBSChart xChart = (DBSChart) pComponent.getParent();
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHARTVALUE.MAIN + " ";
		
		Integer xIndexPosition = 0;//DBSChart.Padding / 2;
		Integer xZeroPosition = xChart.getZeroPosition();
		Integer xValue = 0;
		
		//Configura id a partir do index
		xChartValue.setId("index_" + xChartValue.getIndex());

		//Configura class
		if (xChartValue.getStyleClass()!=null){
			xClass = xClass + xChartValue.getStyleClass() + " ";
		}
	
		//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
		xValue = DBSNumber.multiply(xChart.getHeight() - (DBSChart.Padding * 2),
				 					DBSNumber.divide(DBSNumber.abs(xChartValue.getValue()), 
				 					  	  		     xChart.getTotalValue())).intValue();
		
		String xClientId = xChartValue.getClientId(pContext);

		xWriter.startElement("g", xChartValue);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "index", xChartValue.getIndex(), null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChartValue.getStyle(), null);
			
//			xWriter.startElement("foreignObject", xChartValue);
//				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
//				xWriter.startElement("span", xChartValue);
//					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
//					renderChildren(pContext, xChartValue);
//				xWriter.endElement("span");
//			xWriter.endElement("foreignObject");

			//Grafico
			if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
				//Força a exibição de pelo menos uma linha caso valor não seja zero, mas o tamanho ajustado dê zero
				if (xValue == 0
				 && xChartValue.getValue() != 0D){
					xValue = 1;
				}
				
				//Ajusta para posição inicial da barra 
				if (xChartValue.getValue() > 0D){
					xZeroPosition -= xValue;
				}
						
				//Calcula posição da próxima barra
				xIndexPosition += DBSNumber.multiply(xChartValue.getIndex() - 1,
													 xChart.getLineWidth() + xChart.getWhiteSpace()).intValue();
				
				xWriter.startElement("rect", xChartValue);
					DBSFaces.setAttribute(xWriter, "x", 	xIndexPosition, null);
					DBSFaces.setAttribute(xWriter, "y", 	xZeroPosition, null);
					DBSFaces.setAttribute(xWriter, "width", xChart.getLineWidth(), null);
					DBSFaces.setAttribute(xWriter, "height", xValue, null);
					DBSFaces.setAttribute(xWriter, "fill",	xChartValue.getFillColor(), null);
//					DBSFaces.setAttribute(xWriter, "rx", 	"2", null);
//					DBSFaces.setAttribute(xWriter, "ry", 	"2", null);
	
					RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChartValue, DBSPassThruAttributes.getAttributes(Key.DIV));
					encodeClientBehaviors(pContext, xChartValue);
					pvEncodeJS(xClientId, xWriter);

				xWriter.endElement("rect");
			}
			
			UIComponent xExtraInfo = xChartValue.getFacet("extrainfo");
			//Extrainfo
			xWriter.startElement("foreignObject", xChartValue);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
				xWriter.startElement("span", xChartValue);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
					String xExtraInfoStyle = "position:absolute;";
					Long xLeft = xIndexPosition + xChart.getLineWidth();
					Integer xTop = xZeroPosition;
					if (xChartValue.getValue() < 0D){
						xTop += xValue;
						xExtraInfoStyle += "bottom:-" + xTop + "px;";
					}else{
						xExtraInfoStyle += "top:" + xTop + "px;";
					}
					xExtraInfoStyle += "left:" + xLeft + "px;";
					
					DBSFaces.setAttribute(xWriter, "style", xExtraInfoStyle, null);
					//Se existir o facet Extrainfo
					if (xExtraInfo != null){
						xExtraInfo.encodeAll(pContext);
					//Se não existir, encode o valor como extrainfo
					}else{
						xWriter.write(DBSFormat.getFormattedNumber(xChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, xChart.getFormatMask()));
					}
				xWriter.endElement("span");
			xWriter.endElement("foreignObject");
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
