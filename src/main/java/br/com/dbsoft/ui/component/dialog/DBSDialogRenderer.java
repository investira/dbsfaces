package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.dialog.DBSDialog.POSITION;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;

/**
 * @author ricardo.villar
 *
 */
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

		pvInitialize(xDialog, pContext);
		
		if (!pvIsValid(xDialog)){return;}

		ResponseWriter 		xWriter = pContext.getResponseWriter();
		String 				xClass = CSS.DIALOG.MAIN + CSS.MODIFIER.CLOSED;
		TYPE 	 			xType = TYPE.get(xDialog.getType());

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
			//Configura time somente quando não for MOD
			if (xType == TYPE.MSG 
			&& !xDialog.getCloseTimeout().equals("0")){
				DBSFaces.setAttribute(xWriter, "timeout", xDialog.getCloseTimeout());
			}
			DBSFaces.setAttribute(xWriter, "p", xDialog.getPosition());
			DBSFaces.setAttribute(xWriter, "cs", xDialog.getContentSize());
			DBSFaces.setAttribute(xWriter, "ca", xDialog.getContentAlignment());
			if (xDialog.getOpen()){
				DBSFaces.setAttribute(xWriter, "open", "open");
			}
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
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		DBSDialog 		xDialog = (DBSDialog) pComponent;
		if (!pvIsValid(xDialog)){return;}

				//FAZ O ENCODE FINAL
				pvEncodeContentEnd(xDialog, pContext, xWriter);
				xWriter.endElement("div");
			pvEncodeJS(xDialog.getClientId(pContext), xWriter);
		xWriter.endElement("div");
	}
	
	private void pvEncodeContentBegin(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		TYPE xType = TYPE.get(pDialog.getType());
		String xClass = CSS.MODIFIER.CONTENT + CSS.THEME.FC + CSS.THEME.BC + pDialog.getContentStyleClass();
		if (xType == TYPE.MSG){
			xClass += CSS.THEME.INVERT;
		}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", xClass); 
			//Header
			pvEncodeHeader(pDialog, xType, pContext, pWriter);
			//Nav
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTAINER, null);
				pWriter.startElement("div", pDialog);
					pWriter.startElement("div", pDialog);
						pWriter.startElement("div", pDialog);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTENT + CSS.MODIFIER.CLOSED, null);
							DBSFaces.setAttribute(pWriter, "style", "padding:" + pDialog.getContentPadding(), null);
							pvEncodeChildren(pDialog, pContext, pWriter);
	}
	
	private void pvEncodeContentEnd(DBSDialog pDialog, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		TYPE xType = TYPE.get(pDialog.getType());

						pWriter.endElement("div");
					pWriter.endElement("div");
				pWriter.endElement("div");
			pWriter.endElement("div");
			//Footer
			pvEncodeFooter(pDialog, xType, pContext, pWriter);
			//ButtonBack
			pvEncodeButtonBack(pDialog, xType, pWriter);
			//ButtonClose
			pvEncodeButtonClose(pDialog, xType, pWriter);
		pWriter.endElement("div");
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

	private void pvEncodeHeader(DBSDialog pDialog, TYPE pType, FacesContext pContext,  ResponseWriter pWriter) throws IOException{
		UIComponent xHeader = pDialog.getFacet(DBSDialog.FACET_HEADER);
		if (xHeader == null
		 && DBSObject.isEmpty(pDialog.getCaption())){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.setAttribute(pWriter, "style", pvGetPaddingHeader(pDialog));
				pvEncodeCaption(pDialog, pType, pWriter);
				//Encode o conteudo do Header definido no FACET HEADER
				if (!DBSObject.isNull(xHeader)){
					xHeader.encodeAll(pContext);
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeCaption(DBSDialog pDialog, TYPE pType, ResponseWriter pWriter) throws IOException{
		if (DBSObject.isEmpty(pDialog.getCaption())){return;}
		
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.MODIFIER.NOT_SELECTABLE);
			//Label
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
				pWriter.write(pDialog.getCaption());
			pWriter.endElement("div");
			//Icone do tipo de mensagem
			if(pType == TYPE.MSG
		  	&& pvHasMessage(pDialog)){
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON);
					pWriter.startElement("div", pDialog);
						DBSFaces.setAttribute(pWriter, "class", MESSAGE_TYPE.get(pDialog.getMsgType()).getIconClass());
					pWriter.endElement("div");
				pWriter.endElement("div");
			}
		pWriter.endElement("div");
	}

	private void pvEncodeButtonClose(DBSDialog pDialog, TYPE pType, ResponseWriter pWriter) throws IOException{
		//Não cria bar de fechar se for MOD ou existir Toolbar
		if (pType == TYPE.NAV 
		|| (pType == TYPE.MSG && (pDialog.getFacet(DBSDialog.FACET_TOOLBAR) == null || pDialog.getFacet(DBSDialog.FACET_TOOLBAR).getChildCount() <= 1))){
			String xClass = "-btclose" + CSS.THEME.ACTION;
			//Exibe espaço do button timeout
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", xClass);
	//			DBSFaces.setAttribute(pWriter, "style", xStyle);
			pWriter.endElement("div");
		}
	}

	private void pvEncodeButtonBack(DBSDialog pDialog, TYPE pType, ResponseWriter pWriter) throws IOException{
		//Exibe botão de back
		if (pType == TYPE.MOD){
			String xClass = "-btback" + CSS.THEME.ACTION;
			String xStyle = null;
			xClass += "-i_navigate_previous";
			xStyle = "padding:" + pDialog.getContentPadding();
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", xClass);
				DBSFaces.setAttribute(pWriter, "style", xStyle);
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
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.FOOTER);
			if (xFooter != null){
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "style", pvGetPaddingFooter(pDialog));
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
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
		 || (pType == TYPE.MSG && POSITION.get(pDialog.getPosition()) == POSITION.CENTER)){
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR);
				if (xToolbar == null){
					pvEncodeButtonOk(pDialog, pWriter);
				}else{
					xToolbar.encodeAll(pContext);
				}
			pWriter.endElement("div");
		}
	}
	
	/**
	 * Botão padrão do close
	 * @param pDialog
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeButtonOk(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		//Só faz o encode se for MOD
		String xClass = "-btok -i_ok" + CSS.THEME.ACTION;
		//Exibe espaço do button timeout
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", xClass);
		pWriter.endElement("div");
	}
	
	/**
	 * javaScript
	 * @param pClientId
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xDialogId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_dialog(xDialogId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
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
	
	private boolean pvIsValid(DBSDialog pDialog){
		TYPE 	 xType = TYPE.get(pDialog.getType());
		POSITION xPosition = POSITION.get(pDialog.getPosition());
		if (xType == TYPE.NAV){
			if (xPosition == POSITION.CENTER){
				pvEncodeError(pDialog, xType, xPosition, "use type 'msg' or 'dialog'");
				return false;
			}
		}else if(xType == TYPE.MSG){
			return pvHasMessage(pDialog);
		}
		return true;
	}
	
	private void pvEncodeError(DBSDialog pDialog, TYPE pType, POSITION pPosition, String pString){
		wLogger.error("DBSDialog\tid=" +  pDialog.getClientId() + ":type=" + pType + ",p=" + pPosition + " not allowed." + pString);
	}
	
	private boolean pvHasMessage(DBSDialog pDialog){
		if ((pDialog.getListDBSMessage() != null && pDialog.getListDBSMessage().size() > 0)
		 || (pDialog.getListFacesMessage() != null && pDialog.getListFacesMessage().size() > 0)
		 || pDialog.getChildren().size() > 0){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends IDBSMessage> void pvInitialize(DBSDialog pDialog, FacesContext pContext) throws IOException{
		pDialog.setListDBSMessage(null);
		pDialog.setListFacesMessage(null);
		TYPE 	 xType = TYPE.get(pDialog.getType());
		//Configura mensagem vinda por facesmessage ou children
		if(xType == TYPE.MSG){
			//É mensagem FacesMessage
			if (!DBSObject.isEmpty(pDialog.getMsgFor())){
				//Se houver mensagem DBSMessages
				if (pContext.getAttributes().get(IDBSMessage.ATTRIBUTE_NAME) != null){
					@SuppressWarnings("rawtypes")
					IDBSMessages xDBSMessages = (IDBSMessages) pContext.getAttributes().get(IDBSMessage.ATTRIBUTE_NAME);
					List<T> xDMsgs = null;
					//Mensagens globais 
					if (pDialog.getMsgFor().equals(DBSDialog.MSG_FOR_GLOBAL)){
						//Se existe mensagem sem componente(clientid) definito
						xDMsgs = xDBSMessages.getMessagesForClientId(null);
					//Mensagem para componente(clientid) 
					}else if (!pDialog.getMsgFor().equals(DBSDialog.MSG_FOR_ALL)){
						//Se existe mensagem para o componente(clientid)
						xDMsgs = xDBSMessages.getMessagesForClientId(pDialog.getMsgFor());
					//Todas as mensagens
					}else{
						xDMsgs = xDBSMessages.getMessages();
					}
					pDialog.setListDBSMessage(xDMsgs);
					if (xDMsgs != null && xDMsgs.size() > 0){
						pDialog.setCaption(xDMsgs.get(0).getMessageType().getName());
						pDialog.setMsgType(xDMsgs.get(0).getMessageType().getCode());
						pDialog.setOpen(true);
					}
				//Se houver mensagem FacesMessage
				}else if (pContext.getMessageList().size() > 0){
					List<FacesMessage> xFMsg = null;
					//Mensagens globais 
					if (pDialog.getMsgFor().equals(DBSDialog.MSG_FOR_GLOBAL)){
						//Se existe mensagem sem componente(clientid) definito
						xFMsg = pContext.getMessageList(null);
					//Mensagem para componente(clientid) 
					}else if (!pDialog.getMsgFor().equals(DBSDialog.MSG_FOR_ALL)){
						//Se existe mensagem para o componente(clientid)
						xFMsg = pContext.getMessageList(pDialog.getMsgFor());
					//Todas as mensagens
					}else{
						xFMsg = pContext.getMessageList();
					}
					pDialog.setListFacesMessage(xFMsg);
					//Configura para exibir o dialog já aberto/
					if (xFMsg != null && xFMsg.size() > 0){
						if (xFMsg.get(0).getSeverity() == FacesMessage.SEVERITY_ERROR
						 || xFMsg.get(0).getSeverity() == FacesMessage.SEVERITY_FATAL){
							pDialog.setMsgType(MESSAGE_TYPE.ERROR.getCode());
							pDialog.setCaption(MESSAGE_TYPE.ERROR.getName());
						}else if (xFMsg.get(0).getSeverity() == FacesMessage.SEVERITY_WARN){
							pDialog.setMsgType(MESSAGE_TYPE.WARNING.getCode());
							pDialog.setCaption(MESSAGE_TYPE.WARNING.getName());
						}else if (xFMsg.get(0).getSeverity() == FacesMessage.SEVERITY_INFO){
							pDialog.setMsgType(MESSAGE_TYPE.INFORMATION.getCode());
							pDialog.setCaption(MESSAGE_TYPE.INFORMATION.getName());
						}
						pDialog.setOpen(true);
					}
				}
			}else{
				if (pDialog.getChildCount() > 0){
					pDialog.setCaption(MESSAGE_TYPE.get(pDialog.getMsgType()).getName());
					pDialog.setOpen(true);
				}
			}
		}
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
		}else if (pDialog.getListDBSMessage() != null){
			for (IDBSMessage xMsg:pDialog.getListDBSMessage()){
				pWriter.write(xMsg.getMessageText());
			}
		}else if (pDialog.getListFacesMessage() != null){
			for (FacesMessage xMsg:pDialog.getListFacesMessage()){
				pWriter.write(xMsg.getDetail());
			}
		}
	}

}




