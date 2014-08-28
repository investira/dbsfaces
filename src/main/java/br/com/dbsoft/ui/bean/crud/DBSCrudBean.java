package br.com.dbsoft.ui.bean.crud;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.enterprise.context.Conversation;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.com.dbsoft.core.DBSApproval;
import br.com.dbsoft.core.DBSApproval.APPROVAL_STAGE;
import br.com.dbsoft.core.DBSSDK;
import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.io.DBSColumn;
import br.com.dbsoft.io.DBSDAO;
import br.com.dbsoft.io.DBSResultDataModel;
import br.com.dbsoft.message.DBSMessage;
import br.com.dbsoft.message.DBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.bean.crud.DBSCrudBeanEvent.CRUD_EVENT;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.component.crudform.DBSCrudForm;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSIO;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


/**
 * Os DBSCrudBean devem ser declarados como @ConversationScoped.
 * O timeout da conversação esta definido em 10minutos
 * @author ricardo.villar
 *
 */
public abstract class DBSCrudBean extends DBSBean{

	private static final long serialVersionUID = -8550893738791483527L;
	
	public static enum EditingMode {
		NONE 			("Not Editing", 0),
		INSERTING 		("Inserting", 1),
		UPDATING 		("Updating", 2), 
		DELETING 		("Deleting", 3),
		APPROVING 		("Approving", 4),
		REPROVING 		("Reproving", 5);
		
		private String 	wName;
		private int 	wCode;
		
		private EditingMode(String pName, int pCode) {
			this.wName = pName;
			this.wCode = pCode;
		}

		public String getName() {
			return wName;
		}

		public int getCode() {
			return wCode;
		}
		
		public static EditingMode get(int pCode) {
			switch (pCode) {
			case 0:
				return NONE;
			case 1:
				return INSERTING;
			case 2:
				return UPDATING;
			case 3:
				return DELETING;
			case 4:
				return APPROVING;
			case 5:
				return REPROVING;
			default:
				return NONE;
			}
		}		
	}
	
	public static enum EditingStage{
		NONE 			("None", 0),
		COMMITTING 		("Committing", 1),
		IGNORING 		("Ignoring", 2);
		
		private String 	wName;
		private int 	wCode;
		
		private EditingStage(String pName, int pCode) {
			this.wName = pName;
			this.wCode = pCode;
		}

		public String getName() {
			return wName;
		}

		public int getCode() {
			return wCode;
		}
		
		public static EditingStage get(int pCode) {
			switch (pCode) {
			case 0:
				return NONE;
			case 1:
				return COMMITTING;
			case 2:
				return IGNORING;
			default:
				return NONE;
			}
		}		
	}

	@Inject
	private Conversation						wConversation;
	
	private static final long wConversationTimeout = 600000;  //10 minutos


	protected DBSDAO<?>							wDAO;
	private List<IDBSCrudBeanEventsListener>	wEventListeners = new ArrayList<IDBSCrudBeanEventsListener>();
	private EditingMode							wEditingMode = EditingMode.NONE;
	private EditingStage						wEditingStage = EditingStage.NONE;
	private String								wCrudFormFile = "";
	private DBSCrudForm 						wCrudForm;
	private List<Integer> 						wSelectedRowsIndexes =  new ArrayList<Integer>();
	private	Collection<DBSColumn> 				wSavedCurrentColumns = null;
	private boolean								wValueChanged;
	private int									wCopiedRowIndex = -1;
	private boolean								wValidateComponentHasError = false;
	private boolean								wDialogEdit = true;
	private Boolean 							wDialogOpened = false;
	private String								wDialogConfirmationEditMessage = "Confirmar a edição?";
	private String								wDialogConfirmationInsertMessage = "Confirmar a inclusão?";
	private String								wDialogConfirmationDeleteMessage = "Confirmar a exclusão?";
	private String								wDialogConfirmationApproveMessage = "Confirmar a aprovação?";
	private String								wDialogConfirmationReproveMessage = "Confirmar a reprovação?";
	private String								wDialogIgnoreEditMessage = "Ignorar a edição?";
	private String								wDialogIgnoreInsertMessage = "Ignorar a inclusão?";
	private String								wDialogIgnoreDeleteMessage = "Ignorar a exclusão?";
	private String								wDialogCaption;
	private	Boolean								wInsertSelected = false;
	private Boolean								wAllowUpdate = true;
	private Boolean								wAllowInsert = true;
	private Boolean								wAllowDelete = true;
	private Boolean								wAllowRefresh = true;
	private Boolean								wAllowApproval = false;
	private Boolean								wAllowApprove = true;
	private Boolean								wAllowReprove = true;
	private	Integer								wApprovalUserStages = 0;
	private String								wColumnNameApprovalStage = null;
	private String								wColumnNameApprovalUserIdRegistered = null;
	private String								wColumnNameApprovalUserIdVerified = null;
	private String								wColumnNameApprovalUserIdConferred = null;
	private String								wColumnNameApprovalUserIdApproved = null;
	private String								wColumnNameApprovalDateApproved = null;
	private String								wColumnNameDateOnInsert = null;
	private String								wColumnNameDateOnUpdate = null;
	private String								wColumnNameUserIdOnInsert = null;
	private String								wColumnNameUserIdOnUpdate = null;
	private Integer								wUserId;
	private Boolean								wMultipleSelection = false;
	private DBSCrudBean							wParentCrudBean = null;
	private List<DBSCrudBean>					wChildrenCrudBean = new ArrayList<DBSCrudBean>();


	//Mensagens
	private DBSMessage							wMessageNoRowComitted = 
												new DBSMessage(MESSAGE_TYPE.ERROR,"Erro durante a gravação.\n Nenhum registro foi afetado.\n");
	private DBSMessage							wMessageOverSize = 
												new DBSMessage(MESSAGE_TYPE.ERROR,"Quantidade de caracteres do texto digitado no campo '%s' ultrapassou a quantidade permitida de %s caracteres. Por favor, diminua o texto digitado.");
	private DBSMessage							wMessageNoChange = 
												new DBSMessage(MESSAGE_TYPE.INFORMATION,"Não houve alteração de informação.");
	private DBSMessage							wMessaggeApprovalSameUserError =
												new DBSMessage(MESSAGE_TYPE.ERROR,"Não é permitida a aprovação de um registro incluido pelo próprio usuário.");
			
	
	@Override
	protected void initializeClass() {
		pvConversationBegin();
		pvFireEventInitialize();
		//Finaliza os outros crudbeans antes de inicializar este.
//		DBSFaces.finalizeDBSBeans(this, false); << Comentado pois os beans passaram a ser criados como ConversationScoped - 12/Ago/2014
	}
	

	
	@Override
	protected void finalizeClass(){
		pvFireEventFinalize();
		//Exclui os listeners associadao, antes de finalizar
		wEventListeners.clear();
	}
	
	/**
	 * Classe que receberá as chamadas dos eventos quando ocorrerem.<br/>
	 * Para isso, classe deverá implementar a interface DBSTarefa.TarefaEventos<br/>
	 * Lembre-se de remove-la utilizando removeEventListener quando a classe for destruida, para evitar que ela seja chamada quando já não deveria. 
	 * @param pEventListener Classe
	 */
	public void addEventListener(IDBSCrudBeanEventsListener pEventListener) {
		if (!wEventListeners.contains(pEventListener)){
			wEventListeners.add(pEventListener);
		}
	}

	
	public void removeEventListener(IDBSCrudBeanEventsListener pEventListener) {
		if (wEventListeners.contains(pEventListener)){
			wEventListeners.remove(pEventListener);
		}
	}	
	
	
	/**
	 * Seta o valor da coluna na posição atual do registro 
	 * O valor será convertido para a class informada
	 * obs:Os nomes das colunas são definidos a partir da query efetuado no DAO
	 * @param pColumnName
	 * @param pColumnValue
	 * @param pValueClass Classe para a qual o valor será convertido
	 */
	public <T> void setValue(String pColumnName, T pColumnValue, Class<?> pValueClass){
		T xValue = DBSObject.toClass(pColumnValue, pValueClass);
		
		setValue(pColumnName, xValue);
	}

	/**
	 * Seta o valor da coluna na posição atual do registro
	 * O valor será convertido para o mesmo tipo da class
	 * obs:Os nomes das colunas são definidos a partir da query efetuado no DAO
	 * @param pColumnName
	 * @param pColumnValue
	 */
	public <T> void setValue(String pColumnName, T pColumnValue){
		//Utiliza ListValue para controlar os valores de todas as linhas
		if (!wDialogEdit){
			setListValue(pColumnName, pColumnValue);
		}else{
			pvSetValueDAO(pColumnName, pColumnValue);
		}
	}
	
	/**
	 * Retorna o valor da coluna
	 * @param pColumnName
	 * @return
	 */
	
	public <T> T getValue(String pColumnName){
		//Utiliza ListValue para controlar os valores de todas as linhas
		if (!wDialogEdit){
			return getListValue(pColumnName);
		}else{
			return pvGetValue(pColumnName);
		}
	}
	
	
	/**
	 * Retorna o valor da coluna convertida para a classe do tipo informado
	 * @param pColumnName Nome da coluna
	 * @param pValueClass Classe para a qual será convertido o valor recebido
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String pColumnName, Class<?> pValueClass){
		return (T) DBSObject.toClass(getValue(pColumnName), pValueClass);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValueOriginal(String pColumnName){
		//Se existir registro corrente
		if (wDAO != null 
		 && (wDAO.getColumns().size() > 0
		  || wDAO.getCommandColumns().size() > 0)){
			return (T) wDAO.getValueOriginal(pColumnName);
		}else{
			return null;
		}
	}
	
	/**
	 * Retorna o valor da coluna convertida para a classe do tipo informado
	 * @param pColumnName Nome da coluna
	 * @param pValueClass Classe para a qual será convertido o valor recebido
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValueOriginal(String pColumnName, Class<?> pValueClass){
		return (T) DBSObject.toClass(getValueOriginal(pColumnName), pValueClass);
	}
	

	/**
	 * Retorna o valor da coluna na lista.
	 * Este valor deve ser utilizado quando o controle do registro atual estiver a cargo do datatable, como é o caso dos na exibição das colunas via DBSDataTableColumn
	 * @param pColumnName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getListValue(String pColumnName){
		if (wDAO != null){
			return (T) wDAO.getListValue(pColumnName);
		}else{
			return null;
		}		
	}
	
	/**
	 * Retorna o valor da coluna na lista.
	 * Este valor deve ser utilizado quando o controle do registro atual estiver a cargo do datatable, como é o caso dos na exibição das colunas via DBSDataTableColumn
	 * @param pColumnName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getListValue(String pColumnName, Class<?> pValueClass){
		return (T) DBSObject.toClass(getListValue(pColumnName), pValueClass);
	}

	/**
	 * Seta o valor da coluna informada diretamenteo no ResultDataModel.
	 * @param pColumnName Nome da coluna
	 * @param pValue Valor da coluna
	 * @return
	 */		
	public void setListValue(String pColumnName, Object pColumnValue){
		if (wDAO != null){
			wDAO.setListValue(pColumnName, pColumnValue);
		}		
	}
	
	/**
	 * Retorna se o registro atual é um novo registro.<br/>
	 * Os dados deste registro existem somente em memória, sendo necessário implementar a rotina para salva-los.
	 * @return
	 */
	public boolean getIsListNewRow(){
		if (wDAO != null){
			return wDAO.getIsNewRow();
		}else{
			return false;
		}
	}

	/**
	 * Deve-se sobreescrever este método para colunas que necessitem que formatação específica.<br/>
	 * Para recuperar os valores das colunas da linha atual deve-se utilizar o atributo <b>getListValue(pColumnName)<b/>.
	 * @param pColumnId Nome para ser utilizado para identificar qual a coluna será formatada.
	 * @return Texto formadado
	 */
	public String getListFormattedValue(String pColumnId) throws DBSIOException{return "pColumnId '" + pColumnId + "' desconhecida";}


