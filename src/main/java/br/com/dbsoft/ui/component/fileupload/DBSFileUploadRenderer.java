package br.com.dbsoft.ui.component.fileupload;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSFileUpload.RENDERER_TYPE)
public class DBSFileUploadRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSFileUpload xFileUpload = (DBSFileUpload) pComponent;
        if(xFileUpload.getReadOnly()) {return;}
        
    	decodeBehaviors(pContext, xFileUpload);
//    	System.out.println("DECODE");
//		String xClientIdAction = getInputDataClientId(xCheckbox);
//		String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientIdAction);
//        if(xSubmittedValue != null && pvIsChecked(xSubmittedValue)) {
//        	xCheckbox.setSubmittedValue(true);
//        }else{
//	    	xCheckbox.setSubmittedValue(false);
//        }
	}	


	   
    @Override
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
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
		DBSFileUpload xFileUpload = (DBSFileUpload) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xFileUpload.getClientId(pContext);
		String xClass = CSS.FILEUPLOAD.MAIN;
		if (xFileUpload.getStyleClass()!=null){
			xClass += xFileUpload.getStyleClass();
		}

		if (xFileUpload.getReadOnly()){
			xClass += CSS.MODIFIER.DISABLED;
		}
		//Configura para não save o state, pois componete não pode ser usado em chamadas ajax já que o upload é efetuado pelo browser enquanto a tela esta aberta
		xFileUpload.setTransient(true);
		
		xWriter.startElement("div", xFileUpload);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			DBSFaces.setAttribute(xWriter, "style", xFileUpload.getStyle());
			encodeClientBehaviors(pContext, xFileUpload);
			//Container
			xWriter.startElement("div", xFileUpload);
				DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				
				pvEncodeToolbar(pContext, xFileUpload, xWriter);
		
				pvEncodeInput(pContext, xFileUpload, xWriter);
				
				pvEncodeMessage(xFileUpload, xWriter);

			xWriter.endElement("div");


		xWriter.endElement("div");
		if (!xFileUpload.getReadOnly()){
			pvEncodeJS(xFileUpload, xWriter, xClientId);
		}
	}

	private void pvEncodeToolbar(FacesContext pContext, DBSFileUpload pFileUpload, ResponseWriter pWriter) throws IOException{
		//Encode do toolbar
		pWriter.startElement("div", pFileUpload);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR);
			//Botão START
			DBSButton xButtonStart = (DBSButton) pFileUpload.getFacet("btStart");
			if (xButtonStart != null){
				xButtonStart.encodeAll(pContext);
			}
			//Botão CANCEL
			DBSButton xButtonCancel = (DBSButton) pFileUpload.getFacet("btCancel");
			if (xButtonCancel != null){
				xButtonCancel.encodeAll(pContext);
			}
		pWriter.endElement("div");
	}

	private void pvEncodeInput(@SuppressWarnings("unused") FacesContext pContext, DBSFileUpload pFileUpload, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("input", pFileUpload);
			DBSFaces.setAttribute(pWriter, "id", pFileUpload.getClientId().replaceAll(":", "_") + CSS.MODIFIER.INPUT.trim());
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.INPUT);
			DBSFaces.setAttribute(pWriter, "style", "display:none;");
			DBSFaces.setAttribute(pWriter, "type", "file");
			if (pFileUpload.getMultiple()){
				DBSFaces.setAttribute(pWriter, "multiple", "multiple");
			}
			if (!DBSObject.isEmpty(pFileUpload.getAccept())){
				DBSFaces.setAttribute(pWriter, "accept", pFileUpload.getAccept());
			}
			if (!DBSObject.isEmpty(pFileUpload.getMaxSize())){
				DBSFaces.setAttribute(pWriter, "maxSize", pFileUpload.getMaxSize());
			}
		pWriter.endElement("input");
	}
	
	private void pvEncodeMessage(DBSFileUpload pFileUpload, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pFileUpload);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.MESSAGE + CSS.BACK_TEXTURE_WHITE_GRADIENT);
			DBSFaces.setAttribute(pWriter, "style", "display:none;");
		pWriter.endElement("div");
	}

	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(DBSFileUpload pFileUpload, ResponseWriter pWriter, String pClientId) throws IOException {
		String xFileUploadServlet = DBSObject.getNotEmpty(pFileUpload.getFileUploadServletPath(), "");
		
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xFileUploadId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_fileUpload(xFileUploadId, '" + xFileUploadServlet + "'); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
}






