package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.dialog.DBSDialog.POSITION;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.ui.core.DBSMessagesFacesContext;
import br.com.dbsoft.util.DBSObject;

/**
 * @author ricardo.villar
 *
 */
@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSDialog.RENDERER_TYPE)
public class DBSDialogRenderer extends DBSRenderer{
	

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSDialog xDialog = (DBSDialog) pComponent;
		String xClientId = pComponent.getClientId(pContext);
		//Se houver mensagem a ser validada.
		if (xDialog.getDBSMessage() != null){
			String xSourceId = DBSFaces.getDecodedSourceId(pContext);
			//Se decode foi disparado em função de uma ação
			if (xSourceId != null){
				String xInputMsgKey = xClientId + ":" + DBSDialog.INPUT_MSGKEY;
				String xMsgKey = DBSFaces.getDecodedComponenteValue(pContext, xInputMsgKey);
				//Se existe alguma mensagem sendo validada
				if (xMsgKey != null){
					if (xSourceId.equals(xClientId + ":" + DBSDialog.BUTTON_NO)){
						xDialog.getDBSMessage().setMessageValidated(false);
					} else if (xSourceId.equals(xClientId + ":" + DBSDialog.BUTTON_YES)){
						xDialog.getDBSMessage().setMessageValidated(true);
					}
				}
			}
		}
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
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		String 				xClass = CSS.DIALOG.MAIN + CSS.MODIFIER.CLOSED;
		TYPE 	 			xType = TYPE.get(xDialog.getType());
		String 				xClientId = xDialog.getClientId(pContext);

		pvInitialize(xDialog, pContext);

		if (!pvIsValid(xDialog)){return;}

		if (xDialog.getStyleClass()!=null){
			xClass += xDialog.getStyleClass();
		}
		
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

			pvEncodeContent(xDialog, xType, pContext, xWriter);
			
