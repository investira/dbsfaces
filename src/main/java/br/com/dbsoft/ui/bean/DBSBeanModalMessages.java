package br.com.dbsoft.ui.bean;


import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PreDestroy;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.modalmessages.IDBSModalMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardo.villar
 */
public abstract class DBSBeanModalMessages extends DBSBean implements IDBSModalMessages{
 
	private static final long serialVersionUID = -4177605027911436645L;

	protected 	Connection				wConnection;
	protected 	boolean					wBrodcastingEvent = false;

	protected 	IDBSMessage 			wMessageError = new DBSMessage(MESSAGE_TYPE.ERROR,"Erro: %s");
	protected 	IDBSMessages 			wMessages = new DBSMessages(true);

	
	@Override
	@PreDestroy
	void pvFinalizeClass(){
		super.pvFinalizeClass();
		wMessages.clear();
		closeConnection();
	}

	//Public -------------------------------------------------------------
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	public String getMessageKey(){
		return wMessages.getCurrentMessage().getMessageKey();
	}
	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	@Override
	public String getMessageText(){
		return wMessages.getCurrentMessage().getMessageText();
	}
	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	@Override
	public String getMessageTooltip(){
		return wMessages.getCurrentMessage().getMessageTooltip();
	}

	/**
	 * Retorna se é uma mensagem de alerta.
	 * As mensagens de alerta são as únicas que permiter a seleção SIM/NÃO
	 * @return
	 */
	@Override
	public MESSAGE_TYPE getMessageType(){
		if (wMessages.getCurrentMessage() != null){
			return wMessages.getCurrentMessage().getMessageType();
		}
		return null;
	}


	/**
	 * Retorna se há alguma mensagem na fila
	 * @return
	 */
	@Override
	public Boolean getHasMessage(){
		return wMessages.hasMessages();
	}

	/**
	 * Valida mensagem corrente. <br/>
	 * Se for <i>Warning</i> chama o método warningMessageValidated passando o opção que o usuário escolheu.<br/>
	 * Para implementar algum código após a confirmação, este método(warningMessageValidated) deverá ser sobreescrito.<br>
	 * @param pIsValidated
	 * @return Retorna view que será exibida logo em seguida. Normalmente é a própria view, porém pode-se retornar outro view
	 * a partir do overwrite do método <b>warningMessageValidated</b>.
	 * @throws DBSIOException 
	 */
	@Override
	public String setMessageValidated(Boolean pIsValidated) throws DBSIOException{
		IDBSMessage xCurrentMessage = wMessages.getCurrentMessage();
		if (xCurrentMessage != null){
//			String xMessageKey = xCurrentMessage.getMessageKey(); //Salva a chave, pois o setValidated posiciona na próxima mensagem.
			xCurrentMessage.setMessageValidated(pIsValidated);
			if (xCurrentMessage.getMessageType().getIsQuestion()){
				//Chama método indicando que warning foi validado
				warningMessageValidated(xCurrentMessage.getMessageKey(), pIsValidated);
			}
			return onMessageValidate(xCurrentMessage.getMessageKey(), pIsValidated);
		}
		return DBSFaces.getCurrentView();
	}

	public Connection getConnection() {
		return wConnection;
	}

	public void setConnection(Connection pConnection) {
		wConnection = pConnection;
	}

	/**
	 * Retorna mensagens do dialog.
	 * @return
	 */
	public IDBSMessages getMessages(){
		return wMessages;
	}

	/**
	 * Método chamado após a validação de uma mensagem de alerta(warning) onde foi solicitado o Sim ou Não.
	 * @param pIsValidated
	 * @throws DBSIOException 
	 */
	@SuppressWarnings("unused")
	protected void warningMessageValidated(String pMessageKey, Boolean pIsValidated) throws DBSIOException{}

	/**
	 * Método chamado após a validação de qualquer mensagem.<br/>
	 * Pode-se utilizar o retorno deste método para redirecionar para uma outra tela, 
	 * sendo o padrão retornar para a tela corrente.
	 * @param pMessageKey
	 * @param pIsValidated
	 * @return Retorna tela(xhtml) que será exibida em seguida.<br>O padrão é retornar a tela corrente.
	 * @throws DBSIOException 
	 */
	protected String onMessageValidate(String pMessageKey, Boolean pIsValidated) throws DBSIOException{
		return DBSFaces.getCurrentView();
	}

