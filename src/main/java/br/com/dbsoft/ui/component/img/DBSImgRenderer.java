package br.com.dbsoft.ui.component.img;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSImg.RENDERER_TYPE)
public class DBSImgRenderer extends DBSRenderer {

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSImg xImg = (DBSImg) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.IMG.MAIN;
		if (xImg.getStyleClass() != null){
			xClass += " " + xImg.getStyleClass(); 
		}
		xWriter.startElement("div", xImg);
			if (shouldWriteIdAttribute(xImg)){
				DBSFaces.encodeAttribute(xWriter, "id", xImg.getClientId(pContext));
			}
			DBSFaces.encodeAttribute(xWriter, "style", xImg.getStyle());
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			if (xImg.getSrc()!=null 
			 || xImg.getChildren().size() > 0){
				String xEle = (xImg.getSrc()==null) ? "span":"img";
				xWriter.startElement(xEle, xImg);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
					DBSFaces.encodeAttribute(xWriter, "alt", xImg.getAlt());
					if (xImg.getSrc()!=null){
						DBSFaces.encodeAttribute(xWriter, "src", xImg.getSrc());
					}else{
						DBSFaces.renderChildren(pContext, xImg);
					}
				xWriter.endElement(xEle);
			}
			DBSFaces.encodeTooltip(pContext, xImg, xImg.getTooltip());
		xWriter.endElement("div");
	}

	
}
