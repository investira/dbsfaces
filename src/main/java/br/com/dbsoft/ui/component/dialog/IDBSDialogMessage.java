package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;


public interface IDBSDialogMessage extends IDBSMessage {

	public DIALOG_ICON getIcon();
	public void setIcon(DIALOG_ICON pIcon);

	public int getWidth();
	public void setWidth(int pWidth);
	
	public int getHeight();
	public void setHeight(int pHeight);
}
