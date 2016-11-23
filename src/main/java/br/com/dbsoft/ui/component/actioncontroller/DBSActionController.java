package br.com.dbsoft.ui.component.actioncontroller;

import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessagesListener;
import br.com.dbsoft.ui.core.DBSMessagesFacesContext;

/**
 * Controla a execução de um action possibilitando o envio de mensagens e a respectiva validação.
 *
 */
public abstract class DBSActionController{

	private IDBSMessages 	wBeforeMessages = new DBSMessages(true);
	private IDBSMessages 	wAfterMessages = new DBSMessages(true);
	private LocalListener	wLocalListener = new LocalListener();
	private String			wMessageControlClientId = null;
	private	Boolean 		wOk = false;
	
	/**
	 * @param pMessageControlClientId ClientId do componente que receberá as mensagens quando houver.
	 */
	public DBSActionController() {
		wBeforeMessages.addMessagesListener(wLocalListener);
		wAfterMessages.addMessagesListener(wLocalListener);
	}
	
	/**
	 * @param pMessageControlClientId ClientId do componente que receberá as mensagens quando houver.
	 */
	public DBSActionController(String pMessageControlClientId) {
		this();
		wMessageControlClientId = pMessageControlClientId;
	}

	/**
	 * Efetua a chamada para o processamento.</br>
	 * @return Outcome
	 */
	public final String execute(){
		wOk = false;
		if (canValidate()){
			beforeExecute(wBeforeMessages);
		}
		if (canExecute()){
			wOk = onExecute();
			afterExecute(wAfterMessages);
		}
		sendMessages(wMessageControlClientId);
		return getOutcome();

	}
	
	/**
	 * Retorna se execução foi finalizada com sucesso.
	 * @return
	 */
	public final boolean isOk(){
		return wOk;
	}
	

	/**
	 * Evento disparado antes de executar o processamento existente em <b>onExecute</b>.</br>
	 * Este evento deve ser utilizado para enviar mensagens de validação ou confirmação anterior a execução. 
	 * @param pMessagesToSend 
	 */
	protected void beforeExecute(IDBSMessages pMessagesToSend){};
	/**
	 * Evento disparado após a execusão do processamento existente em <b>onExecute</b>.</br>
	 * Este evento deve ser utilizado para enviar mensagens de erro ou informações gerados pela execução.
	 * @param pMessagesToSend
	 */
	protected void afterExecute(IDBSMessages pMessagesToSend){};
	
	/**
	 * @return Página a ser direcionada após a finalização <b>com sucesso</b> do processamento e exibição de todas as mensagens.
	 */
	protected String getOutcome(){return null;};
	
	/**
	 * Implementação do processamento que se deseja efetuar.
	 * @return Se o processamento foi executado com sucesso.
	 */
	protected abstract boolean onExecute();
	
	/**
	 * Mensagens que serão exibidas antes da execução do processamento principal.
	 * @return
	 */
	protected final IDBSMessages getMessagesBeforeAction(){
		return wBeforeMessages;
	}
	
	/**
	 * Mensagens que serão exibidas após a execução do processamento principal.
	 * @return
	 */
	protected final IDBSMessages getMessagesAfterAction(){
		return wAfterMessages;
	}

	/**
	 * Se pode executar as rotinas de validação e confirmação anterior a execução.<br/>
	 * Dentro deste if, deve-se setar as mensagens utilizando <b>getMessagesBeforeAction().add</b>.
	 * @return
	 */
	protected final boolean canValidate(){
		return wBeforeMessages.size() == 0;
	}
	
	/**
	 * Se pode executar o processamento desejado.<br/>
	 * Dentro deste if, deve-se implementar o processamento e
	 * setar as mensagens de erro ou finalização utilizando <b>getMessagesAfterAction().add</b>.
	 * @return
	 */
	protected final boolean canExecute(){
		return !wBeforeMessages.hasMessages() && wAfterMessages.size() == 0;
	}
	
	/**
	 * Envia mensagens.
	 * @param pClientId
	 * @return
	 */
	protected final boolean sendMessages(String pClientId){
		DBSMessagesFacesContext.sendMessages(pvGetMessages(), pClientId); 
		return pvCanRedirect();
	}
	
	private final boolean pvCanRedirect(){
		boolean xRedirect = !wBeforeMessages.hasMessages() && !wAfterMessages.hasMessages();
		if (xRedirect){
			pvClearMessages();
		}
		return xRedirect;
	}

	private final IDBSMessages pvGetMessages(){
		if (wBeforeMessages.hasMessages()){
			return wBeforeMessages;
		}else{
			return wAfterMessages;
		}
	}

	
	private final void pvClearMessages(){
		wBeforeMessages.clear();
		wAfterMessages.clear();
	}
	
	/**
	 * Classe local que receberá as chamadas dos eventos disparados pelas mensagens.<br/>
	 * Código foi implemetado localmente para iniciar o acesso <i>public</i>.
	 */
	private class LocalListener implements IDBSMessagesListener{
		@Override
		public void afterAddMessage(IDBSMessages pMessages, IDBSMessage pMessage) {}

		@Override
		public void afterRemoveMessage(IDBSMessages pMessages, String pMessageKey) {}
		
		@Override
		public void afterClearMessages(IDBSMessages pMessages) {}

		@Override
		public void afterMessageValidated(IDBSMessages pMessages, IDBSMessage pMessage){
			//Excluir todas as mensagens para reiniciar todo o processo de controle das mensagens.
			if (!pMessage.isMessageValidatedTrue() && pMessage.getMessageType().getIsError()){
				pvClearMessages();
			}
		}

	}

}	
