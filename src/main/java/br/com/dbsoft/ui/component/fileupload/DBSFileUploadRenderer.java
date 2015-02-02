package br.com.dbsoft.ui.component.fileupload;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSFileUpload.RENDERER_TYPE)
public class DBSFileUploadRenderer extends DBSRenderer {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
    	DBSFileUpload xFileUpload = (DBSFileUpload) pComponent;
        if(xFileUpload.getReadOnly()) {return;}
        
    	decodeBehaviors(pContext, xFileUpload);
    	System.out.println("DECODE");
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
		String xClass = DBSFaces.CSS.FILEUPLOAD.MAIN + " ";
		if (xFileUpload.getStyleClass()!=null){
			xClass = xClass + xFileUpload.getStyleClass();
		}

		//Configura para não save o state, pois componete não pode ser usado em chamadas ajax já que o upload é efetuado pelo browser enquanto a tela esta aberta
		xFileUpload.setTransient(true);
		
		xWriter.startElement("div", xFileUpload);
			xWriter.writeAttribute("id", xClientId, null);
			xWriter.writeAttribute("name", xClientId, null);
			xWriter.writeAttribute("class", xClass, null);
			DBSFaces.setAttribute(xWriter, "style", xFileUpload.getStyle(), null);
			//Container
			xWriter.startElement("div", xFileUpload);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, "class");
				
				pvEncodeToolbar(pContext, xFileUpload, xWriter);
		
				pvEncodeInput(xFileUpload, xWriter);
				
				pvEncodeMessage(xFileUpload, xWriter);

			xWriter.endElement("div");

		xWriter.endElement("div");
		
		pvEncodeJS(xFileUpload, xWriter, xClientId);
	}

	private void pvEncodeToolbar(FacesContext pContext, DBSFileUpload pFileUpload, ResponseWriter pWriter) throws IOException{
		//cria botão START ----------------
		DBSButton xButtonStart = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
		xButtonStart.setId("btStart");
		xButtonStart.setIconClass(DBSFaces.CSS.ICON + " -i_upload");
//		xButtonStart.setonclick("dbsfaces.fileUpload.select(this)");
		xButtonStart.setonclick("return false;");
		xButtonStart.setTooltip("Upload de arquivo");
		if (DBSObject.isEmpty(pFileUpload.getFileUploadServletPath())){
			xButtonStart.setReadOnly(true);
		}
		pFileUpload.getChildren().add(xButtonStart);

		//cria botão CANCEL ----------------
		DBSButton xButtonCancel = (DBSButton) FacesContext.getCurrentInstance().getApplication().createComponent(DBSButton.COMPONENT_TYPE);
		xButtonCancel.setId("btCancel");
		xButtonCancel.setIconClass(DBSFaces.CSS.ICON + " -i_bullet_blue");
		xButtonCancel.setStyle("display:none;");
//		xButtonCancel.setonclick("dbsfaces.fileUpload.cancel()");
		xButtonCancel.setonclick("return false;");
		xButtonCancel.setTooltip("Cancelar upload");
		if (DBSObject.isEmpty(pFileUpload.getFileUploadServletPath())){
			xButtonCancel.setReadOnly(true);
		}
		//Adiciona como filho para gerar o id corretamente
		pFileUpload.getChildren().add(xButtonCancel);

		//Encode do toolbar
		pWriter.startElement("div", pFileUpload);
			pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.TOOLBAR, null);
			xButtonStart.encodeAll(pContext);
			xButtonCancel.encodeAll(pContext);
		pWriter.endElement("div");
	}

	private void pvEncodeInput(DBSFileUpload pFileUpload, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("input", pFileUpload);
		 	pWriter.writeAttribute("id", pFileUpload.getClientId() + DBSFaces.CSS.MODIFIER.INPUT.trim(), null);
			pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.INPUT, null);
			pWriter.writeAttribute("style", "display:none;", null);
			pWriter.writeAttribute("type", "file", null);
			if (pFileUpload.getMultiple()){
				pWriter.writeAttribute("multiple", "multiple", null);
			}
		pWriter.endElement("input");
	}
	
	private void pvEncodeMessage(DBSFileUpload pFileUpload, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pFileUpload);
			pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.MESSAGE + " dbs_back_gradient_white_texture", null);
			pWriter.writeAttribute("style", "display:none;", null);
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
				     " var xFileUploadId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_fileUpload(xFileUploadId, '" + xFileUploadServlet + "'); \n" +
                     "}); \n";
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
}






