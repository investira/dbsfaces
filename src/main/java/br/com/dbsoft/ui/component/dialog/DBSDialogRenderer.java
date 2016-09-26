package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;


import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.dialog.DBSDialog.LOCATION;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSDialog.RENDERER_TYPE)
public class DBSDialogRenderer extends DBSRenderer {
	
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
		
		DBSDialog 			xDialog = (DBSDialog) pComponent;
		
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		LOCATION 		xLocation = LOCATION.get(xDialog.getLocation(), xDialog.getContentVerticalAlign(), xDialog.getContentHorizontalAlign());
		String 			xClass = CSS.DIALOG.MAIN + CSS.THEME.FC + xLocation.getCSS();
//		if (xDialog.getOpened()){
////			xClass += CSS.THEME.INVERT; //Inverte cor
////			xClass += CSS.MODIFIER.OPENED; //Indica que esta aberto
//		}else{
			xClass += CSS.MODIFIER.CLOSED; //Indica que esta fechado
//		}
		if (xDialog.getStyleClass()!=null){
			xClass += xDialog.getStyleClass();
		}
		
		String xClientId = xDialog.getClientId(pContext);
		xWriter.startElement("div", xDialog);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xDialog.getStyle());
			DBSFaces.setAttribute(xWriter, "timeout", xDialog.getCloseTimeout());
			DBSFaces.setAttribute(xWriter, "open", (xDialog.getOpen() ? "true" : "false"));
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xDialog, DBSPassThruAttributes.getAttributes(Key.DIALOG));
			xWriter.startElement("div", xDialog);
				DBSFaces.setAttribute(xWriter, "style", "opacity:0", null); //Inicia escondido para ser exibido após a execução do JS
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				//Icon
				pvEncodeIcon(xDialog, xWriter);
				//Mask
				xWriter.startElement("div", xDialog);
					DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC + CSS.THEME.INVERT);
				xWriter.endElement("div");
				//Nav
				pvEncodeContentBegin(xDialog, pContext, xWriter);
	}
	
	private void pvEncodeContentBegin(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.FC + CSS.THEME.BC + pDialog.getContentStyleClass() + CSS.MODIFIER.CLOSED, null); //(pDialog.getOpened() ? CSS.MODIFIER.OPENED : CSS.MODIFIER.CLOSED)
			//Header
			pvEncodeHeader(pDialog, pContext, pWriter);
			//Nav
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTAINER, null);
				pWriter.startElement("div", pDialog);
					pWriter.startElement("div", pDialog);
						pWriter.startElement("div", pDialog);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTENT, null);

//							DBSFaces.setAttribute(pWriter, "class", pDialog.getContentStyleClass(), null);
							DBSFaces.setAttribute(pWriter, "style", "padding:" + pDialog.getContentPadding(), null);
							//Encode dos conteúdo
							DBSFaces.renderChildren(pContext, pDialog);
	}
	
	private void pvEncodeNavEnd(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException{
						pWriter.endElement("div");
					pWriter.endElement("div");
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Footer
			pvEncodeFooter(pDialog, pContext, pWriter);
			//Iconclose
			pvEncodeIconClose(pDialog, pWriter);
			//Progress Timeout
			pvEncodeProgressTimeout(pDialog, pWriter);
		pWriter.endElement("div");
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		DBSDialog 		xDialog = (DBSDialog) pComponent;
				
				//FAZ O ENCODE FINAL
				pvEncodeNavEnd(xDialog, pContext, xWriter);
				xWriter.endElement("div");
			pvEncodeJS(xDialog.getClientId(pContext), xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeIcon(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION);
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + pDialog.getIconClass() + CSS.MODIFIER.INHERIT);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeHeader(DBSDialog pDialog, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xHeader = pDialog.getFacet(DBSDialog.FACET_HEADER);
		if (!DBSObject.isEqual(pDialog.getLocation(), "c")) {
			if (xHeader == null){return;}
		}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.setAttribute(pWriter, "style", pvGetPaddingHeader(pDialog));
				//Encode do botão fechar para nav centralizado
				pvEncodeCloseButton(pDialog, pWriter);
				//Encode o conteudo do Header definido no FACET HEADER
				if (!DBSObject.isNull(xHeader)){
					xHeader.encodeAll(pContext);
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeIconClose(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		LOCATION xLocation = LOCATION.get(pDialog.getLocation(), pDialog.getContentVerticalAlign(), pDialog.getContentHorizontalAlign());
//		String xClass = CSS.THEME.ACTION;
		String xClass = "";
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICONCLOSE);
			pWriter.startElement("div", pDialog);
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
	
	private void pvEncodeCloseButton(DBSDialog pDialog, ResponseWriter pWriter) throws IOException {
		//Faz o encode apenas se for nav centralizado
		if (DBSObject.isEqual(pDialog.getLocation(), "c")) {
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION + " -iconcloseCentral");
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + " -i_cancel " + CSS.MODIFIER.INHERIT);
				pWriter.endElement("div");
			pWriter.endElement("div");
		}
	}
	
	private void pvEncodeFooter(DBSDialog pDialog, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xFooter = pDialog.getFacet(DBSDialog.FACET_FOOTER);
		if (xFooter == null){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "style", pvGetPaddingFooter(pDialog));
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				xFooter.encodeAll(pContext);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xDialogId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_dialog(xDialogId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

	private void pvEncodeProgressTimeout(DBSDialog pDialog, ResponseWriter pWriter) throws IOException {
		if (DBSNumber.toInteger(pDialog.getCloseTimeout()) > 0) {
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.PROGRESS_TIMEOUT + CSS.THEME.BC, null);
			pWriter.endElement("div");
		}
	}

	private String pvGetPaddingHeader(DBSDialog pDialog){
		LOCATION xLocation = LOCATION.get(pDialog.getLocation(), pDialog.getContentVerticalAlign(), pDialog.getContentHorizontalAlign());
		String xPT = pDialog.getContentPadding();
		String xPB = pDialog.getContentPadding();
		String xPL = pDialog.getContentPadding();
		String xPR = pDialog.getContentPadding();
		
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
	
	private String pvGetPaddingFooter(DBSDialog pDialog){
		LOCATION xLocation = LOCATION.get(pDialog.getLocation(), pDialog.getContentVerticalAlign(), pDialog.getContentHorizontalAlign());
		String xPT = pDialog.getContentPadding();
		String xPB = pDialog.getContentPadding();
		String xPL = pDialog.getContentPadding();
		String xPR = pDialog.getContentPadding();
		
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



