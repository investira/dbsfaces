package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;

public interface IDBSDialogMessages<MessageClass extends IDBSDialogMessage> extends IDBSMessages<MessageClass>{

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
	public boolean getIsWarning();
	
	/**
	 * Retorna o icone da mensagem corrente
	 * @return
	 */
	public DIALOG_ICON getIcon();
	
	/**
	 * Retorna a largura da janela que será exibida a mensagem corrente
	 * @return
	 */
	public int getWidth();
	
	/**
	 * Retorna a altura da janela que será exibida a mensagem corrente
	 * @return
	 */
	public int getHeight();
}
