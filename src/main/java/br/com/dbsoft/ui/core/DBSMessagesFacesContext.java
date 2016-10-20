package br.com.dbsoft.ui.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.util.DBSObject;

public class DBSMessagesFacesContext {

	public static final String ATTRIBUTE_NAME = "DBSMESSAGES";//Utilizado para enviar atributo via facescontext
	public final static String MSG_FOR_ALL = "@all";
	public final static String MSG_FOR_GLOBAL = "@global";

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>m(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.
	 * @param pMessage
	 */
	public static void sendMessage(IDBSMessage pMessage, String pClientId){
		if (pMessage == null){return;}
		FacesContext xContext = FacesContext.getCurrentInstance();
		if (xContext == null){return;}

		//Envia mensagem padrão FacesMessage
		pvSendFacesMessage(xContext, pMessage.getMessageType(), pMessage.getMessageText(), pClientId);
		//Envia mensagem no padrão DBSMessages
		pvSendDBSMessage(xContext, pMessage, pClientId);
	}

 
	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>m(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.
	 * @param pMessage
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 */
	public static void sendMessage(IDBSMessage pMessage){
		sendMessage(pMessage, null);
	}	

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>m(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 * @param pMessageType
	 * @param pMessageText
	 */
	public static void sendMessage(MESSAGE_TYPE pMessageType, String pMessageText, String pClientId){
		DBSMessage xMsg = new DBSMessage(pMessageType, pMessageText);
		sendMessage(xMsg, pClientId);
	}	

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>m(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 * @param pMessageType
	 * @param pMessageText
	 */
	public static void sendMessage(MESSAGE_TYPE pMessageType, String pMessageText){
		DBSMessage xMsg = new DBSMessage(pMessageType, pMessageText);
		sendMessage(xMsg, null);
	}	
	
	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>IDBSMessage</b>.<br/>
	 * As mensagens <b>IDBSMessage</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>m(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.
	 * @param pClientId null = mensagem global. <br/> clientId = id do componente para que se destina a mensagem.
	 * @param pMessages
	 */
	@SuppressWarnings("rawtypes")
	public static void sendMessages(IDBSMessages pMessages, String pClientId){
		if (pMessages == null){return;}
		@SuppressWarnings("unchecked")
		Iterator<IDBSMessage> xMsgs = pMessages.getMessages().iterator();
		//Envia as mensagens individualmente
		while (xMsgs.hasNext()){
			sendMessage(xMsgs.next(), pClientId);
		}
	}

	/**
	 * Envia mensagem do tipo <b>FacesMessage</b> e <b>DBSMessages</b>.<br/>
	 * As mensagens <b>DBSMessages</b> são somente capturadas pelo componenente <b>DBSDialog</b> com tipo <b>m(Message)</b>.<br/>
	 * O componente <b>DBSDialog</b> também captura as mensagens <b>FacesMessage</b>.<br/>
	 * As mensagens <b>FacesMessage</b> também são capturadas pelo componenente <b>messages</b> e <b>message</b> padrão do JSF.
	 * @param pMessages
	 */
	@SuppressWarnings("rawtypes")
	public static void sendMessages(IDBSMessages pMessages){
		if (pMessages == null){return;}
		sendMessages(pMessages, null);
	}

	/**
	 * Retorna mensagem destinada ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * @param pClientId
	 * @return
	 */
	public static IDBSMessage getMessage(String pClientId){
		List<IDBSMessage> xList = getMessages(pClientId);
		if (xList != null){
			return xList.get(0);
		}
		return null;
	}
	
	/**
	 * Retorna a lista de mensagens destinadas ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * @param pClientId
	 * @return
	 */
	public static List<IDBSMessage> getMessages(String pClientId){
		FacesContext 		xContext = FacesContext.getCurrentInstance();
		List<IDBSMessage>  xList = null;
		
		pClientId = DBSObject.getNotEmpty(pClientId, MSG_FOR_GLOBAL);
		pClientId = pClientId.trim().toLowerCase();

		xList = pvGetDBSMessage(xContext, pClientId);

		if (xList == null){
			xList = pvGetFacesMessage(xContext, pClientId);
		}
		return xList;
	}
	
	/**
	 * Retorna a lista de mensagens do tipo IDBSMessage destinadas ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * @param pContext
	 * @param pClientId
	 * @return
	 */
	private static List<IDBSMessage> pvGetDBSMessage(FacesContext pContext, String pClientId){
		if (pContext.getAttributes().get(ATTRIBUTE_NAME) == null){return null;}
		List<IDBSMessage> 					xList = new ArrayList<IDBSMessage>();
		HashMap<String, List<IDBSMessage>> xMap = pvGetMap(pContext);
		
		//Considera todas as mensagens
		if (pClientId.equals(MSG_FOR_ALL)){
			//Inclui todas as mensagens independentemente do clientId
			for (List<IDBSMessage> xMsgs: xMap.values()){
				xList.addAll(xMsgs);
			}
		//Considera das mensagens globais ou com clientId definido
		}else{
			xList = xMap.get(pClientId);
		}
		
		if (xList.size() == 0){return null;}
		
		return xList;
	}

	/**
	 * Retorna a lista de mensagens do tipo IDBSMessage, a partir de <i>FacesMessage</i> contida do <i>FacesContext</i>, 
	 * destinadas ao <b>clientId</b> informado ou <i>null</i> se não encontrar mensagem.
	 * @param pContext
	 * @param pClientId
	 * @return
	 */
	private static List<IDBSMessage> pvGetFacesMessage(FacesContext pContext, String pClientId){
		if (pContext.getMessageList().size() == 0){return null;}
		List<IDBSMessage> xList = new ArrayList<IDBSMessage>();
		List<FacesMessage> xFMsgs = null;
		//Mensagens globais 
		if (pClientId.equals(MSG_FOR_GLOBAL)){
			//Se existe mensagem sem componente(clientid) definito
			xFMsgs = pContext.getMessageList(null);
		//Mensagem para componente(clientid) 
		}else if (!pClientId.equals(MSG_FOR_ALL)){
			//Se existe mensagem para o componente(clientid)
			xFMsgs = pContext.getMessageList(pClientId);
		//Todas as mensagens
		}else{
			xFMsgs = pContext.getMessageList();
		}
		
		//Configura para exibir o dialog já aberto/
		if (xFMsgs == null 
		 || xFMsgs.size() == 0){return null;}
	
		for (FacesMessage xFMsg:xFMsgs){
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
			xList.add(xDBSM);
		}
		return xList;
	}
	
	/**
	 * Envia mensagem IDBSMessage para o FacesContext 
	 * @param pContext
	 * @param pMessage
	 * @param pClientId
	 */
	private static void pvSendDBSMessage(FacesContext pContext, IDBSMessage pMessage, String pClientId){
		HashMap<String, List<IDBSMessage>> xMap = pvGetMap(pContext);
		
		pClientId = DBSObject.getNotEmpty(pClientId, MSG_FOR_GLOBAL);
		pClientId = pClientId.trim().toLowerCase();

		//Recupera list referente ao clientId
		List<IDBSMessage> xList = xMap.get(pClientId);
		//Cria list se não existir
		if (xList == null){
			xList = new ArrayList<IDBSMessage>();
			xMap.put(pClientId, xList);
		}
		//Adiciona a mensagem se não existir
		if (!xList.contains(pMessage)){
			xList.add(pMessage);
		}
	}
	
	/**
	 * Retorna atributo dentro do FacesContext resposável por armazenar as mensagens IDBSMessage.
	 * @return
	 */
	private static HashMap<String, List<IDBSMessage>> pvGetMap(FacesContext pContext){
		@SuppressWarnings("unchecked")
		HashMap<String, List<IDBSMessage>> xMap = (HashMap<String, List<IDBSMessage>>) pContext.getAttributes().get(ATTRIBUTE_NAME);
		//Cria map se não existir
		if (xMap == null){
			xMap = new HashMap<String, List<IDBSMessage>>();
			pContext.getAttributes().put(ATTRIBUTE_NAME, xMap);
		}
		return xMap;
	}
	
	/**
	 * Envia FacesMessage para a view
	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessageText texto da mensagem
	 */
	private static void pvSendFacesMessage(FacesContext pContext, FacesMessage.Severity pSeverity, String pMessageText, String pClientId){
		if (pSeverity == null
		 || pMessageText == null){return;}
		pContext.addMessage(pClientId, new FacesMessage(pSeverity, pMessageText, null));		
	}	
	
	/**
	 * Envia FacesMessage para a view
	 * @param pClientId Nome do componente ao aqual esta vinculado a mensagem
	 * @param pSeverity Tipo de severidade da mensagem
	 * @param pMessageText texto da mensagem
	 */
	private static void pvSendFacesMessage(FacesContext pContext, MESSAGE_TYPE pMessageType, String pMessageText, String pClientId){
		if (pMessageType == null
		 || pMessageText == null){return;}
		pvSendFacesMessage(pContext, pMessageType.getSeverity(), pMessageText, pClientId);
	}	

}
