package br.com.dbsoft.ui.component.dialog;

import org.joda.time.DateTime;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;
import br.com.dbsoft.ui.core.DBSFaces;

public class DBSDialogMessage extends DBSMessage implements IDBSDialogMessage {

	private DIALOG_ICON wIcon;
	private Integer		wWidth;
	private Integer		wHeight;
	
	public DBSDialogMessage() {}
	
	public DBSDialogMessage(MESSAGE_TYPE pMessageType, String pMessageText){
		pvSetMessage(pMessageText, 0, pMessageType, pMessageText, null,  null);
	}
	
	public DBSDialogMessage(MESSAGE_TYPE pMessageType, Integer pMessageCode, String pMessageText){
		pvSetMessage(pMessageText, pMessageCode, pMessageType, pMessageText, null,  null);
	}

	public DBSDialogMessage(MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip){
		pvSetMessage(pMessageText,0, pMessageType, pMessageText, pMessageTooltip,  null);
	}

	public DBSDialogMessage(MESSAGE_TYPE pMessageType, String pMessageText, DateTime pMessageTime){
		pvSetMessage(pMessageText,0, pMessageType, pMessageText, null,  pMessageTime);
	}

	public DBSDialogMessage(MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DateTime pMessageTime){
		pvSetMessage(pMessageText,0, pMessageType, pMessageText, pMessageTooltip,  pMessageTime);
	}

	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText){
		pvSetMessage(pMessageKey,0, pMessageType, pMessageText, null,  null);
	}
	
	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip){
		pvSetMessage(pMessageKey,0, pMessageType, pMessageText, pMessageTooltip,  null);
	}
	
	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DateTime pMessageTime){
		pvSetMessage(pMessageKey,0, pMessageType, pMessageText, null,  pMessageTime);
	}

	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DateTime pMessageTime){
		pvSetMessage(pMessageKey,0, pMessageType, pMessageText, pMessageTooltip,  pMessageTime);
	}
	
	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pMessageIcon){
		pvSetMessageDialog(pMessageKey, null, pMessageType, pMessageText, null, null, pMessageIcon);
	}
	
	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DIALOG_ICON pMessageIcon){
		pvSetMessageDialog(pMessageKey, null, pMessageType, pMessageText, pMessageTooltip, null, pMessageIcon);
	}
	
	@Override
	public DIALOG_ICON getIcon() {
		if (wIcon == null){
			//Força configuração do icone caso não tenha sido informado
			setIcon(DBSFaces.toDIALOG_ICON(getMessageType()));
		}
		return wIcon;
	}
	
	@Override
	public void setIcon(DIALOG_ICON pIcon) {
		wIcon = pIcon;
	}
	
	@Override
	public int getWidth() {
		if (wWidth==null){
			//Força o calculo da largura se for nulo
			setWidth(DBSFaces.getDialogMessageWidth(getMessageText().length()));
		}
		return wWidth;
	}
	
	@Override
	public void setWidth(int pWidth) {
		wWidth = pWidth;
	}
	
	@Override
	public int getHeight() {
		if (wHeight==null){
			//Força o calculo da altura se for nulo
			setHeight(DBSFaces.getDialogMessageHeight(getWidth()));
		}
		return wHeight;
	}
	
	@Override
	public void setHeight(int pHeight) {
		wHeight = pHeight;
	}
	
	@Override
	public void setMessageText(String pMessageText) {
		pMessageText = DBSFaces.getHtmlStringWithLineBreak(pMessageText);
		super.setMessageText(pMessageText);
	}
	
	@Override
	public void setMessageTooltip(String pMessageTooltip) {
		pMessageTooltip = DBSFaces.getHtmlStringWithLineBreak(pMessageTooltip);
		super.setMessageTooltip(pMessageTooltip);
	}
	
	protected void pvSetMessageDialog(String pMessageKey, Integer pMessageCode, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DateTime pMessageTime, DIALOG_ICON pMessageIcon){
		pvSetMessage(pMessageKey, pMessageCode, pMessageType, pMessageText, pMessageTooltip, pMessageTime);
		setIcon(pMessageIcon);
	}
	
}
