package br.com.dbsoft.ui.component.actioncontroller;

import javax.faces.context.FacesContext;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessagesListener;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;
import br.com.dbsoft.ui.core.DBSFaces.FACESCONTEXT_ATTRIBUTE;
import br.com.dbsoft.ui.core.DBSMessagesFacesContext;

/**
 * Controla a execução de um action possibilitando o envio de mensagens e a respectiva validação.
 * @author ricardo.villar
 *
 */
public abstract class DBSActionController{

	private IDBSMessages 	wBeforeMessages = new DBSMessages(true);
	private IDBSMessages 	wAfterMessages = new DBSMessages(true);
	private LocalListener	wLocalListener = new LocalListener();
	private String			wMessageControlClientId = null;
	private	Boolean 		wOk = false;
	private Boolean			wExecuting = false;
	private String			wOutcome = null;
	private	IDBSMessage 	wMessageError = new DBSMessage(MESSAGE_TYPE.ERROR,"Erro: %s");
	
	
	/**
	 * Controla a execução de um action possibilitando o envio de mensagens e a respectiva validação.<br/>
	 * <b>Eventos</b>:
	 * <ul>
	 * <li>beforeExecute</li>
	 * <li>onExecute</li>
	 * <li>afterExecute</li>
	 * <li>onSuccess ou onError</li>
	 * </ul>
	 */
	public DBSActionController() {
		wBeforeMessages.addMessagesListener(wLocalListener);
		wAfterMessages.addMessagesListener(wLocalListener);
	}
	
	/**
	 * Controla a execução de um action possibilitando o envio de mensagens e a respectiva validação.<br/>
	 * <b>Eventos</b>:
	 * <ul>
	 * <li>beforeExecute</li>
	 * <li>onExecute</li>
	 * <li>afterExecute</li>
	 * <li>onSuccess ou onError</li>
	 * </ul>
	 * @param pMessageControlClientId ClientId do componente que receberá as mensagens quando houver.
	 */
	public DBSActionController(String pMessageControlClientId) {
		this();
		wMessageControlClientId = pMessageControlClientId;
	}
	
	/**
	 * ClientId do componente da view que receberá as mensagens
	 * @return
	 */
	public final String getMessageControlClientId() {
		return wMessageControlClientId;
	}

	/**
	 * ClientId do componente da view que receberá as mensagens
	 * @return
	 */
	public final void setMessageControlClientId(String pMessageControlClientId) {
		wMessageControlClientId = pMessageControlClientId;
	}
	