	/**
	 * Configura os inputs da tela
	 * Metodo chamado pelo DBSCrudForm para poder configurar os atributos dos componentes na tela antes que sejam exibidos
	 * @param pComponentInput
	 */
	public void crudFormBeforeShowComponent(UIComponent pComponent){
		if (wDAO!=null){
			//Configura os campos do tipo input
			if (pComponent instanceof DBSUIInput){ 
				DBSColumn xColumn = pvGetDAOColumnFromInputValueExpression((DBSUIInput) pComponent);
				if (xColumn!=null){
					if (pComponent instanceof DBSUIInputText){
						//Configura o tamanho máximo de caracteres do input
						DBSUIInputText xInput = (DBSUIInputText) pComponent;
						xInput.setMaxLength(xColumn.getSize()); 
					}
				}
			}
		}
	}
	
//	/**
//	 * Configura os valores iniciais antes de uma inclusão a partir do valor do componente,
//	 * para diminuir a chance de considerar que houve alteração de valor, mesmo sem o usuário ter digitado algo 
//	 * durante a inclusão.
//	 * @param pComponent
//	 */
//	public void crudFormBeforeInsert(UIComponent pComponent){
//		if (wDAO!=null){
			//Configura os campos do tipo input
//			if (pComponent instanceof DBSUIInput){ 
//				DBSColumn xColumn = pvGetDAOColumnFromInputValueExpression((DBSUIInput) pComponent);
//				if (xColumn!=null){
					//Força a inicialização dos valores para que no "Insert" seja evitado a solicitação de confirmação do comando de "Ignorar" quando nada tenha sido digitado. 
//					if (getEditingMode() == EditingMode.INSERTING &&
//						getEditingStage() == EditingStage.NONE){
//						DBSUIInput xInput = (DBSUIInput) pComponent;
//						//Move o valor do componente para a coluna
//						if (wDialogEdit){
//							setValue(xColumn.getColumnName(), xInput.getValue());
//						}
//						//Força a indicação que não houve alteração de valores
//					}
//					setValueChanged(false);
//				}
//			}
//		}
//	}
	
	/**
	 * !!! ESTE ATRIBUTO NÃO DEVE SER SETADO MANUALMENTE !!!
	 * Seta a qual crudForm este crudBean esta vinculado.<br/>
	 * Este método é chamado automaticamente pelo DBSCrudForm.
	 * @param pCrudForm
	 */
	public void setCrudForm(DBSCrudForm pCrudForm){
		wCrudForm = pCrudForm;
	}
	
	/**
	 * Método padrão para validação dos campos que existentes dentro do crudForm do usuário.
	 * Método para validar o conteúdo do valor digitaro em função do DAO.
	 * Este método é chamado pelo DBSCrudForm.
	 * @param pComponent
	 */
	public void crudFormValidateComponent(FacesContext pContext, UIComponent pComponent, Object pValue){
		//Efetua a validação dos campos conforme estive definido no DAO, 
		//Se estiver em edição, houve DAO e não tiver sido pressionado o botão de cancela do CrudForm
		if (wEditingMode!=EditingMode.NONE){ 
			if (wDAO!=null){
				if (pValue!=null){
					String xSourceId = pContext.getExternalContext().getRequestParameterMap().get(DBSFaces.PARTIAL_SOURCE_PARAM);
					if (xSourceId !=null && 
						!xSourceId.endsWith(":cancel")){ //TODO verificar se existe uma forma melhor de identificar se foi um cancelamento
						if (pComponent instanceof DBSUIInputText){
							DBSUIInputText xInput = (DBSUIInputText) pComponent;
							DBSColumn 	xColumn = pvGetDAOColumnFromInputValueExpression(xInput);
							if (xColumn!=null){
								String xValue = pValue.toString();
								//Se for número, despreza a os caracteres não numéricos
								if (pValue instanceof Number){
									xValue = DBSNumber.getOnlyNumber(xValue);
								}
								if (xValue.length() > xColumn.getSize()){
									wMessageOverSize.setMessageTextParameters(xInput.getLabel(), xColumn.getSize());
									addMessage(wMessageOverSize);
									wValidateComponentHasError = true;
								}
							}
						} 
					}
				}
			}
		}
	}
	

	//=================================================================

	/**
	 * Retorna a situação da execução
	 * @return
	 */
	public EditingMode getEditingMode() {
		return wEditingMode;
	}

	/**
	 * Configura a situação da execução (Método PRIVADO)
	 * @param pRunningState
	 */
	private synchronized void setEditingMode(EditingMode pEditingMode) {
		if (wEditingMode != pEditingMode){
			wEditingMode = pEditingMode;
			//Qualquer troca no editingMode, desativa o editingstage
			setEditingStage(EditingStage.NONE);
			if (pEditingMode.equals(EditingMode.NONE)){
				pvFireEventAfterEdit();
				setValueChanged(false);
			}
		}
	}

	/**
	 * Retorna a situação da execução
	 * @return
	 */
	public EditingStage getEditingStage() {
		return wEditingStage;
	}

	/**
	 * Configura a situação da execução (Método PRIVADO)
	 * @param pRunningState
	 */
	private void setEditingStage(EditingStage pEditingStage) {
		if (wEditingStage != pEditingStage){
//Comentado em 17/05/2013 por não exibir as mensagens geradas no validateComponent, que ocorre antes de trocar o EditingStage
//			//Limpa as mensagens da fila, caso existam, antes de iniciar o comando para confirmar ou ignorar
//			if (wEditingStage.equals(EditingStage.NONE)){
//				clearMessages();
//			}
			//Limpa as mensagens da fila, caso existam
//			if (pEditingStage.equals(EditingStage.NONE)){
//				clearMessages();
//			}
			//Salva novo estado
			wEditingStage = pEditingStage;
		}
	}

	//=================================================================
	
	/**
	 * Texto que será exibido no cabeçalho do dialog
	 * @param pDialogCaption
	 */
	public void setDialogCaption(String pDialogCaption) {wDialogCaption = pDialogCaption;}

	public String getDialogCaption() {return wDialogCaption;}

	/**
	 * Retorna a situação da execução
	 * @return
	 */
	public Boolean getDialogOpened() {
		return wDialogOpened;
	}

	/**
	 * Configura a situação da execução (Método PRIVADO)
	 * @param pRunningState
	 */
	private synchronized void setDialogOpened(Boolean pDialogOpened) {
		if (wDialogEdit){
			if (wDialogOpened != pDialogOpened){
				wDialogOpened = pDialogOpened;
			}
		}
	}

	/**
	 * Indica se edição será efetuado dentro de um dialog.<br/>
	 * Caso positivo, deverá ser implementado o form utilizando o componente crudForm e adicioná-lo a view que onde está o crudTable.  
	 * @return
	 */
	
	public Boolean getDialogEdit() {return wDialogEdit;}

	/**
	 * Indica se edição será efetuado dentro de um dialog.<br/>
	 * Caso positivo, deverá ser implementado o form utilizando o componente crudForm e adicioná-lo a view que onde está o crudTable.  
	 */
	public void setDialogEdit(Boolean pDialogEdit) {wDialogEdit = pDialogEdit;}

	public String getDialogConfirmationEditMessage() {return wDialogConfirmationEditMessage;}
	public void setDialogConfirmationEditMessage(String pDialogConfirmationEditMessage) {wDialogConfirmationEditMessage = pDialogConfirmationEditMessage;}

	public String getDialogConfirmationInsertMessage() {return wDialogConfirmationInsertMessage;}
	public void setDialogConfirmationInsertMessage(String pDialogConfirmationInsertMessage) {wDialogConfirmationInsertMessage = pDialogConfirmationInsertMessage;}

	public String getDialogConfirmationDeleteMessage() {return wDialogConfirmationDeleteMessage;}
	public void setDialogConfirmationDeleteMessagem(String pDialogConfirmationDeleteMessagem) {wDialogConfirmationDeleteMessage = pDialogConfirmationDeleteMessagem;}

	public String getDialogConfirmationApproveMessage() {return wDialogConfirmationApproveMessage;}
	public void setDialogConfirmationApproveMessagem(String pDialogConfirmationApproveMessagem) {wDialogConfirmationApproveMessage = pDialogConfirmationApproveMessagem;}

	public String getDialogConfirmationReproveMessage() {return wDialogConfirmationReproveMessage;}
	public void setDialogConfirmationReproveMessagem(String pDialogConfirmationReproveMessagem) {wDialogConfirmationReproveMessage = pDialogConfirmationReproveMessagem;}

	public String getDialogIgnoreEditMessage() {return wDialogIgnoreEditMessage;}
	public void setDialogIgnoreEditMessage(String pDialogIgnoreEditMessage) {wDialogIgnoreEditMessage = pDialogIgnoreEditMessage;}

	public String getDialogIgnoreInsertMessage() {return wDialogIgnoreInsertMessage;}
	public void setDialogIgnoreInsertMessage(String pDialogIgnoreInsertMessage) {wDialogIgnoreInsertMessage = pDialogIgnoreInsertMessage;}

	public String getDialogIgnoreDeleteMessage() {return wDialogIgnoreDeleteMessage;}
	public void setDialogIgnoreDeleteMessage(String pDialogIgnoreDeleteMessage) {wDialogIgnoreDeleteMessage = pDialogIgnoreDeleteMessage;}

	//==============================================================================
	/**
	 * Use este atributo para popular páginas.<br/>
	 * Retorna os registros de forma que possam ser acessados nas páginas xhtml por EL.<br/>
	 * As colunas poderão ser acessada diretamente como atributos de uma classe, 
	 * onde os nomes dos atributos são os próprios nomes definidos as colunas do select.<br/>
	 * Exemplo de código xhtlm "#{list.campo}"
	 * @return Retorna registros
	 * @throws DBSIOException 
	 */
	public DBSResultDataModel getList() throws DBSIOException{
		//Força a criação do resultdatamodel se ainda não existir
		if (wDAO==null){
			pvRefreshList();
			if (wDAO==null
			 || wDAO.getResultDataModel() == null){
				return new DBSResultDataModel();
			}
			//Apaga as mensagem que tenham sido incluidas em função da refreshList quando ser tratar da inicialização do wDAO
			clearMessages();
		}
		return wDAO.getResultDataModel();
	}

	/**
	 * Retorna a quantidade de registros da pesquisa principal.
	 * @return
	 * @throws DBSIOException
	 */
	public Integer getRowCount() throws DBSIOException{
		if (getList() != null){
			return getList().getRowCount();
		}else{
			return 0;
		}
	}
	
	public Boolean getAllowDelete() throws DBSIOException {
		return wAllowDelete && pvApprovalUserAllowRegister() && getApprovalStage() == APPROVAL_STAGE.REGISTERED;
	}
	public void setAllowDelete(Boolean pAllowDelete) {wAllowDelete = pAllowDelete;}

	public Boolean getAllowUpdate() throws DBSIOException {
		return wAllowUpdate && pvApprovalUserAllowRegister() && getApprovalStage() == APPROVAL_STAGE.REGISTERED;
	}
	public void setAllowUpdate(Boolean pAllowUpdate) {wAllowUpdate = pAllowUpdate;}

	public Boolean getAllowInsert() {
		return wAllowInsert && pvApprovalUserAllowRegister();
	}
	public void setAllowInsert(Boolean pAllowInsert) {wAllowInsert = pAllowInsert;}
	
	public Boolean getAllowRefresh() {return wAllowRefresh;}
	public void setAllowRefresh(Boolean pAllowRefresh) {wAllowRefresh = pAllowRefresh;}

	public Boolean getMultipleSelection() {return wMultipleSelection;}
	public void setMultipleSelection(Boolean pMultipleSelection) {wMultipleSelection = pMultipleSelection;}

	public Boolean getAllowApproval() {return wAllowApproval;}
	public void setAllowApproval(Boolean pAllowApproval) {wAllowApproval = pAllowApproval;}

	public Boolean getAllowApprove(){return wAllowApprove;}
	public void getAllowApprove(Boolean pAllowApprove){wAllowApprove = pAllowApprove;}

	public Boolean getAllowReprove(){return wAllowReprove;}
	public void getAllowReprove(Boolean pAllowReprove){wAllowReprove = pAllowReprove;}
	
	/**
	 * Retorna a estágio de aprovação atual do registro corrente
	 * @return
	 * @throws DBSIOException 
	 */
	public APPROVAL_STAGE getApprovalStage() throws DBSIOException {
		return pvGetApprovalStage(true);
	}

	/**
	 * Retorna a estágio de aprovação atual do registro corrente
	 * @return
	 * @throws DBSIOException 
	 */
	public APPROVAL_STAGE getApprovalStageListValue() throws DBSIOException {
		return pvGetApprovalStage(false);
	}
	
