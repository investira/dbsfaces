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
import br.com.dbsoft.ui.component.charts.DBSCharts;
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
		String 		xClass = DBSFaces.CSS.CHART.MAIN;
		TYPE 		xType = DBSChart.TYPE.get(xChart.getType());
		DBSCharts	xCharts;
		Integer		xChartsWidth;
		Integer 	xChartsHeight;
		if (!(xChart.getParent() instanceof DBSCharts)){
			return;
		}
		xCharts =  (DBSCharts) xChart.getParent();
		xChartsWidth = xCharts.getChartWidth() + xCharts.getPadding();
		xChartsHeight = xCharts.getChartHeight() + xCharts.getPadding();
		
		if (xChart.getStyleClass()!=null){
			xClass += xChart.getStyleClass() + " ";
		}

		String xClientId = xChart.getClientId(pContext);
//		xChart.restoreState(pContext, xChart.getSavedState());

		xWriter.startElement("g", xChart);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass.trim(), null);
			DBSFaces.setAttribute(xWriter, "style", xChart.getStyle(), null);
			DBSFaces.setAttribute(xWriter, "type", xChart.getType(), null);
			DBSFaces.setAttribute(xWriter, "index", xChart.getIndex(), null);
			DBSFaces.setAttribute(xWriter, "cs", xChart.getColumnScale(), null);
			if (xType == TYPE.LINE){
				if (xChart.getShowDelta()){ //Artificio para padronizar o false como não existindo o atributo(comportamento do chrome)
					DBSFaces.setAttribute(xWriter, "showdelta", xChart.getShowDelta(), null);
				}
			}
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);
			
			//Divisão onde serão desenhadas as linhas que ligam os pontos no gráfico por linha.
			//O desenho é efetuado via JS no chartValue
			if (xType == TYPE.LINE){
				xWriter.startElement("g", xChart);
					DBSFaces.setAttribute(xWriter, "class", "-path", null);
					//Area que ira captura o mousemove
					DBSFaces.encodeSVGRect(xChart, xWriter, xCharts.getPadding() / 2D, xCharts.getPadding() / 2D, xChartsWidth.toString(), xChartsHeight.toString(), DBSFaces.CSS.MODIFIER.MASK.trim(), null, null);
				xWriter.endElement("g");
				xWriter.startElement("g", xChart);
					DBSFaces.setAttribute(xWriter, "class", "-delta", null);
				xWriter.endElement("g");
			}
			//Valores-------------------------
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
		//Loop por todos os registros lidos
		//Lido de forma decrescentes por o saveState e restoreState invertou
		//a ordem da consulta
//		xChart.restoreState(FacesContext.getCurrentInstance(), xChartValue.getSavedState());
//		pChart.restoreState(FacesContext.getCurrentInstance(), pChart.getSavedState());
//    	System.out.println("restore pvEncodeResultSetChartValue");
		for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
//        for (int xRowIndex = xRowCount - 1; xRowIndex >= 0; xRowIndex--) {
        	pChart.setRowIndex(xRowIndex);
        	//Loop no componente filho contendo as definições dos valores
			for (UIComponent xC : pChart.getChildren()){
				if (xC instanceof DBSChartValue){
					DBSChartValue xChartValue = (DBSChartValue) xC;
//					xChartValue.processUpdates(pContext);
					if (xChartValue.isRendered()){
//						xChartValue.restoreState(FacesContext.getCurrentInstance(), pChart.getSavedState());
						xChartValue.restoreState(FacesContext.getCurrentInstance(), xChartValue.getSavedState());
//						xChartValue.restoreTransientState(pContext, xChartValue.getSavedState());
//						xChartValue.restoreAttachedState(pContext, xChartValue.get);
//						System.out.println("restore:\t" + xChartValue.getIndex() + "\t" + xChartValue.getLabel() + "\t" + xChartValue.getValue() + "\t" + xChartValue.getPreviousValue());
						xChartValue.encodeAll(pContext);
					}
				}
			}
        }
        pChart.setRowIndex(-1);
	}
	
	private void pvEncodeChartValue(FacesContext pContext, DBSChart pChart) throws IOException {
		DBSFaces.renderChildren(pContext, pChart);
	}
	

}
