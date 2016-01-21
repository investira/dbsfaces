package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;

/**
 * @author ricardovillar
 *
 */
public class DBSDialogMessages extends DBSMessages<IDBSDialogMessage> implements IDBSDialogMessages {

	/**
	 * Retorna se mensagem é do tipo Warning
	 * Mensagens do tipo warning são as que precisam de validação 
	 * @return
	 */
	@Override
	public boolean getCurrentMessageIsWarning(){
		if (wCurrentMessageKey !=null){
			if (getCurrentMessage().getMessageType() == MESSAGE_TYPE.WARNING){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Retorna o icone da mensagem corrente
	 * @return
	 */
	@Override
	public DIALOG_ICON getCurrentMessageIcon(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getMessageIcon();
		}else{
			return DIALOG_ICON.NENHUM;
		}
	}
	
	
	/**
	 * Retorna a largura da janela que será exibida a mensagem corrente
	 * @return
	 */
	@Override
	public int getCurrentMessageWidth(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getMessageWidth();
		}else{
			return 100;
		}
	}
	
	/**
	 * Retorna a altura da janela que será exibida a mensagem corrente
	 * @return
	 */
	@Override
	public int getCurrentMessageHeight(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getMessageHeight();
		}else{
			return 100;
		}
	}
	

}
