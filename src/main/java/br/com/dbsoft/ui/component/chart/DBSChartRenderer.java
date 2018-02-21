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
import br.com.dbsoft.ui.component.chartvalue.DBSChartValue;
import br.com.dbsoft.ui.component.chartvalue.DBSDadosChartValue;
import br.com.dbsoft.ui.component.chartvalue.IDBSChartValue;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.charts.DBSCharts;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSJson;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChart.RENDERER_TYPE)
public class DBSChartRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChart xChart = (DBSChart) pComponent;
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		String 				xClass = CSS.CHART.MAIN + CSS.THEME.FLEX;
		DBSCharts			xCharts = null;
		TYPE					xType;

		//Procura pelo parent DSCharts;
		xCharts = DBSFaces.getFirstParent(xChart, DBSCharts.class);
		if (xCharts == null) {return;}

		xType = TYPE.get(xCharts.getType());
		xClass += xType.getStyleClass();
		
		if (xCharts.getShowLabel()){
			xClass += " -showLabel ";
		}
		if (xCharts.getShowValue()){
			xClass += " -showValue ";
		}
		if ((xCharts.getShowDelta() != null && xCharts.getShowDelta()) || 
		    (xCharts.getShowDelta() == null && xType == TYPE.PIE)){
			xClass += " -showDelta ";
		}

		String xClientId = xChart.getClientId(pContext);

		xWriter.startElement("div", xChart);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "caption", xChart.getCaption());
			DBSFaces.encodeAttribute(xWriter, "color", xChart.getColor());
			DBSFaces.encodeAttribute(xWriter, "vdp", xCharts.getValueDecimalPlaces());
			DBSFaces.encodeAttribute(xWriter, "vpf", xCharts.getValuePrefix());
			DBSFaces.encodeAttribute(xWriter, "vsf", xCharts.getValueSufix());
//			if (!DBSObject.isEmpty(xChart.getColor())){
//				DBSFaces.encodeAttribute(xWriter, "style", "color:" + xChart.getColor());
//			}
//			DBSFaces.encodeAttribute(xWriter, "width", "100%");
//			DBSFaces.encodeAttribute(xWriter, "height", "100%");

			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.CHART));
			
			encodeClientBehaviors(pContext, xChart);

			pvEncodeContainer(xChart, xWriter, xType);
			
			pvEncodeJS(xChart, xWriter);
			
		xWriter.endElement("div");
	}

	private void pvEncodeContainer(DBSChart pChart, ResponseWriter pWriter, TYPE pType) throws IOException{
		//CONTAINER--------------------------
		if (pType == TYPE.LINE
		 || pType == TYPE.BAR){
			pWriter.startElement("div", pChart);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.FLEX_COL + CSS.MODIFIER.INFO);
			pWriter.endElement("div");
		}
		pWriter.startElement("svg", pChart);
			DBSFaces.encodeSVGNamespaces(pWriter);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.FLEX_COL + "-chart");
