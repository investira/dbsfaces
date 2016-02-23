package br.com.dbsoft.ui.component.chart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.chart.DBSChart.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChart.RENDERER_TYPE)
public class DBSChartRenderer extends DBSRenderer {

	
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
		DBSChart xChart = (DBSChart) pComponent;
		if (xChart.getType()==null){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHART.MAIN + " ";

		if (xChart.getStyleClass()!=null){
			xClass = xClass + xChart.getStyleClass() + " ";
		}

		String xClientId = xChart.getClientId(pContext);
		xWriter.startElement("g", xChart);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChart.getStyle(), null);
			DBSFaces.setAttribute(xWriter, "type", xChart.getType(), null);
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);
			
			//Divisão onde serão desenhadas as linhas que ligam os pontos no gráfico por linha.
			//O desenho é efetuado via JS no chartValue
			if (DBSChart.TYPE.get(xChart.getType()) == TYPE.LINE){
				xWriter.startElement("g", xChart);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.LINE, null);
				xWriter.endElement("g");
			}
			//Se não foi informado DBSResultSet
			if (DBSObject.isEmpty(xChart.getVar())
			 || DBSObject.isEmpty(xChart.getValueExpression("value"))){
				pvEncodeChartValue(pContext, xChart);
			}else{
				pvEncodeResultSetChartValue(pContext, xChart, xWriter);
			}

			pvEncodeJS(xClientId, xWriter);
			
		xWriter.endElement("g");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_chart(xChartId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	/**
	 * Encode do corpo da tabela contendo as linhas com os dados
	 * @param pContext
	 * @param pChart
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeResultSetChartValue(FacesContext pContext, DBSChart pChart, ResponseWriter pWriter) throws IOException {
        int xRowCount = pChart.getRowCount(); 
		pChart.setRowIndex(-1);
		pChart.getFirst();
		pChart.getRows(); 
		Double xTotalValue  = 0D;
		Integer xIndex = 0;
		Double xPreviousValue = 0D;
		//Loop por todos os registros lidos
        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
        	pChart.setRowIndex(xRowIndex);
        	//Loop no componente filho contendo as definições dos valores
			for (UIComponent xC : pChart.getChildren()){
				if (xC instanceof DBSChartValue){
					DBSChartValue xChartValue = (DBSChartValue) xC;
					if (xChartValue.isRendered()){
						xIndex++;
						xChartValue.setIndex(xIndex);
						xChartValue.setPreviousValue(xPreviousValue);
						xPreviousValue = xChartValue.getValue();
						if (xPreviousValue !=null){
							xTotalValue += xPreviousValue;
						}
						xChartValue.encodeAll(pContext);
					}
				}
			}
        }
        pChart.setTotalValue(xTotalValue);
        pChart.setRowIndex(-1);
	}
	
	private void pvEncodeChartValue(FacesContext pContext, DBSChart pChart) throws IOException {
		Double 	xPreviousValue = 0D;
		Integer xIndex = 0;
		Double xTotalValue  = 0D;
		//Loop por todos os filhos
		for (UIComponent xC : pChart.getChildren()){
			if (xC instanceof DBSChartValue){
				DBSChartValue xChartValue = (DBSChartValue) xC;
				if (xChartValue.isRendered()){
					xIndex++;
					xChartValue.setIndex(xIndex);
					xChartValue.setPreviousValue(xPreviousValue);
					xPreviousValue = xChartValue.getValue();
					if (xPreviousValue !=null){
						xTotalValue += xPreviousValue;
					}
				}
			}
		}
		pChart.setTotalValue(xTotalValue);
		DBSFaces.renderChildren(pContext, pChart);
	}




}
