package br.com.dbsoft.ui.component.command;

import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessagesListener;
import br.com.dbsoft.ui.core.DBSMessagesFacesContext;

public class DBSActionMessagesController implements IDBSMessagesListener {

	private IDBSMessages 	wBeforeMessages = new DBSMessages(true);
	private IDBSMessages 	wAfterMessages = new DBSMessages(true);
	
	public DBSActionMessagesController() {
		wBeforeMessages.addMessagesListener(this);
		wAfterMessages.addMessagesListener(this);
	}

	/**
	 * Mensagens que serão exibidas antes da execução do processamento principal.
	 * @return
	 */
	public IDBSMessages getMessagesBeforeAction(){
		return wBeforeMessages;
	}
	
	/**
	 * Mensagens que serão exibidas após a execução do processamento principal.
	 * @return
	 */
	public IDBSMessages getMessagesAfterAction(){
		return wAfterMessages;
	}

	/**
	 * Se pode executar as rotinas de validação e confirmação anterior a execução.<br/>
	 * Dentro deste if, deve-se setar as mensagens utilizando <b>getMessagesBeforeAction().add</b>.
	 * @return
	 */
	public boolean canValidate(){
		return wBeforeMessages.size() == 0;
	}
	
	/**
	 * Se pode executar o processamento desejado.<br/>
	 * Dentro deste if, deve-se implementar o processamento e
	 * setar as mensagens de erro ou finalização utilizando <b>getMessagesAfterAction().add</b>.
	 * @return
	 */
	public boolean canExecute(){
		return !wBeforeMessages.hasMessages() && wAfterMessages.size() == 0;
	}
	
	/**
	 * Envia mensagens.
	 * @param pClientId
	 * @return
	 */
	public boolean sendMessages(String pClientId){
		DBSMessagesFacesContext.sendMessages(pvGetMessages(), pClientId); 
		return pvCanRedirect();
	}
	
	@Override
	public void afterAddMessage(IDBSMessages pMessages, IDBSMessage pMessage) {}

	@Override
	public void afterRemoveMessage(IDBSMessages pMessages, String pMessageKey) {}
	
	@Override
	public void afterClearMessages(IDBSMessages pMessages) {}

	@Override
	public void afterMessageValidated(IDBSMessages pMessages, IDBSMessage pMessage){
//		System.out.println("DBSActionMessagesController afterMessageValidated\t" + pMessage.getMessageText());
//		System.out.println("DBSActionMessagesController\t" + ((DBSMessages) pMessages).wChave + "\t" + pMessage.getMessageText());
//		if (pMessages.equals(wBeforeMessages)){
//			System.out.println("DBSActionMessagesController afterMessageValidated BEFORE\t" + pMessage.getMessageText());
//		}else{
//			System.out.println("DBSActionMessagesController afterMessageValidated AFTER\t" + pMessage.getMessageText());
//		}
		//Excluir todas as mensagens para reiniciar todo o processo de controle das mensagens.
		if (!pMessage.isMessageValidatedTrue() && pMessage.getMessageType().getIsError()){
			pvClearMessages();
		}
	}

	private boolean pvCanRedirect(){
		boolean xRedirect = !wBeforeMessages.hasMessages() && !wAfterMessages.hasMessages();
		if (xRedirect){
			pvClearMessages();
		}
		return xRedirect;
	}

	private IDBSMessages pvGetMessages(){
		if (wBeforeMessages.hasMessages()){
			return wBeforeMessages;
		}else{
			return wAfterMessages;
		}
	}

	
	private void pvClearMessages(){
		wBeforeMessages.clear();
		wAfterMessages.clear();
	}

//	private void pvSetNextStage(){
//		if (wBeforeMessages.hasMessages()){
//			wActionStage = ACTION_STAGE.BEFORE;
//			return;
//		}else if (wActionStage.equals(ACTION_STAGE.BEFORE)){
//			wActionStage = ACTION_STAGE.EXECUTE;
//			return;
//		}	
//		if (wAfterMessages.hasMessages()){
//			wActionStage = ACTION_STAGE.AFTER;
//			return;
//		}else if (wActionStage.equals(ACTION_STAGE.AFTER)){
//			wActionStage = ACTION_STAGE.EXECUTE;
//			return;
//		}	
//	}
	
}	
