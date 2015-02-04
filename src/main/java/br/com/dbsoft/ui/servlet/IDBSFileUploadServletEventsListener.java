package br.com.dbsoft.ui.servlet;

import br.com.dbsoft.error.DBSIOException;

public interface IDBSFileUploadServletEventsListener {

	/**
	 * Evento ocorre antes de iniciar o upload.<br/>
	 * Este evento ocorre somente uma vez, mesmo que esteja sendo efetuado o upload de diversos arquivos.<br/>
	 * Para tratar arquivos individualmente, utilize o evento <b>beforeSave</b>.<br/>  
	 * Para impedir o upload, deve-se setar <b>setOk(False)</b>.
	 * Neste evento deve-se configurar o caminho local onde o arquivo será salvo utilizando o método <b>setLocalPath</b>, 
	 * bem como os listners ser houver.<br/>
	 */
	public abstract void beforeUpload(DBSFileUploadServletEvent pEvent) throws DBSIOException;;
	
	/**
	 * Evento ocorre após finializado o upload.<br/>
	 * Este evento ocorre somente uma vez, mesmo que esteja sendo efetuado o upload de diversos arquivos.<br/>
	 */
	public abstract void afterUpload(DBSFileUploadServletEvent pEvent) throws DBSIOException;;

	/**
	 * Evento ocorre após finalizado o upload do arquivo e antes que ele seja salvo localmente.<br/>
	 * Pode-se neste evento, alterar o nome do arquivo utilizando <b>setFileName</b> para que seja salvo com outro nome.
	 * Pode-se impedir que ele seja salvo,  setando <b>setOk(False)</b>.<br/>
	 */
	public abstract void beforeSave(DBSFileUploadServletEvent pEvent) throws DBSIOException;;
	
	/**
	 * Evento ocorre após o arquivo ter sido salvo localmente.<br/>
	 */
	public abstract void afterSave(DBSFileUploadServletEvent pEvent) throws DBSIOException;;
	
	
	public abstract void onError(DBSFileUploadServletEvent pEvent) throws DBSIOException;;

}
