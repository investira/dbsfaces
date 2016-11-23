package br.com.dbsoft.ui.component.messagelist;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;


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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()) {
			return;
		}
		DBSMessageList 	xMessageList = (DBSMessageList) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClientId = xMessageList.getClientId(pContext);

		String xClass = CSS.MESSAGELIST.MAIN + DBSObject.getNotEmpty(xMessageList.getStyleClass(), "");

		MESSAGE_TYPE xType = MESSAGE_TYPE.INFORMATION; //Padrão
		Integer xCount = 0;
		Integer xNew = 0;
		
		//Encode principal
		xWriter.startElement("div", xMessageList);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xMessageList.getStyle());

			//Verica se há mensagem a ser exibida
			if (xMessageList.getValue() != null){
				xCount = xMessageList.getValue().getListMessage().size();
				if (xCount > 0){
					IDBSMessage xMsg;

					Iterator<IDBSMessage> xI = xMessageList.getValue().iterator();
					while (xI.hasNext()){
						 xMsg = xI.next();
						 //Se msg ainda não foi validada(visualizada, neste caso)
						 if (!xMsg.isMessageValidatedTrue()){
							 //Conta a quantidade de mensagens novas
							 xNew++;
							 xMsg.setMessageValidated(false); //Marca que mensagem como vista
						 }
						//Definie a cor do campo da quantidade de mensagens a partir da mensagem de maior revelância(error/warning/information). 
						 if (xMsg.getMessageType().getSeverityLevel() > xType.getSeverityLevel()){
							 xType = xMsg.getMessageType();
						 }
					}
					xWriter.startElement("div", xMessageList);
						DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
						//Encode do icone principal e o balão com o contador de mensagens
						xWriter.startElement("div", xMessageList);
							DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.BUTTON);
							//Exibe Ícone
							xWriter.startElement("div", xMessageList);
								DBSFaces.encodeAttribute(xWriter, "class", "-i_message");
							xWriter.endElement("div");
							//Exibe contador de mensagens
							if (xCount > 0){
								xWriter.startElement("div", xMessageList);
									DBSFaces.encodeAttribute(xWriter, "class", "-count " + " -severity" + xType.getSeverity().getOrdinal());
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

		pvEncodeJS(xMessageList, xWriter, (xNew > 0 && xType != MESSAGE_TYPE.INFORMATION)); //Emite beep se houver nova mensagem que não seja INFORMATION
	}
	
	private void pvEncodeMessageInputFoo(FacesContext pContext, DBSMessageList pMessageList, ResponseWriter pWriter) throws IOException {
		String xClientId = pMessageList.getClientId();
		String xFooId = pvGetInputFooId(pContext, pMessageList);
		//Botão que receberá a chave do item selecionado e efetuará o submit posteriormente via chamada por JS.
		pWriter.startElement("input", pMessageList);
			DBSFaces.encodeAttribute(pWriter, "id", xFooId);
			DBSFaces.encodeAttribute(pWriter, "name", xFooId);
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "class", "-foo");
			DBSFaces.encodeAttribute(pWriter, "autocomplete", "off");
			DBSFaces.encodeAttribute(pWriter, "onclick", DBSFaces.getSubmitString(pMessageList, "", xClientId, xClientId));
			DBSFaces.encodeAttribute(pWriter, "value", pMessageList.getMessageKey());
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
			DBSFaces.encodeAttribute(pWriter, "class",  xClass);
			pWriter.startElement("div", pMessageList);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
				//Exibe a lista de mensagens em ordem invertida de inclusão(PEPS/LIFO), onde o mais recente é será exibido primeiro;
				List<IDBSMessage> xMsgs = pMessageList.getValue().getListMessage();
				IDBSMessage xMsg;
				//Le todas as mensagens
				for (Integer xI=xMsgs.size()-1; xI!=-1; xI--){
					xMsg = xMsgs.get(xI);
					pWriter.startElement("div", pMessageList);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.MESSAGE + " -severity" + xMsg.getMessageType().getSeverity().getOrdinal());
						DBSFaces.encodeAttribute(pWriter, "index", xMsg.getMessageKey());
						pWriter.startElement("div", pMessageList);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
							//Hora da mensagem
							pWriter.startElement("div", pMessageList);
								DBSFaces.encodeAttribute(pWriter, "class", "-time");
								if (xMsg.getMessageTime() !=null){
									pWriter.write(xMsg.getMessageTime().toLocalTime().toString("HH:mm"));
								}
							pWriter.endElement("div");
							//Texto da mensagem
							pWriter.startElement("div", pMessageList);
								DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
								pWriter.write(xMsg.getMessageText());
							pWriter.endElement("div");
							if (!pMessageList.getReadOnly()){
								//Botões
								pWriter.startElement("div", pMessageList);
									DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.BUTTON);
									//Botão de exclusão da mensagem
									pWriter.startElement("span", pMessageList);
										DBSFaces.encodeAttribute(pWriter, "class", "dbs_button ");
										pWriter.startElement("span", pMessageList);
											DBSFaces.encodeAttribute(pWriter, "class", "-i_delete");
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
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter, Boolean pBeep) throws IOException {
//		String xBeep = "";
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		if (pBeep){
			DBSFaces.encodeJavaScriptBeep(pWriter);
		}
		String xJS = "$(document).ready(function() { \n" +
				     " var xMessageListId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_messageList(xMessageListId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}


}
