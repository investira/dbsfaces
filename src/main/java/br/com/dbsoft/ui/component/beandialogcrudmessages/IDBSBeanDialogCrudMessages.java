package br.com.dbsoft.ui.component.beandialogcrudmessages;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.ui.component.beandialogmessages.IDBSBeanDialogMessages;

public interface IDBSBeanDialogCrudMessages extends IDBSBeanDialogMessages{
	
	public Boolean getMessageConfirmationExists();
	
	public Boolean getMessageIgnoreExists();
	
	public Boolean getIsCommitting();
	
	public Boolean getIsIgnoring();
	
	public Boolean getIsUpdating();
	
	public Boolean getIsDeleting();
	
	public Boolean getIsInserting();
	
	public String endEditing(Boolean pConfirm) throws DBSIOException;
	
	public String getMessageConfirmationEdit();
	public void setMessageConfirmationEdit(String pMessageConfirmationEdit);

	public String getMessageConfirmationInsert();
	public void setMessageConfirmationInsert(String pDialogConfirmationInsertMessage);

	public String getMessageConfirmationDelete();
	public void setMessageConfirmationDelete(String pMessageConfirmationDelete);

	public String getMessageConfirmationApprove();
	public void setMessageConfirmationApprove(String pMessageConfirmationApprove);

	public String getMessageConfirmationReprove();
	public void setMessageConfirmationReprove(String pMessageConfirmationReprove);

	public String getMessageIgnoreEdit();
	public void setMessageIgnoreEdit(String pMessageIgnoreEdit);

	public String getMessageIgnoreInsert();
	public void setMessageIgnoreInsert(String pMessageIgnoreInsert);

	public String getMessageIgnoreDelete();
	public void setMessageIgnoreDelete(String pMessageIgnoreDelete);


}
