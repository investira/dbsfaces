package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.event.DBSEvent;
import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean.EditingMode;
import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean.EditingStage;

/**
 * @author ricardo.villar
 *
 */
public class DBSCrudOldEvent extends DBSEvent<DBSCrudOldBean> implements IDBSCrudOldEvent{

	public static enum CRUD_EVENT{
		INITIALIZE,
		FINALIZE,
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
	
	public DBSCrudOldEvent(DBSCrudOldBean pObject, CRUD_EVENT pEvent, EditingMode pEditingMode) {
		super(pObject);
		wEvent = pEvent;
		wEditingMode = pEditingMode;
	}

	@Override
	public Integer getCommittedRowCount() {
		return wCommittedRowCount;
	}

	@Override
	public void setCommittedRowCount(Integer pCommittedRowCount) {
		wCommittedRowCount = pCommittedRowCount;
	}

	@Override
	public CRUD_EVENT getEvent() {
		return wEvent;
	}

	/**
	 * Retorna o modo de edição corrente ou que está para ser ativado.
	 * @return
	 */
	@Override
	public EditingMode getEditingMode() {
		return wEditingMode;
	}

	/**
	 * Informa se a edição está sendo confirmada ou ignorada ou não está em processo de confirmação.
	 * @return
	 */
	@Override
	public EditingStage getEditingStage(){
		return getSource().getEditingStage();
	}

}
