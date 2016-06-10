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


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSParallax.RENDERER_TYPE)
public class DBSParallaxRenderer extends DBSRenderer {
	
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSParallax xParallax = (DBSParallax) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = DBSFaces.CSS.PARALLAX.MAIN;
		if (xParallax.getStyleClass()!=null){
			xClass += xParallax.getStyleClass();
		}
		String xClientId = xParallax.getClientId(pContext);
		xWriter.startElement("div", xParallax);
			DBSFaces.setAttribute(xWriter, "id", xClientId, null);
			DBSFaces.setAttribute(xWriter, "name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xParallax.getStyle(), null);

			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xParallax, DBSPassThruAttributes.getAttributes(Key.PARALLAX));
			
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException{
		if (!pComponent.isRendered()){return;}
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = pComponent.getClientId(pContext);
		//Fim do componente
		xWriter.endElement("div");
		
		DBSFaces.encodeJavaScriptTagStart(xWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xParallaxId = dbsfaces.util.jsid('" + xClientId + "'); \n " + 
				     " dbs_parallax(xParallaxId); \n" +
                     "}); \n"; 
		xWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(xWriter);	
	}
	
}
