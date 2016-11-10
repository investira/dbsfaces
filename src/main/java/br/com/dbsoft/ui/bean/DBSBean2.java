package br.com.dbsoft.ui.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessagesController;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessagesController;
import br.com.dbsoft.ui.component.modalmessages.IDBSModalMessages;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardo.villar
 */
public abstract class DBSBean2 implements Serializable, IDBSModalMessages{
 
	private static final long serialVersionUID = -5273728796912868413L;

	protected 	Logger 		wLogger =  Logger.getLogger(this.getClass());
	
	
	protected 	static IDBSMessage wMessageError = new DBSMessage(MESSAGE_TYPE.ERROR,"Erro: %s");
	
	protected 	Connection					wConnection;
	
	protected 	DBSMessagesController				wDialogMessages = new DBSMessagesController();
	protected 	boolean								wBrodcastingEvent = false;
	private   	DBSBean2							wMasterBean = null;
	private 	List<DBSBean2>						wSlavesBean = new ArrayList<DBSBean2>();
	private 	Locale								wLocale;
 
	//--------------------------------------------------------------------------------------
	//Código para impedir o erro de 'Cannot create a session after the response has been committed'
	//que ocorre em algumas situações que a página(como resultado da quantidade de registros do ResultDataModel) por conter muitos dados
	//o que faz que por algum motivo o JSF envie algum resposta no momento que não deveria, principalmente com @ResquestScoped
	@PostConstruct
	void pvInitializeClass() {
		if (FacesContext.getCurrentInstance() == null){
			wLogger.warn(this.getClass().getCanonicalName() + ":Não há scope ativo para este bean.");
		}else{ 
			FacesContext xFC = FacesContext.getCurrentInstance();
			if(xFC.getExternalContext().getSession(false) == null){
				xFC.getExternalContext().getSession(true);
			}
			pvGetUserLocate();
		}
		initializeClass();
	}
	
