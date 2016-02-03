package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.dialog.DBSDialog.CONFIRMATION_TYPE;

public interface IDBSDialogMessages extends IDBSMessages<IDBSDialogMessage>{

	/**
	 * Inclui uma mensagem na fila para ser exibida.
	 * A exibição se derá na mesma ondem da inclusão
	 * @param pMessageKey Chave da mensagem que será utilizada para verificar a resposta do usuário
	 * @param pMessageIcon pIcone da mensagem para indicar o seu tipo
	 * @param pMessageText Texto da mensagem
	 * @param pButtons Botões que serão exibidos poelo usuario. Deve-se utilizar a constante DBSDialog.MESAGEM_BUTTONS
	 */
//	public MessageClass add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pMessageIcon);
//	
//	public MessageClass add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DIALOG_ICON pMessageIcon);
	
	/**
	 * Retorna se mensagem é do tipo Warning
	 * Mensagens do tipo warning são as que precisam de validação 
	 * @return
	 */
	public boolean getCurrentMessageIsWarning();
	
	/**
	 * Retorna o icone da mensagem corrente
	 * @return
	 */
	public CONFIRMATION_TYPE getCurrentMessageIcon();
	
	/**
	 * Retorna a largura da janela que será exibida a mensagem corrente
	 * @return
	 */
	public int getCurrentMessageWidth();
	
	/**
	 * Retorna a altura da janela que será exibida a mensagem corrente
	 * @return
	 */
	public int getCurrentMessageHeight();
}