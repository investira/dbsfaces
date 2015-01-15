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
	
		String xClientId = xChartValue.getClientId(pContext);

		xWriter.startElement("g", xChartValue);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "index", xChartValue.getIndex(), null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChartValue.getStyle(), null);
			
			//Grafico
			if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)
			 || xChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
				//Calcula valor em pixel a partir do valor real. subtrai padding para dar espaço para a margem
				xValue = DBSNumber.multiply(xChart.getChartHeight() - (DBSChart.Padding * 2),
						 					DBSNumber.divide(xChartValue.getValue(), 
						 					  	  		     xChart.getTotalValue())).intValue();
				
				//Seta valor absolute dentro do gráfico
				xChartValue.setAbsoluteY(DBSNumber.subtract(xChart.getZeroPosition(), xValue).intValue());
				//Seta valor absolute dentro do gráfico
				xChartValue.setAbsoluteX(DBSNumber.multiply(xChartValue.getIndex() - 1,
													 		xChart.getLineWidth() + xChart.getWhiteSpace()).intValue());
				
				//Encode label da coluna
				if (xChartValue.getLabel() != null){
					DBSFaces.encodeSVGText(xChartValue, xWriter,  DBSFaces.CSS.MODIFIER.LABEL, "text-anchor:middle", xChartValue.getAbsoluteX() + (xChart.getLineWidth().intValue()/2), xChart.getHeight().intValue(), xChartValue.getLabel());
				}

				//Encode bar
				if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
					if (xValue.equals(0)){
						xValue = 3;
						xChartValue.setAbsoluteY(xChart.getZeroPosition() -1);					
					}
					//Utliza valor absolute
					xValue = DBSNumber.abs(xValue);
					if (xChartValue.getAbsoluteY() < xChart.getZeroPosition()){
						DBSFaces.encodeSVGRect(xChartValue, xWriter, null, null, xChartValue.getAbsoluteX(), xChartValue.getAbsoluteY(), xValue, xChart.getLineWidth().intValue(), xChartValue.getFillColor());
					}else{
						DBSFaces.encodeSVGRect(xChartValue, xWriter, null, null, xChartValue.getAbsoluteX(), xChart.getZeroPosition(), xValue, xChart.getLineWidth().intValue(), xChartValue.getFillColor());
					}
				//Encode line - ponto. as linhas que ligam os pontos, são desenhadas no código JS.
				}else if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.LINE)){
					xChartValue.setAbsoluteX(xChartValue.getAbsoluteX() + DBSNumber.divide(xChart.getLineWidth(), 2).intValue());
					DBSFaces.encodeSVGCircle(xChartValue, xWriter, DBSFaces.CSS.MODIFIER.VALUE, null, xChartValue.getAbsoluteX(), xChartValue.getAbsoluteY(), 2, 2, xChartValue.getFillColor());
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
						xExtraInfoStyle += "left:" + (xChartValue.getAbsoluteX() + DBSNumber.divide(xChart.getLineWidth() + xChart.getWhiteSpace(),2).intValue()) + "px;";
						if (xChartValue.getValue() < 0D){
							xExtraInfoStyle += "bottom:-" + xChartValue.getAbsoluteY() + "px;";
						}else{
							xExtraInfoStyle += "top:" + xChartValue.getAbsoluteY() + "px;";
						}
					}
					
					DBSFaces.setAttribute(xWriter, "style", xExtraInfoStyle, null);
					//Se existir o facet Extrainfo
					if (xExtraInfo != null){
						xExtraInfo.encodeAll(pContext);
					//Se não existir, encode o valor como extrainfo
					}else{
						xWriter.write(DBSFormat.getFormattedNumber(xChartValue.getValue(), NUMBER_SIGN.MINUS_PREFIX, xChart.getValueFormatMask()));
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
