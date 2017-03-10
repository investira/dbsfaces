package br.com.dbsoft.ui.component.charts;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.charts.DBSChartsX.TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;



@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSChartsX.RENDERER_TYPE)
public class DBSChartsXRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
	
		DBSChartsX 		xCharts = (DBSChartsX) pComponent;
		//Tipo de gráficos não informado
		if (xCharts.getType()==null){return;}
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = CSS.CHARTSX.MAIN + CSS.MODIFIER.NOT_SELECTABLE + " -hide";
		TYPE 			xType = TYPE.get(xCharts.getType());

		if (xCharts.getStyleClass()!=null){
			xClass += xCharts.getStyleClass();
		}

		if (xCharts.getShowLabel()){
			xClass += " -showLabel ";
		}
		if (xType == TYPE.LINE
		 || xType == TYPE.PIE){
			if (xCharts.getShowDelta()){
				xClass += " -showDelta ";
			}
		}
		if (xType == TYPE.LINE
		 || xType == TYPE.BAR){
			if (xCharts.getShowGrid()){
				xClass += " -showGrid ";
				if (xCharts.getShowGridValue()){
					xClass += " -showGridValue ";
				}
			}
		}

		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xCharts.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xCharts.getType());
			DBSFaces.encodeAttribute(xWriter, "groupid", xCharts.getGroupId());
			if (xCharts.getValueFormatMask().indexOf("%") > -1){
				DBSFaces.encodeAttribute(xWriter, "perc", "perc");
			}
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));

			pvEncodeContainer(pContext, xWriter, xCharts);
			
			pvEncodeJS(xWriter, xCharts);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(ResponseWriter pWriter, DBSChartsX pCharts) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pCharts, pWriter);
//		Gson xDeltaListJson = new Gson();
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = dbsfaces.util.jsid('" + pCharts.getClientId() + "'); \n " + 
				     " dbs_chartsX(xChartsId" 
//				     			+ ", " + xDeltaListJson.toJsonTree(pCharts.getDeltaList(), List.class)
//				     			+ ", " + xDeltaListJson.toJsonTree(pCharts.getDeltaList(), List.class)
				                + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private void pvEncodeContainer(FacesContext pContext, ResponseWriter pWriter, DBSChartsX pCharts) throws IOException{
		//CONTAINER--------------------------
		pWriter.startElement("div", pCharts);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER  + CSS.THEME.FLEX + " -hide") ;

			//CAPTION--------------------------
			if (pCharts.getCaption() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION  + CSS.THEME.FLEX_COL + CSS.NOT_SELECTABLE);
					pWriter.write(pCharts.getCaption());
				pWriter.endElement("div");
			}

			//SUBCAPTION--------------------------
			if (pCharts.getCaption() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", "-childrenCaption"  + CSS.THEME.FLEX_COL + CSS.NOT_SELECTABLE);
					pWriter.startElement("div", pCharts);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER + CSS.THEME.FLEX);
					pWriter.endElement("div");
				pWriter.endElement("div");
			}

			//CHARTS--------------------------
//			pWriter.startElement("div", pCharts);
//				DBSFaces.encodeAttribute(pWriter, "class", "-charts " + THEME.FLEX_COL);
//				DBSFaces.renderChildren(pContext, pCharts);
//			pWriter.endElement("div");
			pWriter.startElement("div", pCharts);
				DBSFaces.encodeAttribute(pWriter, "class", "-charts " + CSS.THEME.FLEX_COL);
//				pWriter.startElement("div", pCharts);
//					DBSFaces.encodeAttribute(pWriter, "class",CSS.MODIFIER.CONTAINER);
					DBSFaces.renderChildren(pContext, pCharts);
//				pWriter.endElement("div");
			pWriter.endElement("div");
			//FOOTER--------------------------
			if (pCharts.getFooter() !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FOOTER  + CSS.THEME.FLEX_COL + CSS.NOT_SELECTABLE);
					pWriter.write(pCharts.getFooter());
				pWriter.endElement("div");
			}
		pWriter.endElement("div");
	}
	
}
