package br.com.dbsoft.ui.component.dialog;

import org.joda.time.DateTime;

import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessages;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.component.dialog.DBSDialog.DIALOG_ICON;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
public class DBSDialogMessages extends DBSMessages<DBSDialogMessage> {

	public DBSDialogMessages(Class<DBSDialogMessage> pMessageClass) {
		super(pMessageClass);
	}

	@Override
	public void add(DBSIOException e){
		add(MESSAGE_TYPE.ERROR, e.getLocalizedMessage(), e.getOriginalException().getLocalizedMessage());
	}
	
	/* (non-Javadoc)
	 * @see br.com.dbsoft.message.DBSMessages#add(br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE, java.lang.String)
	 */
	@Override
	public void add(MESSAGE_TYPE pMessageType, String pMessageText){
		add(pMessageText, pMessageType, pMessageText, "");
	}
	
	/* (non-Javadoc)
	 * @see br.com.dbsoft.message.DBSMessages#add(br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE, java.lang.String, java.lang.String)
	 */
	@Override
	public void add(MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip){
		add(pMessageText, pMessageType, pMessageText, pMessageTooltip);
	}

	/* (non-Javadoc)
	 * @see br.com.dbsoft.message.DBSMessages#add(br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE, java.lang.String, java.lang.String, org.joda.time.DateTime)
	 */
	@Override
	public void add(MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip, DateTime pTime){
		add(pMessageText, pMessageType, pMessageText, pMessageTooltip);
	}

	/* (non-Javadoc)
	 * @see br.com.dbsoft.message.DBSMessages#add(java.lang.String, br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE, java.lang.String, java.lang.String)
	 */
	@Override
	public void add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, String pMessageTooltip){
		//Configura o icone do dialog confome o tipo de mensagem
		DIALOG_ICON xDialogIcon = DBSFaces.toDIALOG_ICON(pMessageType);
		add(pMessageKey, pMessageType, pMessageText, xDialogIcon, pMessageTooltip);
	}

	/**
	 * Inclui uma mensagem na fila para ser exibida.
	 * A exibição se derá na mesma ondem da inclusão
	 * @param pMessageKey Chave da mensagem que será utilizada para verificar a resposta do usuário
	 * @param pMessageIcon pIcone da mensagem para indicar o seu tipo
	 * @param pMessageText Texto da mensagem
	 * @param pButtons Botões que serão exibidos poelo usuario. Deve-se utilizar a constante DBSDialog.MESAGEM_BUTTONS
	 */
	public void add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pMessageIcon){
		add(pMessageKey, pMessageType, pMessageText, pMessageIcon, null);
	}
	
	public void add(String pMessageKey, MESSAGE_TYPE pMessageType, String pMessageText, DIALOG_ICON pMessageIcon, String pMessageTooltip){
		//Calcula largura e altura da janela
//		Double xWidth = DBSNumber.exp((double)(pMessageText.length()*10), 0.70) + 150;
//		xWidth = DBSNumber.inte(xWidth);
//		Double xHeight;
//		xHeight = xWidth * 0.70;
//		xHeight = DBSNumber.inte(xHeight);
		Integer xWidth = DBSFaces.getDialogMessageWidth(pMessageText.length());
		Integer xHeight = DBSFaces.getDialogMessageHeight(xWidth);
		
		//Inclui quebra de linha
		pMessageText = DBSFaces.getHtmlStringWithLineBreak(pMessageText);
		pMessageTooltip = DBSFaces.getHtmlStringWithLineBreak(pMessageTooltip);

		//Adiciona mensagem
		super.add(pMessageKey, pMessageType, pMessageText, pMessageTooltip);
		
		//Configura os atributos
		wMessages.get(pMessageKey).setIcon(pMessageIcon);
		wMessages.get(pMessageKey).setWidth(xWidth);
		wMessages.get(pMessageKey).setHeight(xHeight);
	}
	

	/**
	 * Retorna se mensagem é do tipo Warning
	 * Mensagens do tipo warning são as que precisam de validação 
	 * @return
	 */
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
	public int getHeight(){
		if (wCurrentMessageKey !=null){
			return wMessages.get(wCurrentMessageKey).getHeight();
		}else{
			return 100;
		}
	}
	
	/**
	 * Retorna o tipo de mensagem
	 * @return
	 */
	public MESSAGE_TYPE getMessageType(){
		if (wCurrentMessageKey !=null){
			return getCurrentMessage().getMessageType();
		}
		return null;
	}

	/**
	 * Adiciona todas as mensagems a fila
	 * @param pMessages
	 */
	@Override
	public <M extends DBSMessages<?>> void addAll(M pMessages){
		for (Object xM : pMessages.getMessages().values()) {
			DBSMessage xMsg = (DBSMessage) xM;
			add(xMsg.getMessageText(), xMsg.getMessageType(), xMsg.getMessageText(), xMsg.getMessageTooltip());
		}
	}

}
