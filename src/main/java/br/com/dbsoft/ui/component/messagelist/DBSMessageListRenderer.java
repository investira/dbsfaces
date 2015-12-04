package br.com.dbsoft.ui.component.messagelist;


import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily = DBSFaces.FAMILY, rendererType = DBSMessageList.RENDERER_TYPE)
public class DBSMessageListRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		decodeBehaviors(pContext, pComponent); 
		DBSMessageList 	xMessageList = (DBSMessageList) pComponent;
		//Verifica se foi um submit de um dos botões de exclusão e exclui o item se for
//		pComponent.getClientId(pContext) + ":btDel";
		String 			xInputFooId = pvGetInputFooId(pContext, xMessageList);
		String			xSourceId = pContext.getExternalContext().getRequestParameterMap().get(DBSFaces.PARTIAL_SOURCE_PARAM);
		if (xSourceId !=null && xSourceId.startsWith(xInputFooId)){
			String xMessageKey = pContext.getExternalContext().getRequestParameterMap().get(xInputFooId);
			if (xMessageKey!=null){
				//Seta valor da mensagem 
				xMessageList.setMessageKey(xMessageKey);
				//Exclui mensagem da lista
				xMessageList.getValue().remove(xMessageKey);
//				xMessageList.getValue().getMessages().keySet().
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

		String xClass = DBSFaces.CSS.MESSAGELIST.MAIN.trim() + " " + xMessageList.getStyleClass();
		String xStyle = xMessageList.getStyle();

		MESSAGE_TYPE xType = MESSAGE_TYPE.INFORMATION; //Padrão
		Integer xCount = 0;
		Integer xNew = 0;
		//Encode principal
		xWriter.startElement("div", xMessageList);
			xWriter.writeAttribute("id", xClientId, null);
			xWriter.writeAttribute("name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass.trim(), null);
			if (!DBSObject.isEmpty(xStyle)){
				DBSFaces.setAttribute(xWriter, "style", xStyle, null);
			}

			//Verica se há mensagem a ser exibida
			if (xMessageList.getValue() != null){
				xCount = xMessageList.getValue().getMessages().size();
				if (xCount > 0){
					DBSMessage xMsg;
					Iterator<Entry<String, DBSMessage>> xI = xMessageList.getValue().iterator();
					while (xI.hasNext()){
						 xMsg = xI.next().getValue();
						 //Se msg ainda não foi validada(visualizada, neste caso)
						 if (xMsg.isValidated() == null 
						  || !xMsg.isValidated()){
							 //Conta a quantidade de mensagens novas
							 xNew++;
							 xMsg.setValidated(true); //Marca que mensagem como vista
						 }
						//Definie a cor do campo da quantidade de mensagens a partir da mensagem de maior revelância(error/warning/information). 
						 if (xMsg.getMessageType().getCode() > xType.getCode()){
							 xType = xMsg.getMessageType();
						 }
					}
					xWriter.startElement("div", xMessageList);
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER, null);
						//Encode do icone principal e o balão com o contador de mensagens
						xWriter.startElement("div", xMessageList);
							xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.BUTTON.trim(), null);
							//Exibe Ícone
							xWriter.startElement("div", xMessageList);
								xWriter.writeAttribute("class", "-i_message", null);
							xWriter.endElement("div");
							//Exibe contador de mensagens
							if (xCount > 0){
								xWriter.startElement("div", xMessageList);
									xWriter.writeAttribute("class", "-count " + xType.getName(), null);
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
			DBSFaces.setAttribute(pWriter, "id", xFooId, null);
			DBSFaces.setAttribute(pWriter, "name", xFooId, null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", "-foo", null);
			DBSFaces.setAttribute(pWriter, "autocomplete", "off", null);
			DBSFaces.setAttribute(pWriter, "onclick", DBSFaces.getSubmitString(pMessageList, "", xClientId, xClientId), null);
			DBSFaces.setAttribute(pWriter, "value", pMessageList.getMessageKey(), null);
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
			String xClass = "-list " + DBSFaces.CSS.BACK_GRADIENT_WHITE.trim();
			if (pMessageList.getDeleting()){
				xClass+= DBSFaces.CSS.MODIFIER.OPENED;
			}
			DBSFaces.setAttribute(pWriter, "class",  xClass, null);
			pWriter.startElement("div", pMessageList);
				DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
				//Exibe a lista de mensagens em ordem invertida de inclusão(PEPS/LIFO), onde o mais recente é será exibido primeiro;
				Collection<String> xCollection = pMessageList.getValue().getMessages().keySet();
				String[] 	xMsgKey = xCollection.toArray(new String[xCollection.size()]); 
				DBSMessage 	xMsg;
				for (Integer xI=xMsgKey.length-1; xI!=-1; xI--){
					xMsg = pMessageList.getValue().getMessages().get(xMsgKey[xI]);
					pWriter.startElement("div", pMessageList);
						DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.MESSAGE.trim() + " " + xMsg.getMessageType().getName(), null);
						DBSFaces.setAttribute(pWriter, "index",xMsgKey[xI], null);
						pWriter.startElement("div", pMessageList);
							DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CONTAINER.trim(), null);
							//Hora da mensagem
							pWriter.startElement("div", pMessageList);
								DBSFaces.setAttribute(pWriter, "class", "-time", null);
								if (xMsg.getTime() !=null){
									pWriter.write(xMsg.getTime().toLocalTime().toString("HH:mm"));
								}
							pWriter.endElement("div");
							//Texto da mensagem
							pWriter.startElement("div", pMessageList);
								DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
								pWriter.write(xMsg.getMessageText());
							pWriter.endElement("div");
							if (!pMessageList.getReadOnly()){
								//Botões
								pWriter.startElement("div", pMessageList);
									DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.BUTTON.trim(), null);
									//Botão de exclusão da mensagem
									pWriter.startElement("span", pMessageList);
										pWriter.writeAttribute("class", "dbs_button ", null);
										pWriter.startElement("span", pMessageList);
											pWriter.writeAttribute("class", "-i_delete", null);
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
				     " var xMessageListId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_messageList(xMessageListId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}

	
//	/**
//	 * Encode do cabeçalho contendo os filtros e botões do toolbar definidos pelo usuário.
//	 * O encode do header da tabela é efetuado em outra rotina.
//	 * @param pContext
//	 * @param pMessageList
//	 * @param pWriter
//	 * @throws IOException
//	 */
//	private void pvEncodeHeader(FacesContext pContext, DBSMessageList pMessageList,ResponseWriter pWriter) throws IOException {
//		//Input para controle do focus e caracteres digitados----
//		pWriter.startElement("input", pMessageList);
//			DBSFaces.setAttribute(pWriter, "id", pvGetInputFooId(pContext, pMessageList), null);
//			DBSFaces.setAttribute(pWriter, "name", pvGetInputFooId(pContext, pMessageList), null);
//			DBSFaces.setAttribute(pWriter, "type", "text", null);
//			DBSFaces.setAttribute(pWriter, "class", "-foo", null);
//			DBSFaces.setAttribute(pWriter, "autocomplete", "off", null);
//			DBSFaces.setAttribute(pWriter, "value", pMessageList.getCurrentRowIndex(), null);
//			encodeClientBehaviors(pContext, pMessageList);
//		pWriter.endElement("input");
//	}
//	
//	private String pvGetInputFooId(FacesContext pContext, DBSMessageList pMessageList){
//		return pMessageList.getClientId(pContext) + ":foo";
//	}
		
	
}