//			DBSFaces.encodeAttribute(pWriter, "viewBox","0 0 400 400");
//			DBSFaces.encodeAttribute(pWriter, "preserveAspectRatio", "xMidYMid meet");
//			pWriter.startElement("g", pChart);
//				DBSFaces.encodeAttribute(pWriter, "class", "-values");
//			pWriter.endElement("g");
		pWriter.endElement("svg");
	}
	
	/**
	 * @param pWriter
	 * @param pChart
	 * @throws IOException
	 */
	private void pvEncodeJS(DBSChart pChart, ResponseWriter pWriter) throws IOException{
		String xList = pvGetListChartValue(pChart);
		String xRelationalCaptions = pvGetListRelationalCaptions(pChart);
		
		DBSFaces.encodeJavaScriptTagStart(pChart, pWriter);
		String xJS = " var xChartId = dbsfaces.util.jsid('" + pChart.getClientId() + "'); \n " +
			     " dbs_chart(xChartId" + "," + 
			     		xList + "," + 
			     		xRelationalCaptions + "," +
			     		getLocale() +
			     	"); \n";
		//RETIRADO O $(document).ready PARA TODAS O CÓDIGO LOGO DE IMEDIATO
//		String xJS = "$(document).ready(function() { \n" +
//				     " var xChartId = dbsfaces.util.jsid('" + pChart.getClientId() + "'); \n " + 
//				     " dbs_chart(xChartId" + "," + 
//				     		xList + "," + 
//				     		xRelationalCaptions + "," +
//				     		getLocale() +
//				     	"); \n" +
//                     "}); \n"; 
		pWriter.write(xJS); 
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	/**
	 * Retorna lista com os valores no formato json
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private String pvGetListChartValue(DBSChart pChart) throws IOException {
		List<IDBSChartValue> xList = null;
		if (pChart.getVar() != null
		 && pChart.getValueExpression("value") != null){
			xList = pvGetListFromResultset(pChart);
		}else{
			xList = pvGetListFromChildren(pChart);
		}
		if (xList == null) {
			return "[]";
		}else {
			return DBSJson.toJsonTree(xList, List.class).toString();
		}
	}
	
	/**
	 * Retorna lista com os valores no formato json
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private String pvGetListRelationalCaptions(DBSChart pChart) throws IOException {
		if (pChart.getRelationalCaptions() == null){return null;}
		String[] xRelationalCaptions = pChart.getRelationalCaptions().split(";");
		for (int xI=0; xI < xRelationalCaptions.length; xI++){
			xRelationalCaptions[xI] = xRelationalCaptions[xI].trim();
		}
		return DBSJson.toJson(xRelationalCaptions).toString();
	}

	/**
	 * Retorna lista a partir do resulset/list informado no <b>value</b>.
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private List<IDBSChartValue> pvGetListFromResultset(DBSChart pChart) throws IOException {
		List<IDBSChartValue> xList = new ArrayList<IDBSChartValue>();
		int xRowCount = pChart.getRowCount(); 
		pChart.setRowIndex(-1);
		DBSChartValue xChartValue = null;
		UIComponent xParent = DBSFaces.getParentFirstChild(pChart, DBSChartValue.class);
		for (UIComponent xC : xParent.getChildren()){
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
//		for (UIComponent xC : pChart.getChildren()){
//			if (xC instanceof DBSChartValue){
//				xChartValue = (DBSChartValue) xC;
//		        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {		
//		        		pChart.setRowIndex(xRowIndex);
//					pvAddChartValue(xList, xChartValue);
//		        }
//		        pChart.setRowIndex(-1);
//				break;
//			}
//		}
        return xList;
	} 

	/**
	 * Retorna lista a partir dos componentes filhos.
	 * @param pChart
	 * @return
	 * @throws IOException
	 */
	private List<IDBSChartValue> pvGetListFromChildren(DBSChart pChart) throws IOException {
		List<IDBSChartValue> xList = new ArrayList<IDBSChartValue>();
		DBSChartValue xChartValue = null;
		UIComponent xParent = DBSFaces.getParentFirstChild(pChart, DBSChartValue.class);
		if (xParent == null) {return null;}
		for (UIComponent xC : xParent.getChildren()){
			if (xC instanceof DBSChartValue){
				xChartValue = (DBSChartValue) xC;
				pvAddChartValue(xList, xChartValue);
			}
		}
//		for (UIComponent xC : pChart.getChildren()){
//			if (xC instanceof DBSChartValue){
//				xChartValue = (DBSChartValue) xC;
//				pvAddChartValue(xList, xChartValue);
//			}
//		}
        return xList;
	} 

	private void pvAddChartValue(List<IDBSChartValue> pList, DBSChartValue pSource){
    	if (pSource.isRendered()){
    		IDBSChartValue xValue = new DBSDadosChartValue();
    		xValue.setValue(pSource.getValue());
    		//Se não existir label, força que seja o index da lista
		if (DBSObject.isEmpty(pSource.getLabel())) {
			xValue.setLabel(new Integer(pList.size()).toString());
		}else {
			xValue.setLabel(pSource.getLabel());
		}
    		xValue.setDisplayValue(pSource.getDisplayValue());
    		xValue.setTooltip(pSource.getTooltip());
    		xValue.setColor(pSource.getColor());
    		xValue.setStyle(pSource.getStyle());
    		xValue.setStyleClass(pSource.getStyleClass());
    		pList.add(xValue);
        }
	}
}
