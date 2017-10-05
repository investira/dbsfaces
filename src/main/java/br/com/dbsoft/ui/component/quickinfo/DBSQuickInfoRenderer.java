package br.com.dbsoft.ui.component.quickinfo;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSQuickInfo.RENDERER_TYPE)
public class DBSQuickInfoRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent);
	}

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSQuickInfo xQuickInfo = (DBSQuickInfo) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClass = CSS.QUICKINFO.MAIN;

		if (xQuickInfo.getStyleClass()!=null){
			xClass += xQuickInfo.getStyleClass();
		}		
		if (xQuickInfo.getShowOnHover()){
			xClass += "-oh "; //Indica que quickinfo é exibido com a passagem do mouse
		}else{
			xClass += CSS.THEME.ACTION; //Estilo padrão de botão
		}
		xWriter.startElement("div", xQuickInfo);
			DBSFaces.encodeAttribute(xWriter, "id", xQuickInfo.getClientId(pContext));
			DBSFaces.encodeAttribute(xWriter, "style", xQuickInfo.getStyle());
//			DBSFaces.setAttribute(xWriter, "dl", xQuickInfo.getDefaultLocation(), null);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			xWriter.startElement("div", xQuickInfo);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.ICON + xQuickInfo.getIconClass());
				encodeClientBehaviors(pContext, xQuickInfo);
				xWriter.startElement("div", xQuickInfo);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTENT);
					//Conteúdo do quickinfo
					DBSFaces.encodeTooltipQuickInfo(pContext, xQuickInfo, xQuickInfo.getDefaultLocation());
				xWriter.endElement("div");
			xWriter.endElement("div");
			//Tooltip
			DBSFaces.encodeTooltip(pContext, pComponent, xQuickInfo.getTooltip());
			pvEncodeJS(xQuickInfo, xWriter);
		xWriter.endElement("div");
	}
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xQuickInfoId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_quickInfo(xQuickInfoId); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
}
