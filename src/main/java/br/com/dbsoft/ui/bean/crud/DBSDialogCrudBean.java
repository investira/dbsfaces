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
/**
 * @author ricardo.villar
 *
 */
/**
 * @author ricardo.villar
 *
 */
/**
 * @author ricardo.villar
 *
 */
public abstract class DBSDialogCrudBean extends DBSBean {

	private static final long serialVersionUID = 2736035271733987328L;

	public static enum CrudBeanAction {
		NONE 		("Not Editing", 0, ICrudAction.NONE, null),
		INSERT 		("Insert", 1, ICrudAction.MERGING, " -i_add "),
		UPDATE 		("Update", 2, ICrudAction.MERGING, " -i_edit "), 
		DELETE 		("Delete", 3, ICrudAction.DELETING, " -i_delete "),
		VIEW 		("View", 4, ICrudAction.NONE, " -i_eye "),
		APPROVE 	("Approve", 10, ICrudAction.MERGING, " -i_prize_ribbon -gree "),
		REPROVE 	("Reprove", 11, ICrudAction.MERGING, " -i_prize_ribbon -red ");
		
		private String 		wName;
		private int 		wCode;
		private ICrudAction wCrudAction;
		private String		wIconClass;
		
		private CrudBeanAction(String pName, int pCode, ICrudAction pCrudAction, String pIconClass) {
			this.wName = pName;
			this.wCode = pCode;
			this.wCrudAction = pCrudAction;
			this.wIconClass = pIconClass;
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

		/**
		 * Icone que representa a ação
		 * @return
		 */
		public String getIconClass() {
			return wIconClass;
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
			if (pMessageBeforeConfirmation !=null 
			&& !pMessageBeforeConfirmation.getMessageType().equals(MESSAGE_TYPE.CONFIRM)){
				wLogger.error("setMessageBeforeConfirmation:\tMensagem precisa ser do tipo CONFIRM");
				return;
			}
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
			if (pMessageAfterConfirmation !=null
			&& !pMessageAfterConfirmation.getMessageType().equals(MESSAGE_TYPE.SUCCESS)){
				wLogger.error("setMessageBeforeConfirmation:\tMensagem precisa ser do tipo SUCESS");
				return;
			}
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
			if (pMessageBeforeIgnore !=null
			&& !pMessageBeforeIgnore.getMessageType().equals(MESSAGE_TYPE.IGNORE)){
				wLogger.error("setMessageBeforeConfirmation:\tMensagem precisa ser do tipo IGNORE");
				return;
			}
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
	private String						wOutcome = null;
	private Map<CrudBeanAction, Config> wConfigs = new HashMap<CrudBeanAction, Config>();
	private String						wCaption = null;
	
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
	 * Cabeçalho do crud
	 * @return
	 */
	public String getCaption(){return wCaption;}
	/**
	 * Cabeçalho do crud
	 * @param pCaption
	 */
	public void setCaption(String pCaption){wCaption = pCaption;}
	
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
	// PROTECTED
	// =====================================================================
	/**
	 * Esta próporia classe para ser acessada pelos ActionController
	 * @return
	 */
	protected DBSDialogCrudBean getOuter() {return this;}
	

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
				getOuter().pvFinalize();
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
				getOuter().pvFinalize();
			}
		}
		
		@Override
		protected boolean onExecute(IDBSMessages pMessagesToSend) {
			try {
				return getOuter().onValidate(pMessagesToSend) && pMessagesToSend.isAllMessagesValidatedTrue();
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
				getOuter().pvFinalize();
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
				pMessagesToSend.addAll(wACValidate.getMessages());
				if (!pMessagesToSend.isAllMessagesValidatedTrue()){return;}
				//---- Mensagem padrão(se existir)
				pMessagesToSend.add(wConfigs.get(wCrudBeanAction).getMessageBeforeConfirmation());
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
				getOuter().pvFinalize();
			}
		}
		
		@Override
		protected boolean onExecute(IDBSMessages pMessagesToSend) {
			try {
				//----
				wACValidate.execute();
				pMessagesToSend.addAll(wACValidate.getMessages());
				if (!pMessagesToSend.isAllMessagesValidatedTrue()){return false;}
				//----
				return getOuter().onConfirm(pMessagesToSend) && pMessagesToSend.isAllMessagesValidatedTrue();
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
				getOuter().pvFinalize();
			}
			return false;
		}

		@Override
		protected void onSuccess(IDBSMessages pMessagesToSend) {
			try {
				//----
				getOuter().onSuccess(pMessagesToSend);
				if (!pMessagesToSend.hasMessages()){
					//---- Mensagem padrão(se existir)
					pMessagesToSend.add(wConfigs.get(wCrudBeanAction).getMessageAfterConfirmation());
				}
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
				getOuter().pvFinalize();
			}
		}

		@Override
		protected void onError(IDBSMessages pMessagesToSend) {
			try {
				//----
				getOuter().onError(pMessagesToSend);
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
				getOuter().pvFinalize();
			}
		}

