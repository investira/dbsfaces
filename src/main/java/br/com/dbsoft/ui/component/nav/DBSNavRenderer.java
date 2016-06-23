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
				DBSFaces.setAttribute(xWriter, "style", "padding:" + xNav.getPadding(), null);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC + CSS.THEME.INVERT);
			xWriter.endElement("div");
			//Nav
			pvEncodeNav(xNav, pContext, xWriter);
			//Icon
			pvEncodeIcon(xNav, xWriter);
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeNav(DBSNav pNav, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", "-nav" + CSS.THEME.FC + CSS.THEME.BC + CSS.THEME.INVERT, null);
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER, null);
				pWriter.startElement("div", pNav);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT, null);
					pWriter.startElement("div", pNav);
						pWriter.startElement("nav", pNav);
							DBSFaces.setAttribute(pWriter, "style", "padding:" + pNav.getPadding(), null);
							//Encode dos conteúdo
							DBSFaces.renderChildren(pContext, pNav);
						pWriter.endElement("nav");
					pWriter.endElement("div");
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Footer
			pvEncodeFooter(pNav, pContext, pWriter);
		pWriter.endElement("div");
	}
	private void pvEncodeFooter(DBSNav pNav, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xFooter = pNav.getFacet(DBSNav.FACET_FOOTER);
		if (xFooter == null){return;}
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
			DBSFaces.setAttribute(pWriter, "style", pvGetPaddingFooter(pNav));
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				xFooter.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	private void pvEncodeIcon(DBSNav pNav, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON);
			DBSFaces.setAttribute(pWriter, "style", pvGetPaddingIcon(pNav));
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.ACTION + pNav.getIconClass());
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
//	private void pvEncodeHeader(DBSNav pNav, ResponseWriter pWriter) throws IOException{
//		pWriter.startElement("div", pNav);
//			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
//			DBSFaces.setAttribute(pWriter, "style", pvGetPaddingCaption(pNav));
//			pWriter.startElement("div", pNav);
////				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION + pNav.getIconClass());
//			pWriter.endElement("div");
//		pWriter.endElement("div");
//	}
//	
//
//	private void pvEncodeToolbar(DBSNav pNav, ResponseWriter pWriter) throws IOException{
//		pWriter.startElement("div", pNav);
//			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR);
//			DBSFaces.setAttribute(pWriter, "style", pvGetPaddingCaption(pNav));
//			pWriter.startElement("div", pNav);
////				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION + pNav.getIconClass());
//			pWriter.endElement("div");
//		pWriter.endElement("div");
//	}
	

	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xNavId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_nav(xNavId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	
	private String pvGetPaddingIcon(DBSNav pNav){
		LOCATION xLocation = LOCATION.get(pNav.getLocation());
		String xPT = pNav.getPadding();
		String xPB = pNav.getPadding();
		String xPL = pNav.getPadding();
		String xPR = pNav.getPadding();
		
		//Padding top e bottom
		if (xLocation.getIsVertical()){
			if (xLocation.getIsTop()){
				xPB = "0";
			}else{
				xPT = "0";
			}
		}else{
			if (xLocation.getIsLeft()){
				xPR = "0";
			}else{
				xPL = "0";
			}
		}
		return "padding:" + xPT + " " + xPR + " " + xPB + " " + xPL + ";"; 
	}
	private String pvGetPaddingFooter(DBSNav pNav){
		LOCATION xLocation = LOCATION.get(pNav.getLocation());
		String xPT = pNav.getPadding();
		String xPB = pNav.getPadding();
		String xPL = pNav.getPadding();
		String xPR = pNav.getPadding();
		
		//Padding top e bottom
		if (xLocation.getIsVertical()){
			if (xLocation.getIsTop()){
				xPT = "0";
			}else{
				xPB = "0";
			}
		}else{
			if (xLocation.getIsLeft()){
				xPL = "0";
			}else{
				xPR = "0";
			}
		}
		return "padding:" + xPT + " " + xPR + " " + xPB + " " + xPL + ";"; 
	}

}
