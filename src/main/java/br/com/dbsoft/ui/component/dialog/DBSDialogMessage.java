package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;
import br.com.dbsoft.ui.core.DBSFaces;

public class DBSDialogMessage extends DBSMessage {

	private DIALOG_ICON wIcon;
	private Integer		wWidth;
	private Integer		wHeight;
	
	public DIALOG_ICON getIcon() {
		if (wIcon == null){
			//Força configuração do icone caso não tenha sido informado
			setIcon(DBSFaces.toDIALOG_ICON(getMessageType()));
		}
		return wIcon;
	}
	public void setIcon(DIALOG_ICON pIcon) {
		wIcon = pIcon;
	}

	public int getWidth() {
		if (wWidth==null){
			//Força o calculo da largura se for nulo
			setWidth(DBSFaces.getDialogMessageWidth(getMessageText().length()));
		}
		return wWidth;
	}
	public void setWidth(int pWidth) {
		wWidth = pWidth;
	}
	
	public int getHeight() {
		if (wHeight==null){
			//Força o calculo da altura se for nulo
			setHeight(getWidth());
		}
		return wHeight;
	}
	public void setHeight(int pHeight) {
		wHeight = pHeight;
	}
}
