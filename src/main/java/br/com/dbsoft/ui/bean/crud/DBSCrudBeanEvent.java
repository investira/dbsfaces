package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.event.DBSEvent;
import br.com.dbsoft.ui.bean.crud.DBSCrudBean.EditingMode;
import br.com.dbsoft.ui.bean.crud.DBSCrudBean.EditingStage;

/**
 * @author ricardo.villar
 *
 */
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
		BEFORE_INSERT,
		BEFORE_REFRESH,
		AFTER_REFRESH,
		BEFORE_SELECT,
		AFTER_SELECT,
		AFTER_COPY,
		BEFORE_PASTE,
		BEFORE_VALIDATE,
		VALIDATE
	}

	private Integer 	wCommittedRowCount = 0;
	private CRUD_EVENT 	wEvent = null;
	private EditingMode wEditingMode = EditingMode.NONE;
	
	public DBSCrudBeanEvent(DBSCrudBean pObject, CRUD_EVENT pEvent, EditingMode pEditingMode) {
		super(pObject);
		wEvent = pEvent;
		wEditingMode = pEditingMode;
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

	/**
	 * Informa se a edição está sendo confirmada ou ignorada ou não está em processo de confirmação.
	 * @return
	 */
	public EditingStage getEditingStage(){
		return getSource().getEditingStage();
	}

}
