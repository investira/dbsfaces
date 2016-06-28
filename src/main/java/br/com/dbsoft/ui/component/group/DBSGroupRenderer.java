package br.com.dbsoft.ui.component.group;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSGroup.RENDERER_TYPE)
public class DBSGroupRenderer extends DBSRenderer {
	
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
		DBSGroup xGroup = (DBSGroup) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.GROUP.MAIN;
		if (xGroup.getStyleClass()!=null){
			xClass = xClass + " " + xGroup.getStyleClass();
		}

		String xClientId = xGroup.getClientId(pContext);
		xWriter.startElement("div", xGroup);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			if (xClass!=""){
				DBSFaces.setAttribute(xWriter, "class", xClass);
			}
			DBSFaces.setAttribute(xWriter, "style", xGroup.getStyle());
			xWriter.startElement("div", xGroup);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				xWriter.startElement("div", xGroup);
					DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.HEADER);
					xWriter.startElement("div", xGroup);
						xWriter.startElement("div", xGroup);
							DBSFaces.setAttribute(xWriter, "class", CSS.THEME.INPUT_DATA + CSS.NOT_SELECTABLE);
							if (!xGroup.getLabel().equals("")){
								xWriter.write(xGroup.getLabel() + ":");
							}
						xWriter.endElement("div");
					xWriter.endElement("div");
					xWriter.startElement("div", xGroup);
						DBSFaces.setAttribute(xWriter, "class", "-line");
						xWriter.startElement("span", xGroup);
						xWriter.endElement("span");
					xWriter.endElement("div");
				xWriter.endElement("div");
				xWriter.startElement("div", xGroup);
					if (xGroup.getFloatLeft()){ 
						DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT + "-floatleft");
					}else{
						DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
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
