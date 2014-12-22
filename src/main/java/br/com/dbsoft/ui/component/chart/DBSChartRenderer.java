package br.com.dbsoft.ui.component.chart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


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
//    	if (pComponent.getChildren().size()!=0){
//    	}
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSChart xChart = (DBSChart) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.CHART.MAIN + " " + xChart.getStyleClass();

		if (xChart.getStyleClass()!=null){
			xClass = xClass + xChart.getStyleClass() + " ";
		}
		String xClientId = xChart.getClientId(pContext);
		xWriter.startElement("div", xChart);
			if (shouldWriteIdAttribute(xChart)){
				DBSFaces.setAttribute(xWriter, "id", xClientId, null);
				DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			}
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xChart.getStyle(), null);

			encodeClientBehaviors(pContext, xChart);
			
			xWriter.startElement("div", xChart);
				DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
				xWriter.startElement("svg", xChart);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT, null);
					RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xChart, DBSPassThruAttributes.getAttributes(Key.DIV));
					renderChildren(pContext, xChart);
				xWriter.endElement("svg");
			xWriter.endElement("div");
		xWriter.startElement("div", xChart);
	}
	
}
