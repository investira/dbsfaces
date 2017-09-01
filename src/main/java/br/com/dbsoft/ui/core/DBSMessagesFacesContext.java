package br.com.dbsoft.ui.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessageListener;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.core.DBSFaces.FACESCONTEXT_ATTRIBUTE;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;

public class DBSMessagesFacesContext {

	/**
	 * Todas e qualquer a mensagem.
	 */
	public final static String ALL = "@all";

	/**
	 * Mensagens que <b>não</b> são específicas de um clientId.
	 */
	public final static String GLOBAL = "@global";

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessage Mensagem a ser enviada.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 * @param pMessageListener Listener que receberá os eventos disparados pela mensagem
	 */
	public static void sendMessage(IDBSMessage pMessage, String pClientId, IDBSMessageListener pMessageListener){
		FacesContext xContext = FacesContext.getCurrentInstance();
		if (xContext == null
		 || pMessage == null){return;}
		//Força que mensagem tenha o listener informado
		pMessage.addMessageListener(pMessageListener);
		List<String> xListClientIds = pvGetClientsIds(pClientId);
		for (String xClientId: xListClientIds){
			pvSendMessage(xContext, pMessage, xClientId);
			//Envia mensagem no padrão DBSMessages
			pvSendDBSMessage(xContext, pMessage, xClientId);
		}
		for (String xSourceId:pMessage.getMessageTargetsIds()){
			//Envia mensagem padrão FacesMessage
			pvSendMessage(xContext, pMessage, xSourceId);
		}
	}
	
	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessages menssagens a serem envidas
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 * @param pMessageListener Listener que receberá os eventos disparados pela mensagem
	 */
	public static void sendMessages(IDBSMessages pMessages, String pClientId, IDBSMessageListener pMessageListener){
		FacesContext xContext = FacesContext.getCurrentInstance();
		if (xContext == null
		 || pMessages == null){return;}
		List<String> xListClientIds = pvGetClientsIds(pClientId);
		Iterator<IDBSMessage> xMsgs = pMessages.iterator();
		//Envia as mensagens individualmente
		while (xMsgs.hasNext()){
			IDBSMessage xMsg = xMsgs.next();
			//Força que mensagem tenha o listener informado
			xMsg.addMessageListener(pMessageListener);
			//Envia mensagem padrão JSF para todos os clients
			for (String xClientId: xListClientIds){
				//Envia mensagem padrão FacesMessage
				pvSendMessage(xContext, xMsg, xClientId);
			}
			for (String xSourceId:xMsg.getMessageTargetsIds()){
				//Envia mensagem padrão FacesMessage
				pvSendMessage(xContext, xMsg, xSourceId);
			}
		}
		//Seta a lista de mensagems como a própria lista de mensagens recebida.
		for (String xClientId: xListClientIds){
			pvSendDBSMessages(xContext, pMessages, xClientId);
		}
	}
	
	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessage Mensagem a ser enviada.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 * @param pMessageListener Listener que receberá os eventos disparados pela mensagem
	 */
	public static void sendMessage(IDBSMessage pMessage, String pClientId){
		sendMessage(pMessage, pClientId, null);
	}
 
	/**
	 * Envia mensagem global do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessage Mensagem a ser enviada.
	 */
	public static void sendMessage(IDBSMessage pMessage){
		sendMessage(pMessage, null);
	}	

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessageType
	 * @param pMessageText
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 */
	public static void sendMessage(MESSAGE_TYPE pMessageType, String pMessageText, String pClientId){
		DBSMessage xMsg = new DBSMessage(pMessageType, pMessageText);
		sendMessage(xMsg, pClientId);
	}	

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessageType
	 * @param pMessageText
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 * @param pMessageListener Listener que receberá os eventos disparados pela mensagem
	 */
	public static void sendMessage(MESSAGE_TYPE pMessageType, String pMessageText, String pClientId, IDBSMessageListener pMessageListener){
		DBSMessage xMsg = new DBSMessage(pMessageType, pMessageText);
		sendMessage(xMsg, pClientId, pMessageListener);
	}	

	/**
	 * Envia mensagem global do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessageType
	 * @param pMessageText
	 */
	public static void sendMessage(MESSAGE_TYPE pMessageType, String pMessageText){
		DBSMessage xMsg = new DBSMessage(pMessageType, pMessageText);
		sendMessage(xMsg, null);
	}	

	/**
	 * Envia mensagem global do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessageType
	 * @param pMessageText
	 * @param pMessageListener Listener que receberá os eventos disparados pela mensagem
	 */
	public static void sendMessage(MESSAGE_TYPE pMessageType, String pMessageText, IDBSMessageListener pMessageListener){
		DBSMessage xMsg = new DBSMessage(pMessageType, pMessageText);
		sendMessage(xMsg, null, pMessageListener);
	}	
	

	
	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessages
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 */
	public static void sendMessages(IDBSMessages pMessages, String pClientId){
		sendMessages(pMessages, pClientId, null);
	}

