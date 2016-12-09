package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.component.DBSUIOutput;

import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.ui.core.DBSFaces.FACESCONTEXT_ATTRIBUTE;
import br.com.dbsoft.util.DBSObject;

@FacesComponent(DBSDialogContent.COMPONENT_TYPE)
public class DBSDialogContent extends DBSUIOutput{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOGCONTENT;
	
	@Override
	public void encodeBegin(FacesContext pContext) throws IOException {
		if (!isRendered()){return;}
		
		DBSDialog 			xDialog = (DBSDialog) getParent();
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		TYPE 	 			xType = TYPE.get(xDialog.getType());
		String xClass = CSS.MODIFIER.CONTENT + CSS.THEME.FC + CSS.THEME.BC + xDialog.getContentStyleClass();
		if (xType == TYPE.MSG){
			//Usa o cor invertida quando pfor mensagem
			xClass += CSS.THEME.INVERT;
		}

		xWriter.startElement("div", xDialog);
			DBSFaces.encodeAttribute(xWriter, "id", getClientId());
			DBSFaces.encodeAttribute(xWriter, "name", getClientId());
			DBSFaces.encodeAttribute(xWriter, "class", xClass); 
			//Header
			pvEncodeHeader(xDialog, xType, pContext, xWriter);
			//Sub_Container
			xWriter.startElement("div", xDialog);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.SUB_CONTAINER);
				xWriter.startElement("div", xDialog);
					xWriter.startElement("div", xDialog);
						xWriter.startElement("div", xDialog);
							DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.SUB_CONTENT);
							DBSFaces.encodeAttribute(xWriter, "style", "padding:" + xDialog.getContentPadding());
							pvEncodeChildren(xDialog, pContext, xWriter);
						xWriter.endElement("div");
					xWriter.endElement("div");
				xWriter.endElement("div");
			xWriter.endElement("div");
			//Footer
			pvEncodeFooter(xDialog, xType, pContext, xWriter);
			//ButtonClose
			pvEncodeHandle(xDialog, xType, xWriter);
			pvEncodeJS(xDialog, xWriter);
		xWriter.endElement("div");

			
	}
	
	
	private void pvEncodeHeader(DBSDialog pDialog, TYPE pType, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xHeaderLeft = pDialog.getFacet(DBSDialog.FACET_HEADER_LEFT);
		UIComponent xHeaderRight = pDialog.getFacet(DBSDialog.FACET_HEADER_RIGHT);
		if (xHeaderLeft == null
		 && xHeaderRight == null
		 && pDialog.getMsgType() == null
		 && DBSObject.isEmpty(pDialog.getCaption())){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.encodeAttribute(pWriter, "style", pvGetPaddingHeader(pDialog));
					pvEncodeCaption(pDialog, pType, xHeaderRight, xHeaderLeft, pContext, pWriter);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeCaption(DBSDialog pDialog, TYPE pType, UIComponent pFacetHeaderRight, UIComponent pFacetHeaderLeft, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.MODIFIER.NOT_SELECTABLE);
			if (pDialog.getMsgType() != null
			 || !DBSObject.isEmpty(pDialog.getCaption())){
				//Label
				pWriter.startElement("div", pDialog);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
					//Se não for mensagem padrão, usa caption informada pelo usuário
					if (pDialog.getMsgType() == null){
						pWriter.write(pDialog.getCaption());
					//Tipo de mensagem como caption
					}else{
						pWriter.write(MESSAGE_TYPE.get(pDialog.getMsgType()).getName());
					}
				pWriter.endElement("div");
			}
			//Icone do tipo de mensagem
			if(pType == TYPE.MSG
		  	&& pDialog.hasMessage()){
				pWriter.startElement("div", pDialog);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON);
					pWriter.startElement("div", pDialog);
						DBSFaces.encodeAttribute(pWriter, "class", MESSAGE_TYPE.get(pDialog.getMsgType()).getIconClass());
					pWriter.endElement("div");
				pWriter.endElement("div");
			}
			
			//Encode o conteudo do Header definido no FACET HEADER_LEFT
			if (!DBSObject.isNull(pFacetHeaderLeft)){
				pWriter.startElement("div", pDialog);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LEFT);
					pFacetHeaderLeft.encodeAll(pContext);
				pWriter.endElement("div");
			}
			//Encode o conteudo do Header definido no FACET HEADER_RIGHT
			if (!DBSObject.isNull(pFacetHeaderRight)){
				pWriter.startElement("div", pDialog);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.RIGHT);
					pFacetHeaderRight.encodeAll(pContext);
				pWriter.endElement("div");
			}
			
		pWriter.endElement("div");
	}

	private void pvEncodeHandle(DBSDialog pDialog, TYPE pType, ResponseWriter pWriter) throws IOException{
		//Não cria bar de fechar se for MOD ou existir Toolbar
		MESSAGE_TYPE xMsgType = MESSAGE_TYPE.get(pDialog.getMsgType());
//		if (xMsgType.getRequireConfirmation()){
		if (pType == TYPE.NAV 
		|| (pType == TYPE.MSG && !xMsgType.getIsQuestion())){
			String xClass = "-bthandle" + CSS.THEME.ACTION;
			//Exibe espaço do button timeout
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", xClass);
			pWriter.endElement("div");
		}
	}

	/**
	 * Footer
	 * @param pDialog
	 * @param pContext
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeFooter(DBSDialog pDialog, TYPE pType, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		UIComponent xFooter = pDialog.getFacet(DBSDialog.FACET_FOOTER);
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
			if (xFooter != null){
				pWriter.startElement("div", pDialog);
					DBSFaces.encodeAttribute(pWriter, "style", pvGetPaddingFooter(pDialog));
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
					xFooter.encodeAll(pContext);
				pWriter.endElement("div");
			}
			//Toolbar
			pvEncodeToolbar(pDialog, pType, pContext, pWriter);
		pWriter.endElement("div");
	}
	
	/**
	 * Footer
	 * @param pDialog
	 * @param pContext
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeToolbar(DBSDialog pDialog, TYPE pType, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		UIComponent xToolbar = pDialog.getFacet(DBSDialog.FACET_TOOLBAR);
		if ((xToolbar != null) 
		 || pType == TYPE.MOD
		 || pType == TYPE.MSG){
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR);
				if (pType == TYPE.MOD){
					if (xToolbar == null){
						pvEncodeToolbarSimpleButtonOk(pDialog, pWriter);
					}
				}else if (pType == TYPE.MSG){
					pvEncodeToolbarMSGControls(pDialog, pContext, pWriter);
				}
				if (xToolbar != null){
					xToolbar.encodeAll(pContext);
				}
			pWriter.endElement("div");
		}
	}
	
	private String pvGetPaddingHeader(DBSDialog pDialog){
		String xPT = pDialog.getContentPadding();
		String xPB = pDialog.getContentPadding(); //"0";
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
	
	/**
	 * Encode do conteúdo
	 * @param pDialog
	 * @param pContext
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeChildren(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		if (pDialog.getChildren().size() > 0){
			//Encode dos conteúdo
			DBSFaces.renderChildren(pContext, pDialog);
		}else if (pDialog.getDBSMessages() != null && pDialog.getDBSMessages().size() > 0){
			pWriter.write(pDialog.getDBSMessages().getListMessage().get(0).getMessageText());
		}
	}

	/**
	 * Botão padrão do close quando não existir toolbar em MOD e MSG(Center)
	 * @param pDialog
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeToolbarSimpleButtonOk(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		//Só faz o encode se for MOD
		String xClass = "-btok -i_ok" + CSS.THEME.ACTION;
		//Exibe espaço do button ok
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", xClass);
		pWriter.endElement("div");
	}

	/**
	 * Controles para confirmação da mesagem
	 * @param pDialog
	 * @param pContext
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeToolbarMSGControls(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException  {
		
		pvEncodeMsgButtons(pDialog, pContext);
		
		if (pDialog.getDBSMessages() != null && pDialog.getDBSMessages().size() > 0){
			pvEncodeInputHiddenMessageKey(pDialog, pContext, pWriter);
		}
	}
	/**
	 * Campo que recebe valor da chave para salvar qual mensagem será confirmada
	 * @param pDialog
	 * @param pContext
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeInputHiddenMessageKey(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException  {
		//Ordem da coluna
		HtmlInputHidden xInput = (HtmlInputHidden) pDialog.getFacet(DBSDialog.INPUT_MSGKEY);
		if (xInput == null){
			xInput = (HtmlInputHidden) pContext.getApplication().createComponent(HtmlInputHidden.COMPONENT_TYPE);
			xInput.setId(DBSDialog.INPUT_MSGKEY);
			xInput.setValue(pDialog.getDBSMessages().getListMessage().get(0).getMessageKey());
			pDialog.getFacets().put(DBSDialog.INPUT_MSGKEY, xInput);
		}
		xInput.encodeAll(pContext);
	}

	/**
	 * Botão de confirmação
	 * @param pDialog
	 * @param pContext
	 * @throws IOException
	 */
	private void pvEncodeMsgButtons(DBSDialog pDialog, FacesContext pContext) throws IOException{
		MESSAGE_TYPE xMsgType = MESSAGE_TYPE.get(pDialog.getMsgType());
//		String xStyle = "";
//		if (xMsgType.getIsQuestion()){
//			pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_NO, "Não","-i_no -red", null, null);
//		}else{
//			xStyle = "display:none;";
//		}
//		//Não utiliza o action do botão que originou este dialog se mensagem for do tipo que impede que action seja efetuado
//		if (!xMsgType.getIsQuestion() && xMsgType.getIsError()){
//			pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_YES, "Sim","-i_yes -green", xStyle, null);
//		//Executa o action do botão que originou este dialog
//		}else{
//			pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_YES, "Sim","-i_yes -green", xStyle, (DBSUICommand) pContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE));
//		}
//		
		String xStyle = "";
		DBSUICommand xActionSource = (DBSUICommand) pContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE);
		//DOIS BOTÕES
		if (xMsgType.getIsQuestion()){
			if (xMsgType.getIsError()){
				//Não utiliza o action do botão que originou este dialog se mensagem for erro. Erro impede que action seja efetuado.
				pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_NO, "Não","-i_no -red", null, null);
			}else{
				pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_NO, "Não","-i_no -red", null, xActionSource);
			}
		//UM BOTÃO
		}else{
			xStyle = "display:none;";
			//Não utiliza o action do botão que originou este dialog se mensagem for erro. Erro impede que action seja efetuado.
			if (xMsgType.getIsError()){
				xActionSource = null;
			}
		}
		pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_YES, "Sim","-i_yes -green", xStyle, xActionSource);
	}


	private void pvEncodeMsgButton(DBSDialog pDialog, FacesContext pContext, String pId, String pLabel, String pIconClass, String pStyle, DBSUICommand pActionSource) throws IOException{
//		String		xClientId = pDialog.getClientId(pContext);
		DBSButton 	xBtn = (DBSButton) pDialog.getFacet(pId); 
		//Verifica se botão já havia sido criado
		if (xBtn == null){
			xBtn = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xBtn.setId(pId);
			xBtn.setLabel(pLabel);
//			xBtn.setActionSourceClientId(pActionSourceClientId);
			if (pActionSource != null){
				xBtn.setUpdate(pActionSource.getUpdate());
				xBtn.setActionExpression(pActionSource.getActionExpression());

//				xBtn.setUpdate("@none");
//				xBtn.setonclick("$(dbsfaces.util.jsid('" + pActionSource.getClientId() + "')).click()");
//				xBtn.setExecute(pActionSource.getExecute());
//				xBtn.setCloseDialog(false);
//				if (pActionSource.getCloseDialog()){
//					xBtn.setStyleClass("-closeParent");
//				}
			}else{
				xBtn.setUpdate("@none");
			}
			xBtn.setCloseDialog(true);
			xBtn.setExecute(pDialog.getClientId());
			xBtn.setStyle(pStyle);
			xBtn.setIconClass(CSS.MODIFIER.ICON + pIconClass);
			//Inclui botão com facet do modal para poder separa-lo dos componentes filhos criados pelo usuário.
			pDialog.getFacets().put(pId, xBtn);
		}
		xBtn.encodeAll(pContext);
	}
	
//	private String pvGetActionSourceClientId(FacesContext pContext){
//		//Indica que fechará o dialog pai(se houver) quando este dialog tiver sido acerto em função de um action e o action for closeDialog
//		DBSUICommand xActionSource = (DBSUICommand) pContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE);
//		if (xActionSource != null){
//			if (xActionSource.getCloseDialog()){
//				return xActionSource.getClientId();
//			}
//		}
//		return "";
//	}
	
	/**
	 * javaScript
	 * @param pClientId
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xDialogId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_dialogContent(xDialogId); \n" +
	                 "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}