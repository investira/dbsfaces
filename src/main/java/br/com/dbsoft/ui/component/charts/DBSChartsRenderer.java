package br.com.dbsoft.ui.component.charts;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.charts.DBSCharts.TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSCharts.RENDERER_TYPE)
public class DBSChartsRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
	
		DBSCharts 		xCharts = (DBSCharts) pComponent;
		//Tipo de gráficos não informado
		if (xCharts.getType()==null){return;}
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = CSS.CHARTS.MAIN + CSS.MODIFIER.NOT_SELECTABLE;
//		TYPE 			xType = TYPE.get(xCharts.getType());

		if (xCharts.getStyleClass()!=null){
			xClass += xCharts.getStyleClass();
		}

		if (xCharts.getShowLabel()){
			xClass += " -showLabel ";
		}
		if (xCharts.getShowValue()){
			xClass += " -showValue ";
		}
		if ((xCharts.getShowDelta() != null && xCharts.getShowDelta()) || 
		    (xCharts.getShowDelta() == null && TYPE.get(xCharts.getType()) == TYPE.PIE)){
			xClass += " -showDelta ";
		}
		if (xCharts.getShowAnimation()){
			xClass += " -showAnimation ";
		}

		String xClientId = xCharts.getClientId(pContext);
		xWriter.startElement("div", xCharts);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xCharts.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xCharts.getType());
			DBSFaces.encodeAttribute(xWriter, "groupid", xCharts.getGroupId());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xCharts, DBSPassThruAttributes.getAttributes(Key.CHARTS));

			pvEncodeContainer(pContext, xCharts, xWriter);
			
			pvEncodeJS(xCharts, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeJS(DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pCharts, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xChartsId = dbsfaces.util.jsid('" + pCharts.getClientId() + "'); \n " + 
				     " dbs_charts(xChartsId" 
				                + "); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private void pvEncodeContainer(FacesContext pContext, DBSCharts pCharts, ResponseWriter pWriter) throws IOException{
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

			//FILTER--------------------------
			UIComponent xFilter = pCharts.getFacet(DBSCharts.FACET_FILTER);
			if (xFilter !=null){
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FILTER  + CSS.THEME.FLEX_COL + CSS.NOT_SELECTABLE);
					xFilter.encodeAll(pContext);
				pWriter.endElement("div");
			}

			//SUBCAPTION--------------------------
			pWriter.startElement("div", pCharts);
				DBSFaces.encodeAttribute(pWriter, "class", "-childrenCaption"  + CSS.THEME.FLEX_COL + CSS.NOT_SELECTABLE);
				pWriter.startElement("div", pCharts);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER + CSS.THEME.BUTTONS_STRIP);
				pWriter.endElement("div");
			pWriter.endElement("div");

			//CHARTS--------------------------
			pWriter.startElement("div", pCharts);
				DBSFaces.encodeAttribute(pWriter, "class", "-charts " + CSS.THEME.FLEX_COL);
				//FILHOS
				DBSFaces.renderChildren(pContext, pCharts);
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