	private void pvGetUserLocate(){
//		for( Cookie cookie : FacesContext.getCurrentInstance().getExternalContext( httpServletRequest.getCookies() ) {
//		    System.out.println( cookie.getName() + " - " + cookie.getValue() );
//		}
//		String cookieValue_Language = new Locale( "tr", "TR" ).getLanguage();
//		Cookie localeCookie_lang = new Cookie( "locale", cookieValue_Language );
//		response.addCookie( localeCookie_lang );
//		Iterator xLocales = (Iterator) FacesContext.getCurrentInstance().getExternalContext().getRequestLocales();
//		while (xLocales.){e
//			Locale xLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocales().next();
//			System.out.println(xLocale);
//		}
//		System.out.println(FacesContext.getCurrentInstance().getApplication().getDefaultLocale());
//		System.out.println(FacesContext.getCurrentInstance().getViewRoot().getLocale());
//		System.out.println(FacesContext.getCurrentInstance().getExternalContext().getRequestLocale());
//		setLocale(FacesContext.getCurrentInstance().getExternalContext().getRequestLocale());
		setLocale(FacesContext.getCurrentInstance().getApplication().getDefaultLocale());
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
	public DBSBean2 getMasterBean() {
		return wMasterBean;
	}
	
	public void setLocaleCode(String pLocale){
		wLocale = new Locale(pLocale);
		setLocale(wLocale);
	}
	public String getLocaleCode(){
		return wLocale.toString();
	}	
	
    public String getLanguage() {
        return wLocale.getLanguage();
    }

    public void setLanguage(String pLanguage) {
    	setLocale(new Locale(pLanguage));
    }
    
	public void setLocale(Locale pLocale){
//		HttpServletResponse xR = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//		xR.setLocale(pLocale);
//		xR.addHeader("Content-Language", "pt-BR");
		FacesContext.getCurrentInstance().getViewRoot().setLocale(pLocale);
	}
	
	public Locale getLocale(){
		return wLocale;
	}
	/**
	 * Lista de CrudBean escravos dentro deste crud.
	 * Os crudbean escravos serão retirados da memória quando o master for
	 * @return
	 */
	public List<DBSBean2> getSlavesBean() {
		return wSlavesBean;
	}
	
	
	/**
	 * CrudBean que será o principal. Responsável por apagar da memória os CrudBean escravos que a ele vinculados
	 */
	public void setMasterBean(DBSBean2 pBean) {
		wMasterBean = pBean;
		if (!pBean.getSlavesBean().contains(this)){
			pBean.getSlavesBean().add(this);
		}
	}
	

	/**
	 * Define o tempo máximo sem atividade para invalidar a seção. O padrão é de 600 segundos(10 minutos).<br/> 
	 * Tempo em segundos.
	 * @return
	 */
	public Integer getMaxInactiveInterval() {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMaxInactiveInterval();
	}

	/**
	 * Define o tempo máximo sem atividade para invalidar a seção. O padrão é de 600 segundos(10 minutos).
	 * @param pMaxInactiveInterval Tempo em segundos
	 */
	public void setMaxInactiveInterval(Integer pMaxInactiveInterval) {
		FacesContext.getCurrentInstance().getExternalContext().setSessionMaxInactiveInterval(pMaxInactiveInterval);
	};
	
	/**
	 * Retorna mensagens do dialog.
	 * @return
	 */
	public IDBSMessagesController getMessages(){
		return wDialogMessages;
	}

	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	public String getMessageKey(){
		return wDialogMessages.getCurrentMessage().getMessageKey();
	}
	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	@Override
	public String getMessageText(){
		return wDialogMessages.getCurrentMessage().getMessageText();
	}
	

	
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	@Override
	public String getMessageTooltip(){
		return wDialogMessages.getCurrentMessage().getMessageTooltip();
	}
	/**
	 * Retorna se é uma mensagem de alerta.
	 * As mensagens de alerta são as únicas que permiter a seleção SIM/NÃO
	 * @return
	 */
	@Override
	public MESSAGE_TYPE getMessageType(){
		if (wDialogMessages.getCurrentMessage()!=null){
			return wDialogMessages.getCurrentMessage().getMessageType();
		}
		return null;
	}


	
	/**
	 * Retorna se há alguma mensagem na fila
	 * @return
	 */
	/**
	 * @return
	 */
	@Override
	public Boolean getHasMessage(){
		return wDialogMessages.getMessages().hasMessages();
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
		if (wDialogMessages!=null){
			IDBSMessage xMessageKey = wDialogMessages.getCurrentMessage(); //Salva a chave, pois o setValidated posiciona na próxima mensagem.
			wDialogMessages.getCurrentMessage().setMessageValidated(pIsValidated);
			if (xMessageKey.getMessageType().getRequireConfirmation()){
				//Chama método indicando que warning foi validado
				warningMessageValidated(xMessageKey.getMessageKey(), pIsValidated);
			}
			return onMessageValidate(xMessageKey.getMessageKey(), pIsValidated);
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
		wDialogMessages.getMessages().clear();
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
		wDialogMessages.getMessages().add(new DBSMessage(pMessageKey, pMessageType, pMessageText, pMessageTooltip));
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
		wDialogMessages.getMessages().addAll(pMessages);
	}
	
	/**
	 * Remove uma mensagem da fila
	 * @param pMessageKey
	 */
	protected void removeMessage(String pMessageKey){
		wDialogMessages.getMessages().remove(pMessageKey);
	}

	/**
	 * Retorna se mensagem foi validada.
	 * @param pMessageKey
	 * @return
	 */
	protected Boolean isMessageValidated(String pMessageKey){
		return wDialogMessages.getMessages().getMessage(pMessageKey).isMessageValidatedTrue();
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
	
	/**
	 * Método após a inicialização do bean.
	 * Ao sobre escrever este método, deve-se estar atendo em chamar o <b>super</b>.
	 */
	protected void initializeClass(){};
	
	/**
	 * Método após a finalização do bean.
	 * Ao sobre escrever este método, deve-se estar atendo em chamar o <b>super</b>.
	 */
	protected void finalizeClass(){}

	
}
