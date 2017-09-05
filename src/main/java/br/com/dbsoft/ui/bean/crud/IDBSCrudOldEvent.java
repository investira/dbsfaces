package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.event.IDBSEvent;
import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean.EditingMode;
import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean.EditingStage;
import br.com.dbsoft.ui.bean.crud.DBSCrudOldEvent.CRUD_EVENT;

/**
 * @author ricardo.villar
 *
 */
public interface IDBSCrudOldEvent extends IDBSEvent<DBSCrudOldBean> {


	public Integer getCommittedRowCount();

	public void setCommittedRowCount(Integer pCommittedRowCount);

	public CRUD_EVENT getEvent();

	/**
	 * Retorna o modo de edição corrente ou que está para ser ativado.
	 * @return
	 */
	public EditingMode getEditingMode();

	/**
	 * Informa se a edição está sendo confirmada ou ignorada ou não está em processo de confirmação.
	 * @return
	 */
	public EditingStage getEditingStage();

}
