package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.dialog.DBSDialog.POSITION;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;

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


			pvEncodeContainer(xDialog, xType, pContext, xWriter);
			
			pvEncodeJS(xDialog.getClientId(pContext), xWriter);
		xWriter.endElement("div");	
	}
	
	
	private void pvEncodeContainer(DBSDialog pDialog, TYPE pType, FacesContext pContext, ResponseWriter pWriter) throws IOException {
		if (pType == TYPE.MSG && !pDialog.hasMessage()){return;}
		String xClass = CSS.MODIFIER.CONTAINER;
		pWriter.startElement("div", pDialog);
//			if (!pDialog.getOpen()){
				DBSFaces.setAttribute(pWriter, "style", "opacity:0", null); //Inicia escondido para ser exibido após a execução do JS
				xClass += CSS.MODIFIER.CLOSED;
//			}
			DBSFaces.setAttribute(pWriter, "class", xClass);
			//Icon
			pvEncodeIcon(pDialog, pWriter);
			//Mask
			pWriter.startElement("div", pDialog);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.MASK + CSS.THEME.BC + CSS.THEME.INVERT);
			pWriter.endElement("div");
//			pvEncodeContent(pDialog, pType, pContext, pWriter);
			
			DBSDialogContent xContent = (DBSDialogContent) pDialog.getFacet(DBSDialog.FACET_CONTENT); 
			xContent.encodeAll(pContext);
			
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