	/**
	 * Envia mensagem globaldo tipo <b>FacesMessage</b> e <b>DBSMessages</b>.<br/>
	 * As mensagens <b>DBSMessages</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessages
	 */

	public static void sendMessages(IDBSMessages pMessages){
		if (pMessages == null){return;}
		sendMessages(pMessages, null);
	}

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessages Lista com mensagens a serem enviadas.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 * @param pMessageListener Listener que receberá os eventos disparados pela mensagem
	 */
	public static <T extends IDBSMessage> void sendMessages(List<T> pMessages, String pClientId, IDBSMessageListener pMessageListener){
		if (pMessages == null){return;}
		//Envia as mensagens individualmente
		for (IDBSMessage xMsgs: pMessages){
			sendMessage(xMsgs, pClientId, pMessageListener);
		}
	}
	
	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>'m'(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.<br/>
	 * Mensagens do tipo <b>PROHIBID</b> e <b>ERROR</b> interrompem o redirect que eventualmente exista na execução de um action.
	 * @param pMessages Lista com mensagens a serem enviadas.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 */
	public static <T extends IDBSMessage> void sendMessages(List<T> pMessages, String pClientId){
		sendMessages(pMessages, pClientId, null);
	}
	/**
	 * Retorna primeira mensagem destinada ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * @param pClientId
	 * @return
	 */
	public static IDBSMessage getMessage(String pClientId){
		IDBSMessages xMessages = getMessages(pClientId);
		if (xMessages != null){
			return xMessages.getListMessage().get(0);
		}
		return null;
	}
	
	/**
	 * Retorna a lista de mensagens destinadas ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.</br>
	 * Caso <b>clientId</b> seja nulo, retorna todas as mensagens.
	 * @param pClientId
	 * @return
	 */
	public static IDBSMessages getMessages(String pClientId){
		FacesContext    xContext = FacesContext.getCurrentInstance();
		IDBSMessages  	xMessages = null;
		
		pClientId = DBSObject.getNotEmpty(pClientId, ALL);
		pClientId = pClientId.trim();

		xMessages = pvGetMessagesForClientId(xContext, pClientId);

		if (xMessages == null){
			xMessages = pvGetFacesMessageForClientId(xContext, pClientId);
		}
		return xMessages;
	}
	
	//==============================================================
	// PRIVATES
	//==============================================================
	
	/**
	 * Envia mensagem no padrão do JSF.<br/>
	 * Se houver, seta o listener para a mensagem.<br/>
	 * Adiciona clientId a lista de componetes que sofrerão updates.<br/>
	 * @param pContext
	 * @param pMessage Mensagem a ser enviada.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 */
	private static void pvSendMessage(FacesContext pContext, IDBSMessage pMessage, String pClientId){
		//Envia mensagem padrão FacesMessage
		pvSendFacesMessage(pContext, pMessage.getMessageType(), pMessage.getMessageText(), pClientId);
		//Força que o componente seja atualizado caso exista uma mensagem destinada a ele.
		if (!pClientId.equals(GLOBAL)
		 && !pClientId.equals(ALL)){
			pContext.getPartialViewContext().getRenderIds().add(pClientId);
		}
	}

	/**
	 * Envia mensagem IDBSMessage para o FacesContext 
	 * @param pContext
	 * @param pMessage Mensagem a ser enviada.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 */
	private static void pvSendDBSMessage(FacesContext pContext, IDBSMessage pMessage, String pClientId){
		HashMap<String, IDBSMessages> xMap = pvGetContextMessagesMap(pContext);
		
		//Recupera mensagens destinadas ao clientId
		IDBSMessages xMessages = xMap.get(pClientId);
		//Cria DBSMessages se não existir
		if (xMessages == null){
			xMessages = new DBSMessages();
			//Inclui DBSMessages no map vinculado a este clienteId 
			xMap.put(pClientId, xMessages);
		}
		//Adiciona a mensagem se não existir
		xMessages.add(pMessage);
	}

	/**
	 * Envia mensagem IDBSMessages para o FacesContext.</br>
	 * Seta a lista de mensagems como a própria lista de mensagens recebida.
	 * @param pContext
	 * @param pMessages Mensagens a serem enviadas.
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 */
	private static void pvSendDBSMessages(FacesContext pContext, IDBSMessages pMessages, String pClientId){
		HashMap<String, IDBSMessages> xMap = pvGetContextMessagesMap(pContext);
	
		//Inclui DBSMessages no map vinculado a este clienteId 
		xMap.put(pClientId, pMessages);
	}
	
	/**
	 * Envia FacesMessage para a view.
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessageText texto da mensagem
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 */
	private static void pvSendFacesMessage(FacesContext pContext, FacesMessage.Severity pSeverity, String pMessageText, String pClientId){
		if (pSeverity == null
		 || pMessageText == null){return;}
		pContext.addMessage(pClientId, new FacesMessage(pSeverity, pMessageText, null));		
	}	
	
	/**
	 * Envia FacesMessage para a view
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessageText texto da mensagem
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 */
	private static void pvSendFacesMessage(FacesContext pContext, MESSAGE_TYPE pMessageType, String pMessageText, String pClientId){
		if (pMessageType == null
		 || pMessageText == null){return;}
		pvSendFacesMessage(pContext, pMessageType.getSeverity(), pMessageText, pClientId);
	}

	/**
	 * Retorna a lista de mensagens do tipo IDBSMessage destinadas ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * Caso <b>clientId</b> seja nulo, retorna todas as mensagens.
	 * @param pContext
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 * @return
	 */
	private static IDBSMessages pvGetMessagesForClientId(FacesContext pContext, String pClientId){
		if (pContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.MESSAGES) == null){return null;}
		HashMap<String, IDBSMessages> xMap = pvGetContextMessagesMap(pContext);
		
		//Cria componente DBSMessages que conterá as mensagens para o pClientId desejado
		IDBSMessages	xMessages = new DBSMessages();
		
		//Considera todas as mensagens
		if (pClientId.equals(ALL)){
			//Inclui todas as mensagens independentemente do clientId
			for (IDBSMessages xMsgs: xMap.values()){
				xMessages.addAll(xMsgs);
			}
		//Considera das mensagens globais ou com clientId definido
		}else{
			xMessages.addAll(xMap.get(pClientId));
		}
		
		if (xMessages == null || xMessages.size() == 0){return null;}
		
		return xMessages;
	}