	/**
	 * Página a ser direcionada após a finalização <b>com sucesso</b> do processamento e exibição de todas as mensagens.<br/>
	 * @param pOutcome
	 */
	public final void setOutcome(String pOutcome){
		wOutcome = pOutcome;
	}
	/**
	 * @return Página a ser direcionada após a finalização <b>com sucesso</b> do processamento e exibição de todas as mensagens.
	 */
	public final String getOutcome(){return wOutcome;};

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
	 * Efetua a chamada para o processamento.</br>
	 * @return Outcome
	 */
	public final String execute(){
		//Indica que action esta sendo controlado via actionController. Isso é importante para o DialogMsg que faz o encode dos botões com o action original para que o método seja chamado consecutivamente até que não haja mais mensagem.
		FacesContext.getCurrentInstance().getAttributes().put(FACESCONTEXT_ATTRIBUTE.ACTION_CONTROLLED, true);
		try {
			if (pvCanBeforeExecute()){
				//Reseta controles
				wOutcome = null;
				wOk = false;
	//			System.out.println("actioController validate [" + (pvGetMessages().hasMessages() ? pvGetMessages().getListMessage().get(0).getMessageKey() : "") + "]");
				beforeExecute(wBeforeMessages);
			}
			if (pvCanExecute()){
	//			System.out.println("actioController execute [" + (pvGetMessages().hasMessages() ? pvGetMessages().getListMessage().get(0).getMessageKey() : "") + "]");
				wExecuting = true;
				wOk = onExecute(wAfterMessages);
				afterExecute(wAfterMessages);
				if (wOk){
					onSuccess(wAfterMessages);
				}else{
					onError(wAfterMessages);
				}
			}
	//		System.out.println("actioController sendmessage [" + (pvGetMessages().hasMessages() ? pvGetMessages().getListMessage().get(0).getMessageKey() : "") + "]");
			//Envia mensagens e retorna se pode seguir com o outcome(quando não há mais mensagens a serem exibidas).
			if (pvSendMessages(wMessageControlClientId)){
				return wOutcome;
			}
		} catch (Exception e) {
			wMessageError.setMessageText(e.getLocalizedMessage());
			wBeforeMessages.add(wMessageError);
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
	 * Disparado antes de executar o <b>onExecute</b>.</br>
	 * Este evento deve ser utilizado para enviar mensagens de validação ou confirmação antes da execução do <b>onExecute</b>. 
	 * @param pMessagesToSend Mensagens a serem enviadas.
	 */
	protected void beforeExecute(IDBSMessages pMessagesToSend){}

	/**
	 * Disparado após a execusão do <b>onExecute</b> independentemente se ele retornou true ou false.</br>
	 * Ele deve ser <b>somente</b> utilizado para casos onde o código seja iqual independemente do resultado da execução.</br>
	 * @param pMessagesToSend Mensagens a serem enviadas.
	 */
	protected void afterExecute(IDBSMessages pMessagesToSend){}

	/**
	 * Disparado após a execusão do <b>afterExecute</b> quando não existir erro no <b>onExecute</b>.</br>
	 * É importante setar o <b>Outcome</b> caso haja redirecionamento para outra página.</br>
	 * Se existir redirecionamento, ele ocorrerá após a exibição de todas as mensagens.
	 * Este evento deve ser utilizado para enviar mensagens de sucesso ou informações gerados pela execução utilizando o <b>pMessagesToSend</b>.
	 * @param pMessagesToSend Mensagens a serem enviadas.
	 */
	protected void onSuccess(IDBSMessages pMessagesToSend){}

	/**
	 * Disparado após a execusão do <b>afterExecute</b> quando <b>existir erro</b> no <b>onExecute</b>.</br>
	 * É importante setar o <b>Outcome</b> caso haja redirecionamento para outra página.</br>
	 * Se existir redirecionamento, ele ocorrerá após a exibição de todas as mensagens.</br>
	 * Este evento deve ser utilizado para enviar mensagens de erro gerados pela execução utilizando o <b>pMessagesToSend</b>.
	 * @param pMessagesToSend Mensagens a serem enviadas.
	 */
	protected void onError(IDBSMessages pMessagesToSend){}
	
	/**
	 * Disparado após a execusão do <b>beforeExecute</b> e não existir mensagem a ser exibida.</br>
	 * Neste método deve-se implementar o processamento que se deseja efetuar.</br>
	 * Pode-se enviar mensagens de erro ou informações gerados pela execução utilizando o <b>pMessagesToSend</b>.<br/>
	 * Por questão de organização do código, para envio de mensagens não geradas pela execução, 
	 * recomenda-se a utilização do evento <b>onSuccess</b> em caso de sucesso ou <b>onError</b> em caso de erro ou <b>afterExecute</b>.
	 * @param pMessagesToSend Mensagens a serem enviadas.
	 * @return Se o processamento foi executado com sucesso.
	 */
	protected abstract boolean onExecute(IDBSMessages pMessagesToSend);
	
	/**
	 * Envia mensagens e retorna se pode ser redirecionado(quando não há mensagem a ser exibida).
	 * @param pClientId
	 * @return 
	 */
	private final boolean pvSendMessages(String pClientId){
		DBSMessagesFacesContext.sendMessages(pvGetMessages(), pClientId); 
//		return pvCanRedirect();
		return pvCanFinalize();
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
	 * 
	 * @return
	 */
	private final boolean pvCanFinalize(){
		boolean xFinalize = wExecuting && !wBeforeMessages.hasMessages() && !wAfterMessages.hasMessages();
		if (xFinalize){
			pvFinalize();
		}
		return xFinalize;
	}
	
//	private final boolean pvCanRedirect(){
//		boolean xRedirect = !wBeforeMessages.hasMessages() && !wAfterMessages.hasMessages();
//
//		return xRedirect;
//	}

	public IDBSMessages getMessages(){return pvGetMessages();}
	
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
			//Finaliza(reseta) action se não houver mais mensagens a serem validadas e a esta última mensagem for do tipo que interrompe(Error) e tiver sido validada como false. 
			if (pMessage.getMessageType().getIsError() && !pMessage.isMessageValidatedTrue() && pMessages.notValidatedSize().equals(0)){ 
				pvFinalize();
			}
		}

	}

}	
