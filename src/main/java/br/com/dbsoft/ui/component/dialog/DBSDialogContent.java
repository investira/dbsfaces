package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.ui.core.DBSFaces.FACESCONTEXT_ATTRIBUTE;
import br.com.dbsoft.util.DBSBoolean;
import br.com.dbsoft.util.DBSObject;

@FacesComponent(DBSDialogContent.COMPONENT_TYPE)
public class DBSDialogContent extends DBSUIOutput{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOGCONTENT;
	
	@Override
	public void decode(FacesContext pContext) {
		//Valida mensagens
		DBSDialog xDialog = (DBSDialog) getParent();
		String xClientId = xDialog.getClientId(pContext);
		IDBSMessages xMessages = xDialog.getDBSMessages(); 
		if (xMessages!=null){
			//Se houver mensagem a ser validada.
			if (xMessages.hasMessages()){
				String xSourceId = DBSFaces.getDecodedSourceId(pContext); 
				//Se decode foi disparado em função de uma ação
				if (xSourceId != null){
					String xMsgKeyInputId = xClientId + ":" + DBSDialog.INPUT_MSGKEY;
					String xMsgKey = (String) DBSFaces.getDecodedComponenteValue(pContext, xMsgKeyInputId);
					//Se existe alguma mensagem sendo validada
					if (xMsgKey != null){
						IDBSMessage xMessage = xMessages.getMessage(xMsgKey);
						if (xMessage != null){
							//Salva qual a mensagem esta sendo validada para ser utilizado na execução do action
//							pContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.ACTION_MESSAGEKEY, xMsgKey);
//							System.out.println("DBSDialogRenderer decode messaga Validated--\t" + xMsgKey + "\t" + xClientId);
							if (xMessage.getMessageType().getIsQuestion()){
								if (xSourceId.equals(xClientId + ":" + DBSDialog.BUTTON_NO)){
									//Seta mensagem como validada negativamente. Lembrando que o validade dispara eventuais listeners atrelados a mensagem.
									xMessage.setMessageValidated(false);
								} else if (xSourceId.equals(xClientId + ":" + DBSDialog.BUTTON_YES)){
									//Seta mensagem como validada positivamente
									xMessage.setMessageValidated(true);
								}
							}else{
								//Seta a validação conforme o tipo de mensagem.
								//Mensagens de erro é validada como false
								if (xMessage.getMessageType().getIsError()){
									xMessage.setMessageValidated(false);
								//Mensagens de normal é validada como true
								}else{
									xMessage.setMessageValidated(true);
								}
							}
							//Remove mensagem da lista dentro do componente
							xMessages.remove(xMessage);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void encodeBegin(FacesContext pContext) throws IOException {
		if (!isRendered()){return;}
		
		DBSDialog 			xDialog = (DBSDialog) getParent();
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		TYPE 	 			xType = TYPE.get(xDialog.getType());
		String xClass = CSS.MODIFIER.CONTENT + CSS.THEME.FC + CSS.THEME.BC + xDialog.getContentStyleClass();
		if (xType == TYPE.MSG){
			//Usa o cor invertida quando for mensagem
			xClass += CSS.THEME.INVERT;
		}
//		System.out.println("DBSDialogContent encodeBegin\t" + this.getClientId());
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
							if (xType != TYPE.BTN){
								DBSFaces.encodeAttribute(xWriter, "style", "padding:" + xDialog.getContentPadding());
							}
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
		if (!pvHasHeader(pDialog, xHeaderRight, xHeaderLeft) && pDialog.getMsgType() == null){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.HEADER + CSS.MODIFIER.NOT_SELECTABLE);
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + CSS.THEME.FLEX);
				//Encode o conteudo do Header definido no FACET HEADER_LEFT
				if (!DBSObject.isNull(xHeaderLeft)){
					pWriter.startElement("div", pDialog);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LEFT + CSS.THEME.FLEX_COL);
						xHeaderLeft.encodeAll(pContext);
					pWriter.endElement("div");
				}
				//Encode do icon + caption
					pvEncodeCaption(pDialog, pType, pWriter);
//				}
				//Encode o conteudo do Header definido no FACET HEADER_RIGHT
				if (!DBSObject.isNull(xHeaderRight)){
					pWriter.startElement("div", pDialog);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.RIGHT + CSS.THEME.FLEX_COL);
						xHeaderRight.encodeAll(pContext);
					pWriter.endElement("div");
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private boolean pvHasHeader(DBSDialog pDialog, UIComponent pFacetHeaderRight, UIComponent pFacetHeaderLeft){
		if (pFacetHeaderLeft == null
		 && pFacetHeaderRight == null
		 && DBSObject.isEmpty(pDialog.getCaption())){
			return false;
		}
		return true;
	}
	private void pvEncodeCaption(DBSDialog pDialog, TYPE pType, ResponseWriter pWriter) throws IOException{
		MESSAGE_TYPE xMsgType = MESSAGE_TYPE.get(pDialog.getMsgType());
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.THEME.FLEX_COL);
			//Set padding do cabeçalho iqual ao padding interno do content
			if (!DBSObject.isEmpty(pDialog.getCaption())){
				DBSFaces.encodeAttribute(pWriter, "style", pvGetPaddingHeader(pDialog));
			}
			//Icon
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON);
				pWriter.startElement("div", pDialog);
					if (pType == TYPE.MSG
					 && xMsgType != null
				  	 && (pDialog.hasMessage() || pDialog.getChildCount() > 0)){
						//Icone do tipo de mensagem
						DBSFaces.encodeAttribute(pWriter, "class", xMsgType.getIconClass());
					}else{
						//Icone principal
						DBSFaces.encodeAttribute(pWriter, "class", pDialog.getIconClass());
					}
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Label
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
				//Se não for mensagem padrão, usa caption informada pelo usuário
				if (xMsgType == null && pDialog.getCaption() != null){
					pWriter.write(pDialog.getCaption());
				//Tipo de mensagem como caption
//				}else if (xMsgType != null){
//					pWriter.write(xMsgType.getName());
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
		
	}
	

	private void pvEncodeHandle(DBSDialog pDialog, TYPE pType, ResponseWriter pWriter) throws IOException{
		//Não cria bar de fechar se for MOD ou existir Toolbar
		MESSAGE_TYPE xMsgType = MESSAGE_TYPE.get(pDialog.getMsgType());
//		if (xMsgType.getRequireConfirmation()){
		if (pType == TYPE.NAV 
		|| pType == TYPE.BTN 
		|| (pType == TYPE.MSG && (xMsgType == null || !xMsgType.getIsQuestion()))){
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
		if (xToolbar != null 
		 || pType == TYPE.MOD
		 || pType == TYPE.MSG){
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR);
				if (pType == TYPE.MSG){
					pvEncodeToolbarMSGControls(pDialog, pContext, pWriter);
//				}else if (pType == TYPE.MOD){
//					if (xToolbar == null){
//						pvEncodeToolbarSimpleButtonOk(pDialog, pWriter);
//					}
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
//	if (pDialog.getChildren().size() > 0){
//		//Encode dos conteúdo
//		DBSFaces.renderChildren(pContext, pDialog);
//	}else if (pDialog.getDBSMessages() != null && pDialog.getDBSMessages().size() > 0){
//		pWriter.write(pDialog.getDBSMessages().getListMessage().get(0).getMessageText());
//	}else{
//		pDialog.encodeChildren(pContext);
//	}
	}

	/**
	 * Botão padrão do close quando não existir toolbar em MOD e MSG(Center)
	 * @param pDialog
	 * @param pWriter
	 * @throws IOException
	 */
//	private void pvEncodeToolbarSimpleButtonOk(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
//		//Só faz o encode se for MOD
//		String xClass = "-btok -i_ok -close" + CSS.THEME.ACTION;
//		//Exibe espaço do button ok
//		pWriter.startElement("div", pDialog);
//			DBSFaces.encodeAttribute(pWriter, "class", xClass);
//		pWriter.endElement("div");
//	}

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
		getFacets().remove(DBSDialog.INPUT_MSGKEY);
		HtmlInputHidden xInput = (HtmlInputHidden) pContext.getApplication().createComponent(HtmlInputHidden.COMPONENT_TYPE);
		xInput.setId(DBSDialog.INPUT_MSGKEY);
		xInput.setValue(pDialog.getDBSMessages().getListMessage().get(0).getMessageKey());
		getFacets().put(DBSDialog.INPUT_MSGKEY, xInput);

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
		//Exclui botões se já existirem
//		UIComponent xBtYes = getFacets().get(DBSDialog.BUTTON_YES);
//		if (xBtYes != null){
//			System.out.println("Achou Yes");
//		}
//		UIComponent xBtNo = getFacets().get(DBSDialog.BUTTON_NO);
//		if (xBtNo != null){
//			System.out.println("Achou No");
//		}
		getFacets().remove(DBSDialog.BUTTON_NO);
		getFacets().remove(DBSDialog.BUTTON_YES);
		
		//Cria botões
		String xStyle = "";

		DBSUICommand xActionSource = null;
		//Se o action chamou método do bean controlado por actionController, força que o botão seja o mesmo action original para que o método seja chamado novamente após a confirmação
		if (DBSBoolean.toBoolean(FacesContext.getCurrentInstance().getAttributes().get(FACESCONTEXT_ATTRIBUTE.ACTION_CONTROLLED))){
			xActionSource = (DBSUICommand) pContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE);
		};
		DBSUICommand xActionSourceNO = xActionSource;
		DBSUICommand xActionSourceYES = xActionSource;
		//DOIS BOTÕES - NÃO(NO) e SIM(YES)
		if (xMsgType != null && xMsgType.getIsQuestion()){
			//BOTÃO - NÃO(NO)
			if (xMsgType.getIsError()){
				//Não utiliza o action do botão que originou este dialog se mensagem for erro. Erro impede que action seja efetuado.
				xActionSourceNO = null;
			}
			pvEncodeMsgButton(pContext, DBSDialog.BUTTON_NO, "Não","-i_no -red", null, pDialog.getClientId(), xActionSourceNO);
		//UM BOTÃO - SIM(YES)
		}else{
			xStyle = "width:0;height:0;position:absolute;"; //Exibe sem dimensão somente para poder receber o keydown
//			xStyle = "display:none;";
			//Se esta é a última mensagem para ser validada.
			if (pDialog.getDBSMessages() == null 
			 || pDialog.getDBSMessages().notValidatedSize() <= 1){
				//Não utiliza o action do botão que originou este dialog se mensagem for erro. Erro impede que action seja efetuado.
				if (xMsgType == null || xMsgType.getIsError()){
						xActionSourceYES = null;
	//					pvEncodeMsgButtonRecall(pContext, DBSDialog.BUTTON_YES, "Sim","-i_yes -green", xStyle, pDialog.getClientId(), xActionSourceYES);
	//					return;
				}
			}
		}
		//BOTÃO - SIM(YES)
		pvEncodeMsgButton(pContext, DBSDialog.BUTTON_YES, "Sim","-i_yes -green", xStyle, pDialog.getClientId(), xActionSourceYES); 
	}

	
	private void pvEncodeMsgButton(FacesContext pContext, 
								   String 		pId, 
								   String 		pLabel, 
								   String 		pIconClass, 
								   String 		pStyle, 
								   String 		pExecute, 
								   DBSUICommand pActionSource) throws IOException{
		DBSButton xBtn = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
//		xBtn.setTransient(true);
		xBtn.setId(pId);
		xBtn.setLabel(pLabel);
		xBtn.setIconClass(CSS.MODIFIER.ICON + pIconClass);
		xBtn.setStyle(pStyle);
//		xBtn.setExecute("@this " + pExecute + ":" + DBSDialog.INPUT_MSGKEY);
//		xBtn.setExecute(pExecute);
//		xBtn.setExecute(pExecute);
		xBtn.setExecute(getClientId());
		if (pActionSource == null){
			xBtn.setUpdate("@none");
		}else{
			xBtn.setActionExpression(pActionSource.getActionExpression());
			String xUpdate = DBSObject.getNotNull(pActionSource.getUpdate() , "");
			if (xUpdate.indexOf(pExecute) == -1){
				xUpdate += " :" + pExecute;
			}
			xBtn.setUpdate(xUpdate);
			
//			xBtn.setUpdate(pExecute);
		}
		xBtn.setCloseDialog(true);
		getFacets().put(pId, xBtn);
//		getChildren().add(xBtn);
		xBtn.encodeAll(pContext);
	}
	
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