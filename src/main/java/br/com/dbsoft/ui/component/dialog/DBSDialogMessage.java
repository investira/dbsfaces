package br.com.dbsoft.ui.component.dialog;

import org.joda.time.DateTime;

import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.ui.component.dialog.DBSDialog.CONFIRMATION_TYPE;
import br.com.dbsoft.ui.core.DBSFaces;

public class DBSDialogMessage extends DBSMessage implements IDBSDialogMessage {

	private CONFIRMATION_TYPE wMessageIcon;
	private Integer		wMessageWidth;
	private Integer		wMessageHeight;
	
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
	
	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, CONFIRMATION_TYPE pMessageIcon){
		pvSetMessageDialog(pMessageKey, null, pMessageType, pMessageText, null, null, pMessageIcon);
	}
	
	public DBSDialogMessage(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, CONFIRMATION_TYPE pMessageIcon){
		pvSetMessageDialog(pMessageKey, null, pMessageType, pMessageText, pMessageTooltip, null, pMessageIcon);
	}
	
	@Override
	public CONFIRMATION_TYPE getMessageIcon() {
		if (wMessageIcon == null){
			//Força configuração do icone caso não tenha sido informado
			setMessageIcon(DBSFaces.toCONFIRMATION_TYPE(getMessageType()));
		}
		return wMessageIcon;
	}
	
	@Override
	public void setMessageIcon(CONFIRMATION_TYPE pIcon) {
		wMessageIcon = pIcon;
	}
	
	@Override
	public int getMessageWidth() {
		if (wMessageWidth==null){
			//Força o calculo da largura se for nulo
			setMessageWidth(DBSFaces.getDialogMessageWidth(getMessageText().length()));
		}
		return wMessageWidth;
	}
	
	@Override
	public void setMessageWidth(int pMessageWidth) {
		wMessageWidth = pMessageWidth;
	}
	
	@Override
	public int getMessageHeight() {
		if (wMessageHeight==null){
			//Força o calculo da altura se for nulo
			setMessageHeight(DBSFaces.getDialogMessageHeight(getMessageWidth()));
		}
		return wMessageHeight;
	}
	
	@Override
	public void setMessageHeight(int pMessageHeight) {
		wMessageHeight = pMessageHeight;
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
	
	protected void pvSetMessageDialog(String pMessageKey, Integer pMessageCode, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DateTime pMessageTime, CONFIRMATION_TYPE pMessageIcon){
		pvSetMessage(pMessageKey, pMessageCode, pMessageType, pMessageText, pMessageTooltip, pMessageTime);
		setMessageIcon(pMessageIcon);
	}
	
}
