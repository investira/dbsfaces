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
		Long xX = (xChartValue.getIndex() - 1) * xChart.getLineWidth();
		Long xY = 15L;//xChart.getMaxValue() - xChartValue.getValue();

		if (xChartValue.getStyleClass()!=null){
			xClass = xClass + xChartValue.getStyleClass() + " ";
		}
		String xClientId = xChartValue.getClientId(pContext);
		
		if (xChart.getType().equalsIgnoreCase(DBSChart.TYPE.BAR)){
			xWriter.startElement("rect", xChartValue);
				DBSFaces.setAttribute(xWriter, "id", xClientId, null);
				DBSFaces.setAttribute(xWriter, "name", xClientId, null);
				DBSFaces.setAttribute(xWriter, "class", xClass, null);
				DBSFaces.setAttribute(xWriter, "style", xChartValue.getStyle(), null);
				
				DBSFaces.setAttribute(xWriter, "x", 	xX, null);
				DBSFaces.setAttribute(xWriter, "y", 	xY, null);
				DBSFaces.setAttribute(xWriter, "width", xChart.getLineWidth(), null);
				DBSFaces.setAttribute(xWriter, "height", xChartValue.getValue(), null);
				DBSFaces.setAttribute(xWriter, "stroke", "none", null);
//				DBSFaces.setAttribute(xWriter, "stroke-width", "0", null);
				DBSFaces.setAttribute(xWriter, "fill",	xChartValue.getFillColor(), null);
				DBSFaces.setAttribute(xWriter, "rx", 	"0", null);
				DBSFaces.setAttribute(xWriter, "ry", 	"0", null);

				RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChartValue, DBSPassThruAttributes.getAttributes(Key.DIV));
				encodeClientBehaviors(pContext, xChartValue);
				pvEncodeJS(xClientId, xWriter);
//				DBSFaces.encodeTooltip(pContext, xChartValue, xChartValue.getTooltip());
			xWriter.endElement("rect");
		}

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
