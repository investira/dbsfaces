package br.com.dbsoft.ui.component.quickrangeselection;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSRange.RENDERER_TYPE)
public class DBSRangeRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent);
	}

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSRange xRange = (DBSRange) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.RANGE.MAIN;
		
		
		xWriter.startElement("span", xRange);
			DBSFaces.encodeAttribute(xWriter, "id", xRange.getClientId(pContext));
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", "display:none;");
			DBSFaces.encodeAttribute(xWriter, "label", xRange.getLabel());
			DBSFaces.encodeAttribute(xWriter, "value1", xRange.getValue1());
			DBSFaces.encodeAttribute(xWriter, "value2", xRange.getValue2());
			DBSFaces.encodeAttribute(xWriter, "tooltip", xRange.getTooltip());
			DBSFaces.encodeAttribute(xWriter, "iconclass", xRange.getIconClass());
		xWriter.endElement("span");
	}
	


}
