package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;


public interface IDBSDialogMessage extends IDBSMessage {

	public DIALOG_ICON getMessageIcon();
	public void setMessageIcon(DIALOG_ICON pMessageIcon);

	public int getMessageWidth();
	public void setMessageWidth(int pMessageWidth);
	
	public int getMessageHeight();
	public void setMessageHeight(int pMessageHeight);
}
