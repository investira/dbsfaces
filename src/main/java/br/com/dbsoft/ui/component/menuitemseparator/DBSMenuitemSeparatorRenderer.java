package br.com.dbsoft.ui.component.menuitemseparator;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSMenuitemSeparator.RENDERER_TYPE)
public class DBSMenuitemSeparatorRenderer extends DBSRenderer {
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSMenuitemSeparator xMenuitemSeparator = (DBSMenuitemSeparator) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.startElement("span", xMenuitemSeparator);
			DBSFaces.encodeAttribute(xWriter, "class", CSS.MENUITEMSEPARATOR.MAIN + DBSObject.getNotNull(xMenuitemSeparator.getStyleClass(), ""));
			DBSFaces.encodeAttribute(xWriter, "style", xMenuitemSeparator.getStyle());
		xWriter.endElement("span");
	}

}
