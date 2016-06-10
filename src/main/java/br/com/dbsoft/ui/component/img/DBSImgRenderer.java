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
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true;//True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
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
		DBSImg xImg = (DBSImg) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.IMG.MAIN;
		if (xImg.getStyleClass() != null){
			xClass += " " + xImg.getStyleClass(); 
		}
		xWriter.startElement("div", xImg);
			if (shouldWriteIdAttribute(xImg)){
				DBSFaces.setAttribute(xWriter, "id", xImg.getClientId(pContext));
			}
			DBSFaces.setAttribute(xWriter, "style", xImg.getStyle());
			DBSFaces.setAttribute(xWriter, "class", xClass);
			if (xImg.getSrc()!=null 
			 || xImg.getChildren().size() > 0){
				String xEle = (xImg.getSrc()==null) ? "span":"img";
				xWriter.startElement(xEle, xImg);
					DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
					DBSFaces.setAttribute(xWriter, "alt", xImg.getAlt());
					if (xImg.getSrc()!=null){
						DBSFaces.setAttribute(xWriter, "src", xImg.getSrc());
					}else{
						DBSFaces.renderChildren(pContext, xImg);
					}
				xWriter.endElement(xEle);
			}
			DBSFaces.encodeTooltip(pContext, xImg, xImg.getTooltip());
		xWriter.endElement("div");
	}

	
}
