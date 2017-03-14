package br.com.dbsoft.ui.component.chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.charts.DBSChartsX;
import br.com.dbsoft.ui.component.charts.DBSChartsX.TYPE;
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.chartvalue.DBSDadosChartValue;
import br.com.dbsoft.ui.component.chartvalue.IDBSChartValue;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSJson;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChartX.RENDERER_TYPE)
public class DBSChartXRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChartX xChart = (DBSChartX) pComponent;
		ResponseWriter 			xWriter = pContext.getResponseWriter();
		String 					xClass = CSS.CHARTX.MAIN + CSS.THEME.FLEX;
		DBSChartsX				xDBSChartsX;
		if (!(xChart.getParent() instanceof DBSChartsX)){
			return;
		}else{
			xDBSChartsX = (DBSChartsX) xChart.getParent();
			xClass += TYPE.get(xDBSChartsX.getType()).getStyleClass();
		}
		
		String xClientId = xChart.getClientId(pContext);

		xWriter.startElement("div", xChart);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "caption", xChart.getCaption());
			DBSFaces.encodeAttribute(xWriter, "color", xChart.getColor());
//			DBSFaces.encodeAttribute(xWriter, "width", "100%");
//			DBSFaces.encodeAttribute(xWriter, "height", "100%");

			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);

			pvEncodeContainer(xWriter, xChart);
			
			pvEncodeJS(xWriter, xChart);
			
		xWriter.endElement("div");
	}

	private void pvEncodeContainer(ResponseWriter pWriter, DBSChartX pChart) throws IOException{
		//CONTAINER--------------------------
 		pWriter.startElement("div", pChart);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.FLEX_COL + "-info");
		pWriter.endElement("div");
		pWriter.startElement("svg", pChart);
			DBSFaces.encodeSVGNamespaces(pWriter);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.FLEX_COL + "-chart");
		pWriter.endElement("svg");
	}
	
	/**
	 * @param pWriter
	 * @param pChart
	 * @throws IOException
	 */
	private void pvEncodeJS(ResponseWriter pWriter, DBSChartX pChart) throws IOException{
		String xList = pvGetListChartValue(pChart);
		
		DBSFaces.encodeJavaScriptTagStart(pChart, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartId = dbsfaces.util.jsid('" + pChart.getClientId() + "'); \n " + 
				     " dbs_chartX(xChartId" 
				     			  + "," + xList
				     			  + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS); 
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	/**
	 * Retorna lista com os valores no formato json
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private String pvGetListChartValue(DBSChartX pChart) throws IOException {
		List<IDBSChartValue> xList = null;
		if (pChart.getVar() != null
		 && pChart.getValueExpression("value") != null){
			xList = pvGetListFromResultset(pChart);
		}else{
			xList = pvGetListFromChildren(pChart);
		}
		return DBSJson.toJsonTree(xList, List.class).toString();
	}

	/**
	 * Retorna lista a partir do resulset/list informado no <b>value</b>.
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private List<IDBSChartValue> pvGetListFromResultset(DBSChartX pChart) throws IOException {
		List<IDBSChartValue> xList = new ArrayList<IDBSChartValue>();
		int xRowCount = pChart.getRowCount(); 
		pChart.setRowIndex(-1);
		DBSChartValue xChartValue = null;
		for (UIComponent xC : pChart.getChildren()){
			if (xC instanceof DBSChartValue){
				xChartValue = (DBSChartValue) xC;
		        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {		
		        	pChart.setRowIndex(xRowIndex);
					pvAddChartValue(xList, xChartValue);
		        }
		        pChart.setRowIndex(-1);
				break;
			}
		}
        return xList;
	} 

	/**
	 * Retorna lista a partir dos componentes filhos.
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private List<IDBSChartValue> pvGetListFromChildren(DBSChartX pChart) throws IOException {
		List<IDBSChartValue> xList = new ArrayList<IDBSChartValue>();
		DBSChartValue xChartValue = null;
		for (UIComponent xC : pChart.getChildren()){
			if (xC instanceof DBSChartValue){
				xChartValue = (DBSChartValue) xC;
				pvAddChartValue(xList, xChartValue);
			}
		}
        return xList;
	} 

	private void pvAddChartValue(List<IDBSChartValue> pList, DBSChartValue pSource){
    	if (pSource.isRendered()){
    		IDBSChartValue xValue = new DBSDadosChartValue();
    		xValue.setValue(pSource.getValue());
    		xValue.setLabel(pSource.getLabel());
    		xValue.setDisplayValue(pSource.getDisplayValue());
    		xValue.setTooltip(pSource.getTooltip());
    		pList.add(xValue);
        }
	}
}
