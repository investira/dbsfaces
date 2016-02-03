package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialog.CONFIRMATION_TYPE;


public interface IDBSDialogMessage extends IDBSMessage {

	public CONFIRMATION_TYPE getMessageIcon();
	public void setMessageIcon(CONFIRMATION_TYPE pMessageIcon);

	public int getMessageWidth();
	public void setMessageWidth(int pMessageWidth);
	
	public int getMessageHeight();
	public void setMessageHeight(int pMessageHeight);
}