	public void setApprovalStage(APPROVAL_STAGE pApprovalStage) {
//		DBSColumn xColumn = wDAO.getCommandColumn(getColumnNameApprovalStage());
//		if (xColumn!=null){
//			xColumn.setValue(pApprovalStage.getCode());
//		}
		setValue(getColumnNameApprovalStage(), pApprovalStage.getCode());
	}
	
	/**
	 * Retorna próximo estágio no processo de aprovação
	 * @return
	 * @throws DBSIOException 
	 */
	public APPROVAL_STAGE getApprovalUserNextStage() throws DBSIOException{
		return pvGetApprovalNextUserStage();
	}

	/**
	 * Retorna próximo estágio no processo de aprovação
	 * @return
	 * @throws DBSIOException 
	 */
	public APPROVAL_STAGE getApprovalUserNextStageListValue() throws DBSIOException{
		return pvGetApprovalNextUserStage();
	}

	/**
	 * Retorna o estágio de maior nível do usuário
	 * @return
	 */
	public APPROVAL_STAGE getApprovalUserMaxStage(){
		return DBSApproval.getMaxStage(getApprovalUserStages());
	}
	
	public Integer getApprovalUserStages() {return wApprovalUserStages;}
	public void setApprovalUserStages(Integer pApprovalUserStages) {wApprovalUserStages = pApprovalUserStages;}

	public Boolean getIsApprovalStageRegistered() throws DBSIOException{
		return getApprovalStage() == APPROVAL_STAGE.REGISTERED;
	}

	public Boolean getIsApprovalStageVerified() throws DBSIOException{
		return getApprovalStage() == APPROVAL_STAGE.VERIFIED;
	}

	public Boolean getIsApprovalStageConferred() throws DBSIOException{
		return getApprovalStage() == APPROVAL_STAGE.CONFERRED;
	}

	public Boolean getIsApprovalStageApproved() throws DBSIOException{
		return getApprovalStage() == APPROVAL_STAGE.APPROVED;
	}

	public String getColumnNameApprovalStage() {return wColumnNameApprovalStage;}
	public void setColumnNameApprovalStage(String pColumnNameApprovalStage) {wColumnNameApprovalStage = pColumnNameApprovalStage;}

	public String getColumnNameApprovalUserIdRegistered() {return wColumnNameApprovalUserIdRegistered;}
	public void setColumnNameApprovalUserIdRegistered(String pColumnNameApprovalUserIdRegistered) {wColumnNameApprovalUserIdRegistered = pColumnNameApprovalUserIdRegistered;}
	
	public String getColumnNameApprovalUserIdConferred() {return wColumnNameApprovalUserIdConferred;}
	public void setColumnNameApprovalUserIdConferred(String pColumnNameApprovalUserIdConferred) {wColumnNameApprovalUserIdConferred = pColumnNameApprovalUserIdConferred;}

	public String getColumnNameApprovalUserIdVerified() {return wColumnNameApprovalUserIdVerified;}
	public void setColumnNameApprovalUserIdVerified(String pColumnNameApprovalUserIdVerified) {wColumnNameApprovalUserIdVerified = pColumnNameApprovalUserIdVerified;}

	public String getColumnNameApprovalUserIdApproved() {return wColumnNameApprovalUserIdApproved;}
	public void setColumnNameApprovalUserIdApproved(String pColumnNameApprovalUserIdApproved) {wColumnNameApprovalUserIdApproved = pColumnNameApprovalUserIdApproved;}
	
	public String getColumnNameApprovalDateApproved() {return wColumnNameApprovalDateApproved;}
	public void setColumnNameApprovalDateApproved(String pColumnNameApprovalDateApproved) {wColumnNameApprovalDateApproved = pColumnNameApprovalDateApproved;}
	
	public String getColumnNameDateOnInsert() {return wColumnNameDateOnInsert;}
	public void setColumnNameDateOnInsert(String pColumnNameDateOnInsert) {wColumnNameDateOnInsert = pColumnNameDateOnInsert;}

	public String getColumnNameDateOnUpdate() {return wColumnNameDateOnUpdate;}
	public void setColumnNameDateOnUpdate(String pColumnNameDateOnUpdate) {wColumnNameDateOnUpdate = pColumnNameDateOnUpdate;}

	public String getColumnNameUserIdOnInsert() {return wColumnNameUserIdOnInsert;}
	public void setColumnNameUserIdOnInsert(String pColumnNameUserIdOnInsert) {wColumnNameUserIdOnInsert = pColumnNameUserIdOnInsert;}

	public String getColumnNameUserIdOnUpdate() {return wColumnNameUserIdOnUpdate;}
	public void setColumnNameUserIdOnUpdate(String pColumnNameUserIdOnUpdate) {wColumnNameUserIdOnUpdate = pColumnNameUserIdOnUpdate;}

	public Integer getUserId() {return wUserId;}
	public void setUserId(Integer pUserId) {wUserId = pUserId;}

	/**
	 * CrudBean Pai, caso este crud estar destro de outro crud.
	 * Neste caso, a conexão e as trasnsações são herdadas automaticamente do crud pai.
	 * O commit ou rollback só efetuado do primeiro crud pai.
	 * @param pCrudBean
	 */
	public void setParentCrudBean(DBSCrudBean pCrudBean) {
		wParentCrudBean = pCrudBean;
		if (!pCrudBean.getChildrenCrudBean().contains(this)){
			pCrudBean.getChildrenCrudBean().add(this);
		}
	}

	public DBSCrudBean getParentCrudBean() {
		return wParentCrudBean;
	}


	/**
	 * Lista de CrudBean dentro deste crud.
	 * O refreshList, beforeView e o afterView dos CrudBean fihos serão chamados automaticamente caso 
	 * o beforeView e afterview deste crudBean seja disparado.<br>
	 * Permitindo que os crud filhos atualizem seus dados em função da posição atual deste crud.
	 * @return
	 */
	public List<DBSCrudBean> getChildrenCrudBean() {
		return wChildrenCrudBean;
	}
	

	/**
	 * Nome do arquivo(xhtml) que será chamado após as ações de consulta e inclusão 
	 * @return
	 */
	public String getCrudFormFile() {return wCrudFormFile;}
	/**
	 * Nome do arquivo(xhtml) que será chamado após as ações de consulta e inclusão 
	 */
	public void setCrudFormFile(String pCrudFormFile) {wCrudFormFile = pCrudFormFile;}
	
	/**
	 * Seta se houve alteração de valores
	 * Metodo só deve ser chamada, caso o valor/coluna anterado não pertença aos valores/colunas controlados pelo wDAO 
	 * @param pChanged
	 */
	public void setValueChanged(Boolean pChanged){
		wValueChanged = pChanged;
	}
	public boolean getIsValueChanged(){
		return wValueChanged;
	}

	
	/**
	 * Informa se está em validação
	 * @return
	 */
	public Boolean getIsCommitting(){
		return (wEditingStage == EditingStage.COMMITTING);
	}
	
	/**
	 * Informa se está em modo UPDATE. Modo de edição
	 * @return
	 */
	public Boolean getIsUpdating(){
		return (wEditingMode == EditingMode.UPDATING);
	}
	
	/**
	 * Informa se está em modo UPDATE/INSERT/DELETE. Modo de edição
	 * @return
	 */
	public Boolean getIsEditing(){
		return (wEditingMode != EditingMode.NONE);
	}
	
	
	/**
	 * Informa se está em modo DELETING. Modo de exclusão
	 * @return
	 */
	public Boolean getIsDeleting(){
		return (wEditingMode == EditingMode.DELETING);
	}
	
	/**
	 * Informa se está em modo APPROVING(Aprovação).
	 * @return
	 */
	public Boolean getIsApproving(){
		return (wEditingMode == EditingMode.APPROVING);
	}
	
	/**
	 * Informa se está em modo REPROVING(Reprovação).
	 * @return
	 */
	public Boolean getIsReproving(){
		return (wEditingMode == EditingMode.REPROVING);
	}

//	public Boolean getIsApprovalStageApproved(Integer pApprovalStage){
//		return DBSApproval.isApproved(pApprovalStage);
//	}
//	public Boolean getIsApprovalStageConferred(Integer pApprovalStage){
//		return DBSApproval.isConferred(pApprovalStage);
//	}
//	public Boolean getIsApprovalStageVerified(Integer pApprovalStage){
//		return DBSApproval.isVerified(pApprovalStage);
//	}
//	public Boolean getIsApprovalStageRegistered(Integer pApprovalStage){
//		return DBSApproval.isRegistered(pApprovalStage);
//	}
	

	/**
	 * Informa se está em modo de APPROVING ou REPROVING
	 * @return
	 */
	public Boolean getIsApprovingOrReproving(){
		return (wEditingMode == EditingMode.APPROVING || wEditingMode == EditingMode.REPROVING);
	}

	/**
	 * Informa se está em cancelamento
	 * @return
	 */
	public Boolean getIsIgnoring(){
		return (wEditingStage == EditingStage.IGNORING);
	}

	/**
	 * Retorna se está no primeiro registro válido
	 * @return
	 */	
	public Boolean getIsFirst(){
		 if (wDAO != null){
			 return wDAO.getIsFist();
		 }else{
			 return true;
		 }
	}

	 /**
	 * Retorna se está no último registro válido
	 * @return
	 */
	public Boolean getIsLast(){
		 if (wDAO != null){
			return wDAO.getIsLast();
		 }else{
			 return true;
		 }
	}

	/**
	 * Informa se está em modo VIEWING, quando o(s) item(ns) selecionados estão sendo exibidos
	 * @return
	 */
	public Boolean getIsViewing(){
		return (wEditingMode ==  EditingMode.NONE);
	}

	/**
	 * Informa se está em modo INSERTING. Modo de inclusão
	 * @return
	 */
	public Boolean getIsInserting(){
		return (wEditingMode == EditingMode.INSERTING);
	}

