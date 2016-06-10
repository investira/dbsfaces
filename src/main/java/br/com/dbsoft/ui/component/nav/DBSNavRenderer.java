package br.com.dbsoft.ui.component.nav;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.nav.DBSNav.LOCATION;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSNav.RENDERER_TYPE)
public class DBSNavRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}

	@Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
    }

	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSNav 			xNav = (DBSNav) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		LOCATION 		xLocation = LOCATION.get(xNav.getLocation());
		String 			xClass = CSS.NAV.MAIN + CSS.THEME.FC + CSS.MODIFIER.CLOSED + xLocation.getCSS();
		if (xNav.getStyleClass()!=null){
			xClass += xNav.getStyleClass();
		}
		String xClientId = xNav.getClientId(pContext);
		xWriter.startElement("div", xNav);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xNav.getStyle());
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xNav, DBSPassThruAttributes.getAttributes(Key.NAV));
			//Mask
			xWriter.startElement("div", xNav);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC);
			xWriter.endElement("div");
			//Nav
			pvEncodeNav(xNav, pContext, xWriter);
			//Caption
			pvEncodeCaption(xNav, xWriter);
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeNav(DBSNav pNav, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", "-nav" + CSS.THEME.FC + CSS.THEME.BC + CSS.THEME.INVERT, null);
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER, null);
				DBSFaces.setAttribute(pWriter, "style", "padding:" + pNav.getPadding(), null);
				pWriter.startElement("div", pNav);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT, null);
					pWriter.startElement("nav", pNav);
						//Encode dos conteúdo
						DBSFaces.renderChildren(pContext, pNav);
					pWriter.endElement("nav");
				pWriter.endElement("div");
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeCaption(DBSNav pNav, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION);
			DBSFaces.setAttribute(pWriter, "style", pvGetPaddingCaption(pNav));
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION + pNav.getIconClass());
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xNavId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_nav(xNavId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	private String pvGetPaddingCaption(DBSNav pNav){
		LOCATION xLocation = LOCATION.get(pNav.getLocation());
		String xPT = "0";
		String xPB = "0";
		if (xLocation ==  LOCATION.TOP_LEFT_VERTICAL
		 || xLocation ==  LOCATION.TOP_LEFT_HORIZONTAL
		 || xLocation ==  LOCATION.TOP_RIGHT_VERTICAL
		 || xLocation ==  LOCATION.TOP_RIGHT_HORIZONTAL){
			xPT = pNav.getPadding();
		}else if (xLocation ==  LOCATION.BOTTOM_LEFT_VERTICAL
			   || xLocation ==  LOCATION.BOTTOM_LEFT_HORIZONTAL
			   || xLocation ==  LOCATION.BOTTOM_RIGHT_VERTICAL
			   || xLocation ==  LOCATION.BOTTOM_RIGHT_HORIZONTAL){
			xPB = pNav.getPadding();
		}
		return "padding:" + xPT + " " + pNav.getPadding() + " " + xPB + " " + pNav.getPadding() + ";"; 
	}
}
