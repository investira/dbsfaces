package br.com.dbsoft.ui.bean.crud;

import java.util.HashMap;
import java.util.Map;

import br.com.dbsoft.crud.IDBSCrud.ICrudAction;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessageBase.MESSAGE_TYPE;
import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.component.actioncontroller.DBSActionController;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardo.villar
 *
 */
public abstract class DBSDialogCrudBean extends DBSBean {

	private static final long serialVersionUID = 2736035271733987328L;

	public static enum CrudBeanAction {
		NONE 		("Not Editing", 0, ICrudAction.NONE),
		INSERT 		("Insert", 1, ICrudAction.MERGING),
		UPDATE 		("Update", 2, ICrudAction.MERGING), 
		DELETE 		("Delete", 3, ICrudAction.DELETING),
		VIEW 		("View", 4, ICrudAction.NONE),
		APPROVE 	("Approve", 10, ICrudAction.MERGING),
		REPROVE 	("Reprove", 11, ICrudAction.MERGING);
		
		private String 		wName;
		private int 		wCode;
		private ICrudAction wCrudAction;
		
		private CrudBeanAction(String pName, int pCode, ICrudAction pCrudAction) {
			this.wName = pName;
			this.wCode = pCode;
			this.wCrudAction = pCrudAction;
		}

		public String getName() {
			return wName;
		}

		public int getCode() {
			return wCode;
		}
		
		/**
		 * Ação equivalente no DBSCrud
		 * @return
		 */
		public ICrudAction getCrudAction() {
			return wCrudAction;
		}
		
		public static CrudBeanAction get(int pCode) {
			switch (pCode) {
			case 0:
				return NONE;
			case 1:
				return INSERT;
			case 2:
				return UPDATE;
			case 3:
				return DELETE;
			case 4:
				return APPROVE;
			case 5:
				return REPROVE;
			default:
				return NONE;
			}
		}		
	}

	public class Config{
		/**
		 * Se possui tela modal de dialogo
		 */
		private boolean wHasDialog = false;
		
		/**
		 * Se possui mensagem de confirmação antes de executar a ação. Seta como null caso não exista.
		 */
		private IDBSMessage wMessageBeforeConfirmation = null;
		
		/**
		 * Se possui mensagem de confirmação após a execução da ação. Seta como null caso não exista.
		 */
		private IDBSMessage wMessageAfterConfirmation = null;
		
		/**
		 * Se possui mensagem de do comando de ignorar. Seta como null caso não exista.
		 */
		private IDBSMessage wMessageBeforeIgnore = null;
		
		/**
		 * @return Se possui tela modal de dialogo
		 */
		public boolean hasDialog() {
			return wHasDialog;
		}
		/**
		 * @param pHasDialog Se possui tela modal de dialogo
		 */
		public void setHasDialog(boolean pHasDialog) {
			wHasDialog = pHasDialog;
		}
		/**
		 * @return Mensagem de confirmação antes de executar a ação. Seta como null caso não exista.
		 */
		public IDBSMessage getMessageBeforeConfirmation() {
			return wMessageBeforeConfirmation;
		}
		/**
		 * @param pMessageBeforeConfirmation Mensagem de confirmação antes de executar a ação. 
		 * Setar como null caso não exista.
		 * Mensagem deverá ser to tipo CONFIRM.
		 */
		public void setMessageBeforeConfirmation(IDBSMessage pMessageBeforeConfirmation) {
			if (!pMessageBeforeConfirmation.getMessageType().equals(MESSAGE_TYPE.CONFIRM)){return;}
			wMessageBeforeConfirmation = pMessageBeforeConfirmation;
		}
		/**
		 * @return Mensagem de confirmação após a execução da ação. Seta como null caso não exista
		 */
		public IDBSMessage getMessageAfterConfirmation() {
			return wMessageAfterConfirmation;
		}
		/**
		 * @param pMessageAfterConfirmation Mensagem de confirmação após a execução da ação. 
		 * Setar como null caso não exista
		 * Mensagem deverá ser to tipo SUCESS.
		 */
		public void setMessageAfterConfirmation(IDBSMessage pMessageAfterConfirmation) {
			if (!pMessageAfterConfirmation.getMessageType().equals(MESSAGE_TYPE.SUCCESS)){return;}
			wMessageAfterConfirmation = pMessageAfterConfirmation;
		}
		/**
		 * @return Mensagem de do comando de ignorar. Seta como null caso não exista.
		 */
		public IDBSMessage getMessageBeforeIgnore() {
			return wMessageBeforeIgnore;
		}
		/**
		 * @param pMessageBeforeIgnore Mensagem de do comando de ignorar. 
		 * Setar como null caso não exista.
		 * Mensagem deverá ser to tipo IGNORE.
		 */
		public void setMessageBeforeIgnore(IDBSMessage pMessageBeforeIgnore) {
			if (!pMessageBeforeIgnore.getMessageType().equals(MESSAGE_TYPE.IGNORE)){return;}
			wMessageBeforeIgnore = pMessageBeforeIgnore;
		}
		
