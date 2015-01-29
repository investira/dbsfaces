package br.com.dbsoft.ui.servlet;

import br.com.dbsoft.error.DBSIOException;

public interface IDBSFileUploadServletEventsListener {

	/**
	 * Evento ocorre antes de iniciar o upload.<br/>
	 * Para ignorar o upload, deve-se setar <b>setOk(False)</b>.
	 * @return
	 */
	public abstract void beforeUpload(DBSFileUploadServletEvent pEvent) throws DBSIOException;;
	
	/**
	 * Evento ocorre após finializado o upload.<br/>
	 */
	public abstract void afterUpload(DBSFileUploadServletEvent pEvent) throws DBSIOException;;

	/**
	 * Para ignorar o save, deve-se setar <b>setOk(False)</b>.<br/>
	 * Evento ocorre após finalizado o upload do arquivo e antes que ele seja salvo localmente.<br/>
	 */
	public abstract void beforeSave(DBSFileUploadServletEvent pEvent) throws DBSIOException;;
	
	/**
	 * Evento ocorre após o arquivo ter sido salvo localmente.<br/>
	 */
	public abstract void afterSave(DBSFileUploadServletEvent pEvent) throws DBSIOException;;
	
	
	public abstract void onError(DBSFileUploadServletEvent pEvent) throws DBSIOException;;

}
