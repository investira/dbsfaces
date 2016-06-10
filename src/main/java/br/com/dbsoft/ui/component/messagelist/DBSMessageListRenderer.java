package br.com.dbsoft.ui.component.messagelist;


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily = DBSFaces.FAMILY, rendererType = DBSMessageList.RENDERER_TYPE)
public class DBSMessageListRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent); 
		DBSMessageList 	xMessageList = (DBSMessageList) pComponent;
		//Verifica se foi um submit de um dos botões de exclusão e exclui o item se for

		String 			xInputFooId = pvGetInputFooId(pContext, xMessageList);
		String			xSourceId = pContext.getExternalContext().getRequestParameterMap().get(DBSFaces.PARTIAL_SOURCE_PARAM);
		if (xSourceId !=null && xSourceId.startsWith(xInputFooId)){
			String xMessageKey = pContext.getExternalContext().getRequestParameterMap().get(xInputFooId);
			if (xMessageKey!=null){
				//Seta valor da mensagem 
				xMessageList.setMessageKey(xMessageKey);
				//Exclui mensagem da lista
				xMessageList.getValue().remove(xMessageKey);
				xMessageList.setDeleting(true);
			}else{
				xMessageList.setMessageKey("");
			}
		}
	}
	
	@Override
	public boolean getRendersChildren() {
		return true; // True=Chama o encodeChildren abaixo e interrompe a busca
						// por filho pela rotina renderChildren
	}

	@Override
	public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {}


	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()) {
			return;
		}
		DBSMessageList 	xMessageList = (DBSMessageList) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClientId = xMessageList.getClientId(pContext);

		String xClass = CSS.MESSAGELIST.MAIN + xMessageList.getStyleClass();
		String xStyle = xMessageList.getStyle();

		MESSAGE_TYPE xType = MESSAGE_TYPE.INFORMATION; //Padrão
		Integer xCount = 0;
		Integer xNew = 0;
		//Encode principal
		xWriter.startElement("div", xMessageList);
			DBSFaces.setAttribute(xWriter, "id", xClientId);
			DBSFaces.setAttribute(xWriter, "name", xClientId);
			DBSFaces.setAttribute(xWriter, "class", xClass);
			if (!DBSObject.isEmpty(xStyle)){
				DBSFaces.setAttribute(xWriter, "style", xStyle);
			}

			//Verica se há mensagem a ser exibida
			if (xMessageList.getValue() != null){
				xCount = xMessageList.getValue().getMessages().size();
				if (xCount > 0){
					IDBSMessage xMsg;
					@SuppressWarnings("unchecked")
					Iterator<Entry<String, IDBSMessage>> xI = xMessageList.getValue().iterator();
					while (xI.hasNext()){
						 xMsg = xI.next().getValue();
						 //Se msg ainda não foi validada(visualizada, neste caso)
						 if (xMsg.isMessageValidated() == null 
						  || !xMsg.isMessageValidated()){
							 //Conta a quantidade de mensagens novas
							 xNew++;
							 xMsg.setMessageValidated(true); //Marca que mensagem como vista
						 }
						//Definie a cor do campo da quantidade de mensagens a partir da mensagem de maior revelância(error/warning/information). 
						 if (xMsg.getMessageType().getSeverity() > xType.getSeverity()){
							 xType = xMsg.getMessageType();
						 }
					}
					xWriter.startElement("div", xMessageList);
						DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
						//Encode do icone principal e o balão com o contador de mensagens
						xWriter.startElement("div", xMessageList);
							DBSFaces.setAttribute(xWriter, "class", CSS.MODIFIER.BUTTON);
							//Exibe Ícone
							xWriter.startElement("div", xMessageList);
								DBSFaces.setAttribute(xWriter, "class", "-i_message");
							xWriter.endElement("div");
							//Exibe contador de mensagens
							if (xCount > 0){
								xWriter.startElement("div", xMessageList);
									DBSFaces.setAttribute(xWriter, "class", "-count " + " -severity" + xType.getSeverity());
									xWriter.write(xCount.toString());
								xWriter.endElement("div");
							}
						xWriter.endElement("div");

						pvEncodeMessageList(pContext, xMessageList, xWriter);
					xWriter.endElement("div");
				}
			}
			pvEncodeMessageInputFoo(pContext, xMessageList, xWriter);
		xWriter.endElement("div");
		
		xMessageList.setDeleting(false); //Reset indicador que estava sendo efetuado um delete

		pvEncodeJS(xWriter, xClientId, (xNew > 0 && xType != MESSAGE_TYPE.INFORMATION)); //Emite beep se houver nova mensagem que não seja INFORMATION
	}
	
	private void pvEncodeMessageInputFoo(FacesContext pContext, DBSMessageList pMessageList, ResponseWriter pWriter) throws IOException {
		String xClientId = pMessageList.getClientId();
		String xFooId = pvGetInputFooId(pContext, pMessageList);
		//Botão que receberá a chave do item selecionado e efetuará o submit posteriormente via chamada por JS.
		pWriter.startElement("input", pMessageList);
			DBSFaces.setAttribute(pWriter, "id", xFooId);
			DBSFaces.setAttribute(pWriter, "name", xFooId);
			DBSFaces.setAttribute(pWriter, "type", "text");
			DBSFaces.setAttribute(pWriter, "class", "-foo");
			DBSFaces.setAttribute(pWriter, "autocomplete", "off");
			DBSFaces.setAttribute(pWriter, "onclick", DBSFaces.getSubmitString(pMessageList, "", xClientId, xClientId));
			DBSFaces.setAttribute(pWriter, "value", pMessageList.getMessageKey());
		pWriter.endElement("input");
	}
	/**
	 * Encode integral da tabela
	 * @param pContext
	 * @param pMessageList
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeMessageList(FacesContext pContext, DBSMessageList pMessageList, ResponseWriter pWriter) throws IOException {
		pWriter.startElement("div", pMessageList);
			String xClass = "-list " + CSS.BACK_GRADIENT_WHITE;
			if (pMessageList.getDeleting()){
				xClass+= CSS.MODIFIER.OPENED;
			}
			DBSFaces.setAttribute(pWriter, "class",  xClass, null);
			pWriter.startElement("div", pMessageList);
				DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				//Exibe a lista de mensagens em ordem invertida de inclusão(PEPS/LIFO), onde o mais recente é será exibido primeiro;
				@SuppressWarnings("unchecked")
				Collection<String> xCollection = pMessageList.getValue().getMessages().keySet();
				String[] 	xMsgKey = xCollection.toArray(new String[xCollection.size()]); 
				IDBSMessage xMsg;
				for (Integer xI=xMsgKey.length-1; xI!=-1; xI--){
					xMsg = (IDBSMessage) pMessageList.getValue().getMessages().get(xMsgKey[xI]);
					pWriter.startElement("div", pMessageList);
						DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.MESSAGE + " -severity" + xMsg.getMessageType().getSeverity());
						DBSFaces.setAttribute(pWriter, "index",xMsgKey[xI], null);
						pWriter.startElement("div", pMessageList);
							DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
							//Hora da mensagem
							pWriter.startElement("div", pMessageList);
								DBSFaces.setAttribute(pWriter, "class", "-time", null);
								if (xMsg.getMessageTime() !=null){
									pWriter.write(xMsg.getMessageTime().toLocalTime().toString("HH:mm"));
								}
							pWriter.endElement("div");
							//Texto da mensagem
							pWriter.startElement("div", pMessageList);
								DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
								pWriter.write(xMsg.getMessageText());
							pWriter.endElement("div");
							if (!pMessageList.getReadOnly()){
								//Botões
								pWriter.startElement("div", pMessageList);
									DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.BUTTON);
									//Botão de exclusão da mensagem
									pWriter.startElement("span", pMessageList);
										DBSFaces.setAttribute(pWriter, "class", "dbs_button ");
										pWriter.startElement("span", pMessageList);
											DBSFaces.setAttribute(pWriter, "class", "-i_delete");
										pWriter.endElement("span");
									pWriter.endElement("span");
								pWriter.endElement("div");
							}
						pWriter.endElement("div");
					pWriter.endElement("div");
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}
	
	private String pvGetInputFooId(FacesContext pContext, DBSMessageList pDataTable){
		return pDataTable.getClientId(pContext) + ":foo";
	}
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @param pBeep Se emite beep
	 * @throws IOException
	 */
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId, Boolean pBeep) throws IOException {
//		String xBeep = "";
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		if (pBeep){
			DBSFaces.encodeJavaScriptBeep(pWriter);
		}
		String xJS = "$(document).ready(function() { \n" +
				     " var xMessageListId = dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_messageList(xMessageListId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}


}