		@Override
		protected void afterExecute(IDBSMessages pMessagesToSend) {
			try {
				//----
				getOuter().afterConfirm(pMessagesToSend);
			} catch (DBSIOException e) {
				wMessageError.setMessageText(e.getLocalizedMessage());
				pMessagesToSend.add(wMessageError);
				getOuter().pvFinalize();
			}
		}
		
		@Override
		protected void onFinalize() {
			if (isOk()){
				getOuter().pvFinalize();
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
				//---- Mensagem padrão(se existir)
				pMessagesToSend.add(wConfigs.get(wCrudBeanAction).getMessageBeforeIgnore());
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
			}finally {
				getOuter().pvFinalize();
			}
			return false;
		}

	};
	
	/**
	 * Ativa ação de inclusão.<br/>
	 * Após dados inseridos, para confirmar ação deve-se chamar <b>confirm</b>.
	 * Para cancelar ação deve-se chamar <b>cancel</b>.
	 */
	public void insert(){
		pvDoAction(CrudBeanAction.INSERT);
	}
	
	/**
	 * Ativa ação de edição.<br/>
	 * Após dados editados, para confirmar ação deve-se chamar <b>confirm</b>.
	 * Para cancelar ação deve-se chamar <b>cancel</b>.
	 */
	public void update(){
		pvDoAction(CrudBeanAction.UPDATE);
	}
	
	/**
	 * Ativa ação de exclusão.<br/>
	 * Para confirmar ação deve-se chamar <b>confirm</b>.
	 * Para cancelar ação deve-se chamar <b>cancel</b>.
	 */
	public void delete(){
		pvDoAction(CrudBeanAction.DELETE);
	}

	/**
	 * Ativa ação de aprovação.<br/>
	 * Para confirmar ação deve-se chamar <b>confirm</b>.
	 * Para cancelar ação deve-se chamar <b>cancel</b>.
	 */
	public void approve(){
		pvDoAction(CrudBeanAction.APPROVE);
	}
	/**
	 * Ativa ação de reprovação.<br/>
	 * Para confirmar ação deve-se chamar <b>confirm</b>.
	 * Para cancelar ação deve-se chamar <b>cancel</b>.
	 */
	public void reprove(){
		pvDoAction(CrudBeanAction.REPROVE);
	}

	/**
	 * Dispara os eventos de validação sem efetuar qualquer alteração.<br/>
	 */
	public void validate(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
		wACValidate.execute();
	}
	
	/**
	 * Confirma a execução da ação ativa.<br/>
	 */
	public void confirm(){
		System.out.println("confirm action");
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
		wACConfirm.execute();
	}

	/**
	 * Cancela a ação ativa.<br/>
	 */
	public void cancel(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
		wACCancel.execute();
	}

	
	/**
	 * Dispara evento para copia dos dados atuais para memória.<br/>
	 */
	public void copy(){
		if (!wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
	}

	/**
	 * Dispara evento para copia em memória para os dados atuais.<br/>
	 */
	public void paste(){
		if (wCrudBeanAction.equals(CrudBeanAction.NONE)){return;}
	}

	
	/**
	 * Move para o próximo registro 
	 */
	public void move(){
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
	 * Disparado antes de iniciar a execução e antes do <b>beforeValidate</b> e <b>onValidate</b>.<br/> 
	 * A ação será interrompida caso exista alguma mensagem não validada.</b> 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void beforeConfirm(IDBSMessages pMessagesToSend) throws DBSIOException{};

	/**
	 * Disparado antes de iniciar a validação de fato no <b>onValidade</b>.<br/>
	 * Neste método deve-se implementar ajustes dos dados que se façam necessários para adequa-los antes de iniciar a validação. 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected void beforeValidate(IDBSMessages pMessagesToSend) throws DBSIOException{};

	/**
	 * Disparado antes de iniciar a confirmação no <b>onConfirm</b>.<br/> 
	 * @param pMessagesToSend Mensagens a serem enviadas
	 * @throws DBSIOException
	 */
	protected boolean onValidate(IDBSMessages pMessagesToSend) throws DBSIOException{return true;};

	/**
	 * Disparado após <b>onValidade</b>.<br/>
	 * Neste método deve-se implementar o código para efetivar o crud.<br/>
	 * Pode-se enviar mensagens gerados pela execução utilizando o <b>pMessagesToSend</b>.<br/>
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

	/**
	 * Disparado antes de iniciar o cancelamento da ação e antes do <b>onCancel</b>.</br>
	 * O cancelamento será interrompido caso exista alguma mensagem não validada. 
	 * @param pMessagesToSend
	 * @throws DBSIOException
	 */
	protected void beforeCancel(IDBSMessages pMessagesToSend) throws DBSIOException{};
	
	/**
	 * Disparado após o <b>onCancel</b>.
	 * @param pMessagesToSend
	 * @return
	 * @throws DBSIOException
	 */
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
		try{
			//Configura o Action
			if (pvSetCrudBeanAction(pCrudBeanAction)){
				//Executa a inicialização do Action
				wACInitialize.execute();
			}
		}finally{
			//Impede a ativação do Action caso exista erro na inicialização
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
	
	/**
	 * Finaliza Action
	 */
	private void pvFinalize(){
		pvSetCrudBeanAction(CrudBeanAction.NONE);
	}
	
}
