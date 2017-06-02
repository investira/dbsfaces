package br.com.dbsoft.ui.component.modalmessages;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;

public interface IDBSModalMessages {
	
	public String getMessageText();
	
	public MESSAGE_TYPE getMessageType();

	public String getMessageTooltip();

	/**
	 * Retorna se existe alguma mensagem corrente.
	 * @return
	 */
	public Boolean getHasMessage();
	
	public String setMessageValidated(Boolean pIsValidated) throws DBSIOException;
}
