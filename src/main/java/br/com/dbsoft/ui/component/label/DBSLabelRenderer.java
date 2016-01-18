package br.com.dbsoft.ui.component.label;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSLabel.RENDERER_TYPE)
public class DBSLabelRenderer extends DBSRenderer {

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
		DBSLabel xLabel = (DBSLabel) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.LABEL.MAIN;
		
		if (xLabel.getSelectable()!=null){
			if (!xLabel.getSelectable()){
				xClass += DBSFaces.CSS.NOT_SELECTABLE.trim();
			}
		}
		if (!DBSObject.isEmpty(xLabel.getStyleClass())) {
			xClass +=  " " + xLabel.getStyleClass();
		}
		if (DBSObject.isEmpty(xClass)){
			xClass = null;
		}
		
		xWriter.startElement("label", xLabel);
			DBSFaces.setAttribute(xWriter, "id", xLabel.getClientId(pContext), null);
			DBSFaces.setAttribute(xWriter, "style", xLabel.getStyle(), null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "for", xLabel.getLabelFor(), null);
			if (xLabel.getLabelWidth()!=null){
				DBSFaces.setAttribute(xWriter, "style","display:inline-block;width:" + xLabel.getLabelWidth().toString() + ";", null);
			}
			
			encodeClientBehaviors(pContext, xLabel);
			if (xLabel.getIconClass() != null){
				xWriter.startElement("span", xLabel);
					DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.ICON + xLabel.getIconClass(), null);
				xWriter.endElement("span");
			}

			String xValueToRender = DBSFaces.getStringValueToRender(pContext, xLabel);
			if(xValueToRender != null) {
				xWriter.write(DBSString.toString(xValueToRender, ""));
			}

			DBSFaces.renderChildren(pContext, xLabel);
			DBSFaces.encodeTooltip(pContext, xLabel, xLabel.getTooltip());
		xWriter.endElement("label");
	}
	


}
