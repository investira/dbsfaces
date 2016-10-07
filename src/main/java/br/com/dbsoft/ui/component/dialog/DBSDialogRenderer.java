package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;


import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
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
		if (xDialog.getType()==null){return;}

		ResponseWriter 		xWriter = pContext.getResponseWriter();
//		POSITION 			xPosition = POSITION.get(xDialog.getPosition());
//		CONTENT_SIZE 		xContentSize = CONTENT_SIZE.get(xDialog.getContentSize());
//		CONTENT_ALIGNMENT 	xContentAlignment = CONTENT_ALIGNMENT.get(xDialog.getContentAlignment());
		String 				xClass = CSS.DIALOG.MAIN + CSS.THEME.FC + CSS.MODIFIER.CLOSED;

		if (xDialog.getStyleClass()!=null){
			xClass += xDialog.getStyleClass();
		}
		
		String xClientId = xDialog.getClientId(pContext);
		xWriter.startElement("div", xDialog);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xDialog.getStyle());
			DBSFaces.setAttribute(xWriter, "type", xDialog.getType());
			if (!xDialog.getCloseTimeout().equals("0")){
				DBSFaces.setAttribute(xWriter, "timeout", xDialog.getCloseTimeout());
			}
			DBSFaces.setAttribute(xWriter, "p", xDialog.getPosition());
			DBSFaces.setAttribute(xWriter, "cs", xDialog.getContentSize());
			DBSFaces.setAttribute(xWriter, "ca", xDialog.getContentAlignment());
			DBSFaces.setAttribute(xWriter, "open", (xDialog.getOpen() ? "true" : "false"));
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xDialog, DBSPassThruAttributes.getAttributes(Key.DIALOG));
			xWriter.startElement("div", xDialog);
				DBSFaces.setAttribute(xWriter, "style", "opacity:0", null); //Inicia escondido para ser exibido após a execução do JS
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.CLOSED);
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
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.FC + CSS.THEME.BC + pDialog.getContentStyleClass(), null); //(pDialog.getOpened() ? CSS.MODIFIER.OPENED : CSS.MODIFIER.CLOSED)
			//Header
			pvEncodeHeader(pDialog, pContext, pWriter);
			//Nav
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTAINER, null);
				pWriter.startElement("div", pDialog);
					pWriter.startElement("div", pDialog);
						pWriter.startElement("div", pDialog);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTENT + CSS.MODIFIER.CLOSED, null);

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
			//Timeout
//			pvEncodeTimeout(pDialog, pWriter);
		pWriter.endElement("div");
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		DBSDialog 		xDialog = (DBSDialog) pComponent;
		if (xDialog.getType()==null){return;}
		
				
				//FAZ O ENCODE FINAL
				pvEncodeNavEnd(xDialog, pContext, xWriter);
				xWriter.endElement("div");
			pvEncodeJS(xDialog.getClientId(pContext), xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeIcon(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		if (pDialog.getIconClass() != null){
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION);
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + pDialog.getIconClass() + CSS.MODIFIER.INHERIT);
				pWriter.endElement("div");
			pWriter.endElement("div");
		}
	}

	private void pvEncodeHeader(DBSDialog pDialog, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xHeader = pDialog.getFacet(DBSDialog.FACET_HEADER);
		if (xHeader == null){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.setAttribute(pWriter, "style", pvGetPaddingHeader(pDialog));
				pvEncodeCaption(pDialog, pWriter);
				//Encode o conteudo do Header definido no FACET HEADER
				if (!DBSObject.isNull(xHeader)){
					xHeader.encodeAll(pContext);
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeCaption(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		if (DBSObject.isEmpty(pDialog.getCaption())){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION);
			pWriter.startElement("div", pDialog);
				pWriter.write(pDialog.getCaption());
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeIconClose(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		TYPE xType = TYPE.get(pDialog.getType());
		if (xType == TYPE.MOD){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICONCLOSE);
//			pWriter.startElement("div", pDialog);
//				if (xPosition == POSITION.LEFT){
//					xClass += "-i_navigate_previous";
//				}else if (xPosition == POSITION.RIGHT){
//					xClass += "-i_navigate_next";
//				}else if (xPosition == POSITION.TOP){
//					xClass += "-i_navigate_up";
//				}else{
//					xClass += "-i_navigate_down";
//				}
//				DBSFaces.setAttribute(pWriter, "class", xClass);
//			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
//	private void pvEncodeTimeout(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
////		if (pDialog.getCloseTimeout().equals("0")){return;}
//		String xPathTimeout = "M 0,25 A 25,25 0 1 0 0,24.99";
//		pWriter.startElement("svg", pDialog);
//			DBSFaces.encodeSVGNamespaces(pWriter);
//			DBSFaces.setAttribute(pWriter, "class", "-timeout");
//			DBSFaces.setAttribute(pWriter, "viewBox", "0 0 50 50");
//			DBSFaces.encodeSVGPath(pDialog, pWriter, xPathTimeout, "-o", null, null); //"fill=none;stroke=currentColor;stroke-width=.25em;"
//		pWriter.endElement("svg");
//	}

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

//	private void pvEncodeProgressTimeout(DBSDialog pDialog, ResponseWriter pWriter) throws IOException {
//		if (pDialog.getCloseTimeout() > 0) {
//			pWriter.startElement("div", pDialog);
//				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.PROGRESS_TIMEOUT + CSS.THEME.BC, null);
//			pWriter.endElement("div");
//		}
//	}

	private String pvGetPaddingHeader(DBSDialog pDialog){
		String xPT = pDialog.getContentPadding();
		String xPB = "0";
		String xPL = pDialog.getContentPadding();
		String xPR = pDialog.getContentPadding();
		return "padding:" + xPT + " " + xPR + " " + xPB + " " + xPL + ";"; 
	}

	private String pvGetPaddingFooter(DBSDialog pDialog){
		String xPT = "0";
		String xPB = pDialog.getContentPadding();
		String xPL = pDialog.getContentPadding();
		String xPR = pDialog.getContentPadding();
		return "padding:" + xPT + " " + xPR + " " + xPB + " " + xPL + ";"; 
	}
}



