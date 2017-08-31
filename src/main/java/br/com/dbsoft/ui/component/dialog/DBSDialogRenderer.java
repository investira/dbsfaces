package br.com.dbsoft.ui.component.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.DBSPassThruAttributes;
import br.com.dbsoft.ui.component.DBSPassThruAttributes.Key;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.dialog.DBSDialog.POSITION;
import br.com.dbsoft.ui.component.dialog.DBSDialog.TYPE;
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
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xDialog.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xDialog.getType());
			
			//Configura time somente quando não for MOD
			if (xType == TYPE.MSG 
			&& !xDialog.getCloseTimeout().equals("0")){
				DBSFaces.encodeAttribute(xWriter, "ct", xDialog.getCloseTimeout());
			}
			DBSFaces.encodeAttribute(xWriter, "p", xDialog.getPosition());
			DBSFaces.encodeAttribute(xWriter, "cs", xDialog.getContentSize());
			DBSFaces.encodeAttribute(xWriter, "ca", xDialog.getContentAlignment());

			if (xDialog.getOpen()){
				DBSFaces.encodeAttribute(xWriter, "o", true);
			}
			RenderKitUtils.renderPassThruAttributes(pContext, xWriter, xDialog, DBSPassThruAttributes.getAttributes(Key.DIALOG));

			pvEncodeContainer(xDialog, xType, pContext, xWriter);
			
			pvEncodeJS(xDialog, xWriter);
		xWriter.endElement("div");	
	}
	
	
	private void pvEncodeContainer(DBSDialog pDialog, TYPE pType, FacesContext pContext, ResponseWriter pWriter) throws IOException {
		if (pType == TYPE.MSG && pDialog.getChildCount() == 0 && !pDialog.hasMessage()){return;}
		String xClass = CSS.MODIFIER.CONTAINER;
		pWriter.startElement("div", pDialog);
//			if (!pDialog.getOpen()){
				DBSFaces.encodeAttribute(pWriter, "style", "opacity:0"); //Inicia escondido para ser exibido após a execução do JS
				xClass += CSS.MODIFIER.CLOSED;
//			}
			DBSFaces.encodeAttribute(pWriter, "class", xClass);
			
			//Icon
			pvEncodeIcon(pDialog, pWriter);
			
			//Mask
			pWriter.startElement("div", pDialog);
				xClass = CSS.MODIFIER.MASK;
				DBSFaces.encodeAttribute(pWriter, "class", xClass);
				pWriter.startElement("div", pDialog);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				pWriter.endElement("div");
			pWriter.endElement("div");
			
			//Encode do conteúdo
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
				pDialog.setOpen(pvInitializeDBSMessages(pDialog, pContext));
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
	private boolean pvInitializeDBSMessages(DBSDialog pDialog, FacesContext pContext) throws IOException{
		IDBSMessages xMesssages = DBSMessagesFacesContext.getMessages(pDialog.getMsgFor());
		pDialog.setDBSMessages(xMesssages); 
		
		if (xMesssages != null && xMesssages.size() > 0){
			//Primeira mensagem
			pDialog.setMsgType(xMesssages.getListMessage().get(0).getMessageType().getCode());
			return true;
		}else{
			return false;
		}
	}

	
	private void pvEncodeIcon(DBSDialog pDialog, ResponseWriter pWriter) throws IOException{
		pWriter.startElement("div", pDialog);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON + CSS.THEME.ACTION);
			pWriter.startElement("div", pDialog);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT + DBSObject.getNotNull(pDialog.getIconClass(), ""));
			pWriter.endElement("div");
		pWriter.endElement("div");
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
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xDialogId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_dialog(xDialogId); \n" +
	                 "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}

}




