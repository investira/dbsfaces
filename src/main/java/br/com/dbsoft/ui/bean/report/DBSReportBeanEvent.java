package br.com.dbsoft.ui.bean.report;

import br.com.dbsoft.event.DBSEvent;

public class DBSReportBeanEvent extends DBSEvent<DBSReportBean> {
	
	public static enum REPORT_EVENT{
		INITIALIZE,
		FINALIZE,
		VALIDATE;
	}
	
	private REPORT_EVENT	wEvent = null;

	
	public DBSReportBeanEvent(DBSReportBean pObject, REPORT_EVENT pEvent) {
		super(pObject);
		wEvent = pEvent;
	}
	
	public REPORT_EVENT getEvent() {
		return wEvent;
	}

}
