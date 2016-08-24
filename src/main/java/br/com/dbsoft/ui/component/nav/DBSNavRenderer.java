package br.com.dbsoft.ui.component.nav;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.nav.DBSNav.LOCATION;
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
		String 			xClass = CSS.NAV.MAIN + CSS.THEME.FC + xLocation.getCSS();
		if (xNav.getOpened()){
			xClass += (xNav.getThemeInverted() ? CSS.THEME.INVERT : ""); //Inverte cor
		}else{
			xClass += CSS.MODIFIER.CLOSED; //Indica que esta fechado
		}
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
			xWriter.startElement("div", xNav);
				DBSFaces.setAttribute(xWriter, "style", "opacity:0", null); //Inicia escondido para ser exibido após a execução do JS
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				//Icon
				pvEncodeIcon(xNav, xWriter);
				//Mask
				xWriter.startElement("div", xNav);
					DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC + (xNav.getThemeInverted() ? CSS.THEME.INVERT : ""));
				xWriter.endElement("div");
				//Nav
				pvEncodeNav(xNav, pContext, xWriter);
			xWriter.endElement("div");
			pvEncodeJS(xClientId, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeNav(DBSNav pNav, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", "-nav" + CSS.THEME.FC + CSS.THEME.BC + (pNav.getThemeInverted() ? CSS.THEME.INVERT : "") +" -closed", null);
			//Header
			pvEncodeHeader(pNav, pContext, pWriter);
			//Nav
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER, null);
				pWriter.startElement("div", pNav);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT, null);
					pWriter.startElement("div", pNav);
						pWriter.startElement("nav", pNav);
							DBSFaces.setAttribute(pWriter, "styleClass", pNav.getContentStyleClass(), null);
							DBSFaces.setAttribute(pWriter, "style", "padding:" + pNav.getContentPadding(), null);
							//Encode dos conteúdo
							DBSFaces.renderChildren(pContext, pNav);
						pWriter.endElement("nav");
					pWriter.endElement("div");
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Footer
			pvEncodeFooter(pNav, pContext, pWriter);
			//Iconclose
			pvEncodeIconClose(pNav, pWriter);
		pWriter.endElement("div");
	}
	private void pvEncodeIconClose(DBSNav pNav, ResponseWriter pWriter) throws IOException{
		LOCATION xLocation = LOCATION.get(pNav.getLocation());
//		String xClass = CSS.THEME.ACTION;
		String xClass = "";
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICONCLOSE);
			pWriter.startElement("div", pNav);
//				DBSFaces.setAttribute(pWriter, "class", "-i_cancel");
				if (xLocation.getIsVertical()){
					if (xLocation.getIsLeft()){
						xClass += "-i_navigate_previous";
					}else{
						xClass += "-i_navigate_next";
					}
				}else{
					if (xLocation.getIsTop()){
						xClass += "-i_navigate_up";
					}else{
						xClass += "-i_navigate_down";
					}
				}
				DBSFaces.setAttribute(pWriter, "class", xClass);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	private void pvEncodeHeader(DBSNav pNav, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xFooter = pNav.getFacet(DBSNav.FACET_HEADER);
		if (xFooter == null){return;}
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
//			DBSFaces.setAttribute(pWriter, "style", pvGetPaddingIcon(pNav));
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				xFooter.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	private void pvEncodeFooter(DBSNav pNav, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xFooter = pNav.getFacet(DBSNav.FACET_FOOTER);
		if (xFooter == null){return;}
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "style", pvGetPaddingFooter(pNav));
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				xFooter.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	private void pvEncodeIcon(DBSNav pNav, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION);
			pWriter.startElement("div", pNav);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + pNav.getIconClass() + CSS.MODIFIER.INHERIT);
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

	private String pvGetPaddingFooter(DBSNav pNav){
		LOCATION xLocation = LOCATION.get(pNav.getLocation());
		String xPT = pNav.getContentPadding();
		String xPB = pNav.getContentPadding();
		String xPL = pNav.getContentPadding();
		String xPR = pNav.getContentPadding();
		
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
	

	
//	private String pvGetPaddingIcon(DBSNav pNav){
//		LOCATION xLocation = LOCATION.get(pNav.getLocation());
//		String xPT = pNav.getPadding();
//		String xPB = pNav.getPadding();
//		String xPL = pNav.getPadding();
//		String xPR = pNav.getPadding();
//		
//		//Padding top e bottom
//		if (xLocation.getIsVertical()){
//			if (xLocation.getIsTop()){
//				xPB = "0";
//			}else{
//				xPT = "0";
//			}
//		}else{
//			if (xLocation.getIsLeft()){
//				xPR = "0";
//			}else{
//				xPL = "0";
//			}
//		}
//		return "padding:" + xPT + " " + xPR + " " + xPB + " " + xPL + ";"; 
//	}
//	private String pvGetPaddingIconClose(DBSNav pNav){
//		LOCATION xLocation = LOCATION.get(pNav.getLocation());
//		String xPT = pNav.getPadding();
//		String xPB = pNav.getPadding();
//		String xPL = pNav.getPadding();
//		String xPR = pNav.getPadding();
//		
//		//Padding top e bottom
//		if (xLocation.getIsVertical()){
//			if (xLocation.getIsTop()){
//				xPT = "0";
//			}else{
//				xPB = "0";
//			}
//		}else{
//			if (xLocation.getIsLeft()){
//				xPL = "0";
//			}else{
//				xPR = "0";
//			}
//		}
//		return "padding:" + xPT + " " + xPR + " " + xPB + " " + xPL + ";"; 
//	}

}
