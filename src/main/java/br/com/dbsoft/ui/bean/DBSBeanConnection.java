package br.com.dbsoft.ui.bean;


import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PreDestroy;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;
import br.com.dbsoft.message.IDBSMessages;

/**
 * @author ricardo.villar
 * Bean básic quando se precisar utiliazar conexão e controle de mensagens
 */
public abstract class DBSBeanConnection extends DBSBean{
 
	private static final long serialVersionUID = -9043064201329995188L;

	protected 	Connection		wConnection;
	protected 	IDBSMessage 	wMessageError = new DBSMessage(MESSAGE_TYPE.ERROR,"Erro: %s");
	protected 	IDBSMessages 	wMessages = new DBSMessages(true);

	@Override
	@PreDestroy
	void pvFinalizeClass(){
		super.pvFinalizeClass();
		closeConnection();
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
					wMessages.add(wMessageError.clone());
					return false;
				}
			}else{
				return true;
			}
		} catch (SQLException e) {
			wMessageError.setMessageTextParameters(e.getLocalizedMessage());
			wMessages.add(wMessageError.clone());
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
						wMessages.add(wMessageError.clone());
					}
				}
			} catch (SQLException e) {
				wMessageError.setMessageTextParameters(e.getLocalizedMessage());
				wMessages.add(wMessageError.clone());
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