	/**
	 * Retorna a lista de mensagens do tipo IDBSMessage, a partir de <i>FacesMessage</i> contida do <i>FacesContext</i>, 
	 * destinadas ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * @param pContext
	 * @param pClientId Id dos componentes "listeners" que receberão a mensagem, podendo ser: 
	 * <ul>
	 * <li>@global = Mensagens sem clientid (Default)</li>
	 * <li>@all = Todas as mensagens</li>
	 * <li>clientid do componente = Mensagem para um componente</li>
	 * </ul>
	 * @return
	 */
	private static IDBSMessages pvGetFacesMessageForClientId(FacesContext pContext, String pClientId){
		if (pContext.getMessageList().size() == 0){return null;}
		IDBSMessages 		xMessages = new DBSMessages();
		List<FacesMessage> 	xListFacesMsgs = null;
		//Mensagens globais 
		if (pClientId.equals(GLOBAL)){
			//Se existe mensagem sem componente(clientid) definito
			xListFacesMsgs = pContext.getMessageList(null);
		//Mensagem para componente(clientid) 
		}else if (!pClientId.equals(ALL)){
			//Se existe mensagem para o componente(clientid)
			xListFacesMsgs = pContext.getMessageList(pClientId);
		//Todas as mensagens
		}else{
			xListFacesMsgs = pContext.getMessageList();
		}
		
		//Configura para exibir o dialog já aberto/
		if (xListFacesMsgs == null 
		 || xListFacesMsgs.size() == 0){return null;}
	
		for (FacesMessage xFMsg:xListFacesMsgs){
			DBSMessage xDBSM = new DBSMessage();
			xDBSM.setMessageText(xFMsg.getSummary());
			if (xFMsg.getSeverity() == FacesMessage.SEVERITY_ERROR
			 || xFMsg.getSeverity() == FacesMessage.SEVERITY_FATAL){
				xDBSM.setMessageType(MESSAGE_TYPE.ERROR);
			}else if (xFMsg.getSeverity() == FacesMessage.SEVERITY_WARN){
				xDBSM.setMessageType(MESSAGE_TYPE.ERROR);
			}else if (xFMsg.getSeverity() == FacesMessage.SEVERITY_INFO){
				xDBSM.setMessageType(MESSAGE_TYPE.INFORMATION);
			}
			xMessages.add(xDBSM);
		}
		return xMessages;
	}


	/**
	 * Retorna atributo dentro do FacesContext resposável por armazenar as mensagens IDBSMessages.<br/>
	 * Hashmap contém <clientId, e mensagens>.
	 * @return
	 */
	private static HashMap<String, IDBSMessages> pvGetContextMessagesMap(FacesContext pContext){
		@SuppressWarnings("unchecked")
		HashMap<String, IDBSMessages> xMap = (HashMap<String, IDBSMessages>) pContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.MESSAGES);
		//Cria map se não existir
		if (xMap == null){
			xMap = new HashMap<String, IDBSMessages>();
			pContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.MESSAGES, xMap);
		}
		return xMap;
	}	


	/**
	 * Retorna lista com clients ids.<br/>
	 * Se não existe clients ids, retorna informando que mensagem será para GLOBAL(qualquer componente que escute facesmessages).
	 * @param pClientId
	 * @return
	 */
	private static List<String> pvGetClientsIds(String pClientId){
		List<String> xListClientIds = new ArrayList<String>();
		if (!DBSObject.isEmpty(pClientId)){
			xListClientIds = DBSString.toArrayListRegex(pClientId.trim(), "[,;\\s]");
		}else{
			xListClientIds.add(GLOBAL);
		}
		return xListClientIds;
	}
	
}