		Config(){};
		Config (boolean pHasDialog, IDBSMessage pMessageBeforeConfirmation, IDBSMessage pMessageAfterConfirmation, IDBSMessage pMessageBeforeIgnore){
			setValues(pHasDialog, pMessageBeforeConfirmation, pMessageAfterConfirmation, pMessageBeforeIgnore);
		}
		
		/**
		 * @param pHasDialog Se possui tela modal de dialogo
		 * @param pMessageBeforeConfirmation Se possui mensagem de confirmação antes de executar a ação. Seta como null caso não exista.
		 * @param pMessageAfterConfirmation Se possui mensagem de confirmação após a execução da ação. Seta como null caso não exista.
		 * @param pMessageBeforeIgnore Se possui mensagem de do comando de ignorar. Seta como null caso não exista.
		 */
		public void setValues(boolean pHasDialog, IDBSMessage pMessageBeforeConfirmation, IDBSMessage pMessageAfterConfirmation, IDBSMessage pMessageBeforeIgnore){
			wHasDialog = pHasDialog;
			wMessageBeforeConfirmation = pMessageBeforeConfirmation;
			wMessageAfterConfirmation = pMessageAfterConfirmation;
			wMessageBeforeIgnore = pMessageBeforeIgnore;
		}
	}
	
	private CrudBeanAction				wCrudBeanAction = CrudBeanAction.NONE;
	private String					wOutcome = null;
	private Map<CrudBeanAction, Config> 	wConfigs = new HashMap<CrudBeanAction, Config>();
	
