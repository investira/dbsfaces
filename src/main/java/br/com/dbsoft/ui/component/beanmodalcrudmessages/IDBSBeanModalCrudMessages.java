package br.com.dbsoft.ui.component.beanmodalcrudmessages;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.ui.component.beanmodalmessages.IDBSBeanModalMessages;

public interface IDBSBeanModalCrudMessages extends IDBSBeanModalMessages{
	
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
	public void setMessageConfirmationInsert(String pMessageConfirmationInsert);

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
