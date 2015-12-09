package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.event.IDBSEvent;
import br.com.dbsoft.ui.bean.crud.DBSCrudBean.EditingMode;
import br.com.dbsoft.ui.bean.crud.DBSCrudBean.EditingStage;
import br.com.dbsoft.ui.bean.crud.DBSCrudBeanEvent.CRUD_EVENT;

/**
 * @author ricardo.villar
 *
 */
public interface IDBSCrudBeanEvent extends IDBSEvent<DBSCrudBean> {


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