	private Config	wInsertConfig = new Config(false, 
		       new DBSMessage(MESSAGE_TYPE.CONFIRM, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.confirmInsert")),
		       new DBSMessage(MESSAGE_TYPE.SUCCESS, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.successInsert")),
		       new DBSMessage(MESSAGE_TYPE.IGNORE, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.ignoreInsert")));
	private Config	wUpdateConfig = new Config(false, 
		       new DBSMessage(MESSAGE_TYPE.CONFIRM, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.confirmUpdate")),
		       new DBSMessage(MESSAGE_TYPE.SUCCESS, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.successUpdate")),
		       new DBSMessage(MESSAGE_TYPE.IGNORE, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.ignoreUpdate")));
	private Config	wDeleteConfig = new Config(false, 
		       new DBSMessage(MESSAGE_TYPE.CONFIRM, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.confirmDelete")),
		       new DBSMessage(MESSAGE_TYPE.SUCCESS, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.successDelete")),
		       null);
	private Config	wApproveConfig = new Config(false, 
		       new DBSMessage(MESSAGE_TYPE.CONFIRM, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.confirmApprove")),
		       new DBSMessage(MESSAGE_TYPE.SUCCESS, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.successApprove")),
		       null);
	private Config	wReproveConfig = new Config(false, 
		       new DBSMessage(MESSAGE_TYPE.CONFIRM, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.confirmReprove")),
		       new DBSMessage(MESSAGE_TYPE.SUCCESS, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.successReprove")),
		       null);

	private	IDBSMessage 	wMessageError = new DBSMessage(MESSAGE_TYPE.ERROR,"Erro: %s");
	private IDBSMessage		wMessageAcaoNaoPermitida = 
						new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.actionNotPermitted"));
//	private IDBSMessage		wMessageNoRowComitted = 
//				new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.noRowComitted"));
//	private IDBSMessage		wMessageOverSize = 
//				new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.overSize"));
//	private IDBSMessage		wMessageNoChange = 
//				new DBSMessage(MESSAGE_TYPE.INFORMATION, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.noChange"));
//	private IDBSMessage		wMessaggeApprovalSameUser =
//				new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.approvalSameUser"));
	
	public DBSDialogCrudBean() {
		//Configuração padrão das ações
		wConfigs.put(CrudBeanAction.INSERT, wInsertConfig);
		wConfigs.put(CrudBeanAction.UPDATE, wUpdateConfig);
		wConfigs.put(CrudBeanAction.DELETE, wDeleteConfig);
		wConfigs.put(CrudBeanAction.APPROVE, wApproveConfig);
		wConfigs.put(CrudBeanAction.REPROVE, wReproveConfig);
	}
	
	/**
	 * Esta próporia classe para ser acessada pelos ActionController
	 * @return
	 */
	protected DBSDialogCrudBean getOuter() {return this;}
	
	/**
	 * Configuração da inserção
	 * @return
	 */
	public Config getInsertConfig(){return wInsertConfig;}
	
	/**
	 * Configuração da edição
	 * @return
	 */
	public Config getUpdateConfig(){return wUpdateConfig;}
	
	/**
	 * Configuração da deleção
	 * @return
	 */
	public Config getDeleteConfig(){return wDeleteConfig;}
	
	/**
	 * Configuração da aprovação
	 * @return
	 */
	public Config getApproveConfig(){return wApproveConfig;}

	/**
	 * Configuração da reprovação
	 * @return
	 */
	public Config getReproveConfig(){return wReproveConfig;}

	// =====================================================================
	// ACTIONS e ACTIONS CONTROLLER
	// =====================================================================
	//Controller para validar a inicializaçãp
	private DBSActionController wACInitialize = new DBSActionController() {
		
		@Override
		protected boolean onExecute(IDBSMessages pMessagesToSend) {
			try {
				return getOuter().onInitialize(pMessagesToSend) && pMessagesToSend.isAllMessagesValidatedTrue();
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
			return false;
		}
	};
	
	//Controller para validar os dados
	private DBSActionController wACValidate = new DBSActionController() {
		
		@Override
		protected void beforeExecute(IDBSMessages pMessagesToSend) {
			try {
				//----
				getOuter().beforeValidate(pMessagesToSend);
				if (!pMessagesToSend.isAllMessagesValidatedTrue()){return;}
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
		}
		
		@Override
		protected boolean onExecute(IDBSMessages pMessagesToSend) {
			try {
				return getOuter().onValidate(pMessagesToSend) && pMessagesToSend.isAllMessagesValidatedTrue();
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
			return false;
		}
	};

	//Controller para executar a ação
	private DBSActionController wACConfirm = new DBSActionController() {
		
		@Override
		protected void beforeExecute(IDBSMessages pMessagesToSend) {
			try {
				//----
				getOuter().beforeConfirm(pMessagesToSend);
				if (!pMessagesToSend.isAllMessagesValidatedTrue()){return;}
				//----
				wACValidate.execute();
				if (!wACValidate.isOk()){return;}
				//----
				pMessagesToSend.add(wConfigs.get(wCrudBeanAction).wMessageBeforeConfirmation);
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
		}
		
		@Override
		protected boolean onExecute(IDBSMessages pMessagesToSend) {
			try {
				//----
				wACValidate.execute();
				pMessagesToSend.addAll(wACValidate.getMessages());
				if (wACValidate.isOk()){
					return getOuter().onConfirm(pMessagesToSend) && pMessagesToSend.isAllMessagesValidatedTrue();
				}
				return false;
				//----
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
			return false;
		}

		@Override
		protected void afterExecute(IDBSMessages pMessagesToSend) {
			//----
			try {
				getOuter().afterConfirm(pMessagesToSend);
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
		}

		@Override
		protected void onSuccess(IDBSMessages pMessagesToSend) {
			//----
			try {
				getOuter().onSuccess(pMessagesToSend);
				if (!pMessagesToSend.hasMessages()){
					pMessagesToSend.add(wConfigs.get(wCrudBeanAction).wMessageAfterConfirmation);
				}
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
		}

		@Override
		protected void onError(IDBSMessages pMessagesToSend) {
			//----
			try {
				getOuter().onError(pMessagesToSend);
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
		}
	};

	//Controller para cancelar a ação
	private DBSActionController wACCancel = new DBSActionController() {
		
		@Override
		protected void beforeExecute(IDBSMessages pMessagesToSend) {
			try {
				getOuter().beforeCancel(pMessagesToSend);
				if (!pMessagesToSend.isAllMessagesValidatedTrue()){return;}
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
		}
		
		@Override
		protected boolean onExecute(IDBSMessages pMessagesToSend) {
			try {
				return getOuter().onCancel(pMessagesToSend);
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
			}
			return false;
		}

	};
	
	public final void insert(){
		pvDoAction(CrudBeanAction.INSERT);
	}
	public final void update(){
		pvDoAction(CrudBeanAction.UPDATE);
	}
	public final void delete(){
		pvDoAction(CrudBeanAction.DELETE);
	}
	public final void approve(){
		pvDoAction(CrudBeanAction.APPROVE);
	}
	public final void reprove(){
		pvDoAction(CrudBeanAction.REPROVE);
	}

	public final void validate(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
		wACValidate.execute();
	}
	
	public final void confirm(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
		wACConfirm.execute();
	}

	public final void cancel(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
		wACCancel.execute();
	}

	
	public final void copy(){
		if (!wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
	}

	public final void paste(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
	}

	public final void move(){
		if (!wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
	}


	// =====================================================================
	// ABSTRACTS
	// =====================================================================

	/**
	 * Disparado antes de ativar a ação.<br/>
	 * Neste método deve-se implementar código para verificar se a ação do crud poderá ser iniciada, 
	 * bem como configurar algums valores dos dados antes de exibi-los.
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @return Se é permitida inicializar a ação
	 * @throws DBSIOException
	 */
	protected boolean onInitialize(IDBSMessages pMessagesToSend) throws DBSIOException{return true;};

	/**
	 * Disparado antes de iniciar a execução.<br/> 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void beforeConfirm(IDBSMessages pMessagesToSend) throws DBSIOException{};

	/**
	 * Disparado antes de iniciar a válidação de fator.<br/>
	 * Neste método deve-se implementar ajustes dos dados que se façam para adequa-los antes de iniciar a validação no <b>onValidate</b> 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void beforeValidate(IDBSMessages pMessagesToSend) throws DBSIOException{};

	/**
	 * Disparado antes de iniciar a execução.<br/> 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected boolean onValidate(IDBSMessages pMessagesToSend) throws DBSIOException{return true;};

	/**
	 * Disparado após <b>onValidade</b>.<br/>
	 * Neste método deve-se implementar o código para efetivar o crud.<br/>
	 * Pode-se enviar mensagens de erro ou informações gerados pela execução utilizando o <b>pMessagesToSend</b>.<br/>
	 * Por questão de organização do código, para envio de mensagens não geradas pela execução, 
	 * recomenda-se a utilização do evento <b>onSuccess</b> em caso de sucesso ou <b>onError</b> em caso de erro ou <b>afterExecute</b>.
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected boolean onConfirm(IDBSMessages pMessagesToSend) throws DBSIOException{return true;};

	/**
	 * Disparado após <b>onConfirm</b> independentemente se foi executado com sucesso ou erro. 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void afterConfirm(IDBSMessages pMessagesToSend) throws DBSIOException{};

	/**
	 * Disparado após <b>afterConfirm</b> caso a execução tenha sido com sucesso. 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void onSuccess(IDBSMessages pMessagesToSend) throws DBSIOException{};

	/**
	 * Disparado após <b>afterConfirm</b> caso a execução tenha sido com erro. 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void onError(IDBSMessages pMessagesToSend) throws DBSIOException{};

	@SuppressWarnings("unused")
	protected void beforeCancel(IDBSMessages pMessagesToSend) throws DBSIOException{};
	
	@SuppressWarnings("unused")
	protected boolean onCancel(IDBSMessages pMessagesToSend) throws DBSIOException{return true;};
	
	// =====================================================================
	// ATTRIBUTES
	// =====================================================================

	public Boolean isDialogOpened(){
		if (wCrudBeanAction.equals(CrudBeanAction.INSERT) && wInsertConfig.hasDialog()
		 || wCrudBeanAction.equals(CrudBeanAction.UPDATE) && wUpdateConfig.hasDialog()
		 || wCrudBeanAction.equals(CrudBeanAction.DELETE) && wDeleteConfig.hasDialog()
		 || wCrudBeanAction.equals(CrudBeanAction.APPROVE) && wApproveConfig.hasDialog()
		 || wCrudBeanAction.equals(CrudBeanAction.REPROVE) && wReproveConfig.hasDialog()){
			return true;
		}
		return false;
	}
	
	/**
	 * View destino após a confirmação
	 * @return
	 */
	public String getOutcome(){
		return wOutcome;
	}
	
	/**
	 * View destino após a confirmação
	 * @return
	 */
	public void setOutcome(String pOutcome){
		wOutcome = pOutcome;
	}
	
	/**
	 * Ação em andamento
	 * @return
	 */
	public CrudBeanAction getCrudBeanAction(){
		return wCrudBeanAction;
	}
	
	// =====================================================================
	// PRIVATE
	// =====================================================================
	/**
	 * Inicia a ação
	 * @param pCrudBeanAction
	 * @return
	 */
	private void pvDoAction(CrudBeanAction pCrudBeanAction){
		//Configura o Action
		try{
			if (pvSetCrudBeanAction(pCrudBeanAction)){
				wACInitialize.execute();
			}
		}finally{
			if (!wACInitialize.isOk()){
				wCrudBeanAction.equals(CrudBeanAction.NONE);
			}
		}
	}

	/**
	 * Seta a ação corrente
	 * @param pCrudBeanAction
	 * @return
	 */
	private boolean pvSetCrudBeanAction(CrudBeanAction pCrudBeanAction){
		//Ignora por já estar no mesmo action
		if (pCrudBeanAction.equals(wCrudBeanAction)){return false;}
		//Não permite novo action sem finalizar o anterior
		if (!pCrudBeanAction.equals(CrudBeanAction.NONE) && !wCrudBeanAction.equals(CrudBeanAction.NONE)){return false;}
		wCrudBeanAction = pCrudBeanAction;
		return true;
	}
	
}
