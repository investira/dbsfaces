package br.com.dbsoft.ui.component.quickrangeselection;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSRange.RENDERER_TYPE)
public class DBSRangeRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent);
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSRange xRange = (DBSRange) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.RANGE.MAIN;
		
		
		xWriter.startElement("span", xRange);
			DBSFaces.setAttribute(xWriter, "id", xRange.getClientId(pContext), null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", "display:none;", null);
			DBSFaces.setAttribute(xWriter, "label", xRange.getLabel(), null);
			DBSFaces.setAttribute(xWriter, "value1", xRange.getValue1(), null);
			DBSFaces.setAttribute(xWriter, "value2", xRange.getValue2(), null);
			DBSFaces.setAttribute(xWriter, "tooltip", xRange.getTooltip(), null);
			DBSFaces.setAttribute(xWriter, "iconclass", xRange.getIconClass(), null);
		xWriter.endElement("span");
	}
	


}
