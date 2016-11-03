package br.com.dbsoft.ui.component.group;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSGroup.RENDERER_TYPE)
public class DBSGroupRenderer extends DBSRenderer {
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSGroup xGroup = (DBSGroup) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.GROUP.MAIN;
		if (xGroup.getStyleClass()!=null){
			xClass = xClass + " " + xGroup.getStyleClass();
		}

		String xClientId = xGroup.getClientId(pContext);
		xWriter.startElement("div", xGroup);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			if (xClass!=""){
				DBSFaces.encodeAttribute(xWriter, "class", xClass);
			}
			DBSFaces.encodeAttribute(xWriter, "style", xGroup.getStyle());
			xWriter.startElement("div", xGroup);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				xWriter.startElement("div", xGroup);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.HEADER);
					xWriter.startElement("div", xGroup);
					if (!DBSObject.isEmpty(xGroup.getLabel())){
						xWriter.startElement("div", xGroup);
							DBSFaces.encodeAttribute(xWriter, "class", CSS.THEME.INPUT_LABEL + CSS.NOT_SELECTABLE);
							xWriter.write(xGroup.getLabel());
						xWriter.endElement("div");
					}
					xWriter.endElement("div");
					xWriter.startElement("div", xGroup);
						DBSFaces.encodeAttribute(xWriter, "class", "-line");
						xWriter.startElement("span", xGroup);
						xWriter.endElement("span");
					xWriter.endElement("div");
				xWriter.endElement("div");
				xWriter.startElement("div", xGroup);
					if (xGroup.getFloatLeft()){ 
						DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT + "-floatleft");
					}else{
						DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
					}
					DBSFaces.renderChildren(pContext, xGroup);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
				xWriter.endElement("div");
			xWriter.endElement("div");
		xWriter.endElement("div");
	}
}
