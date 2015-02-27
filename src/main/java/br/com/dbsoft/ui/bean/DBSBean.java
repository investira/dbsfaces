package br.com.dbsoft.ui.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;
import br.com.dbsoft.ui.component.dialog.DBSDialogMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialogMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardo.villar
 */
public abstract class DBSBean implements Serializable {
 
	private static final long serialVersionUID = -5273728796912868413L;

	protected 	Logger 		wLogger =  Logger.getLogger(this.getClass());
	
	protected 	static DBSMessage	wMessageError = 
									new DBSMessage(MESSAGE_TYPE.ERROR,"Erro: %s");

	
	protected 	Connection			wConnection;
	
	protected 	DBSDialogMessages		wDialogMessages = new DBSDialogMessages(DBSDialogMessage.class);
	protected 	boolean					wBrodcastingEvent = false;
	private   	DBSBean					wMasterBean = null;
	private 	List<DBSBean>			wSlavesBean = new ArrayList<DBSBean>();
	
	
	//--------------------------------------------------------------------------------------
	//Código para impedir o erro de 'Cannot create a session after the response has been committed'
	//que ocorre em algumas situações que a página(como resultado da quantidade de registros do ResultDataModel) por conter muitos dados
	//o que faz que por algum motivo o JSF envie algum resposta no momento que não deveria, principalmente com @ResquestScoped
	@PostConstruct
	void pvInitializeClass() {
		if (FacesContext.getCurrentInstance() == null){
			wLogger.warn(this.getClass().getCanonicalName() + ":Não há scope ativo para este bean.");
		}else if(FacesContext.getCurrentInstance().getExternalContext().getSession(false) == null){
				FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		}
		initializeClass();
	}
	
	@PreDestroy
	void pvFinalizeClass(){
		clearMessages();
		finalizeClass();
		closeConnection();
	}
	
	
	//Public -------------------------------------------------------------
	
	/**
	 * Crudbean que será o principal. Responsável por apagar da memória os crudbean escravaos que existem
	 * @return
	 */
	public DBSBean getMasterBean() {
		return wMasterBean;
	}
	
	/**
	 * Lista de CrudBean escravos dentro deste crud.
	 * Os crudbean escravos serão retirados da memória quando o master for
	 * @return
	 */
	public List<DBSBean> getSlavesBean() {
		return wSlavesBean;
	}
	
	
	/**
	 * CrudBean que será o principal. Responsável por apagar da memória os CrudBean escravos que a ele vinculados
	 */
	public void setMasterBean(DBSBean pBean) {
		wMasterBean = pBean;
		if (!pBean.getSlavesBean().contains(this)){
			pBean.getSlavesBean().add(this);
		}
	}
	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	public String getMessageKey(){
		return wDialogMessages.getCurrentMessageKey();
	}
	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	public String getMessageText(){
		return wDialogMessages.getCurrentMessageText();
	}
	

	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	public String getMessageTooltip(){
		return wDialogMessages.getCurrentMessageTooltip();
	}
	/**
	 * Retorna icone da mensagem que está na fila
	 * @return
	 */
	public String getMessageIcon(){
		return wDialogMessages.getIcon().toString();
	}

	/**
	 * Retorna largura da janela da mensagem que está na fila
	 * @return
	 */
	public Integer getMessageWidth(){
		return wDialogMessages.getWidth();
	}

	/**
	 * Retorna altura da janela da mensagem que está na fila
	 * @return
	 */
	public Integer getMessageHeight(){
		return wDialogMessages.getHeight();
	}
	
	/**
	 * Retorna se é uma mensagem de alerta.
	 * As mensagens de alerta são as únicas que permiter a seleção SIM/NÃO
	 * @return
	 */
	public Boolean getMessageIsWarning(){
		return wDialogMessages.getIsWarning();
	}
	
	/**
	 * Retorna se é uma mensagem de alerta.
	 * As mensagens de alerta são as únicas que permiter a seleção SIM/NÃO
	 * @return
	 */
	public MESSAGE_TYPE getMessageType(){
		return wDialogMessages.getMessageType();
	}
	
