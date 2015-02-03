package br.com.dbsoft.ui.servlet;

import br.com.dbsoft.event.DBSEvent;

/**
 * @author ricardo.villar
 *
 */
public class DBSFileUploadServletEvent extends DBSEvent<DBSFileUploadServlet> {

	private String wFileName;

	/**
	 * Nome do arquivo que recebido.
	 * @return
	 */
	public String getFileName() {
		return wFileName;
	}

	/**
	 * Nome do arquivo que recebido.
	 * @param pFileName
	 */
	public void setFileName(String pFileName) {
		wFileName = pFileName;
	}

	public DBSFileUploadServletEvent(DBSFileUploadServlet pObject) {
		super(pObject);
	}



}
