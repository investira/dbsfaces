package br.com.dbsoft.ui.component.beanmodalmessages;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;

public interface IDBSBeanModalMessages {
	
	public String getMessageText();
	
	public MESSAGE_TYPE getMessageType();

	public String getMessageTooltip();

	public Boolean getHasMessage();
	
	public String setMessageValidated(Boolean pIsValidated) throws DBSIOException;
}