	/**
	 * Informa se está em modo VIEWING, quando o(s) item(ns) selecionados estão sendo exibidos
	 * @return
	 */
	public Boolean getIsReadOnly(){
		if (wEditingMode == EditingMode.DELETING
		 || wEditingMode == EditingMode.NONE){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Informa se cadastro está fechado
	 * @return
	 */
	public Boolean getIsClosed(){
		return (!wDialogOpened);
	}

	/**
	 * Se tem algumm registro copiado
	 * @return
	 */
	public Boolean getIsCopied(){
		if (wCopiedRowIndex != -1){
			return true;
		}else{
			return false;
		}
	}

	// Methods ############################################################
	
	/**
	 * Retorna se item está selecionado
	 * @return
	 * @throws DBSIOException 
	 */
	public Boolean getSelected() throws DBSIOException {
		if (wSelectedRowsIndexes.contains(getList().getRowIndex())){
			return true;		
		}
		return false;
	}

	/**
	 * Configura se o item está selecionado
	 * @param pSelectOne
	 * @throws DBSIOException 
	 */
	public void setSelected(Boolean pSelectOne) throws DBSIOException {
//		wDAO.synchronize();
		
		pvSetSelected(pSelectOne);
		
		if (pvFireEventBeforeSelect()){
			pvFireEventAfterSelect();
		}else{
			//Desfaz seleção
			pvSetSelected(!pSelectOne);
		}
	}
	
	/**
	 * Retorna se existem alguma linha selecionada
	 * @return
	 */
	public boolean getHasSelected(){
		if (wSelectedRowsIndexes != null){
			if (wSelectedRowsIndexes.size()>0){
				return true;
			}
		}
		return false;
	}	
	/**
	 * Selectiona todas as linhas que exibidas
	 */
	public synchronized String selectAll() throws DBSIOException{
		//Só permite a seleção quando o dialog estiver fechado
		if (!wDialogOpened){
			pvSelectAll();
			if (pvFireEventBeforeSelect()){
				pvFireEventAfterSelect();
			}else{
				//Desfaz seleção
				pvSelectAll();
			}			
		}else{
			//exibir erro de procedimento
		}
		return DBSFaces.getCurrentView();
	}

	// Methods ############################################################
	
	/**
	 * Indica que deseja salvar a edição/inclusão/exclusão que está em andamento.
	 * Nesta etapa deverão ser efetuadas as validações atráves do evento validate.
	 * Após esta indicação, passará para a etapa de confirmação, onde efetivamente será efetuado o commit(beforeCommit) 
	 * @return
	 */
	public synchronized String confirmEditing() throws DBSIOException{
		//Se estive em processo de modificação
		if (wEditingMode!=EditingMode.NONE){
			//Se já não estiver em processo de validação ou cancelamento das modificações
			if (wEditingStage==EditingStage.NONE){
				//Se houve erro na validação automática dos componentes
				//Nao continua com o confirmEditing e reseta a wValidateComponentHasError pois será novamente verificada no método validateComponent
				if (wValidateComponentHasError){
					wValidateComponentHasError = false;
				}else{
					//Efetua a validação se for exclusão(não houve mudança de valores) ou houver mudança de valores e não for exclusão 
					if ((getIsValueChanged() && getIsDeleting() == false) 
					 || getIsDeleting()){
						//Chama eventos para validação dos dados
						if (pvFireEventValidate()){
							//Exibe tela de confirmação para efetuar o commit
							setEditingStage(EditingStage.COMMITTING);
						}else{
							//Ignora a exclusão em caso de erro de validação
							//Adicionado em 12/11/13 verificação para ignorar caso haja erro na validacao de assinatura
							if(getIsDeleting() 
							|| getIsApprovingOrReproving()){
								setEditingMode(EditingMode.NONE);
							}
						}
					}else{
						addMessage(wMessageNoChange);
					}
				}
			}else{
				//exibe mensagem de erro de procedimento
			}
		}else{
			//exibe mensagem de erro de procedimento
		}
		return DBSFaces.getCurrentView();
	}

	// Methods ############################################################
	
	/**
	 * Indica que deseja ignorar a edição/inclusão/exclusão que está em andamento.
	 * Após esta indicação, passará para a etapa de confirmação, onde efetivamente será ignorado o comando 
	 * @return
	 * @throws DBSIOException 
	 */
	public synchronized String ignoreEditing() throws DBSIOException{
		//Se estive em processo de modificação
		if (wEditingMode!=EditingMode.NONE){
			if (wEditingStage==EditingStage.NONE){
				//Chama eventos antes de ignorar
				setEditingStage(EditingStage.IGNORING);
				//Se não houve alteração de valores, sai sem confirmação
				if (!getIsValueChanged()){
					//Confirma o estágio/comando de 'ignorar'
					endEditing(true);
				}
			}else{
				//exibe mensagem de erro de procedimento
			}
		}else{
			//exibe mensagem de erro de procedimento
		}
		return DBSFaces.getCurrentView();
	}

	/**
		 * Confirma ou cancela o estágio atual. Podendo, por exemplo, confirmar ou cancelar o pedido de ignorar a edição. 
		 * Está é a última etapa do CRUD
		 * @param pConfirm true = Confirma, false = cancela
		 * @return
	 * @throws DBSIOException 
		 */
		public synchronized String endEditing(Boolean pConfirm) throws DBSIOException{
			try{
				if (pConfirm){
					//Verifica se está no estágio correto
					if (wEditingStage!=EditingStage.NONE){
						if (wEditingStage==EditingStage.COMMITTING){
							//Chama eventos
							if (pvFireEventBeforeCommit()){
								pvFireEventAfterCommit();
								pvRefreshList();
								pvFinalizeEditing(true);
							}else{
								pvFinalizeEditing(false);
							}
						}else if (wEditingStage==EditingStage.IGNORING){
							//Chama eventos
							if (pvFireEventBeforeIgnore()){
								pvFireEventAfterIgnore();
								pvFinalizeEditing(true);
							}else{
								pvFinalizeEditing(false);
							}
						}
					}else{
						//exibe mensagem de erro de procedimento
					}
				}else{
					//Retorna ao estágio sem edição, inclusão ou deleção
					setEditingStage(EditingStage.NONE);
					switch(wEditingMode){
						case UPDATING:
							//Restaura os valores anteriores a modificação
							break;
						case INSERTING:
							break;
						case DELETING: 
							setEditingMode(EditingMode.NONE);
							view();
							break;
						case APPROVING: 
							setEditingMode(EditingMode.NONE);
							close(false);
							break;
						case REPROVING: 
							setEditingMode(EditingMode.NONE);
							close(false);
							break;
						default:
							//Exibe mensagem de erro de procedimento
					}
				}
			}catch(Exception e){
				wLogger.error("Crud:" + getDialogCaption() + ":endEditing", e);
				setEditingMode(EditingMode.NONE);
				DBSIO.throwIOException(e);
			}
			return DBSFaces.getCurrentView();
		}

	// Methods ############################################################
	
	/**
	 * Efetua uma nova pesquisa e chama os eventos <b>beforeRefresh</b> e <b>afterRefresh</b>.
	 * @throws DBSIOException 
	 */
	public synchronized String searchList() throws DBSIOException{
		pvRefreshList();
		return DBSFaces.getCurrentView();
	}

	/**
	 * Dispara o evento <b>initialize</b> para obrigar que as lista, caso existam, sejam preenchidas novamente com dados atuais. 
	 * Efetua uma nova pesquisa e chama os eventos <b>beforeRefresh</b> e <b>afterRefresh</b>.<br/>
	 * @throws DBSIOException 
	 */
	public synchronized String refreshList() throws DBSIOException{
		pvFireEventInitialize();
		pvRefreshList();
		return DBSFaces.getCurrentView();
	}

	/**
	 * Copia os valores dos campos para a memória para poderem ser colados em outro registro
	 * @return
	 */
	public synchronized String copy() throws DBSIOException{
		wCopiedRowIndex = wDAO.getCurrentRowIndex(); 
		return DBSFaces.getCurrentView();
	}



	/**
	 * Seta os valores atuais com os valores do registro copiado
	 * @throws DBSIOException 
	 */
	public synchronized String paste() throws DBSIOException{
		//Seta o registro atual como sendo o registro copiado
		wDAO.paste(wCopiedRowIndex);
		setValueChanged(true);
		return DBSFaces.getCurrentView();
	}

	/**
	 * Exibe todos os itens selecionados 
	 */
	public synchronized String viewSelection() throws DBSIOException{
		//Limpa todas as mensagens que estiverem na fila
		clearMessages();

		if (wDAO.getCurrentRowIndex() != -1){
			//Só permite a seleção quando o dialog estiver fechado
			if (!wDialogOpened){
				if (wEditingStage==EditingStage.NONE){
					//Chama evento
					if (pvFireEventBeforeView()){
						setDialogOpened(true);
						pvFireEventAfterView();
					}
				}else{
					//exibir erro de procedimento
				}
			}else{
				//exibir erro de procedimento
			}
		}
		return DBSFaces.getCurrentView();
	}

	//==============================================================================================
	/**
	 * Exibe o item selecionado
	 */
	public synchronized String view() throws DBSIOException{
		//Limpa todas as mensagens que estiverem na fila
		clearMessages();
		
		if (wDAO.getCurrentRowIndex()!=-1){
			//Só permite a seleção quando o dialog estiver fechado
			if (wEditingStage==EditingStage.NONE){
				//Chama evento
				if (pvFireEventBeforeView()){
					setDialogOpened(true);
					pvFireEventAfterView();
				}
			}else{
				//exibir erro de procedimento
			}
		}else{
			//exibir erro de procedimento
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Informa com cadastro foi fechado 
	 */
	public synchronized String close() throws DBSIOException{
		return close(true);
	}

	/**
	 * Informa que cadastro foi fechado 
	 */
	public synchronized String close(Boolean pClearMessage) throws DBSIOException{
		//Só permite a seleção quando o dialog estiver fechado
		if (wDialogOpened){
			//Dispara evento
			if (pvFireEventBeforeClose()){
				setDialogOpened(false);
				if (pClearMessage) {
					clearMessages();
				}
			}
			//getLastInstance("a");
		}else{
			//exibe mensagem de erro de procedimento
		}
		return DBSFaces.getCurrentView();
	}


	public synchronized String insertSelected() throws DBSIOException{
		wInsertSelected = true;
		view();
		copy();
		insert();
		paste();
		wCopiedRowIndex = -1; //Reseta registro copiado para evitar a exibição do botão Paste.
		return DBSFaces.getCurrentView();
	}

	/**
	 * Entra no modo de inclusão 
	 * @throws DBSIOException 
	 */
	public synchronized String insert() throws DBSIOException{
		if (wAllowInsert 
		 || wInsertSelected){
			if (!wInsertSelected){
				clearMessages();
			}
			//Inclui linha em branco quando edição for diretamente no grid e edição já estiver habilitada(EditingMode.UPDATING)
			if (!wDialogEdit
			 && wEditingMode==EditingMode.UPDATING){
				pvInsertEmptyRow();
			}else{
				//Só permite a seleção do insert quando o dialog estiver fechado
				if (wEditingMode==EditingMode.NONE){
					try {
						if (pvFireEventBeforeEdit(EditingMode.INSERTING)){
							//Desmarca registros selecionados
							wSelectedRowsIndexes.clear(); 
							setEditingMode(EditingMode.INSERTING);
							
							pvMoveBeforeFistRow();
							
							if (pvFireEventBeforeInsert()){
								setDialogOpened(true);
							}
						}else{
							setValueChanged(false);
							//exibe mensagem de erro de procedimento
						}
					} catch (Exception e) {
						wLogger.error("Crud:" + getDialogCaption() + ":insert", e);
						setEditingMode(EditingMode.NONE);
						DBSIO.throwIOException(e);
					}
				}else{
					//exibe mensagem de erro de procedimento
				}
			}
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Entra no modo de edição 
	 */
	public synchronized String update() throws DBSIOException{
		if (wAllowUpdate){
			clearMessages();
			//Só permite a seleção quando o dialog em exibição
			if (wEditingMode==EditingMode.NONE){
				try {
					if (pvFireEventBeforeEdit(EditingMode.UPDATING)){
						setEditingMode(EditingMode.UPDATING);
						//Insere inicialmente um linha em branco quando edição for diretamente no grid.
						pvInsertEmptyRow();
					}else{
						setValueChanged(false);
						//exibe mensagem de erro de procedimento
					}
				} catch (Exception e) {
					wLogger.error("Crud:" + getDialogCaption() + ":update", e);
					setEditingMode(EditingMode.NONE);
					DBSIO.throwIOException(e);
				}
			}else{
				//exibe mensagem de erro de procedimento
			}
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Entra no modo de exclusão 
	 * @throws DBSIOException 
	 */
	public synchronized String delete() throws DBSIOException{
		if (wAllowDelete){
			clearMessages();
			//Só permite a exclusão quando o dialog em exibição
			if (wEditingMode==EditingMode.NONE){
				try {
					if (pvFireEventBeforeEdit(EditingMode.DELETING)){
						//Muda para o modo de exclusão
						setEditingMode(EditingMode.DELETING);
						//Exibe confirmação da exclusão.
						confirmEditing();
						//setEditingStage(EditingStage.COMMITTING);
					}else{
						setValueChanged(false);
						//setEditingStage(EditingStage.COMMITTING);
					}
				} catch (Exception e) {
					wLogger.error("Crud:" + getDialogCaption() + ":delete", e);
					setEditingMode(EditingMode.NONE);
					DBSIO.throwIOException(e);
				}
			}else{
				//exibe mensagem de erro de procedimento
			}
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Entra no modo de inclusão 
	 * @throws DBSIOException 
	 */
	public synchronized String approve() throws DBSIOException{
		if (wAllowApproval 
		 && wAllowApprove){
			if (wUserId== null){
				//wLogger.error("[DBSCrudBean]UserId - Não informado");
				addMessage("UserId", MESSAGE_TYPE.ERROR,"DBSCrudBean: UserId - Não informado!");
				return DBSFaces.getCurrentView();
			}
			//Só permite a seleção quando o dialog em exibição
			if (wEditingMode==EditingMode.NONE){
				try {
					if (pvFireEventBeforeEdit(EditingMode.APPROVING)){
						setEditingMode(EditingMode.APPROVING);
						setValueChanged(true);
						confirmEditing();
					}else{
						setValueChanged(false);
						//exibe mensagem de erro de procedimento
					}
				} catch (Exception e) {
					wLogger.error("Crud:" + getDialogCaption() + ":approve", e);
					setEditingMode(EditingMode.NONE);
					DBSIO.throwIOException(e);
				}
			}else{
				//exibe mensagem de erro de procedimento
			}
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Entra no modo de inclusão 
	 * @throws DBSIOException 
	 */
	public synchronized String reprove() throws DBSIOException{
		//Só permite a seleção quando o dialog em exibição
		if (wAllowApproval && wAllowApprove){
			if (wUserId== null){
				//wLogger.error("[DBSCrudBean]UserId - Não informado");
				addMessage("UserId", MESSAGE_TYPE.ERROR,"DBSCrudBean: UserId - Não informado!");
				return DBSFaces.getCurrentView();
			}
			if (wEditingMode==EditingMode.NONE){
				try {
					if (pvFireEventBeforeEdit(EditingMode.REPROVING)){
						setEditingMode(EditingMode.REPROVING);
						setValueChanged(true);
						confirmEditing();
					}else{
						setValueChanged(false);
						//exibe mensagem de erro de procedimento
					}
				} catch (Exception e) {
					wLogger.error("Crud:" + getDialogCaption() + ":reprove", e);
					setEditingMode(EditingMode.NONE);
					DBSIO.throwIOException(e);
				}
			}else{
				//exibe mensagem de erro de procedimento
			}
		}
		return DBSFaces.getCurrentView();
	}

	public synchronized String moveFirst() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.moveFirstRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}
	public synchronized String movePrevious() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.movePreviousRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}
	
	public synchronized String moveNext() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.moveNextRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}
	
	public synchronized String moveLast() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.moveLastRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}


	@Override
	protected void warningMessageValidated(String pMessageKey, Boolean pIsValidated) throws DBSIOException{
		//Se mensagem de warning foi validade..
		if (pIsValidated){
			//Chama novamente a confirmação da edição.
			confirmEditing();
		}else{
			if (getEditingMode().equals(EditingMode.DELETING)){
				//Ignora exclusão
				ignoreEditing();
			}
		}
	}	
	
	@Override
	protected boolean openConnection() {
		if (wParentCrudBean == null){
			//Abre a conexão se não estiver edição e não estiver processando as chamadas dos eventos, 
			//pois em ambos os casos a conexão já está aberta
			if (!getIsEditing() && !wBrodcastingEvent){
				super.openConnection();
				//O DAO deve utilizar sempre a mesmo conexão ativa deste CrudBean
				if (wDAO !=null){
					wDAO.setConnection(wConnection);
				}
				//Configura os crudbean filhos que possam existir
				pvBroadcastConnection(this);
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void closeConnection() {
		if (wParentCrudBean == null){
			//Fecha a conexão se não estiver edição e não estiver processando as chamadas dos eventos
			//pois em ambos os casos a conexão não pode ser fechada até que seja finalizados a edição e as chamadas dos eventos
			if (!getIsEditing() && !wBrodcastingEvent){
				super.closeConnection();
				//Configura os crudbean filhos que possam existir
				pvBroadcastConnection(this);
			}
		}
	}
	
	// Abstracted
	// PROTECTED ============================================================================
	/* ESTES MÉTODOS FORAM DEFINIDOS DIRETAMENTE NO CÓDIGO DESTA CLASS, AO INVÉS 
	* DE IMPLEMENTAR IDBSCrudEventsListeners, PARA QUE FIQUEM COMO PROTECTED. 
	* EVITANDO QUE ESTES MÉTODOS SEJAM CHAMADOS EXTERNAMENTE POR OUTRAS CLASSES.
	* O QUE SERIA UM PROBLEMA DE SEGURANÇA JÁ QUE ESTA CLASS(DBSCRUD) SERÁ UTILIZADA COMO UM MANAGEDBEAN.
	*/
	
	
	/**
	 * Chamado quando a class é instanciada.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected abstract void initialize(DBSCrudBeanEvent pEvent) throws DBSIOException;


	/**
	 * Chamado antes da class ser finalizada.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void finalize(DBSCrudBeanEvent pEvent){};
	
	
	/**
	 * Chamado antes do crudform ser fechado.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void beforeClose(DBSCrudBeanEvent pEvent) throws DBSIOException {} ;

	/**
	 * Chamado antes de limpar os dados existentes e fazer uma nova pesquisa.<br/>
	 * A pesquisa principal dos dados que utiliza o wDAO, deverá ser efetuada neste evento.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	protected void beforeRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Chamado após efetuada uma nova pesquisa.
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void afterRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException{};

	
	/**
	 * Chamado após o click do usuário no insert/delete/update e antes de iniciar o respectivo insert/delete/update
	 * Neste evento pode-se ignorar o click do usuário, evitado que continue o comando de insert/update/delete.
	 * Para isso, informe pEvent.setOk(false).<br> 
	 * Neste evento o editingMode do Crud ainda não foi configurado, portanto para saber qual a atividade(Inser/update/delete) foi selecionada
	 * deve-se consultar o atributo editingMode do Evento(ex:if (pEvent.getEditingMode() == EditingMode.INSERTING){}).<br/>
	 * Pode-se forçar a indicação que houve alteração de dados logo na iniciação da edição, mesmo que ainda não tenha sido efetuada qualquer alteração pelo usuário, setando a propriedade setValueChanged para true.<br/>
	 * Para configurar os valores default dos campos no caso de uma inclusão, utilize o evento <b>beforeInsert</b>.
	 * Conexão com o banco encontra-se aberta.<br/> 
	 * @param pEvent Informações do evento
	 */
	protected void beforeEdit(DBSCrudBeanEvent pEvent) throws DBSIOException {};
	
	/**
	 * Chamado logo após a finalização da edição(insert/delete/update), independentemente da edição ter sido confirmada ou ignorada.<br/>
	 * Conexão com o banco encontra-se aberta, porém será fechado logo após a finalização deste evento.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void afterEdit(DBSCrudBeanEvent pEvent) throws DBSIOException {};
	
	/**
	 * Chamado antes de iniciar um insert.<br/>
	 * Neste evento pode-se configura os valores default dos campos.<br/>
	 * Para ignorar a inclusão, deve-se setar setOk(False).<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void beforeInsert(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Chamado antes de exibir os dados em uma edição ou exclusão.<br/>
	 * Durante este evento, para saber o modo de ediçãos deve-se consultar o <b>pEvent.getEditingMode()</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void beforeView(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Chamado depois depois de exibir os dados.<br/>
	 * Procure utilizar o evento <b>beforeView</b><br/>
	 * Neste evento pode-se, também, configurar os valores default dos campos no caso de uma inclusão.
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void afterView(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Chamado depois da validação e mensagens de confirmação.
	 * Ocorre após a gravação dos dados(sem commit) e antes de voltar ao modo sem edição(EditingMode.NONE).<br/>
	 * A transação(Begintrans/Commit/Rollback) são controladas automaticamete.<br/>
	 * Será efetuado o <b>rollback</b> em caso de <b>exception</b> ou se o atributo <b>pEvent.setOk</b> do evento for <b>false</b>.<br>
	 * Neste evento, pode-se forçar o valor de alguma coluna utilizando o <b>wDAO</b>, devendo-se, contudo, chamar o <b>super.beforeCommit()</b> ao final, 
	 * caso o CRUD seja a tabela definida no próprio wDAO.<br> 
	 * Caso o CRUD não seja da tabela definida no wDAO, este método deverá ser sobreescrito
	 * para que seja implementado o CRUD específico e NÃO deverá ser chamado o <b>super.beforeCommit()</b>.<br>
	 * No caso do método ser sobreescrito, é necessário setar o atributo <b>pEvent.setCommittedRowCount</b> com
	 * a quantidade de registros afetados.<br>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	protected void beforeCommit(DBSCrudBeanEvent pEvent) throws DBSIOException {
		//Copia dos valores pois podem ter sido alterados durante o beforecommit
		
		//Aprovação/Reprovação
		if (getIsApprovingOrReproving()){ 
			pvBeforeCommitSetAutomaticColumnsValues(pEvent); 
			if (pEvent.isOk()){
				pEvent.setCommittedRowCount(wDAO.executeUpdate());
			}else{
				return;
			}
		//Insert/Update/Delete
		}else{ 
			pvBeforeCommitSetAutomaticColumnsValues(pEvent);
			if (pEvent.isOk()){
				//Insert
				if(getIsInserting()){
					if (wDAO.isAutoIncrementPK()){
						wDAO.setValue(wDAO.getPK(), null);
					}
					pEvent.setCommittedRowCount(wDAO.executeInsert());
				//Update
				}else if (getIsUpdating()){
					//Incluir registro se for edição diretamente do grid e for novo registro
					if (!wDialogEdit
					 && wDAO.getIsNewRow()){
						pEvent.setCommittedRowCount(wDAO.executeInsert());
					}else{
						pEvent.setCommittedRowCount(wDAO.executeUpdate());
					}
				//Delete
				}else if(getIsDeleting()){
					pEvent.setCommittedRowCount(wDAO.executeDelete());
				}
			}
		}
	}
	
	
	// PRIVATE ============================================================================
	

	/**
	 * Inicializa a conversação
	 */
	private void pvConversationBegin(){
		if (wConversation.isTransient()){
			wConversation.begin();
			wConversation.setTimeout(wConversationTimeout);
		}
	}
	
	/**
	 * Atualiza dados da lista
	 * @throws DBSIOException
	 */
	private void pvRefreshList() throws DBSIOException{
		ignoreEditing(); 
		//Dispara evento para atualizar os dados
		if (pvFireEventBeforeRefresh()){
			//Apaga itens selecionados, se houver.
			wSelectedRowsIndexes.clear();
			pvFireEventAfterRefresh();
		}
	}

	
	/**
	 * Finaliza a edição, resetando e editing mode.
	 * @param pOk
	 * @throws DBSIOException
	 */
	private void pvFinalizeEditing(Boolean pOk) throws DBSIOException{
		switch(wEditingMode){
			case UPDATING:
				if (wEditingStage==EditingStage.IGNORING){
					setEditingMode(EditingMode.NONE); 
					pvRestoreValuesOriginal();

					pvRefreshList();
				}else{
					if (pOk){
						setEditingMode(EditingMode.NONE);
					}else{
						//RESTAURAR OS VALORES
//						wEditingStage==EditingStage.IGNORING
						setEditingStage(EditingStage.NONE);
//						pvRestoreValuesOriginal();
					}
				}
				break;
			case INSERTING:
				if (pOk){
					//Fecha crudform se for para ignorar a inclusão ou for uma inclusão a partir da seleção de um item do crudTable
					if (wEditingStage==EditingStage.IGNORING
					|| wInsertSelected){
						setEditingMode(EditingMode.NONE);
//						setDialogStage(DialogStage.CLOSED);
						close();
					}else{
						setEditingMode(EditingMode.NONE); 
						//Reinicia no processe de inclusão
						insert();
					}
				}else{
					//Continua no mesmo processo de inclusão até que o erro seja corrigido ou a inclusão ignorada
					setEditingStage(EditingStage.NONE);
				}
				break;
			case DELETING:
				if (wEditingStage==EditingStage.IGNORING){
					setEditingMode(EditingMode.NONE);
				}else{
					wCopiedRowIndex = -1; //Reseta registro copiado para evitar a exibição do botão Paste.
					setEditingMode(EditingMode.NONE);
					if (pOk){
						setDialogOpened(false);
					}
				}
				break;
			case APPROVING:
				setEditingMode(EditingMode.NONE); 
				setEditingStage(EditingStage.NONE);
				close(false);
				break;
			case REPROVING:
				setEditingMode(EditingMode.NONE); 
				setEditingStage(EditingStage.NONE);
				close(false);
				break;
			default:
				setEditingMode(EditingMode.NONE);
				//Exibe mensagem de erro de procedimento
		}		
	}


	/**
	 * Restaura os valores antes de qualquer modificação
	 */
	private void pvRestoreValuesOriginal(){
		wDAO.restoreValuesOriginal();
	}
	
	
	/**
	 * Move para o registro anterior ao primeiro registro.
	 * @throws DBSIOException
	 */
	private void pvMoveBeforeFistRow() throws DBSIOException{
		wDAO.moveBeforeFirstRow();
	}
		

	/**
	 * Inserir linha em branco quando inclusão estiver habilidata<b>(allowInsert=true)</b> 
	 * e for edição diretamente no grid<b>(dialogEdit=false)</b>
	 * e estiver no modo de edição<b>(UPDATING)</b>.
	 * @throws DBSIOException 
	 */
	private void pvInsertEmptyRow() throws DBSIOException{
		if (wDialogEdit
		 || wEditingMode != EditingMode.UPDATING
		 || !wAllowInsert){
			return;
		}
		wDAO.insertEmptyRow();
		
		pvFireEventBeforeInsert();
	}

	/**
	 * Retorna a respectiva coluna do DAO a partir da propriedade value que foi utilizada no componente.<BR/>
	 * É necessário que campos fl's tenham o mesmo nome da coluna na tabela.
	 * 
	 * @param pInput
	 * @return
	 */
	private DBSColumn pvGetDAOColumnFromInputValueExpression(DBSUIInput pInput){
		String xColumnName = DBSFaces.getAttibuteNameFromInputValueExpression(pInput).toLowerCase();
		if (xColumnName!=null &&
			!xColumnName.equals("")){
			//Retira do os prefixos controlados pelo sistema 
			if (xColumnName.startsWith(DBSSDK.UI.PREFIX.CRUD)){
				xColumnName = DBSString.getSubString(xColumnName, DBSSDK.UI.PREFIX.CRUD.length() + 1, xColumnName.length());
			}else if (xColumnName.startsWith(DBSSDK.UI.PREFIX.AUX)){
				xColumnName = DBSString.getSubString(xColumnName, DBSSDK.UI.PREFIX.AUX.length() + 1, xColumnName.length());
			}
			//Configura o tamanho máximo de caracteres do componente na tela
			if (wDAO.containsColumn(xColumnName)){
				return wDAO.getColumn(xColumnName);
			}
		}
		return null;
	}

	/**
	 * Configura o crudbean filhos com a mesma conexão do pai
	 * @param pBean
	 */
	private void pvBroadcastConnection(DBSCrudBean pBean){
		for (DBSCrudBean xChildBean:pBean.getChildrenCrudBean()){
			//Força o fechamento da conexão para evitar que fique uma conexão aberta perdida na memória
			Connection xCn = xChildBean.getConnection();
	
			DBSIO.closeConnection(xCn);
	
			//Força para que a conexão do crud filho seja a mesma do crud 'pai'
			xChildBean.setConnection(pBean.getConnection());
			//Procura pelos netos
			pvBroadcastConnection(xChildBean);
		}
	}

	/**
	 * Salva indice do linha selacionada
	 * @param pSelectOne
	 * @throws DBSIOException 
	 */
	private void pvSetSelected(Boolean pSelectOne) throws DBSIOException{
		Integer xRowIndex = getList().getRowIndex();
		if (pSelectOne){
			//Força indicação que houve alteração de registro quando edição é efetuada diretamente no grid e há registro selecionado.
			//A seleção que indica que a linha foi alterada é efetuado via JS.
			if (!wDialogEdit){
				setValueChanged(true);
			}
			if (!wSelectedRowsIndexes.contains(xRowIndex)){
				wSelectedRowsIndexes.add(xRowIndex);
			}
		}else{
			if (wSelectedRowsIndexes.contains(xRowIndex)){
				wSelectedRowsIndexes.remove(xRowIndex);
			}
		}
	}

	/**
	 * Seleciona todas as linhas não selecionadas e desseleciona todas as linha selecionadas
	 * @throws DBSIOException 
	 */
	private void pvSelectAll() throws DBSIOException{
		for (Integer xX = 0; xX < getList().getRowCount(); xX++){
			//Recupera se está ou não selecionado e inverte
			if (wSelectedRowsIndexes.contains(xX)){
				wSelectedRowsIndexes.remove(xX);
			}else{
				wSelectedRowsIndexes.add(xX);
			}
		}
	}
	

	// PRIVATE ============================================================================
	
	/**
	 * Seta o valor da coluna no DAO
	 * @param pColumnName
	 * @param pColumnValue
	 */

	private <T> void pvSetValueDAO(String pColumnName, T pColumnValue){
		//Utiliza ListValue para controlar os valores de todas as linhas
		//Verifica se há alguma coluna corrente antes de setar o valor
		if (wDAO != null 
		 && (wDAO.getColumns().size() > 0
		  || wDAO.getCommandColumns().size() > 0)){
			T xOldValue =  pvGetValue(pColumnName);
			if (pColumnValue != null){
				//Converte o valor antigo para o mesmo tipo do valor recebido para garantir a verificação correta se houve alteração de valores
				xOldValue = DBSObject.toClass(xOldValue, pColumnValue.getClass()); 
			}
			//Se valor armazenado(anterior) não for nulo e houve alteração de valores...
			if(!DBSObject.getNotNull(xOldValue,"").toString().equals(DBSObject.getNotNull(pColumnValue,"").toString())){
				if (getEditingMode() == EditingMode.INSERTING
				 && getDialogOpened()){
					//TODO Exibição mantida somente para fins de teste, devendo ser excluida o quanto antes.
					wLogger.info("ALTERADO:" + pColumnName + "[" + DBSObject.getNotNull(xOldValue,"") + "] para [" + DBSObject.getNotNull(pColumnValue,"") + "]");
				}
				//marca como valor alterado
				setValueChanged(true); 
				wDAO.setValue(pColumnName, pColumnValue);
			}
		}
	}
	
	/**
	 * Retorna valor da coluna a partir do DAO
	 * @param pColumnName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T pvGetValue(String pColumnName){
		//Se existir registro corrente
		if (wDAO != null 
		 && (wDAO.getColumns().size() > 0
		  || wDAO.getCommandColumns().size() > 0)){
			return (T) wDAO.getValue(pColumnName);
		}else{
			return null;
		}
	}


	private APPROVAL_STAGE pvGetApprovalStage(boolean pFromValue){
		//Retorna estágio de registro, caso aprovação esteja desabilidata ou não tenha sido informado o nome da coluna para garva o estágfio
		if (!getAllowApproval()){
			return APPROVAL_STAGE.REGISTERED;
		}
		APPROVAL_STAGE xStage;
		if (pFromValue){
			xStage = APPROVAL_STAGE.get(getValue(getColumnNameApprovalStage())); 
		}else{
			xStage = APPROVAL_STAGE.get(getListValue(getColumnNameApprovalStage())); 
		}
		//Força que seja 'REGISTRED' caso o valor retornado seja nulo
		if (xStage==null){
			xStage = APPROVAL_STAGE.REGISTERED;
		}
		return xStage;
	}

	/**
	 * Verifica se há controle de assinatura e se usuário tem poder para registrar.
	 * Se não tiver, bloqueia inclusão/alteração/deleção
	 * @return
	 */
	private boolean pvApprovalUserAllowRegister(){
		if (getAllowApproval() &&
			!DBSApproval.isRegistered(getApprovalUserStages())){
			return false;
		}
		return true;
	}

	/**
	 * Retorna os próximos estágios que o usuário poderá efetuar, 
	 * considerando o estágio do registro atual e os estágios que o usuário tem poder para efetuar.
	 * @return O somatório dos estágios
	 * @throws DBSIOException 
	 */
	private Integer pvGetApprovalNextUserStages() throws DBSIOException{
		return DBSApproval.getNextUserStages(getApprovalStage(), getApprovalUserStages());
	}

	/**
	 * Retorna o próximo estágio que deverá estar o registro atual, 
	 * considerando os estágios que o usuário tem poder para efetuar.
	 * @return
	 * @throws DBSIOException 
	 */
	private APPROVAL_STAGE pvGetApprovalNextUserStage() throws DBSIOException{
		return DBSApproval.getMaxStage(pvGetApprovalNextUserStages());
	}
	

	/**
	 * Configura os valores das colunas que são preenchidas automaticamente pelo DBSCrudBean
	 * @param pEvent
	 * @throws DBSIOException 
	 */
	private void pvBeforeCommitSetAutomaticColumnsValues(DBSCrudBeanEvent pEvent) throws DBSIOException{
		DBSColumn 	xColumn = null;
		//Configura os valores das assinaturas se assinatura estive habilitada. 
		if (getAllowApproval()){
			pvBeforeCommitSetAutomaticColumnsValuesApproval(pEvent);
		}
		//Insert
		if(getIsInserting()){
			//Salva usuário que incluiu
			xColumn = wDAO.getCommandColumn(getColumnNameUserIdOnInsert());
			if (xColumn!=null){
				xColumn.setValue(getUserId());
			}
			//Salva data e hora da inclusão
			xColumn = wDAO.getCommandColumn(getColumnNameDateOnInsert());
			if (xColumn!=null){
				xColumn.setValue(DBSDate.getNowDateTime());
			}
		//Update
		}else if (getIsUpdating()){
			//Salva usuário que alterou
			xColumn = wDAO.getCommandColumn(getColumnNameUserIdOnUpdate());
			if (xColumn!=null){
				xColumn.setValue(getUserId());
			}
			//Salva data e hora da alteração
			xColumn = wDAO.getCommandColumn(getColumnNameDateOnUpdate());
			if (xColumn!=null){
				xColumn.setValue(DBSDate.getNowDateTime());
			}
		//Delete
		}else if(getIsDeleting()){
		}	
	}
	
	/**
	 * Configura os valores dos campos da assinatura eletrônica
	 * @throws DBSIOException 
	 */
	private void pvBeforeCommitSetAutomaticColumnsValuesApproval(DBSCrudBeanEvent pEvent) throws DBSIOException{
		DBSColumn 		xColumn = null;
		APPROVAL_STAGE 	xApprovalNextStage = null;
		Integer 		xUserId = null;
		Timestamp	 	xApprovalDate = null;
		Integer 		xApprovalNextUserStages = null;
		xUserId =  getUserId();
		xApprovalDate = DBSDate.getNowTimestamp();
		
		//Aprovação
		if (getIsApproving()){
			xApprovalNextUserStages = pvGetApprovalNextUserStages();
			xApprovalNextStage = pvGetApprovalNextUserStage();
		//Reprovação
		}else if (getIsReproving()){
			//Anula as assinaturas de conferencia,verificação e aprovação
			xApprovalNextUserStages = DBSApproval.getApprovalStage(false, true,  true,  true);
			xApprovalNextStage = APPROVAL_STAGE.REGISTERED;
			xUserId = null;
			xApprovalDate = null;
		//Inclusão ou Alteração
		}else if(getIsInserting() ||
				 getIsUpdating()){
			//Força o usuário atual como sendo o responsável pelo registro
			xApprovalNextStage = APPROVAL_STAGE.REGISTERED;
			xApprovalNextUserStages = APPROVAL_STAGE.REGISTERED.getCode();
		}
		
		//Salva o usuário que registrou
		if (xApprovalNextStage==APPROVAL_STAGE.REGISTERED){ 
			//Salva usuário que registrou
			xColumn = wDAO.getCommandColumn(getColumnNameApprovalUserIdRegistered());
			if (xColumn!=null){xColumn.setValue(getUserId());}
		}else{
			//Se usuário tem poder para conferir...
			if (DBSApproval.isConferred(xApprovalNextUserStages)){
				//Salva ou apaga usuário que conferiu
				xColumn = wDAO.getCommandColumn(getColumnNameApprovalUserIdConferred());
				if (xColumn!=null){xColumn.setValue(xUserId);}
			}
			//Se usuário tem poder para verificar...
			if (DBSApproval.isVerified(xApprovalNextUserStages)){
				//Salva ou apaga usuário que verificou
				xColumn = wDAO.getCommandColumn(getColumnNameApprovalUserIdVerified());
				if (xColumn!=null){xColumn.setValue(xUserId);}
			}
			//Se usuário tem poder para aprovar...
			if (DBSApproval.isApproved(xApprovalNextUserStages)){
				//Ignora a aprovação se usuário for o mesmo que registrou
				xColumn = wDAO.getCommandColumn(getColumnNameApprovalUserIdRegistered());
				if (xColumn!=null){
					if (DBSNumber.toInteger(xColumn.getValue()).equals(xUserId)){
						addMessage(wMessaggeApprovalSameUserError);
						pEvent.setOk(false);
						return;
					}
				}
				//Salva ou apaga usuário que aprovou
				xColumn = wDAO.getCommandColumn(getColumnNameApprovalUserIdApproved());
				if (xColumn!=null){xColumn.setValue(xUserId);}
				//Salva ou apaga data e hora da aprovação
				xColumn = wDAO.getCommandColumn(getColumnNameApprovalDateApproved());
				if (xColumn!=null){xColumn.setValue(xApprovalDate);}
			}
		}
		
		//Salva estágio da aprovação
		setApprovalStage(xApprovalNextStage);
	}

	/**
	 * Configura os valores iniciais antes de uma inclusão a partir do valor do componente,
	 * para diminuir a chance de considerar que houve alteração de valor, mesmo sem o usuário 
	 * ter digitado algo durante a inclusão.
	 * @param pComponent
	 */
	private void pvBeforeInsertResetValues(UIComponent pComponent){
		if (pComponent==null){return;}
		Iterator<UIComponent> xI = pComponent.getFacetsAndChildren();
		if (wDAO!=null){
			while (xI.hasNext()){
				UIComponent xC = xI.next();
				//Configura os campos do tipo input
				if (xC instanceof DBSUIInput){ 
					DBSColumn xColumn = pvGetDAOColumnFromInputValueExpression((DBSUIInput) xC);
					if (xColumn!=null){
						//Força a inicialização dos valores para que no "Insert" seja evitado a solicitação de confirmação do comando de "Ignorar" quando nada tenha sido digitado. 
						if (getEditingMode() == EditingMode.INSERTING &&
							getEditingStage() == EditingStage.NONE){
							DBSUIInput xInput = (DBSUIInput) xC;
							//Move o valor do componente para a coluna
							if (wDialogEdit){
								setValue(xColumn.getColumnName(), xInput.getValue());
							}
							//Força a indicação que não houve alteração de valores
						}
						setValueChanged(false);
					}
				}else{
					//Chamada recursiva
					pvBeforeInsertResetValues(xC);
				}
			}
		}
	}

	/**
	 * Salva conteúdo da linha atual para posteriormente, após o refresh, procurar pela linha 
	 * que contenha os mesmos dados para selecionar como sendo o registro atual
	 */
	private void pvCurrentRowSave(){
		wSavedCurrentColumns = null;
		if (wDAO != null){
			wSavedCurrentColumns = wDAO.getCommandColumns();
		}
	}

	/**
	 * Posiciona no mesmo registro que foi editado.<br/>
	 * O reposicionamento é efetuado pesquisando-se dentro dos registros existentes, 
	 * aquele que contém os dados salvos anteriormente no <b>pvCurrentRowSave</b>.
	 * @throws DBSIOException
	 */
	private void pvCurrentRowRestore() throws DBSIOException{
		boolean xOk;
		Integer	xRowIndex;
		Object xSavedValue = null;
		Object xCurrentValue = null;
		BigDecimal xSavedNumberValue = null;
		BigDecimal xCurrentNumberValue = null;
		boolean xEqual;
		if (wDAO != null
		 && wSavedCurrentColumns !=null
		 && wDAO.getResultDataModel() != null){
			//Recupera todas as linhas
			Iterator<SortedMap<String, Object>> xIR = wDAO.getResultDataModel().iterator(); 
			xRowIndex = -1;
			//Loop por todas as linhas para procurar pela que é igual a linha salva
			while (xIR.hasNext()){ 
				xOk = true;
				xRowIndex++; 
				//Recupera todas as colunas da linha
				SortedMap<String, Object> xColumns = xIR.next();
				//Loop por todas as colunas da linha
				for (Entry<String, Object> xC:xColumns.entrySet()){
					Iterator<DBSColumn> xIS = wSavedCurrentColumns.iterator();
					//Loop por todas as colunas salvas para pesquisar o conteúdo
					//Procura pelo coluna que possua o mesmo nome
					while (xIS.hasNext()){
						DBSColumn xSC = xIS.next();
						//Verifica se a coluna com o mesmo nome, possui o mesmo conteúdo.
						if (xSC.getColumnName().equalsIgnoreCase(xC.getKey())){
							//Verifica se valor é igual ao valor salvo
							xSavedValue = DBSObject.getNotNull(xSC.getValue(),"");
							xCurrentValue = DBSObject.getNotNull(xC.getValue(),"");
							xEqual = false;
							if (xCurrentValue == null
							 && xSavedValue == null){
								xEqual = true;
							}else if (xCurrentValue instanceof Number){
								xCurrentNumberValue = DBSNumber.toBigDecimal(xCurrentValue);
								if (xSavedValue instanceof Number){
									xSavedNumberValue = DBSNumber.toBigDecimal(xSavedValue);
								}
								if (xSavedNumberValue != null 
								 && xCurrentNumberValue != null){
									//Utiliza o compareTo para evitar diferença por quantidade de casas decimais
									if (xCurrentNumberValue.compareTo(xSavedNumberValue) == 0){
										xEqual = true;
									}
								}
							}else{
								xEqual = xSavedValue.equals(xCurrentValue);
							}
							if (!xEqual){
								//Indica que este registro não é igual ao valor salvo
								xOk = false;
							}
							break;
						}
					}
					if (!xOk){
						//Sai para procurar a próxima linha
						break;
					}
				}
				if (xOk){
					wDAO.setCurrentRowIndex(xRowIndex);
					return;
				}
			}
//			//Se for crud principal e não encontrou o registro anterior
//			if (wParentCrudBean == null){
//				addMessage(MESSAGE_TYPE.IMPORTANT, "Foi selecionado o primeiro registro, por não ter sido encontrato o registro anterior.");
//			}
		}
		if (wDAO != null){
			//Posiciona na primeira linha se não consegui encontrar o registro restaurado.
			wDAO.moveFirstRow();
		}
	}



	/**
	 * Chamado depois de efetuado o CRUD com sucesso.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterCommit(DBSCrudBeanEvent pEvent) throws DBSIOException {};

	/**
	 * Chamado quando houve problema de validação ou não houver a confirmação do usuário para continuar.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void beforeIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException {};

	/**
	 * Chamado depois de ignorar o CRUD.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterIgnore(DBSCrudBeanEvent pEvent){};

	/**
	 * Chamado antes de efetuar a seleção da linha através do ckeckbox padrão do datatable. Podendo, neste momento, inibir a seleção retornando setOk(false) do evento.<br/> 
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void beforeSelect(DBSCrudBeanEvent pEvent) throws DBSIOException {};
	
	/**
	 * Chamado depois da seleção de algum item.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterSelect(DBSCrudBeanEvent pEvent){};

	/**
	 * Chamado após indicar que seja salvar(commit) os dados, e antes de pedir a confirmação da edição.
	 * Para indicar problemas na validação deve-se setar <b>pEvent.setOk(false)</b>.<br/>
	 * Neste método deve-se efetuar as validações das regras de negócios e gerar as mensagens de erro ou alerta, 
	 * caso necessario, via o comando <b>addMessage</b>.<br/>
	 * Qualquer alteração direta nos valores de alguma coluna do wDAO, que não esteja vinculada a um campo da tela(getter e setter), 
	 * serão ignoradas por este evento, deve-se utilizar o evento <b>beforeCommit</b> para tal.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void validate(DBSCrudBeanEvent pEvent) throws DBSIOException{};

	
	//Events -----------------------------------------------------
	private void pvFireEventInitialize(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.INITIALIZE);
		xE.setEditingMode(getEditingMode());
		try {
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventInitialize",e);
		}
	}
	
	private void pvFireEventFinalize(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.FINALIZE);
		xE.setEditingMode(getEditingMode());
		try {
			pvBroadcastEvent(xE, false, false, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventFinalize",e);
		}
	}

	private boolean pvFireEventBeforeClose(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_CLOSE);
		xE.setEditingMode(getEditingMode());
		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeClose",e);
		}
		return xE.isOk();
	}

	private boolean pvFireEventBeforeView(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_VIEW);
		xE.setEditingMode(getEditingMode());
		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeView",e);
		}
		return xE.isOk();
	}
	
	private void pvFireEventAfterView(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_VIEW);
		xE.setEditingMode(getEditingMode());
		//Marca que não houve edição nos campos, para que as configurações iniciais nos campos efetuadas no BeforeView sejam aceita sem ficarem caracterizadas como edição do usuário. 

		setValueChanged(false); 

		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterView",e);
		}
	}

	private boolean pvFireEventBeforeInsert(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_INSERT);
		xE.setEditingMode(getEditingMode());

		pvBeforeInsertResetValues(wCrudForm);

		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeInsert",e);
		}
		
		setValueChanged(false); 

		return xE.isOk();
	}

	private boolean pvFireEventBeforeRefresh(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_REFRESH);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeRefresh",e);
		}
		return xE.isOk();
	}
	
	private void pvFireEventAfterRefresh(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_REFRESH);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventInitialize",e);
		}
	}

	/**
	 * Ocorre após a confirmação do estágio de ignore e antes de voltar ao modo sem edição(EditingMode.NONE)
	 * @return
	 */
	private boolean pvFireEventBeforeIgnore(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_IGNORE);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, false, false, false);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeIgnore",e);
		}
		return xE.isOk();
	}
	
	/**
	 * Ocorre após a confirmação do estágio de ignore e antes de voltar ao modo sem edição(EditingMode.NONE)
	 * @return
	 */
	private void pvFireEventAfterIgnore(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_IGNORE);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, false, false, false);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterIgnore",e);
		}
	}

	/**
	 * Ocorre após a indicação que se deseja salvar(commit) e antes de trocar para o estágio 
	 * de confirmação do commit
	 * @return
	 */
	private boolean pvFireEventValidate(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.VALIDATE);
		xE.setEditingMode(getEditingMode());
		
		try{
			/*Faz loop entre todos os registros selecionados quando for:
			 * .Aprovação e reprovação
			 * .Edição sem dialog
			*/
			if (getIsApprovingOrReproving() || !wDialogEdit){
				if (getHasSelected()){
					wDAO.setCurrentRowIndex(-1);
					for (Integer xRowIndex : wSelectedRowsIndexes){
						wDAO.setCurrentRowIndex(xRowIndex);
						pvBroadcastEvent(xE, false, false, false);
						if (!xE.isOk()){
							if (getIsApprovingOrReproving()){
								addMessage("erroassinatura", MESSAGE_TYPE.ERROR,"Não foi possível efetuar a edição de todos os itens selecionados. Procure efetuar a edição individualmente para identificar o registro com problema.");
								break;
							}else{
								addMessage("erroselecao", MESSAGE_TYPE.ERROR,"Não foi possível efetuar a edição de todos os itens selecionados. Procure efetuar a edição individualmente para identificar o registro com problema.");
								break;
							}
						}
					}
				}else{
					xE.setOk(false);
					addMessage("erroselecao", MESSAGE_TYPE.ERROR,"Não há registro selecionado.");
				}
			}else{
				pvBroadcastEvent(xE, false, false, false);
			}		
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventValidate",e);
		}
		return xE.isOk();

	}
	
	/**
	 * Chamado depois da validação e mensagens de confirmação.
	 * Ocorre após a gravação dos dados(sem commit) e antes de voltar ao modo sem edição(EditingMode.NONE).<br/>
	 * A transação(Begintrans/Commit/Rollback) são controladas automaticamete.<br/>
	 * Será efetuado o <b>rollback</b> em caso de <b>exception</b> ou se o atributo <b>pEvent.setOk</b> do evento for <b>false</b>.<br>
	 * Neste evento, pode-se forçar o valor de alguma coluna utilizando o <b>wDAO</b>, devendo-se, contudo, chamar o <b>super.beforeCommit()</b> ao final, 
	 * caso o CRUD seja a tabela definida no próprio wDAO.<br> 
	 * Caso o CRUD não seja da tabela definida no wDAO, este método deverá ser sobreescrito
	 * para que seja implementado o CRUD específico.<br>
	 * No caso do método ser sobreescrito, é necessário setar o atributo <b>pEvent.setCommittedRowCount</b> com
	 * a quantidade de registros afetados.<br>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	private boolean pvFireEventBeforeCommit(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_COMMIT);
		xE.setEditingMode(getEditingMode());
		String xErrorMsg = null;
		//Chame o metodo(evento) local para quando esta classe for extendida
		try {
			//Zera a quantidade de registros afetados
			xE.setCommittedRowCount(0);
			//Certifica-se que a conexão do wDAO é a conexão do bean
			wDAO.setConnection(wConnection);

			//Se for o crud principal
			//Inicia transação
			if (wParentCrudBean == null){
				DBSIO.beginTrans(wConnection);
			}
			
//Na aprovação ou reprovação, faz oop entre todos os registros selecionados
//			if (getIsApprovingOrReproving()){ Comentado em 10/04/2014 para possibilitar o loop em qualquer situação que haja mais de um registro selecionado

			/*Faz loop entre todos os registros selecionados quando for:
			 * .Aprovação e reprovação
			 * .Edição sem dialog
			*/
			if (getIsApprovingOrReproving() 
			|| !wDialogEdit){
				if (getHasSelected()){
					int xCount = 0;
					wDAO.setCurrentRowIndex(-1);
					for (Integer xRowIndex : wSelectedRowsIndexes){
						wDAO.setCurrentRowIndex(xRowIndex);
						if (!wDialogEdit){
							wDAO.setExecuteOnlyChangedValues(false);
						}
						pvBroadcastEvent(xE, false, false, false);
						xCount += xE.getCommittedRowCount(); 
						if (!xE.isOk()){
							break;
						}
					}
					//Ignora assinatura caso quantidade todal de registros afetados seja inferior a quantidade de itens selectionados 
					if (xCount < wSelectedRowsIndexes.size()){
						xE.setCommittedRowCount(0);
						xE.setOk(false);
						addMessage("erroassinatura", MESSAGE_TYPE.ERROR,"Não foi possível efetuar a assinatura de todos os itens selecionados. Procure efetuar a assinatura individualmente para identificar o registro com problema.");
					}else{
						xE.setCommittedRowCount(xCount);
					}
				}
			}else{
				pvBroadcastEvent(xE, false, false, false);
			}


			//Exibe mensagem de erro padrão, caso nehum registro tenha sido afedado e já não houver mensagem a ser exibida.
			if (!wDialogMessages.hasMessages()
			 && (!xE.isOk() || xE.getCommittedRowCount().equals(0))){
				xE.setOk(false);
				addMessage(wMessageNoRowComitted);
			}
			
			//Se for o crud principal
			//Da commit na transação
			if (wParentCrudBean == null){
				DBSIO.endTrans(wConnection, xE.isOk());
			}
		} catch (Exception e) {
			xE.setOk(false);
			try {
				//Se for o crud principal
				//da rollback na transação
				if (wParentCrudBean == null){
					DBSIO.endTrans(wConnection, false);
				}
				if (e instanceof DBSIOException){
					DBSIOException xDBException = (DBSIOException) e;
					xErrorMsg = e.getMessage(); 
					if (xDBException.isIntegrityConstraint()){
						clearMessages(); //Limpa mensagem padrão
//						addMessage("integridate", MESSAGE_TYPE.ERROR, xDBException.getLocalizedMessage());
						wMessageError.setMessageText(xDBException.getLocalizedMessage());
						wMessageError.setMessageTooltip(xErrorMsg);
						addMessage(wMessageError);

					}else{
						wLogger.error("EventBeforeCommit", e);
					}
				}
			} catch (DBSIOException e1) {
				xErrorMsg = e1.getMessage();
			}
			wMessageNoRowComitted.setMessageTooltip(xErrorMsg);
			addMessage(wMessageNoRowComitted);
		}
		return xE.isOk();
	}
	
	/**
	 * Evento diaparado após a gravação dos dados e antes de voltar ao modo sem edição(EditingMode.NONE) e anted do beforeRefresh()<br/>
	 * 
	 */
	private void pvFireEventAfterCommit(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_COMMIT);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, false, false, false);
			//Marca o parent como alterado caso o crud filho tenha confirmado alguma edição
			if (wParentCrudBean!=null){
				wParentCrudBean.setValueChanged(true);
			}
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterCommit",e);
		}
	}

	/**
	 * Chamado logo após o click do usuário no insert/delete/update/approve/reprove e antes de iniciar o respectivo insert/delete/update/approve/reprove.
	 * Podendo, portanto, cancelar o inicio da edição, informando setOk(False) no evento.<br/>
	 * Neste evento a conexão será aberta e posteriormente fechada no evento AfterEdit(ou em caso de erro ou o usário fechou dentro do evento)
	 * @return
	 */
	private boolean pvFireEventBeforeEdit(EditingMode pEditingMode){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_EDIT);
		xE.setEditingMode(pEditingMode);
		//Marca que não houve edição nos campos 
		setValueChanged(false); 
		try{
			pvBroadcastEvent(xE, false, true, false); 
			return xE.isOk();
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeEdit",e);
		}
		return xE.isOk();
	}
	
	/**
	 * Chamado logo após a finalização da edição(insert/delete/update/approve/reprove), 
	 * independentemente da edição ter sido confirmada ou ignorada.
	 * Fecha a conexão com o banco.</br>
	 * @return
	 */
	private boolean pvFireEventAfterEdit(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_EDIT);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, false, false, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterEdir",e);
		}
		return xE.isOk();
	}

	/**
	 * Ocorre antes de selecionar um item no checkbox do datatable. Podendo, neste momento, inibir a seleção
	 * @return
	 */
	private boolean pvFireEventBeforeSelect(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_SELECT);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeIgnore",e);
		}
		return xE.isOk();
	}
	
	private void pvFireEventAfterSelect(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_SELECT);
		xE.setEditingMode(getEditingMode());
		try{
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterIgnore",e);
		}
	}
	
	
	
	/**
	 * Chama os eventos localmente, nos filhos e nos listerners que eventualmente possam existir.
	 * @param pEvent
	 * @param pInvokeChildren Se chama os filhos
	 * @param pOpenConnection Se abre a conexão antes de chamar os eventos
	 * @param pCloseConnection Se fecha a conexão após chmar os eventos
	 * @throws Exception 
	 */
	private void pvBroadcastEvent(DBSCrudBeanEvent pEvent, boolean pInvokeChildren, boolean pOpenConnection, boolean pCloseConnection) throws Exception {
		try{
			if (pOpenConnection){
				openConnection();
			}
			wBrodcastingEvent = true;
			pvFireEventLocal(pEvent);
			if (pInvokeChildren){
				pvFireEventChildren(pEvent); 
			}
			pvFireEventListeners(pEvent);
		}catch(DBSIOException e){ 
			//Configura a mensagem padrão do dialog
			wMessageError.setMessageText(e.getLocalizedMessage());
			wMessageError.setMessageTooltip(e.getOriginalException().getLocalizedMessage()); 
			if (!DBSObject.isEmpty(e.getCause())){
				wMessageError.setMessageTooltip(e.getCause().getMessage() + "<br/>" + e.getMessage());				
			}
			addMessage(wMessageError);
			pEvent.setOk(false);
//			wLogger.error(pEvent.getEvent().toString(), e);
		}catch(Exception e){
			String xStr = pEvent.getEvent().toString() + ":" + DBSObject.getNotNull(this.getDialogCaption(),"") + ":";   
			if (e.getLocalizedMessage()!=null){
				 xStr = xStr + e.getLocalizedMessage();
			}else{
				xStr = xStr + e.getClass();
			}
			wMessageError.setMessageTextParameters(xStr);
			addMessage(wMessageError);
			pEvent.setOk(false);
			wLogger.error(pEvent.getEvent().toString(), e);
			throw e;
		}finally{
			wBrodcastingEvent = false;
			//Fecha conexão em caso de erro ou for informado para fechar
			if (pCloseConnection
			 || !pEvent.isOk()){
				closeConnection();
			}
		}	
	}
	
	//Chama a metodo(evento) dentro das classe foram adicionadas na lista que possuem a implementação da respectiva interface
	private void pvFireEventLocal(DBSCrudBeanEvent pEvent) throws DBSIOException{
		if (pEvent.isOk()){
			switch (pEvent.getEvent()) {
			case INITIALIZE:
				initialize(pEvent);
				break;
			case FINALIZE:
				finalize(pEvent);
				break;
			case BEFORE_CLOSE:
				beforeClose(pEvent);
				break;
			case BEFORE_VIEW:
				beforeView(pEvent);
				break;
			case AFTER_VIEW:
				afterView(pEvent);
				break;
			case BEFORE_INSERT:
				beforeInsert(pEvent);
				break;
			case BEFORE_REFRESH:
				pvCurrentRowSave();
				beforeRefresh(pEvent);
				pvCurrentRowRestore();
				break;
			case AFTER_REFRESH:
				afterRefresh(pEvent);
				break;
			case BEFORE_COMMIT:
				beforeCommit(pEvent);
				break;
			case AFTER_COMMIT:
				afterCommit(pEvent);
				break;
			case BEFORE_IGNORE:
				beforeIgnore(pEvent);
				break;
			case AFTER_IGNORE:
				afterIgnore(pEvent);
				break;
			case BEFORE_EDIT:
				beforeEdit(pEvent);
				break;
			case AFTER_EDIT:
				afterEdit(pEvent);
				break;
			case BEFORE_SELECT:
				beforeSelect(pEvent);
				break;
			case AFTER_SELECT:
				afterSelect(pEvent);
				break;
			case VALIDATE:
				validate(pEvent);
				break;
			default:
				break;
			}
		}		
	}

	private void pvFireEventChildren(DBSCrudBeanEvent pEvent) throws DBSIOException{
		if (pEvent.isOk()){
			for (DBSCrudBean xBean:wChildrenCrudBean){
				//Força a atualização do lista antes de exibir
				if (pEvent.getEvent() == CRUD_EVENT.BEFORE_VIEW
				 || pEvent.getEvent() == CRUD_EVENT.BEFORE_INSERT){
					pvRefreshList();
				}
				switch (pEvent.getEvent()) {
				case INITIALIZE:
					xBean.initialize(pEvent);
					break;
				case FINALIZE:
					xBean.finalize(pEvent);
					break;
				case BEFORE_CLOSE:
					xBean.beforeClose(pEvent);
					break;
				case BEFORE_VIEW:
					xBean.beforeView(pEvent);
					break;
				case AFTER_VIEW:
					xBean.afterView(pEvent);
					break;
				case BEFORE_INSERT:
					xBean.beforeInsert(pEvent);
					break;
				case BEFORE_REFRESH:
					xBean.beforeRefresh(pEvent);
					break;
				case AFTER_REFRESH:
					xBean.afterRefresh(pEvent);
					break;
				case BEFORE_COMMIT:
					xBean.beforeCommit(pEvent);
					break;
				case AFTER_COMMIT:
					xBean.afterCommit(pEvent);
					break;
				case BEFORE_IGNORE:
					xBean.beforeIgnore(pEvent);
					break;
				case AFTER_IGNORE:
					xBean.afterIgnore(pEvent);
					break;
				case BEFORE_EDIT:
					xBean.beforeEdit(pEvent);
					break;
				case AFTER_EDIT:
					xBean.afterEdit(pEvent);
					break;
				case BEFORE_SELECT:
					xBean.beforeSelect(pEvent);
					break;
				case AFTER_SELECT:
					xBean.afterSelect(pEvent);
					break;
				case VALIDATE:
					xBean.validate(pEvent);
					break;
				default:
					break;
				}
				if (!pEvent.isOk()){
					break;
				}
	        }
		}	
	}
	
	//Chama a metodo(evento) dentro das classe foram adicionadas na lista que possuem a implementação da respectiva interface
	private void pvFireEventListeners(DBSCrudBeanEvent pEvent) throws DBSIOException{
		if (pEvent.isOk()){
			for (int xX=0; xX<wEventListeners.size(); xX++){
				switch (pEvent.getEvent()) {
				case INITIALIZE:
					wEventListeners.get(xX).initialize(pEvent);
					break;
				case FINALIZE:
					wEventListeners.get(xX).finalize(pEvent);
					break;
				case BEFORE_CLOSE:
					wEventListeners.get(xX).beforeClose(pEvent);
					break;
				case BEFORE_VIEW:
					wEventListeners.get(xX).beforeView(pEvent);
					break;
				case AFTER_VIEW:
					wEventListeners.get(xX).afterView(pEvent);
					break;
				case BEFORE_REFRESH:
					wEventListeners.get(xX).beforeRefresh(pEvent);
					break;
				case BEFORE_INSERT:
					wEventListeners.get(xX).beforeInsert(pEvent);
					break;
				case AFTER_REFRESH:
					wEventListeners.get(xX).afterRefresh(pEvent);
					break;
				case BEFORE_COMMIT:
					wEventListeners.get(xX).beforeCommit(pEvent);
					break;
				case AFTER_COMMIT:
					wEventListeners.get(xX).afterCommit(pEvent);
					break;
				case BEFORE_IGNORE:
					wEventListeners.get(xX).beforeIgnore(pEvent);
					break;
				case AFTER_IGNORE:
					wEventListeners.get(xX).afterIgnore(pEvent);
					break;
				case BEFORE_EDIT:
					wEventListeners.get(xX).beforeEdit(pEvent);
					break;
				case AFTER_EDIT:
					wEventListeners.get(xX).afterEdit(pEvent);
					break;
				case BEFORE_SELECT:
					wEventListeners.get(xX).beforeSelect(pEvent);
					break;
				case AFTER_SELECT:
					wEventListeners.get(xX).afterSelect(pEvent);
					break;
				case VALIDATE:
					wEventListeners.get(xX).validate(pEvent);
					break;
				default:
					break;
				}
				//Sa do loop se encontrar erro
				if (!pEvent.isOk()){
					break;
				}
	        }
		}		
	}
}
