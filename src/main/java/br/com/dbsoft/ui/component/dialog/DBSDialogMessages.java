package br.com.dbsoft.ui.component.dialog;

import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;

/**
 * @author ricardovillar
 *
 */
public class DBSDialogMessages extends DBSMessages<IDBSDialogMessage> implements IDBSDialogMessages {

//	public <T extends DBSDialogMessage> DBSDialogMessages(Class<MessageClass> pMessageClass) {
//		super(pMessageClass);
//	}

//	/**
//	 * Inclui uma mensagem na fila para ser exibida.
//	 * A exibição se derá na mesma ondem da inclusão
//	 * @param pMessageKey Chave da mensagem que será utilizada para verificar a resposta do usuário
//	 * @param pMessageIcon pIcone da mensagem para indicar o seu tipo
//	 * @param pMessageText Texto da mensagem
//	 * @param pButtons Botões que serão exibidos poelo usuario. Deve-se utilizar a constante DBSDialog.MESAGEM_BUTTONS
//	 */
//	@Override
//	public MessageClass add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pMessageIcon){
//		return pvCreateMessage(pMessageKey, null, pMessageType, pMessageText, null, null, pMessageIcon);
//	}
//	
//	@Override
//	public MessageClass add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DIALOG_ICON pMessageIcon){
//		return pvCreateMessage(pMessageKey, null, pMessageType, pMessageText, pMessageTooltip, null, pMessageIcon);
//	}
	
//	@Override
//	protected MessageClass pvCreateMessage(String pMessageKey, 
//												Integer pMessageCode, 
//												MESSAGE_TYPE pMessageType,
//												String pMessageText, 
//												String pMessageTooltip, 
//												DateTime pMessageTime) {
//		MessageClass xM = super.pvCreateMessage(pMessageKey, pMessageCode, pMessageType, pMessageText, pMessageTooltip, pMessageTime);
//		//Inclui quebra de linha
//		pMessageText = DBSFaces.getHtmlStringWithLineBreak(pMessageText);
//		pMessageTooltip = DBSFaces.getHtmlStringWithLineBreak(pMessageTooltip);
//		
//		Integer xWidth = DBSFaces.getDialogMessageWidth(pMessageText.length());
//		Integer xHeight = DBSFaces.getDialogMessageHeight(xWidth);
//
//		xM.setIcon(DBSFaces.toDIALOG_ICON(pMessageType)); 
//		xM.setWidth(DBSFaces.getDialogMessageWidth(xWidth));
//		xM.setHeight(DBSFaces.getDialogMessageHeight(xHeight));
//		
//		return xM;
//	}
	
//	protected MessageClass pvCreateMessage(String pMessageKey, 
//												Integer pMessageCode, 
//												MESSAGE_TYPE pMessageType,
//												String pMessageText, 
//												String pMessageTooltip, 
//												DateTime pMessageTime,
//												DIALOG_ICON pMessageIcon) {
//		MessageClass xM = super.pvCreateMessage(pMessageKey, pMessageCode, pMessageType, pMessageText, pMessageTooltip, pMessageTime);
//		xM.setIcon(pMessageIcon);
//		return xM;
//	}

	/**
	 * Retorna se mensagem é do tipo Warning
	 * Mensagens do tipo warning são as que precisam de validação 
	 * @return
	 */
	@Override
	public boolean getIsWarning(){
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
	public DIALOG_ICON getIcon(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getIcon();
		}else{
			return DIALOG_ICON.NENHUM;
		}
	}
	
	
	/**
	 * Retorna a largura da janela que será exibida a mensagem corrente
	 * @return
	 */
	@Override
	public int getWidth(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getWidth();
		}else{
			return 100;
		}
	}
	
	/**
	 * Retorna a altura da janela que será exibida a mensagem corrente
	 * @return
	 */
	@Override
	public int getHeight(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getHeight();
		}else{
			return 100;
		}
	}
	

}
