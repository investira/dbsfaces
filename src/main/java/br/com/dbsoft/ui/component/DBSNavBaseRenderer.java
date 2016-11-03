package br.com.dbsoft.ui.component;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSNavBase.LOCATION;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;

public abstract class DBSNavBaseRenderer extends DBSRenderer {
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		
		DBSNavBase 			xNav = (DBSNavBase) pComponent;
		
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		LOCATION 		xLocation = LOCATION.get(xNav.getLocation(), xNav.getContentVerticalAlign(), xNav.getContentHorizontalAlign());
		String 			xClass = CSS.NAV.MAIN + CSS.THEME.FC + xLocation.getStyleClass();
//		if (xNav.getOpened()){
////			xClass += CSS.THEME.INVERT; //Inverte cor
////			xClass += CSS.MODIFIER.OPENED; //Indica que esta aberto
//		}else{
			xClass += CSS.MODIFIER.CLOSED; //Indica que esta fechado
//		}
		if (xNav.getStyleClass()!=null){
			xClass += xNav.getStyleClass();
		}
		
		String xClientId = xNav.getClientId(pContext);
		xWriter.startElement("div", xNav);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xNav.getStyle());
			DBSFaces.encodeAttribute(xWriter, "timeout", xNav.getCloseTimeout());
			DBSFaces.encodeAttribute(xWriter, "opened", (xNav.getOpened() ? "true" : "false"));
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xNav, DBSPassThruAttributes.getAttributes(Key.NAV));
			xWriter.startElement("div", xNav);
				DBSFaces.encodeAttribute(xWriter, "style", "opacity:0", null); //Inicia escondido para ser exibido após a execução do JS
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				//Icon
				pvEncodeIcon(xNav, xWriter);
				//Mask
				xWriter.startElement("div", xNav);
					DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC + CSS.THEME.INVERT);
				xWriter.endElement("div");
				//Nav
				pvEncodeNavBegin(xNav, pContext, xWriter);
	}
	
	private void pvEncodeNavBegin(DBSNavBase pNav, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.encodeAttribute(pWriter, "class", "-nav" + CSS.THEME.FC + CSS.THEME.BC + pNav.getContentStyleClass() + CSS.MODIFIER.CLOSED); //(pNav.getOpened() ? CSS.MODIFIER.OPENED : CSS.MODIFIER.CLOSED)
			//Header
			pvEncodeHeader(pNav, pContext, pWriter);
			//Nav
			pWriter.startElement("div", pNav);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
				pWriter.startElement("div", pNav);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
					pWriter.startElement("div", pNav);
						pWriter.startElement("nav", pNav);
//							DBSFaces.setAttribute(pWriter, "class", pNav.getContentStyleClass(), null);
							DBSFaces.encodeAttribute(pWriter, "style", "padding:" + pNav.getContentPadding());
							//Encode dos conteúdo
							DBSFaces.renderChildren(pContext, pNav);
	}
	
	private void pvEncodeNavEnd(DBSNavBase pNav, FacesContext pContext, ResponseWriter pWriter) throws IOException{
						pWriter.endElement("nav");
					pWriter.endElement("div");
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Footer
			pvEncodeFooter(pNav, pContext, pWriter);
			//Iconclose
			pvEncodeIconClose(pNav, pWriter);
			//Progress Timeout
			pvEncodeProgressTimeout(pNav, pWriter);
		pWriter.endElement("div");
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		DBSNavBase 		xNav = (DBSNavBase) pComponent;
				
				//FAZ O ENCODE FINAL
				pvEncodeNavEnd(xNav, pContext, xWriter);
				xWriter.endElement("div");
			pvEncodeJS(xNav, xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeIcon(DBSNavBase pNav, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pNav);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION);
			pWriter.startElement("div", pNav);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + pNav.getIconClass() + CSS.MODIFIER.INHERIT);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeHeader(DBSNavBase pNav, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xHeader = pNav.getFacet(DBSNavBase.FACET_HEADER);
		if (!DBSObject.isEqual(pNav.getLocation(), "c")) {
			if (xHeader == null){return;}
		}
		pWriter.startElement("div", pNav);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
			pWriter.startElement("div", pNav);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.encodeAttribute(pWriter, "style", pvGetPaddingHeader(pNav));
				//Encode do botão fechar para nav centralizado
				pvEncodeCloseButton(pNav, pWriter);
				//Encode o conteudo do Header definido no FACET HEADER
				if (!DBSObject.isNull(xHeader)){
					xHeader.encodeAll(pContext);
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeIconClose(DBSNavBase pNav, ResponseWriter pWriter) throws IOException{
		LOCATION xLocation = LOCATION.get(pNav.getLocation(), pNav.getContentVerticalAlign(), pNav.getContentHorizontalAlign());
//		String xClass = CSS.THEME.ACTION;
		String xClass = "";
		pWriter.startElement("div", pNav);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICONCLOSE);
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
				DBSFaces.encodeAttribute(pWriter, "class", xClass);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeCloseButton(DBSNavBase pNav, ResponseWriter pWriter) throws IOException {
		//Faz o encode apenas se for nav centralizado
		if (DBSObject.isEqual(pNav.getLocation(), "c")) {
			pWriter.startElement("div", pNav);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION + " -iconcloseCentral");
				pWriter.startElement("div", pNav);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + " -i_cancel " + CSS.MODIFIER.INHERIT);
				pWriter.endElement("div");
			pWriter.endElement("div");
		}
	}
	
	private void pvEncodeFooter(DBSNavBase pNav, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xFooter = pNav.getFacet(DBSNavBase.FACET_FOOTER);
		if (xFooter == null){return;}
		pWriter.startElement("div", pNav);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
			pWriter.startElement("div", pNav);
				DBSFaces.encodeAttribute(pWriter, "style", pvGetPaddingFooter(pNav));
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				xFooter.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeJS(DBSNavBase pNav, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pNav, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xNavId = dbsfaces.util.jsid('" + pNav.getClientId() + "'); \n " + 
				     " dbs_nav(xNavId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	private void pvEncodeProgressTimeout(DBSNavBase pNav, ResponseWriter pWriter) throws IOException {
		if (DBSNumber.toInteger(pNav.getCloseTimeout()) > 0) {
			pWriter.startElement("div", pNav);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.PROGRESS_TIMEOUT + CSS.THEME.BC);
			pWriter.endElement("div");
		}
	}

	private String pvGetPaddingHeader(DBSNavBase pNav){
		LOCATION xLocation = LOCATION.get(pNav.getLocation(), pNav.getContentVerticalAlign(), pNav.getContentHorizontalAlign());
		String xPT = pNav.getContentPadding();
		String xPB = pNav.getContentPadding();
		String xPL = pNav.getContentPadding();
		String xPR = pNav.getContentPadding();
		
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
	
	private String pvGetPaddingFooter(DBSNavBase pNav){
		LOCATION xLocation = LOCATION.get(pNav.getLocation(), pNav.getContentVerticalAlign(), pNav.getContentHorizontalAlign());
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
}
