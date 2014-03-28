package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.event.DBSEvent;
import br.com.dbsoft.ui.bean.crud.DBSCrudBean.EditingMode;

public class DBSCrudBeanEvent extends DBSEvent<DBSCrudBean> {

	public static enum CRUD_EVENT{
		INITIALIZE,
		FINALIZE,
		FORM_OPENED,
		BEFORE_CLOSE,
		BEFORE_VIEW,
		AFTER_VIEW,
		BEFORE_COMMIT,
		AFTER_COMMIT,
		BEFORE_IGNORE,
		AFTER_IGNORE,
		BEFORE_EDIT,
		AFTER_EDIT,
		BEFORE_REFRESH,
		AFTER_REFRESH,
		BEFORE_SELECT,
		AFTER_SELECT,
		VALIDATE
	}

	private Integer 	wCommittedRowCount = 0;
	private CRUD_EVENT 	wEvent = null;
	private EditingMode wEditingMode = EditingMode.NONE;
	
	public DBSCrudBeanEvent(DBSCrudBean pObject, CRUD_EVENT pEvent) {
		super(pObject);
		wEvent = pEvent;
	}

	public Integer getCommittedRowCount() {
		return wCommittedRowCount;
	}

	public void setCommittedRowCount(Integer pCommittedRowCount) {
		wCommittedRowCount = pCommittedRowCount;
	}

	public CRUD_EVENT getEvent() {
		return wEvent;
	}

	/**
	 * Retorna o modo de edição corrente ou que está para ser ativado.
	 * @return
	 */
	public EditingMode getEditingMode() {
		return wEditingMode;
	}

	public void setEditingMode(EditingMode wEditingMode) {
		this.wEditingMode = wEditingMode;
	}
	

}
