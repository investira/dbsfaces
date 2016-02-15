package br.com.dbsoft.ui.component.beandialogmessages;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;

public interface IDBSBeanDialogMessages {
	
	public String getMessageText();
	
	public MESSAGE_TYPE getMessageType();

	public String getMessageTooltip();

	public Boolean getHasMessage();
	
	public String setMessageValidated(Boolean pIsValidated) throws DBSIOException;
}
