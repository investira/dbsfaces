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

//	private enum Phase{
//		Before,
//		Execute,
//		After,
//		End;
//	}
	private IDBSMessages 	wBeforeMessages = new DBSMessages(true);
	private IDBSMessages 	wAfterMessages = new DBSMessages(true);
	private LocalListener	wLocalListener = new LocalListener();
	private String			wMessageControlClientId = null;
	private	Boolean 		wOk = false;
	private Boolean			wExecuting = false;
	
	
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
		if (pvCanBeforeExecute()){
//			System.out.println("actioController validate [" + (pvGetMessages().hasMessages() ? pvGetMessages().getListMessage().get(0).getMessageKey() : "") + "]");
			beforeExecute(wBeforeMessages);
		}
		if (pvCanExecute()){
//			System.out.println("actioController execute [" + (pvGetMessages().hasMessages() ? pvGetMessages().getListMessage().get(0).getMessageKey() : "") + "]");
			wExecuting = true;
			wOk = onExecute(wAfterMessages);
			afterExecute(wAfterMessages);
		}
//		System.out.println("actioController sendmessage [" + (pvGetMessages().hasMessages() ? pvGetMessages().getListMessage().get(0).getMessageKey() : "") + "]");
		if (pvSendMessages(wMessageControlClientId)){
			if (pvCanFinalize()){
				return getOutcome();
			}
		}
		return null;
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
	 * Este evento deve ser utilizado para enviar mensagens de validação ou confirmação anterior a execução utilizando o <b>pMessagesToSend</b>. 
	 * @param pMessagesToSend 
	 */
	protected void beforeExecute(IDBSMessages pMessagesToSend){};
	/**
	 * Evento disparado após a execusão do processamento existente em <b>onExecute</b>.</br>
	 * Este evento deve ser utilizado para enviar mensagens de erro ou informações gerados pela execução utilizando o <b>pMessagesToSend</b>.
	 * @param pMessagesToSend
	 */
	protected void afterExecute(IDBSMessages pMessagesToSend){};
	
	/**
	 * @return Página a ser direcionada após a finalização <b>com sucesso</b> do processamento e exibição de todas as mensagens.
	 */
	protected String getOutcome(){return null;};
	
	/**
	 * Implementação do processamento que se deseja efetuar.
	 * Pode-se enviar mensagens de erro ou informações gerados pela execução utilizando o <b>pMessagesToSend</b>.<br/>
	 * Para envio de mensagens não geradas pela execução, recomenda-se a utilização do evento <b>afterExecute</b> por questão de organização do código.
	 * @return Se o processamento foi executado com sucesso.
	 */
	protected abstract boolean onExecute(IDBSMessages pMessagesToSend);
	
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
	 * Envia mensagens e retorna se pode ser redirecionado.
	 * @param pClientId
	 * @return 
	 */
	private final boolean pvSendMessages(String pClientId){
		DBSMessagesFacesContext.sendMessages(pvGetMessages(), pClientId); 
		return pvCanRedirect();
	}

	/**
	 * Se pode executar as rotinas de validação e confirmação anterior a execução.<br/>
	 * Dentro deste if, deve-se setar as mensagens utilizando <b>getMessagesBeforeAction().add</b>.
	 * @return
	 */
	private final boolean pvCanBeforeExecute(){
		return !wExecuting && wBeforeMessages.size() == 0 && wAfterMessages.size() == 0;
	}
	
	/**
	 * Se pode executar o processamento desejado.<br/>
	 * Dentro deste if, deve-se implementar o processamento e
	 * setar as mensagens de erro ou finalização utilizando <b>getMessagesAfterAction().add</b>.
	 * @return
	 */
	private final boolean pvCanExecute(){
		return !wExecuting && !wBeforeMessages.hasMessages() && wAfterMessages.size() == 0;
	}
	
	/**
	 * Se pode executar o processamento desejado.<br/>
	 * Dentro deste if, deve-se implementar o processamento e
	 * setar as mensagens de erro ou finalização utilizando <b>getMessagesAfterAction().add</b>.
	 * @return
	 */
	private final boolean pvCanFinalize(){
		boolean xFinalize = wExecuting && !wBeforeMessages.hasMessages() && !wAfterMessages.hasMessages();
		if (xFinalize){
			pvFinalize();
		}
		return xFinalize;
	}
	
	
	private final boolean pvCanRedirect(){
		boolean xRedirect = !wBeforeMessages.hasMessages() && !wAfterMessages.hasMessages();
//		if (xRedirect){
//			pvFinalize();
//		}
		return xRedirect;
	}

	private final IDBSMessages pvGetMessages(){
		if (wBeforeMessages.hasMessages()){
			return wBeforeMessages;
		}else{
			return wAfterMessages;
		}
	}

	
	private final void pvFinalize(){
		wBeforeMessages.clear();
		wAfterMessages.clear();
		wExecuting = false;
//		System.out.println("actioController pvClearMessages-------------------------------");
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
//			System.out.println("actioController afterMessageValidated\t [" + pMessage.getMessageKey() + "]");
			//Se for mensagem que interrompe(Error) e validada como false. 
			if (pMessage.getMessageType().getIsError() && !pMessage.isMessageValidatedTrue()){ 
				pvFinalize();
			}
		}

	}

}	