	/**
	 * Retorna se há alguma mensagem na fila
	 * @return
	 */
	/**
	 * @return
	 */
	public Boolean getHasMessage(){
//		return wDialogMessages.hasMessages();
		if (wDialogMessages.getCurrentMessageKey()!=null){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Retorna mensagens do dialog.
	 * @return
	 */
	public DBSDialogMessages getMessages(){
		return wDialogMessages;
	}
	
	public Connection getConnection() {
		return wConnection;
	}

	public void setConnection(Connection pConnection) {
		wConnection = pConnection;
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
	public String setMessageValidated(Boolean pIsValidated) throws DBSIOException{
		if (wDialogMessages!=null){
			DBSMessage xMessageKey = wDialogMessages.getCurrentMessage(); //Salva a chave, pois o setValidated posiciona na próxima mensagem.
			wDialogMessages.setValidated(pIsValidated);
			if (xMessageKey.getMessageType() == MESSAGE_TYPE.WARNING){
				//Chama método indicando que warning foi validado
				warningMessageValidated(xMessageKey.getMessageKey(), pIsValidated);
			}
			return messageValidated(xMessageKey.getMessageKey(), pIsValidated);
		}
		return DBSFaces.getCurrentView();
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
	protected String messageValidated(String pMessageKey, Boolean pIsValidated) throws DBSIOException{
		return DBSFaces.getCurrentView();
	}

	// Protected ------------------------------------------------------------------------
	/**
	 * Limpa fila de mensagens
	 */
	protected void clearMessages(){
		wDialogMessages.clear();
	}
	
	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessageKey Chave da mensagem para ser utilizada quando se quer saber se a mensagem foi ou não confirmada pelo usuário
	 * @param pMessageType Tipo de mensagem. Messagem do tipo warning requerem a confirmação do usuário
	 * @param pMessageText Texto da mensagem
	 * @param pDialogIcon Icon que aparecerá na mensagem conforme constantes DIALOG_ICON 
	 */
	protected void addMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pDialogIcon){
		wDialogMessages.add(pMessageKey, pMessageType, pMessageText, pDialogIcon);
	}

	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessageKey
	 * @param pMessageType
	 * @param pMessageText
	 * @param pDialogIcon
	 * @param pMessageTooltip
	 */
	protected void addMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pDialogIcon, String pMessageTooltip){
		wDialogMessages.add(pMessageKey, pMessageType, pMessageText, pDialogIcon, pMessageTooltip);
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
	 * @param pMessageKey Chave da mensagem para ser utilizada quando se quer saber se a mensagem foi ou não confirmada pelo usuário
	 * @param pMessageType Tipo de mensagem. Messagem do tipo warning requerem a confirmação do usuário
	 * @param pMessageText Texto da mensagem
	 */
	protected void addMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText){
		addMessage(pMessageKey, pMessageType, pMessageText, "");
	}

	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessage
	 */
	protected void addMessage(DBSMessage pMessage){
		addMessage(pMessage.getMessageText(), pMessage.getMessageType(), pMessage.getMessageText(), pMessage.getMessageTooltip());
	}
	
	/**
	 * Adiciona todas as mensagens a fila.
	 * Mensagens precisão ser do tipo DBSMessages
	 * @param pMessages
	 */
	protected <M extends DBSMessages<?>> void addMessages(M pMessages){
		wDialogMessages.addAll(pMessages);
	}
	
	/**
	 * Adiciona uma mensagem a fila
	 * @param pMessage
	 */
	protected void addMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip){
		wDialogMessages.add(pMessageKey, pMessageType, pMessageText, pMessageTooltip);
	}

	/**
	 * Remove uma mensagem da fila
	 * @param pMessageKey
	 */
	protected void removeMessage(String pMessageKey){
		wDialogMessages.remove(pMessageKey);
	}

	/**
	 * Retorna se mensagem foi validada
	 * @param pMessageKey
	 * @return
	 */
	protected boolean isMessageValidated(String pMessageKey){
		return wDialogMessages.isValidated(pMessageKey);
	}
	
	/**
	 * Retorna se mensagem foi validada
	 * @param pMessageKey
	 * @return
	 */
	protected boolean isMessageValidated(DBSMessage pMessage){
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
	
	/**
	 * Método após a inicialização do bean.
	 * Ao sobre escrever este método, deve-se estar atendo em chamar o <b>super</b>.
	 */
	protected void initializeClass(){};
	
	/**
	 * Método após a finalização do bean.
	 * Ao sobre escrever este método, deve-se estar atendo em chamar o <b>super</b>.
	 */
	protected void finalizeClass(){};
	
}
