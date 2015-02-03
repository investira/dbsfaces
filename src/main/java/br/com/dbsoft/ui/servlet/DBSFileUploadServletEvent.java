package br.com.dbsoft.ui.servlet;

import br.com.dbsoft.event.DBSEvent;

/**
 * @author ricardo.villar
 *
 */
public class DBSFileUploadServletEvent extends DBSEvent<DBSFileUploadServlet> {

	private String wFileName;

	public String getFileName() {
		return wFileName;
	}

	public void setFileName(String pFileName) {
		wFileName = pFileName;
	}

	public DBSFileUploadServletEvent(DBSFileUploadServlet pObject) {
		super(pObject);
	}



}