			pvEncodeJS(xDialog.getClientId(pContext), xWriter);
		xWriter.endElement("div");	
	}
	
	
	public void pvEncodeContent(DBSDialog pDialog, TYPE pType, FacesContext pContext, ResponseWriter pWriter) throws IOException {
		if (pType == TYPE.MSG && !pvHasMessage(pDialog)){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "style", "opacity:0", null); //Inicia escondido para ser exibido após a execução do JS
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER + CSS.MODIFIER.CLOSED);
			//Icon
			pvEncodeIcon(pDialog, pWriter);
			//Mask
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC + CSS.THEME.INVERT);
			pWriter.endElement("div");
			//Nav
			pvEncodeContentBegin(pDialog, pContext, pWriter);
			
			//FAZ O ENCODE FINAL
			pvEncodeContentEnd(pDialog, pContext, pWriter);
		pWriter.endElement("div");
	}
	
	private void pvInitialize(DBSDialog pDialog, FacesContext pContext) throws IOException{
//		pDialog.setDBSMessage(null);
		TYPE 	 xType = TYPE.get(pDialog.getType());
		//Configura mensagem vinda por facesmessage ou children
		if(xType == TYPE.MSG){
			//É mensagem FacesMessage ou DBSMessage
			if (!DBSObject.isEmpty(pDialog.getMsgFor())){
				//Recupera as mensagens
//				pDialog.setOpen(pDialog.getDBSMessage() != null && !DBSObject.isEmpty(pDialog.getDBSMessage().getMessageKey()));
				pDialog.setOpen(pvInitializeDBSMessage(pDialog, pContext));
			}else{
				pDialog.setOpen(pDialog.getChildCount() > 0);
			}
		}
	}

	/**
	 * Configura localmente as mensagem enviadas como DBSMessage
	 * @param pDialog
	 * @param pContext
	 * @return
	 * @throws IOException
	 */
	private boolean pvInitializeDBSMessage(DBSDialog pDialog, FacesContext pContext) throws IOException{
		IDBSMessage xMsg = DBSMessagesFacesContext.getMessage(pDialog.getMsgFor());
		pDialog.setDBSMessage(xMsg);
		
		if (xMsg != null){
			pDialog.setMsgType(xMsg.getMessageType().getCode());
			return true;
		}else{
			return false;
		}
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
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SUB_CONTENT, null);
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
			//ButtonClose
			pvEncodeHandle(pDialog, xType, pWriter);
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
		UIComponent xHeaderLeft = pDialog.getFacet(DBSDialog.FACET_HEADER_LEFT);
		UIComponent xHeaderRight = pDialog.getFacet(DBSDialog.FACET_HEADER_RIGHT);
		if (xHeaderLeft == null
		 && xHeaderRight == null
		 && pDialog.getMsgType() == null
		 && DBSObject.isEmpty(pDialog.getCaption())){return;}
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				DBSFaces.setAttribute(pWriter, "style", pvGetPaddingHeader(pDialog));
					pvEncodeCaption(pDialog, pType, xHeaderRight, xHeaderLeft, pContext, pWriter);
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	private void pvEncodeCaption(DBSDialog pDialog, TYPE pType, UIComponent pFacetHeaderRight, UIComponent pFacetHeaderLeft, FacesContext pContext, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.MODIFIER.NOT_SELECTABLE);
			if (pDialog.getMsgType() != null
			 || !DBSObject.isEmpty(pDialog.getCaption())){
				//Label
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LABEL);
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
		  	&& pvHasMessage(pDialog)){
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.ICON);
					pWriter.startElement("div", pDialog);
						DBSFaces.setAttribute(pWriter, "class", MESSAGE_TYPE.get(pDialog.getMsgType()).getIconClass());
					pWriter.endElement("div");
				pWriter.endElement("div");
			}
			
			//Encode o conteudo do Header definido no FACET HEADER_LEFT
			if (!DBSObject.isNull(pFacetHeaderLeft)){
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.LEFT);
					pFacetHeaderLeft.encodeAll(pContext);
				pWriter.endElement("div");
			}
			//Encode o conteudo do Header definido no FACET HEADER_RIGHT
			if (!DBSObject.isNull(pFacetHeaderRight)){
				pWriter.startElement("div", pDialog);
					DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.RIGHT);
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
		|| (pType == TYPE.MSG && !xMsgType.getRequireConfirmation())){
			String xClass = "-bthandle" + CSS.THEME.ACTION;
			//Exibe espaço do button timeout
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", xClass);
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
		 || pType == TYPE.MSG){
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR);
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
//	if(pType == TYPE.MSG
//		  	&& pvHasMessage(pDialog)){
//						pvEncodeInputHiddenMessageKey(pDialog, pContext, pWriter);
//			}
	
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
		}else if (pDialog.getDBSMessage() != null){
			pWriter.write(pDialog.getDBSMessage().getMessageText());
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
		//Exibe espaço do button timeout
		pWriter.startElement("div", pDialog);
			DBSFaces.setAttribute(pWriter, "class", xClass);
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
		if (pDialog.getDBSMessage() != null){
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
			xInput.setValue(pDialog.getDBSMessage().getMessageKey());
			pDialog.getFacets().put(DBSDialog.INPUT_MSGKEY, xInput);
		}
		xInput.encodeAll(pContext);
	}

	private void pvEncodeMsgButtons(DBSDialog pDialog, FacesContext pContext) throws IOException{
		MESSAGE_TYPE xMsgType = MESSAGE_TYPE.get(pDialog.getMsgType());
		String xStyle = "";
		if (xMsgType.getRequireConfirmation()){
			pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_NO, "Não","-i_no -red", null, null);
		}else{
			xStyle = "display:none;";
		}
		pvEncodeMsgButton(pDialog, pContext, DBSDialog.BUTTON_YES, "Sim","-i_yes -green", null, xStyle);
	}


	private void pvEncodeMsgButton(DBSDialog pDialog, FacesContext pContext, String pId, String pLabel, String pIconClass, String pStyleClass, String pStyle) throws IOException{
//		String		xClientId = pDialog.getClientId(pContext);
		DBSButton 	xBtn = (DBSButton) pDialog.getFacet(pId); 
		//Verifica se botão já havia sido criado
		if (xBtn == null){
			xBtn = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xBtn.setId(pId);
			xBtn.setLabel(pLabel);
			xBtn.setStyleClass("-close " + DBSObject.getNotEmpty(pStyleClass, ""));
			xBtn.setStyle(pStyle);
			xBtn.setIconClass(CSS.MODIFIER.ICON + pIconClass);
//			//Se for EL...
//			if (pMethod.startsWith("#")){
//				xBtn.setActionExpression(DBSFaces.createMethodExpression(pContext, pMethod, String.class, new Class[0]));
//			//Se for um simples redirect para outra página
//			}else if (!DBSObject.isEmpty(pMethod)){
//				MethodExpression xME = pContext.getApplication().getExpressionFactory().createMethodExpression(pContext.getELContext(), pMethod, String.class, new Class[0]);
//				xBtn.setActionExpression(xME);
//			}
			xBtn.setUpdate("@none");
			xBtn.setExecute(pDialog.getClientId());
			//Inclui botão com facet do modal para poder separa-lo dos componentes filhos criados pelo usuário.
			pDialog.getFacets().put(pId, xBtn);
		}
		xBtn.encodeAll(pContext);
	}
	
	private boolean pvIsValid(DBSDialog pDialog){
		TYPE 	 xType = TYPE.get(pDialog.getType());
		POSITION xPosition = POSITION.get(pDialog.getPosition());
		if (xType == TYPE.NAV){
			if (xPosition == POSITION.CENTER){
				pvEncodeError(pDialog, xType, xPosition, "use type 'msg' or 'dialog'");
				return false;
			}
		}
		return true;
	}
	
	private boolean pvHasMessage(DBSDialog pDialog){
		if (pDialog.getDBSMessage() != null
		 || pDialog.getChildren().size() > 0){
			return true;
		}
		return false;
	}
	
	private void pvEncodeError(DBSDialog pDialog, TYPE pType, POSITION pPosition, String pString){
		wLogger.error("DBSDialog\tid=" +  pDialog.getClientId() + ":type=" + pType + ",p=" + pPosition + " not allowed." + pString);
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

}