	// Protected ------------------------------------------------------------------------
	/**
	 * Limpa fila de mensagens
	 */
	protected void clearMessages(){
		wMessages.clear();
	}
	

	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessageKey Chave da mensagem para ser utilizada quando se quer saber se a mensagem foi ou não confirmada pelo usuário
	 * @param pMessageType Tipo de mensagem. Messagem do tipo warning requerem a confirmação do usuário
	 * @param pMessageText Texto da mensagem
	 */
	protected void addMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText){
		addMessage(pMessageKey, pMessageType, pMessageText, "");
	}
	
	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessageType Tipo de mensagem. Messagem do tipo warning requerem a confirmação do usuário
	 * @param pMessageText Texto da mensagem
	 */
	protected void addMessage(MESSAGE_TYPE pMessageType, String pMessageText){
		addMessage(pMessageText, pMessageType, pMessageText, "");
	}

	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessage
	 */
	protected void addMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip){
		wMessages.add(new DBSMessage(pMessageKey, pMessageType, pMessageText, pMessageTooltip));
	}

	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessage
	 */
	protected void addMessage(IDBSMessage pMessage){
		addMessage(pMessage.getMessageText(), pMessage.getMessageType(), pMessage.getMessageText(), pMessage.getMessageTooltip());
	}
	
	/**
	 * Adiciona todas as mensagens a fila.
	 * Mensagens precisão ser do tipo DBSMessages
	 * @param pMessages
	 */
	protected void addMessages(IDBSMessages pMessages){
		wMessages.addAll(pMessages);
	}
	
	/**
	 * Remove uma mensagem da fila
	 * @param pMessageKey
	 */
	protected void removeMessage(String pMessageKey){
		wMessages.remove(pMessageKey);
	}

	/**
	 * Retorna se mensagem foi validada.
	 * @param pMessageKey
	 * @return
	 */
	protected Boolean isMessageValidated(String pMessageKey){
		return wMessages.getMessage(pMessageKey).isMessageValidatedTrue();
	}
	
	/**
	 * Retorna se mensagem foi validada
	 * @param pMessageKey
	 * @return
	 */
	protected Boolean isMessageValidated(IDBSMessage pMessage){
		return isMessageValidated(pMessage.getMessageText());
	}
	
		
	//---------------------------------Métodos Abstratos---------------------------------
	
	/**
	 * Método para abrir a conexão e setar a variável local wConnection.<br/>
	 * Ignora, caso a conexão local, wConnection, já esteja aberta.
	 * @throws SQLException 
	 */
	protected boolean openConnection() {
		try {
			//Cria nova conexão se a conexão local for nula ou se estiver fechada.
			if ((wConnection!=null && wConnection.isClosed()) ||
				wConnection==null){
				try {
					createConnection();
					return true;
				} catch (DBSIOException e) {
					wMessageError.setMessageText(e.getLocalizedMessage());
					addMessage(wMessageError);
					return false;
				}
			}else{
				return true;
			}
		} catch (SQLException e) {
			wMessageError.setMessageTextParameters(e.getLocalizedMessage());
			addMessage(wMessageError);
			return false;
		}
	}
	
	/**
	 * Método para fechar a wConnection local
	 */
	protected void closeConnection(){
		if (wConnection != null){
			try {
				if (!wConnection.isClosed()){
					try {
						destroyConnection();
					} catch (DBSIOException e) {
						wMessageError.setMessageText(e.getLocalizedMessage());
						addMessage(wMessageError);
					}
				}
			} catch (SQLException e) {
				wMessageError.setMessageTextParameters(e.getLocalizedMessage());
				addMessage(wMessageError);
			}
		}
	}
	


	/**
	 * Método que deverá ser implementado para setar a wConnection local com uma conexão válida
	 */
	protected abstract void createConnection() throws DBSIOException;
	
	/**
	 * Método para fechar a wConnection local
	 */
	protected abstract void destroyConnection() throws DBSIOException;
	
}
