package br.com.dbsoft.ui.component.label;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSLabel.RENDERER_TYPE)
public class DBSLabelRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent);
	}

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSLabel xLabel = (DBSLabel) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.LABEL.MAIN;
		
		if (xLabel.getSelectable()!=null){
			if (!xLabel.getSelectable()){
				xClass += CSS.NOT_SELECTABLE;
			}
		}
		if (!DBSObject.isEmpty(xLabel.getStyleClass())) {
			xClass +=  " " + xLabel.getStyleClass();
		}
		if (DBSObject.isEmpty(xClass)){
			xClass = null;
		}
		
		xWriter.startElement("label", xLabel);
			DBSFaces.encodeAttribute(xWriter, "id", xLabel.getClientId(pContext));
			DBSFaces.encodeAttribute(xWriter, "style", xLabel.getStyle());
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "for", xLabel.getLabelFor());
			if (xLabel.getLabelWidth()!=null){
				DBSFaces.encodeAttribute(xWriter, "style", "display:inline-block;width:" + xLabel.getLabelWidth().toString() + ";");
			}
			
			encodeClientBehaviors(pContext, xLabel);
			if (xLabel.getIconClass() != null){
				xWriter.startElement("span", xLabel);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.ICON + xLabel.getIconClass());
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
