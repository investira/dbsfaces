package br.com.dbsoft.ui.component.parallax;

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


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSParallaxSection.RENDERER_TYPE)
public class DBSParallaxSectionRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
		DBSFaces.renderChildren(pContext, pComponent);
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSParallaxSection xParallaxSection = (DBSParallaxSection) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xParallaxSection.getClientId(pContext);
		
		String xClass = DBSFaces.CSS.PARALLAXSECTION.MAIN + " ";
		if (xParallaxSection.getStyleClass()!=null){
			xClass += xParallaxSection.getStyleClass() + " ";
		}
		xWriter.startElement("div", xParallaxSection);
			DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER.trim(), null);

			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xParallaxSection, DBSPassThruAttributes.getAttributes(Key.PARALLAX));

			xWriter.startElement("div", xParallaxSection);
				if (shouldWriteIdAttribute(xParallaxSection)){
					DBSFaces.setAttribute(xWriter, "id", xClientId, null);
					DBSFaces.setAttribute(xWriter, "name", xClientId, null);
				}
				DBSFaces.setAttribute(xWriter, "class", xClass.trim(), null);
				DBSFaces.setAttribute(xWriter, "style", xParallaxSection.getStyle(), null);
				DBSFaces.setAttribute(xWriter, "data-a", xParallaxSection.getA(), null);
				DBSFaces.setAttribute(xWriter, "data-centerAttraction", xParallaxSection.getCenterAttraction(), null);

	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		//Fim do componente
			xWriter.endElement("div");
		xWriter.endElement("div");
	}
	
}
