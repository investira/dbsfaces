package br.com.dbsoft.ui.bean;


import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PreDestroy;

import br.com.dbsoft.error.DBSIOException;

/**
 * @author ricardo.villar
 */
public abstract class DBSBeanConnection extends DBSBean{
 
	private static final long serialVersionUID = -9043064201329995188L;

	protected 	Connection					wConnection;
	
	@Override
	@PreDestroy
	void pvFinalizeClass(){
		super.pvFinalizeClass();
		closeConnection();
	}

	//Public -------------------------------------------------------------
	/**
	 * Retorna texto da mensagem que está na fila
	 * @return
	 */
	public String getMessageKey(){
		return getMessagesController().getCurrentMessage().getMessageKey();
	}
	

	public Connection getConnection() {
		return wConnection;
	}

	public void setConnection(Connection pConnection) {
		wConnection = pConnection;
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
					getMessagesController().getMessages().add(wMessageError);
					return false;
				}
			}else{
				return true;
			}
		} catch (SQLException e) {
			wMessageError.setMessageTextParameters(e.getLocalizedMessage());
			getMessagesController().getMessages().add(wMessageError);
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
						getMessagesController().getMessages().add(wMessageError);
					}
				}
			} catch (SQLException e) {
				wMessageError.setMessageTextParameters(e.getLocalizedMessage());
				getMessagesController().getMessages().add(wMessageError);
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
