package br.com.dbsoft.ui.bean.crud;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
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
import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.bean.DBSBean;
import br.com.dbsoft.ui.bean.crud.DBSCrudBeanEvent.CRUD_EVENT;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.component.beandialogcrudmessages.IDBSBeanDialogCrudMessages;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSDate;
import br.com.dbsoft.util.DBSIO;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSIO.SORT_DIRECTION;


/**
 * Os DBSCrudBean devem ser declarados como @ConversationScoped.
 * O timeout da conversação esta definido em 10minutos
 * @author ricardo.villar
 *
 */

/**
 * Ordem de disparo dos eventos conforme a ação executada
 *  
 * ====================
 * INITIALIZE
 * --------------------
 * initializeClass
 * INITIALIZE
 * BEFORE_REFRESH
 * AFTER_REFRESH
 *   
 * ====================
 * refreshList()
 * --------------------
 * INITIALIZE
 * BEFORE_REFRESH
 * AFTER_REFRESH
 * 
 * ====================
 * searchList()
 * --------------------
 * BEFORE_REFRESH
 * AFTER_REFRESH
 * 
 * ====================
 * setSelected
 * --------------------
 * BEFORE_SELECT
 * AFTER_SELECT
 * 
 * ====================
 * selectAll()
 * --------------------
 * BEFORE_SELECT
 * AFTER_SELECT
 * 
 * ====================
 * view()
 * --------------------
 * BEFORE_VIEW
 * AFTER_VIEW
 * 
 * 	====================
 * 	VIEW - CLOSE
 * 	--------------------
 * 	BEFORE_CLOSE
 * 
 * ====================
 * copy()
 * --------------------
 * AFTER_COPY
 * 
 * ====================
 * paste()
 * --------------------
 * BEFORE_PASTE
 * 
 * ====================
 * insert()
 * --------------------
 * BEFORE_INSERT
 * BEFORE_EDIT
 * 
 *	====================
 *	INSERT - IGNORE
 * 	--------------------
 * 	BEFORE_IGNORE
 * 	AFTER_IGNORE
 * 	AFTER_EDIT
 * 	BEFORE_CLOSE
 * 
 * 	====================
 * 	INSERT - CONFIRM
 * 	--------------------
 * 	BEFORE_VALIDATE
 * 	VALIDATE (ANTES DA CONFIRMAÇÃO)
 * 	VALIDATE (APÓS A CONFIRMAÇÃO)
 * 	BEFORE_COMMIT
 * 	AFTER_COMMIT
 * 	searchList()
 * 	AFTER_EDIT
 * 	BEFORE_INSERT
 * 	BEFORE_EDIT
 * 
 * ====================
 * insertSelected()
 * --------------------
 * view();
 * copy();
 * insert();
 * paste();
 * 
 * ====================
 * update()
 * --------------------
 * BEFORE_EDIT
 * 
 * 	====================
 * 	UPDATE - IGNORE
 * 	--------------------
 * 	BEFORE_IGNORE
 * 	AFTER_IGNORE
 * 	AFTER_EDIT
 * 	searchList()
 * 	view()
 *	 
 * 	====================
 * 	UPDATE - CONFIRM
 * 	--------------------
 * 	BEFORE_VALIDATE
 * 	VALIDATE (ANTES DA CONFIRMAÇÃO)
 * 	VALIDATE (APÓS A CONFIRMAÇÃO)
 * 	BEFORE_COMMIT
 * 	AFTER_COMMIT
 * 	searchList()
 * 	AFTER_EDIT
 * 	view()
 * 
 * ====================
 * DELETE - IGNORE
 * --------------------
 * BEFORE_EDIT
 * BEFORE_VALIDATE
 * AFTER_EDIT
 * view()
 * 
 * 	====================
 * 	DELETE - CONFIRM
 * 	--------------------
 * 	BEFORE_EDIT
 * 	BEFORE_VALIDATE
 * 	VALIDATE (ANTES DA CONFIRMAÇÃO)
 * 	VALIDATE (APÓS A CONFIRMAÇÃO)
 * 	BEFORE_COMMIT
 * 	AFTER_COMMIT
 * 	searchList()
 * 	AFTER_EDIT
 * 
 */
public abstract class DBSCrudBean extends DBSBean implements IDBSBeanDialogCrudMessages{

	private static final long serialVersionUID = -8550893738791483527L;

	public static enum FormStyle {
		DIALOG 			(0),
		TABLE 			(1),
		VIEW 			(2);
		
		private int 	wCode;
		
		private FormStyle(int pCode) {
			this.wCode = pCode;
		}

		public int getCode() {
			return wCode;
		}
		
		public static FormStyle get(int pCode) {
			switch (pCode) {
			case 0:
				return DIALOG;
			case 1:
				return TABLE;
			case 2:
				return VIEW;
			default:
				return DIALOG;
			}
		}		
	}

	
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
	private FormStyle							wFormStyle = FormStyle.DIALOG;
	private List<Integer> 						wSelectedRowsIndexes =  new ArrayList<Integer>();
	private	Collection<DBSColumn> 				wSavedCurrentColumns = null;
	private boolean								wValueChanged;
	private int									wCopiedRowIndex = -1;
	private boolean								wValidateComponentHasError = false;
	private Boolean 							wDialogOpened = false;
	private String								wDialogCaption;
	private	Boolean								wDialogCloseAfterInsert = false;
	private String								wMessageConfirmationEdit = "Confirmar a edição?";
	private String								wMessageConfirmationInsert = "Confirmar a inclusão?";
	private String								wMessageConfirmationDelete = "Confirmar a exclusão?";
	private String								wMessageConfirmationApprove = "Confirmar a aprovação?";
	private String								wMessageConfirmationReprove = "Confirmar a reprovação?";
	private String								wMessageIgnoreEdit = "Ignorar a edição?";
	private String								wMessageIgnoreInsert = "Ignorar a inclusão?";
	private String								wMessageIgnoreDelete = "Ignorar a exclusão?";
	private Boolean								wAllowUpdate = true;
	private Boolean								wAllowInsert = true;
	private Boolean								wAllowDelete = true;
	private Boolean								wAllowRefresh = true;
	private Boolean								wAllowApproval = false;
	private Boolean								wAllowApprove = true;
	private Boolean								wAllowReprove = true;
	private Boolean								wAllowCopy = true;
	private Boolean								wAllowCopyOnUpdate = false;
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
	private String								wSortColumn = "";
	private String								wSortDirection = SORT_DIRECTION.DESCENDING.getCode();

	private Boolean								wRevalidateBeforeCommit = false;
	private Integer								wUserId;
	private Boolean								wMultipleSelection = false;
	private DBSCrudBean							wParentCrudBean = null;
	private List<DBSCrudBean>					wChildrenCrudBean = new ArrayList<DBSCrudBean>();


	//Mensagens
	private IDBSMessage					wMessageNoRowComitted = 
													new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.noRowComitted"));
	private IDBSMessage					wMessageOverSize = 
													new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.overSize"));
	private IDBSMessage					wMessageNoChange = 
													new DBSMessage(MESSAGE_TYPE.INFORMATION, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.noChange"));
	private IDBSMessage					wMessaggeApprovalSameUser =
													new DBSMessage(MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.approvalSameUser"));
	
	
	/**
	 * Retorna o ID da conversação
	 * @return
	 */
	public String getCID(){
		return wConversation.getId();
	}
	
	/**
	 * Inicia conversação caso exista a anotação 'ConversationScoped' na class
	 */
	public void conversationBegin(){
		for (Annotation xAnnotation:this.getClass().getDeclaredAnnotations()){
			if (xAnnotation.annotationType() == ConversationScoped.class){
				pvConversationBegin();
				break;
			}
		}
	}
	
	@Override
	protected void initializeClass() {
		conversationBegin();
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
	 * Seta o valor da coluna na posição atual do regist.
	 * O valor será convertido para a class informada
	 * Obs:Os nomes das colunas são definidos a partir da query efetuado no DAO.
	 * Para que os atributos dos componentes na tela, como tamanho máximo, na sejam configurados automaticamente,
	 * é importante que os <i>getters</i> e <i>setter<i/> tenham o mesmo nome da coluna da tabela e iniciem 'Fl'.<br/>
	 * ex:setFlNOME_DA_COLUNA, getFlNOME_DA_COLUNA.  
	 * @param pColumnName
	 * @param pColumnValue
	 * @param pValueClass Classe para a qual o valor será convertido
	 */
	public <T> void setValue(String pColumnName, Object pColumnValue, Class<T> pValueClass){
		T xValue = DBSObject.<T>toClassValue(pColumnValue, pValueClass);
		
		setValue(pColumnName, xValue);
	}

	/**
	 * Seta o valor da coluna na posição atual do regist.
	 * O valor será convertido para o mesmo tipo da class.
	 * Obs:Os nomes das colunas são definidos a partir da query efetuado no DAO.
	 * Para que os atributos dos componentes na tela, como tamanho máximo, na sejam configurados automaticamente,
	 * é importante que os <i>getters</i> e <i>setter<i/> tenham o mesmo nome da coluna da tabela e iniciem 'Fl'.<br/>
	 * ex:setFlNOME_DA_COLUNA, getFlNOME_DA_COLUNA.  
	 * @param pColumnName
	 * @param pColumnValue
	 */
	public void setValue(String pColumnName, Object pColumnValue){
		//Utiliza ListValue para controlar os valores de todas as linhas
		if (wFormStyle == FormStyle.TABLE){
			setListValue(pColumnName, pColumnValue);
		}else{
			pvSetValueDAO(pColumnName, pColumnValue);
		}
	}
	
	/**
	 * Retorna o valor da coluna.<br/>
	 * Para que os atributos dos componentes na tela, como tamanho máximo, na sejam configurados automaticamente,
	 * é importante que os <i>getters</i> e <i>setter<i/> tenham o mesmo nome da coluna da tabela e iniciem 'Fl'.<br/>
	 * ex:setFlNOME_DA_COLUNA, getFlNOME_DA_COLUNA.  
	 * @param pColumnName
	 * @return
	 */
	
	public <T> T getValue(String pColumnName){
		//Utiliza ListValue para controlar os valores de todas as linhas
		if (wFormStyle == FormStyle.TABLE){
			return getListValue(pColumnName);
		}else{
			return pvGetValue(pColumnName);
		}
	}
	
	
	/**
	 * Retorna o valor da coluna convertida para a classe do tipo informado.<br/>
	 * Para que os atributos dos componentes na tela, como tamanho máximo, na sejam configurados automaticamente,
	 * é importante que os <i>getters</i> e <i>setter<i/> tenham o mesmo nome da coluna da tabela e iniciem 'Fl'.<br/>
	 * ex:setFlNOME_DA_COLUNA, getFlNOME_DA_COLUNA.  
	 * @param pColumnName Nome da coluna
	 * @param pValueClass Classe para a qual será convertido o valor recebido
	 * @return
	 */
	public <T> T getValue(String pColumnName, Class<T> pValueClass){
		return DBSObject.<T>toClassValue(getValue(pColumnName), pValueClass);
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
	public <T> T getValueOriginal(String pColumnName, Class<T> pValueClass){
		return DBSObject.<T>toClassValue(getValueOriginal(pColumnName), pValueClass);
	}
	

	/**
	 * Retorna o valor da coluna.
	 * Este método deve ser utilizado somente quando o controle do registro atual estiver a cargo do datatable, 
	 * como é o caso na exibição das colunas via DBSDataTableColumn.<br/>
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
	 * Retorna o valor da coluna.
	 * Este método deve ser utilizado somente quando o controle do registro atual estiver a cargo do datatable, 
	 * como é o caso na exibição das colunas via DBSDataTableColumn.<br/>
	 * @param pColumnName
	 * @return
	 */
	public <T> T getListValue(String pColumnName, Class<T> pValueClass){
		return DBSObject.<T>toClassValue(getListValue(pColumnName), pValueClass);
	}

	/**
	 * Seta o valor da coluna informada diretamenteo no ResultDataModel.<br/>
	 * Utilizado quando a edição é diretamente no grid.
	 * @param pColumnName Nome da coluna
	 * @param pValue Valor da coluna
	 * @return
	 */		
	private void setListValue(String pColumnName, Object pColumnValue){
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
	 * Para recuperar os valores das colunas da linha atual deve-se utilizar o atributo <b>getListValue(pColumnName)</b>.
	 * @param pColumnId Nome para ser utilizado para identificar qual a coluna será formatada.
	 * @return Texto formadado
	 */
	public String getListFormattedValue(String pColumnId) throws DBSIOException{return "pColumnId '" + pColumnId + "' desconhecida";}

	/**
	 * Retorna a mensagem vinculada a esta coluna.<br/>
	 * Esta mensagem serve para informar qualquer tipo de aviso/erro referente ao valor nela contido.<br/>
	 * Será retornado o valor nulo quando não houve mensagem.<br/>
	 * A mensagem sempre será apagada após o valor da coluna ter sido alterado.
	 */
	public IDBSMessage getColumnMessage(String pColumnName){
		if (wDAO != null 
		 && (wDAO.getColumns().size() > 0
		  || wDAO.getCommandColumns().size() > 0)){
			return wDAO.getMessage(pColumnName);
		}else{
			return null;
		}
	}


	/**
	 * Configura os inputs da tela
	 * Método chamado pelo DBSCrudView para poder configurar os atributos dos componentes na tela antes que sejam exibidos
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
	
	
	
	/**
	 * Método padrão para validação dos campos que existentes dentro do DBSCrudView do usuário.
	 * Método para validar o conteúdo do valor digitaro em função do DAO.
	 * Este método é chamado pelo DBSCrudView.
	 * @param pComponent
	 */
	public void crudFormValidateComponent(FacesContext pContext, UIComponent pComponent, Object pValue){
		//Efetua a validação dos campos conforme estive definido no DAO, 
		//Se estiver em edição, houve DAO e não tiver sido pressionado o botão de cancela do CrudForm
		if (wEditingMode!=EditingMode.NONE){ 
			if (wDAO!=null
			 && pValue!=null){
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
	

	//=================================================================

	/**
	 * Informa o modo de edição(inclusão, alteração, etc)
	 * @return
	 */
	public EditingMode getEditingMode() {
		return wEditingMode;
	}

	/**
	 * Informa o modo de edição(inclusão, alteração, etc).<br/>
	 * Qualquer mudança no modo de edição<b>setEditingMode</b>, setará a propriedade <b>EditingStage(NONE)</b>.
	 * @param pRunningState
	 */
	private synchronized void setEditingMode(EditingMode pEditingMode) {
		if (wEditingMode != pEditingMode){
			//Qualquer troca no editingMode, desativa o editingstage
			setEditingStage(EditingStage.NONE);
			//Dispara o evento de fim da edição quando mode retornar para NONE.
			if (pEditingMode.equals(EditingMode.NONE)){
				pvFireEventAfterEdit(wEditingMode);
				setValueChanged(false);
			}
			wEditingMode = pEditingMode;
		}
	}

	/**
	 * Informa se a edição está sendo confirmada ou ignorada.
	 * @return
	 */
	public EditingStage getEditingStage() {
		return wEditingStage;
	}

	/**
	 * Informa se a edição esta sendo confirmada ou ignorada.(Método PRIVADO)
	 * @param pRunningState
	 */
	private void setEditingStage(EditingStage pEditingStage) {
		if (wEditingStage != pEditingStage){
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
		if (wFormStyle == FormStyle.DIALOG){
			if (wDialogOpened != pDialogOpened){
				wDialogOpened = pDialogOpened;
			}
		}
	}
	
	/**
	 * Indica se dialog será fechado alogo após a inclusão.<br/>
	 * Funcionalidade normalmente utilizada quando não for comum inclusões consecutivas.<br/>
	 * O padrão é <b>false</b>.
	 * @return
	 */
	public Boolean getDialogCloseAfterInsert() {
		return wDialogCloseAfterInsert;
	}

	/**
	 * Indica se dialog será fechado alogo após a inclusão.<br/>
	 * Funcionalidade normalmente utilizada quando não for comum inclusões consecutivas.<br/>
	 * O padrão é <b>false</b>.
	 * @param pDialogCloseAfterInsert
	 */
	public void setDialogCloseAfterInsert(Boolean pDialogCloseAfterInsert) {
		wDialogCloseAfterInsert = pDialogCloseAfterInsert;
	}

	/**
	 * Indica se edição será efetuado dentro de um dialog.<br/>
	 * Caso positivo, deverá ser implementado o form utilizando o componente crudForm e adicioná-lo a view que onde está o crudTable.  
	 * @return
	 */
	public FormStyle getFormStyle() {return wFormStyle;}

	/**
	 * Indica se edição será efetuado dentro de um DIALOG, TABLE ou VIEW.<br/>
	 * Caso positivo, deverá ser implementado o form utilizando os respectivos componentes crudTable/crudView/crudDialog.<br/>
	 * <ul>
	 * 		<li>
	 * 		<b>DIALOG</b><br/>
	 * 		A edição é efetuado dentro de uma tela modal de diálogo utilizando o compoente <b>crudDialog</b>.<br/>
	 * 		Normalmente utilizado a partir de uma lista utilizando o <b>crudTable</b>. 
	 * 		Nesta caso o <b>crudDialog</b> deverá ser adicionado ao final da view que onde está o <b>crudTable</b>.
	 * 		</li>  
	 * 		<li>
	 * 		<b>TABLE</b><br/>
	 * 		A edição é efetuado dentro da própria lista utilizando o componente <b>crudTable</b>.
	 * 		</li>  
	 * 		<li>
	 * 		<b>VIEW</b><br/>
	 * 		A edição é efetuado dentro da própria página utilizando o componente <b>crudView</b>.<br/>
	 * 		No <i>update</i> o registro a ser editado deverá ser selecionado no <b>beforeRefresh</b>.<br/>
	 * 		Caso não exista registro corrente, será considerado uma inclusão(<i>insert</i>).</br>
	 * 		Para definir a próxima página de navegação, deve-se sobreescrever os métodos <b>confirmEditing, ignoreEditing</b> 
	 * 		se não estiver habilitado as confirmações automáticas ou
	 *  	subreescrever o método <b>endEditing(boolean)</b> se estiver habilitado as confirmações automáticas.<br/>
	 * 		Para verificar se há algum erro, deve-se pesquisar se há alguma mensagem na lista utlizando <b>getHasMessage()</b>.
	 * 		</li>  
	 * </ul>
	 */
	public void setFormStyle(FormStyle pFormStyle) {wFormStyle = pFormStyle;}

	@Override
	public String getMessageConfirmationEdit() {return wMessageConfirmationEdit;}
	@Override
	public void setMessageConfirmationEdit(String pMessageConfirmationEdit) {wMessageConfirmationEdit = pMessageConfirmationEdit;}

	@Override
	public String getMessageConfirmationInsert() {return wMessageConfirmationInsert;}
	@Override
	public void setMessageConfirmationInsert(String pDialogConfirmationInsertMessage) {wMessageConfirmationInsert = pDialogConfirmationInsertMessage;}

	@Override
	public String getMessageConfirmationDelete() {return wMessageConfirmationDelete;}
	@Override
	public void setMessageConfirmationDelete(String pMessageConfirmationDelete) {wMessageConfirmationDelete = pMessageConfirmationDelete;}

	@Override
	public String getMessageConfirmationApprove() {return wMessageConfirmationApprove;}
	@Override
	public void setMessageConfirmationApprove(String pMessageConfirmationApprove) {wMessageConfirmationApprove = pMessageConfirmationApprove;}

	@Override
	public String getMessageConfirmationReprove() {return wMessageConfirmationReprove;}
	@Override
	public void setMessageConfirmationReprove(String pMessageConfirmationReprove) {wMessageConfirmationReprove = pMessageConfirmationReprove;}

	@Override
	public String getMessageIgnoreEdit() {return wMessageIgnoreEdit;}
	@Override
	public void setMessageIgnoreEdit(String pMessageIgnoreEdit) {wMessageIgnoreEdit = pMessageIgnoreEdit;}

	@Override
	public String getMessageIgnoreInsert() {return wMessageIgnoreInsert;}
	@Override
	public void setMessageIgnoreInsert(String pMessageIgnoreInsert) {wMessageIgnoreInsert = pMessageIgnoreInsert;}

	@Override
	public String getMessageIgnoreDelete() {return wMessageIgnoreDelete;}
	@Override
	public void setMessageIgnoreDelete(String pMessageIgnoreDelete) {wMessageIgnoreDelete = pMessageIgnoreDelete;}

	/**
	 * Retorna se existe mensagem para confirmar.<br/>
	 * Mensagens vázias não serão exibidas.
	 * @return
	 */
	@Override
	public Boolean getMessageConfirmationExists(){
		if (getIsCommitting()){
			if (getIsUpdating()){
				if (DBSObject.isEmpty(getMessageConfirmationEdit())){
					return false;
				}
			}else if (getIsInserting()){
				if (DBSObject.isEmpty(getMessageConfirmationInsert())){
					return false;
				}
			}else if (getIsDeleting()){
				if (DBSObject.isEmpty(getMessageConfirmationDelete())){
					return false;
				}
			}else{
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Retorna se existe mensagem para ignorar.<br/>
	 * Mensagens vázias não serão exibidas.
	 * @return
	 */
	@Override
	public Boolean getMessageIgnoreExists(){
		if (getIsIgnoring()){
			if (getIsUpdating()){
				if (DBSObject.isEmpty(getMessageIgnoreEdit())){
					return false;
				}
			}else if (getIsInserting()){
				if (DBSObject.isEmpty(getMessageIgnoreInsert())){
					return false;
				}
			}else if (getIsDeleting()){
				if (DBSObject.isEmpty(getMessageIgnoreDelete())){
					return false;
				}
			}else{
				return false;
			}
			return true;
		}
		return false;
	}

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
			pvSearchList();
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
	/**
	 * Habilita ou desabilita o botão de inclusão.<br/>
	 * Como padrão o copy/paste também serão habilitados conforme o valor deste atributo.<br/>
	 * Isso não impede que seja alterado posteriormente via <b>setAllowCopy</b> e/ou <b>setAllowCopyOnUpdate</b>.
	 * @param pAllowInsert
	 */
	public void setAllowInsert(Boolean pAllowInsert) {
		wAllowInsert = pAllowInsert;
		//Habilita ou desabilita o copy/paste conforme a habilitação do insert
		//Isto não impede que o setAllowCopy seja alterado posteriormente.
		setAllowCopy(pAllowInsert);
	}
	
	public Boolean getAllowRefresh() {return wAllowRefresh;}
	public void setAllowRefresh(Boolean pAllowRefresh) {wAllowRefresh = pAllowRefresh;}

	public Boolean getMultipleSelection() {return wMultipleSelection;}
	public void setMultipleSelection(Boolean pMultipleSelection) {wMultipleSelection = pMultipleSelection;}

	/**
	 * Indica se habilita controle de assinatura.<br/>
	 * Padrão é true.<br/>
	 * Consulte também <b>allowApproval</b>.
	 * @return
	 */
	public Boolean getAllowApproval() {return wAllowApproval;}
	/**
	 * Indica se habilita controle de assinatura.<br/>
	 * Padrão é true.<br/>
	 * Consulte também <b>allowApproval</b>.
	 * @return
	 */
	public void setAllowApproval(Boolean pAllowApproval) {wAllowApproval = pAllowApproval;}

	/**
	 * Indica se habilita botão para aprovação.<br/>
	 * Padrão é true.<br/>
	 * Consulte também <b>allowApproval</b>.
	 * @return
	 */
	public Boolean getAllowApprove(){return wAllowApprove;}
	/**
	 * Indica se habilita botão para aprovação.<br/>
	 * Padrão é true.<br/>
	 * Consulte também <b>allowApproval</b>.
	 * @return
	 */
	public void setAllowApprove(Boolean pAllowApprove){wAllowApprove = pAllowApprove;}

	/**
	 * Indica se habilita botão para reprovação.<br/>
	 * Padrão é true.<br/>
	 * Consulte também <b>allowApproval</b>.
	 * @return
	 */
	public Boolean getAllowReprove(){return wAllowReprove;}
	/**
	 * Indica se habilita botão para reprovação.<br/>
	 * Padrão é true.<br/>
	 * Consulte também <b>allowApproval</b>.
	 * @return
	 */
	public void setAllowReprove(Boolean pAllowReprove){wAllowReprove = pAllowReprove;}
	
	/**
	 * Indica se habilita função de copy/paste.<br/>
	 * Padrão é true.
	 * @return
	 */
	public Boolean getAllowCopy(){return wAllowCopy;}
	/**
	 * Indica se habilita função de copy/paste.<br/>
	 * Padrão é true.
	 * @return
	 */
	public void setAllowCopy(Boolean pAllowCopy){wAllowCopy = pAllowCopy;}

	/**
	 * Indica se habilita função de copy/paste para a edição.<br/>
	 * Normalmente o copy/paste está habilidado para o inclusão.<br/>
	 * Padrão é false.
	 * @return
	 */
	public Boolean getAllowCopyOnUpdate(){return wAllowCopyOnUpdate;}
	/**
	 * Indica se habilita função de copy/paste para a edição.<br/>
	 * Normalmente o copy/paste está habilidado para o inclusão.<br/>
	 * Padrão é false.
	 * @return
	 */
	public void setAllowCopyOnUpdate(Boolean pAllowCopyOnUpdate){wAllowCopyOnUpdate = pAllowCopyOnUpdate;}

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
	
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a data da aprovação do registro.<br/>
	 * @return
	 */
	public String getColumnNameApprovalDateApproved() {return wColumnNameApprovalDateApproved;}
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a data da aprovação do registro.<br/>
	 * @return
	 */
	public void setColumnNameApprovalDateApproved(String pColumnNameApprovalDateApproved) {wColumnNameApprovalDateApproved = pColumnNameApprovalDateApproved;}
	
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a data da inclusão do registro.<br/>
	 * @return
	 */
	public String getColumnNameDateOnInsert() {return wColumnNameDateOnInsert;}
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a data da inclusão do registro.<br/>
	 */
	public void setColumnNameDateOnInsert(String pColumnNameDateOnInsert) {wColumnNameDateOnInsert = pColumnNameDateOnInsert;}

	
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a data da alteração do registro.<br/>
	 * @return
	 */
	public String getColumnNameDateOnUpdate() {return wColumnNameDateOnUpdate;}
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a data da alteração do registro.<br/>
	 */
	public void setColumnNameDateOnUpdate(String pColumnNameDateOnUpdate) {wColumnNameDateOnUpdate = pColumnNameDateOnUpdate;}

	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a chave do usuário que inseriu o registro.<br/>
	 * O usuário deve ser informado no atributo <b>setUsedId</b>, normalmente no eventi <b>initialize</b>.
	 * @return
	 */
	public String getColumnNameUserIdOnInsert() {return wColumnNameUserIdOnInsert;}
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a chave do usuário que inseriu o registro.<br/>
	 * O usuário deve ser informado no atributo <b>setUsedId</b>, normalmente no eventi <b>initialize</b>.
	 */
	public void setColumnNameUserIdOnInsert(String pColumnNameUserIdOnInsert) {wColumnNameUserIdOnInsert = pColumnNameUserIdOnInsert;}

	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a chave do usuário que alterou o registro.<br/>
	 * O usuário deve ser informado no atributo <b>setUsedId</b>, normalmente no eventi <b>initialize</b>.
	 * @return
	 */
	public String getColumnNameUserIdOnUpdate() {return wColumnNameUserIdOnUpdate;}
	/**
	 * Nome da coluna do tabela no banco de dados que amazenará a chave do usuário que alterou o registro.<br/>
	 * O usuário deve ser informado no atributo <b>setUsedId</b>, normalmente no eventi <b>initialize</b>.
	 */
	public void setColumnNameUserIdOnUpdate(String pColumnNameUserIdOnUpdate) {wColumnNameUserIdOnUpdate = pColumnNameUserIdOnUpdate;}

	/**
	 * Id(nome da coluna) da coluna que será utilizada para efetuar o sort  
	 * @param pSortColumn
	 */
	public void setSortColumn(String pSortColumn){wSortColumn = pSortColumn;}
	/**
	 * Id(nome da coluna) da coluna que será utilizada para efetuar o sort  
	 * @param pSortColumn
	 */
	public String getSortColumn(){return wSortColumn;}
	
	/**
	 * Direção do sort.<br/>
	 * Aconselha-se utilizar o getCode() da respectiva direção definida no enum SORT_DIRECTION. ex: SORT_DIRECTION.DESCENDING.getCode().   
	 * @param pSortDirection A = Ascendente, B = Descendente, vázio = Sem sort. 
	 */
	public void setSortDirection(String pSortDirection){wSortDirection = pSortDirection;}
	/**
	 * Direção do sort.  
	 * @return A = Ascendente, B = Descendente, vázio = Sem sort 
	 */
	public String getSortDirection(){return wSortDirection;}

	/**
	 * Indica se será disparado novamente o evento <b>validade</b> após a 
	 * confirmação da edição e antes do <b>beforeCommit</b>.<br/>
	 * Aconselhado para os casos que pode ter havido alguma alterações de dados no banco 
	 * por agentes externos durante o tempo de espera da confirmação da edição.<br/>
	 * O padrão é <b>false</b>.  
	 * @return
	 */
	public Boolean getRevalidateBeforeCommit() {return wRevalidateBeforeCommit;}

	/**
	 * Indica se será disparado novamente o evento <b>validade</b> após a 
	 * confirmação da edição e antes do <b>beforeCommit</b>.<br/>
	 * Aconselhado para os casos que pode ter havido alguma alterações de dados no banco 
	 * por agentes externos durante o tempo de espera da confirmação da edição.<br/>
	 * O padrão é <b>false</b>.  
	 * @return
	 */
	public void setRevalidateBeforeCommit(Boolean pRevalidateBeforeCommit) {wRevalidateBeforeCommit = pRevalidateBeforeCommit;}

	
	public Integer getUserId() {return wUserId;}
	public void setUserId(Integer pUserId) {wUserId = pUserId;}

	/**
	 * Define o CrudBean que é pai deste, como é comum nos cruds dentro de crud.<br/>
	 * Neste caso, a conexão e as transações são herdadas automaticamente do crud pai.<br/>
	 * O <i>commit</i> ou <i>rollback</i> só serão efetuados no primeiro crud pai. 
	 * Desta forma, qualquer edição do crud filho, dependerá da confirmação no crud pai.
	 * @param pCrudBean
	 */
	public void setParentCrudBean(DBSCrudBean pCrudBean) {
		wParentCrudBean = pCrudBean;
		if (!pCrudBean.getChildrenCrudBean().contains(this)){
			pCrudBean.getChildrenCrudBean().add(this);
		}
	}

	/**
	 * Define o CrudBean que é pai deste, como é comum nos cruds dentro de crud.<br/>
	 * Neste caso, a conexão e as transações são herdadas automaticamente do crud pai.<br/>
	 * O <i>commit</i> ou <i>rollback</i> só serão efetuados no primeiro crud pai. 
	 * Desta forma, qualquer edição do crud filho, dependerá da confirmação no crud pai.
	 * @param pCrudBean
	 */
	public DBSCrudBean getParentCrudBean() {
		return wParentCrudBean;
	}


	/**
	 * Lista de CrudBean filhos deste crud.<br/>
	 * O <i>refreshList</i> dos CrudBean filhos serão disparados caso 
	 * o <i>beforeView</i> e <i>before_insert</i> deste crudBean seja disparado.<br>
	 * Permitindo que os cruds filhos atualizem seus dados em função da posição atual deste crud.
	 * @return
	 */
	public List<DBSCrudBean> getChildrenCrudBean() {
		return wChildrenCrudBean;
	}
	

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
	@Override
	public Boolean getIsCommitting(){
		return (wEditingStage == EditingStage.COMMITTING);
	}
	
	/**
	 * Informa se está em modo UPDATE. Modo de edição
	 * @return
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
	public Boolean getIsInserting(){
		return (wEditingMode == EditingMode.INSERTING);
	}

	/**
	 * Retorna se algum dos atributos <b>allowApproval, allowDelete, allowInsert e allowUpdate</b> está habilitado.
	 * @return
	 */
	public Boolean getIsEditingDisabled(){
		if (wAllowApproval
		 || wAllowDelete 
		 || wAllowInsert
		 || wAllowUpdate){
			return false;
		}
		return true;
	}
	
	/**
	 * Desabilita qualquer tipo de edição setando <b>allowApproval, allowDelete, allowInsert e allowUpdate</b> para <b>false</b>.
	 */
	public void setDisableEditing(){
		setAllowApproval(false);
		setAllowDelete(false);
		setAllowInsert(false);
		setAllowUpdate(false);
	}

	/**
	 * Retorna se está habilitada a edição dos campos.
	 * @return
	 */
	public Boolean getIsReadOnly(){
		if (getIsViewing()
		 || wEditingMode == EditingMode.DELETING){
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
	 * Retorna lista dos itens selecionados
	 * @return
	 */
	public List<Integer> getSelectedRowsIndexes(){
		return wSelectedRowsIndexes;
	}
	
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
	 * Retorna se existem alguma linha selecionada via checkbox padrão de multipla-seleção.
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
						//Disparado eventos para validação dos dados
						if (pvFireEventBeforeValidate()
						 && pvFireEventValidate()){
							//Exibe tela de confirmação para efetuar o commit
							setEditingStage(EditingStage.COMMITTING);
							//Se não existir mensagem de confirmação a ser exibida, finaliza a edição diretamente.
							if (!getMessageConfirmationExists()){
								return endEditing(true);
							}
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
				//Disparado eventos antes de ignorar
				setEditingStage(EditingStage.IGNORING);
				//Se não houve alteração de valores, sai sem confirmação
				if (!getIsValueChanged()){
					//Confirma o estágio/comando de 'ignorar'
					return endEditing(true);
				}
				//Se não existir mensagem de confirmação a ser exibida, finaliza a edição diretamente.
				if (!getMessageIgnoreExists()){
					return endEditing(true);
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
	@Override
	public synchronized String endEditing(Boolean pConfirm) throws DBSIOException{
		try{
			if (pConfirm){
				//Verifica se está no estágio correto
				if (wEditingStage!=EditingStage.NONE){
					if (wEditingStage==EditingStage.COMMITTING){
						//Disparando eventos
						if (pvFireEventValidate()
						 && pvFireEventBeforeCommit()){
							pvFireEventAfterCommit();
							pvSearchList();
							pvEndEditing(true);
						}else{
							pvEndEditing(false);
						}
					}else if (wEditingStage==EditingStage.IGNORING){
						//Disparando eventos
						if (pvFireEventBeforeIgnore()){
							pvFireEventAfterIgnore();
							pvEndEditing(true);
						}else{
							pvEndEditing(false);
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
	
	
	/**
	 * Ativa a edição automaticamente quando o formStyle é <b>VIEW</b>.<br/>
	 * Método é chamado dentro do <b>crudView.xhtml</b>.
	 * @param pEditingMode
	 * @throws DBSIOException
	 */
	public String beginEditingView() throws DBSIOException{
		if (getFormStyle() != FormStyle.VIEW){
			return DBSFaces.getCurrentView();
		}
		if (getEditingMode() != EditingMode.NONE){
			return DBSFaces.getCurrentView();
		}
		try{
			//Abre conexão
			openConnection();
			//Le o registro
			beforeRefresh(null);
			if (wConnection != null){
				moveFirst();
				//Verifica se existe registro corrente
				if (wDAO != null && wDAO.getCurrentRowIndex() > -1){
					return update();
				}else{
					return insert();
				}
			}
			return insert();
		}finally{
			if (wConnection != null){
				closeConnection(); 
			}
		}
	}


	// Methods ############################################################
	
	/**
	 * Efetua uma nova pesquisa e dispara os eventos <b>beforeRefresh</b> e <b>afterRefresh</b>.<br/>
	 * Não dispara o evento <b>initialize</b>.<br/>
	 * Como alternativa existe o método <b>refreshList</b> que tem o mesmo efeito, porém dispara o evento <b>initialize</b>.<br/>
	 * A conexão do banco é aberta no inicio, caso esteja fechada, e fechada ao final. 
	 * @throws DBSIOException 
	 */
	public synchronized String searchList() throws DBSIOException{
		boolean xOpenConnection = false;
		//Abre conexão se for crud filho e conexão não estiver aberta, abre. 
		//Isso ocorre normalmente quando o botão de REFRESH do crudtable dentro do crudDialog é selecionado antes de se iniciar a edição do crud mãe.
		if (wParentCrudBean!=null
		 && !DBSIO.isConnectionOpened(wParentCrudBean.wConnection)){
			xOpenConnection = true;
			wParentCrudBean.openConnection();
		}
		
		pvSearchList(); 
		//Fecha conexão se tiver sido aberta acima
		if (xOpenConnection){
			wParentCrudBean.closeConnection();
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Efetua uma nova pesquisa e dispara os eventos <b>beforeRefresh</b> e <b>afterRefresh</b>.<br/>
	 * Dispara o evento <b>initialize</b> para obrigar valores iniciais sejam refeitos, como as listas, caso existam.<br/>
	 * Como alternativa existe o método <b>searchList</b> que tem o mesmo efeito, porém <b>não</b> dispara o evento <b>initialize</b>.<br/>
	 * A conexão do banco é aberta no inicio, caso esteja fechada, e fechada ao final. 
	 * @throws DBSIOException 
	 */
	public synchronized String refreshList() throws DBSIOException{
		boolean xOpenConnection = false;
		//Abre conexão se for crud filho e conexão não estiver aberta, abre. 
		//Isso ocorre normalmente quando o botão de REFRESH do crudtable dentro do crudDialog é selecionado antes de se iniciar a edição do crud mãe.
		if (wParentCrudBean!=null
		 && !DBSIO.isConnectionOpened(wParentCrudBean.wConnection)){
			xOpenConnection = true;
			wParentCrudBean.openConnection();
		}
		
		pvFireEventInitialize(); 
		
		//Fecha conexão se tiver sido aberta acima
		if (xOpenConnection){
			wParentCrudBean.closeConnection();
		}
		return searchList();
	}

	/**
	 * Copia os valores dos campos para a memória para poderem ser colados em outro registro
	 * @return
	 */
	public synchronized String copy() throws DBSIOException{
		if (wAllowCopy 
		 || wAllowCopyOnUpdate){
			wCopiedRowIndex = wDAO.getCurrentRowIndex();
			pvFireEventAfterCopy();
		}
		return DBSFaces.getCurrentView();
	}


	/**
	 * Seta os valores atuais com os valores do registro copiado
	 * @throws DBSIOException 
	 */
	public synchronized String paste() throws DBSIOException{
		if (wAllowCopy || wAllowCopyOnUpdate){
			if (pvFireEventBeforePaste()){
				//Seta o registro atual como sendo o registro copiado
				wDAO.paste(wCopiedRowIndex);
				setValueChanged(true);
			}
		}
		return DBSFaces.getCurrentView();
	}

	/**
	 * Exibe todos os itens selecionados 
	 */
	public synchronized String viewSelection() throws DBSIOException{
		if (wFormStyle == FormStyle.TABLE){return DBSFaces.getCurrentView();}
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
		if (wFormStyle == FormStyle.TABLE){return DBSFaces.getCurrentView();}
		
		if (wFormStyle == FormStyle.DIALOG){
			//Limpa todas as mensagens que estiverem na fila
			clearMessages();
		}
		
		if (wConnection != null && wDAO.getCurrentRowIndex()!=-1){
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
		setDialogCloseAfterInsert(true);
		view();
		copy();
		insert();
		paste();
		wCopiedRowIndex = -1; //Reseta registro copiado para evitar a exibição do botão Paste.
		return DBSFaces.getCurrentView();
	}
	
	/**
	 * Método a ser sobre-escrito para implementar o sort.<br/>
	 * Para recuperar o coluna selecionada e a respectiva direção, deve-se utilizar <b>getSortColumn</b> e <b>getSortDirection</b>.<br/>
	 * Caso o sort seja efetuado na pesquisa principal implementada no método <b>beforeRefresh()</b>, basta incluir a chamada a ele. 
	 * @throws DBSIOException
	 */
	public synchronized void sort() throws DBSIOException{}

	/**
	 * Entra no modo de inclusão 
	 * @throws DBSIOException 
	 */
	public synchronized String insert() throws DBSIOException{
		if (wAllowInsert 
		 || wDialogCloseAfterInsert){
			if (!wDialogCloseAfterInsert){
				if (wFormStyle == FormStyle.DIALOG){
					clearMessages();
				}
			}
			//Inclui linha em branco quando edição for diretamente no grid e edição já estiver habilitada(EditingMode.UPDATING)
			if (wFormStyle == FormStyle.TABLE
			 && wEditingMode==EditingMode.UPDATING){
				pvInsertEmptyRow();
			}else{
				//Só permite a seleção do insert quando o dialog estiver fechado
				if (wEditingMode==EditingMode.NONE){
					try {
						if (pvFireEventBeforeInsert()
						 && pvFireEventBeforeEdit(EditingMode.INSERTING)){
							//Desmarca registros selecionados
							wSelectedRowsIndexes.clear(); 

							setEditingMode(EditingMode.INSERTING);
							
							setDialogOpened(true);
						}else{
							setValueChanged(false);
						}

//						if (pvFireEventBeforeEdit(EditingMode.INSERTING)){
//							//Desmarca registros selecionados
//							wSelectedRowsIndexes.clear(); 
//							setEditingMode(EditingMode.INSERTING);
//							
//							pvMoveBeforeFistRow();
//							
//							//Dispara evento BeforeInsert
//							if (pvFireEventBeforeInsert()){
//								setDialogOpened(true);
//							}
//						}else{
//							setValueChanged(false);
//							//exibe mensagem de erro de procedimento
//						}
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

	
	/**
	 * Move para o primeiro registro.
	 * @return
	 * @throws DBSIOException
	 */
	public synchronized String moveFirst() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.moveFirstRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}
	/**
	 * Move para o registro anterior
	 * @return
	 * @throws DBSIOException
	 */
	public synchronized String movePrevious() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.movePreviousRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}
	
	/**
	 * MNove para o próximo registro
	 * @return
	 * @throws DBSIOException
	 */
	public synchronized String moveNext() throws DBSIOException{
		if (wEditingStage==EditingStage.NONE){
			wDAO.moveNextRow();
			view();
		}
		return DBSFaces.getCurrentView();
	}
	
	/**
	 * Move para o último registro
	 * @return
	 * @throws DBSIOException
	 */
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
	 * Disparado quando a class é instanciada ou quando é efetuado um <b>refreshList()</b>.<br/>
	 * O método <b>searchList()</b> efetua a mesma atualização do <b>refreshList()</b> sem disparar o <b>initialize</b>.<br/>
	 * Este evento deve ser utilizado para configurações iniciais que possam depender de alguma alteração de filtro, por exemplo.<br/>
	 * Para configurações que não serão modificadas durante o crud, como o <b>dialogCaption</b>, 
	 * deve-se sobreescrever o evento <b>initializeClass</b> chamando o <b>super</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected abstract void initialize(DBSCrudBeanEvent pEvent) throws DBSIOException;


	/**
	 * Disparado antes da class ser finalizada.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void finalize(DBSCrudBeanEvent pEvent){};
	
	
	/**
	 * Disparado antes do form ser fechado.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void beforeClose(DBSCrudBeanEvent pEvent) throws DBSIOException {} ;

	/**
	 * Disparado antes de limpar os dados existentes e fazer uma nova pesquisa.<br/>
	 * A pesquisa principal dos dados que utiliza o wDAO, deverá ser implementada neste evento.<br/>
	 * Após este evento há uma tentativa de se posicionar no mesmo registro selecionado antes do deste <i>refresh</i> dos dados.
	 * Caso ele não exista, será considerado a primeira posição existente.<br/>
	 * Caso ele exista, os valores do registro corrente podem ser acessados no evento <b>afterRefresh</b>.
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	protected void beforeRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Disparado após efetuada uma nova pesquisa.<br/>
	 * Antes deste evento há uma tentativa de se posicionar no mesmo registro selecionado antes do <i>refresh</i> dos dados.
	 * Caso ele não exista, será considerado a primeira posição existente.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void afterRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException{};

	
	/**
	 * Disparado após o click do usuário no insert/delete/update e antes de iniciar o respectivo insert/delete/update
	 * Neste evento pode-se ignorar o click do usuário, evitado que continue o comando de insert/update/delete.
	 * Para isso, informe pEvent.setOk(false).<br> 
	 * Neste evento o editingMode do Crud ainda não foi configurado, portanto para saber qual a atividade(Inser/update/delete) foi selecionada
	 * deve-se consultar o atributo editingMode do Evento(ex:if (pEvent.getEditingMode() == EditingMode.INSERTING){}).<br/>
	 * Pode-se forçar a indicação que houve alteração de dados logo na iniciação da edição, 
	 * mesmo que ainda não tenha sido efetuada qualquer alteração pelo usuário, setando a propriedade setValueChanged para true.<br/>
	 * Para configurar os valores default dos campos no caso de uma inclusão, utilize o evento <b>beforeInsert</b> que ocorre antes do <>beforeEdit</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/> 
	 * @param pEvent Informações do evento
	 */
	protected void beforeEdit(DBSCrudBeanEvent pEvent) throws DBSIOException {};
	
	/**
	 * Disparado logo após a finalização da edição(insert/delete/update), independentemente da edição ter sido confirmada ou ignorada.<br/>
	 * Conexão com o banco encontra-se aberta, porém será fechado logo após a finalização deste evento.<br/>
	 * Para saber qual a atividade(Inser/update/delete) foi selecionada, 
	 * deve-se consultar o atributo editingMode do Evento(ex:if (pEvent.getEditingMode() == EditingMode.INSERTING){}).<br/>
	 * @param pEvent Informações do evento
	 */
	protected void afterEdit(DBSCrudBeanEvent pEvent) throws DBSIOException {};
	
	/**
	 * Disparado antes de iniciar um insert.<br/>
	 * Neste evento pode-se configurar os valores default dos campos.<br/>
	 * Para ignorar a inclusão, deve-se setar <b>setOk(False)</b>.<br/>
	 * Este evento ocorre antes do <b>beforeEdit</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void beforeInsert(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Disparado antes de exibir os dados em uma edição ou exclusão.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void beforeView(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Disparado depois de exibir os dados.<br/>
	 * Procure utilizar o evento <b>beforeView</b><br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	protected void afterView(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	/**
	 * Disparado depois do evento de validação, <b>validate</b>, e mensagem de confirmação.<br/>
	 * Após a mensagem de confirmação é disparado novamente o evento de validação para diminuir o risco de ter havido
	 * alguma alterações de dados no banco por agentes externos durante o tempo de espera da confirmação.<br/>
	 * A transação(Begintrans/Commit/Rollback) são controladas automaticamete. <br/>
	 * Será efetuado o <b>rollback</b> em caso de <b>exception</b> ou se o atributo <b>pEvent.setOk</b> do evento for <b>false</b>.<br>
	 * Neste evento, pode-se forçar o valor de alguma coluna utilizando o <b>setValue</b>, devendo-se, contudo, 
	 * chamar o <b>super.beforeCommit()</b> ao final caso o CRUD seja a tabela definida no próprio wDAO.<br> 
	 * Caso o CRUD não seja da tabela definida no wDAO, este método deverá ser sobreescrito
	 * para que seja implementado o CRUD específico e NÃO deverá ser chamado o <b>super.beforeCommit()</b>.<br>
	 * No caso do método ser sobre-escrito, é necessário setar o atributo <b>pEvent.setCommittedRowCount</b> com
	 * a quantidade de registros afetados.<br>
	 * Deverá também, neste caso, verificar a necessidade de implementação do <i>copy</i> e <i>paste</i>
	 * atráves do <i>Override</i> dos eventos <b>afterCopy</b> e <b>beforePaste</b> ou 
	 * desabilitar esta funcionalidade através do atributo <b>allowCopy</b>.<br/>
	 * <b>É ACONSELHÁVEL QUE QUALQUER <b>EDIÇÃO</b> DE DADOS DO BANCO SEJA IMPLEMENTADA NESTE EVENTO.</b><br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	protected void beforeCommit(DBSCrudBeanEvent pEvent) throws DBSIOException {
		//Copia dos valores pois podem ter sido alterados durante o beforecommit
		if (wConnection == null){return;}
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
					//Zera o valor da coluna que for PK e autoincremente para forçar que o número seja gerado automaticamente pelo banco
					if (wDAO.isAutoIncrementPK()){
						wDAO.setValue(wDAO.getPK(), null);
					}
					pEvent.setCommittedRowCount(wDAO.executeInsert());
				//Update
				}else if (getIsUpdating()){
					//Incluir registro se for edição diretamente do grid e for novo registro
					if (wFormStyle == FormStyle.TABLE
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
	
	
	/**
	 * Disparado depois de efetuado o CRUD com sucesso.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterCommit(DBSCrudBeanEvent pEvent) throws DBSIOException {}

	/**
	 * Disparado quando houver problema de validação ou não houver a confirmação do usuário para continuar.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void beforeIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException {}

	/**
	 * Disparado depois de ignorar o CRUD.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterIgnore(DBSCrudBeanEvent pEvent){}

	/**
	 * Disparado antes de efetuar a seleção da linha através do checkbox padrão do datatable. 
	 * Podendo, neste momento, inibir a seleção retornando <b>setOk(false)</b> do evento.<br/> 
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void beforeSelect(DBSCrudBeanEvent pEvent) throws DBSIOException {}

	/**
	 * Disparado depois da seleção de algum item.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterSelect(DBSCrudBeanEvent pEvent){}

	/**
	 * Disparado antes do evento <b>validate</b>.
	 * Evento indicado para se preencher alguma informação de regra de negócio em função dos valores que o usuário informou.
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void beforeValidate(DBSCrudBeanEvent pEvent) throws DBSIOException{}

	/**
	 * Disparado após indicar que deseja salvar os dados.<br/>
	 * Caso o atributo <b>revalidateBeforeCommit</b> seja <b>true</b> será disparado novamente após a confirmação da edição.
	 * Portanto, este evento poderá ser disparado duas vezes para diminuir o risco de ter havido
	 * alguma alterações de dados no banco por agentes externos durante o tempo de espera da confirmação da edição.
	 * Pode-se utilizar o <b>getEditingStage</b> para identificar após qual dos eventos o <b>validate</b> foi disparado, 
	 * onde <b>NONE</b> é após indicar que deseja salvar e <b>COMMITTING</b> após a confirmação da edição.<br/>
	 * Para indicar problemas na validação deve-se setar <b>pEvent.setOk(false)</b>.<br/>
	 * Neste método deve-se efetuar as validações das regras de negócios e gerar as mensagens de erro ou alerta, 
	 * caso necessario, via o comando <b>addMessage</b>.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void validate(DBSCrudBeanEvent pEvent) throws DBSIOException{}

	/**
	 * Disparado depois de copiada a posição do registro atual.<br/>
	 * Normalmente nos casos que foi feito <i>Override</i> do evento <b>beforeCommit</b> para
	 * implementação de CRUD específico, deverá também ser implementado o <i>copy</i> e <i>paste</i>
	 * atráves do <i>Override</i> dos eventos <b>afterCopy</b> e <b>beforePaste</b> ou 
	 * desabilitar esta funcionalidade através do atributo <b>allowCopy</b>.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void afterCopy(DBSCrudBeanEvent pEvent) throws DBSIOException{};

	/**
	 * Disparado depois de copiada a posição do registro atual.<br/>
	 * Normalmente nos casos que foi feito <i>Override</i> do evento <b>beforeCommit</b> para
	 * implementação de CRUD específico, deverá também ser implementado o <i>copy</i> e <i>paste</i>
	 * atráves do <i>Override</i> dos eventos <b>afterCopy</b> e <b>beforePaste</b> ou 
	 * desabilitar esta funcionalidade através do atributo <b>allowCopy</b>.<br/>
	 * Informar <b>setOk(false)</b> inibe a execução do <b>paste</b> padrão.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	protected void beforePaste(DBSCrudBeanEvent pEvent) throws DBSIOException{};
	
	// PRIVATE ============================================================================
	

	/**
	 * Inicializa a conversação
	 */
	private void pvConversationBegin(){
//		if (!FacesContext.getCurrentInstance().isPostback() && wConversation.isTransient()){
		if (wConversation.isTransient()){
			wConversation.begin();
			wConversation.setTimeout(wConversationTimeout);
		}
	}
	
	/**
	 * Atualiza dados da lista e dispara os eventos beforeRefresh e afterRefresh.<br/>
	 * @throws DBSIOException
	 */
	private void pvSearchList() throws DBSIOException{
		if (getEditingMode() == EditingMode.UPDATING){
			ignoreEditing(); 
		}
		//Dispara evento para atualizar os dados
		if (pvFireEventBeforeRefresh()){
			//Apaga itens selecionados, se houver.
			wSelectedRowsIndexes.clear();
			pvFireEventAfterRefresh();
		}
	}

	
	/**
	 * Finaliza a edição, 
	 * @param pOk Indica se a confirmação do estágio IGNORING ou COMMITING teve sucesso. 
	 * @throws DBSIOException
	 */
	private void pvEndEditing(Boolean pOk) throws DBSIOException{
		switch(wEditingMode){
			case UPDATING:
				if (wEditingStage==EditingStage.IGNORING){
					setEditingMode(EditingMode.NONE); 
					pvRestoreValuesOriginal();
					pvSearchList();
					view();
				}else{
					if (pOk){
						setEditingMode(EditingMode.NONE);
						view();
					}else{
						setEditingStage(EditingStage.NONE);
					}
				}
				break;
			case INSERTING:
				if (pOk){
					//Fecha form se for para ignorar a inclusão ou for uma inclusão a partir da seleção de um item do crudTable
					if (wEditingStage==EditingStage.IGNORING
					|| wDialogCloseAfterInsert){
						setEditingMode(EditingMode.NONE);
						close();
					}else{
						setEditingMode(EditingMode.NONE); 
						if (getFormStyle() == FormStyle.VIEW){
							view();
						}else{
							//Reinicia no processe de inclusão
							insert();
						}
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
	 * e for edição diretamente no grid<b></b>
	 * e estiver no modo de edição<b>(UPDATING)</b>.
	 * @throws DBSIOException 
	 */
	private void pvInsertEmptyRow() throws DBSIOException{
		if (wFormStyle != FormStyle.TABLE
		 || wEditingMode != EditingMode.UPDATING
		 || !wAllowInsert){
			return;
		}
		wDAO.insertEmptyRow();
		
		pvFireEventBeforeInsert();
	}

	/**
	 * Retorna a respectiva coluna do DAO a partir da propriedade value que foi utilizada no componente.<BR/>
	 * É necessário que campos fl's e fx's tenham o mesmo nome da coluna na tabela após os respectivos prefixos.
	 * 
	 * @param pInput
	 * @return
	 */
	private DBSColumn pvGetDAOColumnFromInputValueExpression(DBSUIInput pInput){
		String xColumnName = DBSFaces.getAttibuteNameFromInputValueExpression(pInput).toLowerCase();
		if (xColumnName!=null &&
			!xColumnName.equals("")){
			//Retira do os prefixos controlados pelo sistema para encontrar o nome da coluna
			if (xColumnName.startsWith(DBSSDK.UI.ID_PREFIX.FIELD_CRUD.getName())){
				xColumnName = xColumnName.substring(DBSSDK.UI.ID_PREFIX.FIELD_CRUD.getName().length());
			}else if (xColumnName.startsWith(DBSSDK.UI.ID_PREFIX.FIELD_AUX.getName())){
				xColumnName = xColumnName.substring(DBSSDK.UI.ID_PREFIX.FIELD_AUX.getName().length());
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
	
			//Se conexão for diferente da já existente no filho, força que a do filho seja fechada e atribui a nova
			if (!xCn.equals(pBean.getConnection())){
				DBSIO.closeConnection(xCn);
				
				//Força para que a conexão do crud filho seja a mesma do crud 'pai'
				xChildBean.setConnection(pBean.getConnection());
			}
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
			if (wFormStyle == FormStyle.TABLE){
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

	private void pvSetValueDAO(String pColumnName, Object pColumnValue){
		//Utiliza ListValue para controlar os valores de todas as linhas
		//Verifica se há alguma coluna corrente antes de setar o valor
		if (wDAO != null 
		 && (wDAO.getColumns().size() > 0
		  || wDAO.getCommandColumns().size() > 0)){
			Object xOldValue =  pvGetValue(pColumnName);
			if (pColumnValue != null){
				//Converte o valor antigo para o mesmo tipo do valor recebido para garantir a verificação correta se houve alteração de valores
				xOldValue = DBSObject.toClassValue(xOldValue, pColumnValue.getClass()); 
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


	/**
	 * Retorna a estágio de aprovação atual do registro corrente.<br/>
	 * @param pFromValue Indica ser recuperar o valor do value ou listValue(usado no grid).
	 * @return
	 */
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
		//Delete
		if(wConnection == null ||
		   getIsDeleting()){
			return;
		}
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
						addMessage(wMessaggeApprovalSameUser);
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
	 * Salva conteúdo da linha atual para posteriormente, após o refresh, procurar pela linha 
	 * que contenha os mesmos dados para selecionar como sendo o registro atual
	 */
	private void pvCurrentRowSave(){
		wSavedCurrentColumns = null;
		if (wDAO != null){
			wSavedCurrentColumns = wDAO.getCommandColumns();
			//Caso não tenha sido definida tabela que será efetuado a edição,
			//utiliza as colunas da query
			if (wDAO.getCommandColumns() == null
			 || wDAO.getCommandColumns().size() == 0){
				wSavedCurrentColumns = wDAO.getColumns();
			}
		}
	}
	/**
	 * Posiciona no mesmo registro que foi editado.<br/>
	 * O reposicionamento é efetuado pesquisando-se dentro dos registros existentes, 
	 * aquele que contém os dados salvos anteriormente no <b>pvCurrentRowSave</b>.
	 * @throws DBSIOException
	 */
	private void pvCurrentRowRestore() throws DBSIOException{
		boolean 	xOk;
		Object 		xSavedValue = null;
		Object 		xCurrentValue = null;
		BigDecimal  xSavedNumberValue = null;
		BigDecimal  xCurrentNumberValue = null;
		boolean 	xEqual;
		if (wDAO != null){
			//Posiciona do registro anterior ao primeiro para caracterizar que não existe registro selecionado
			wDAO.moveBeforeFirstRow();
			if (wSavedCurrentColumns !=null
			 && wSavedCurrentColumns.size() > 0
			 && wDAO.getResultDataModel() != null){
				//Loop por todas as linhas da query para procurar pela que é igual a linha salva
				DBSResultDataModel xQueryRows =  wDAO.getResultDataModel();
				for (int xRowIndex = 0; xRowIndex <= xQueryRows.getRowCount()-1; xRowIndex++){
					xQueryRows.setRowIndex(xRowIndex);
					xOk = true;
					//Loop por todas as colunas da linha da query
					for (String xQueryColumnName:xQueryRows.getRowData().keySet()){
						Object xQueryColumnValue = xQueryRows.getRowData().get(xQueryColumnName);
						//Loop por todas as colunas salvas para pesquisar o conteúdo
						//Procura pelo coluna que possua o mesmo nome
						for (DBSColumn xColumnSaved: wSavedCurrentColumns){
							if (xColumnSaved.getColumnName().equalsIgnoreCase(xQueryColumnName)){
								//Verifica se valor é igual ao valor salvo 
								xSavedValue = DBSObject.getNotNull(xColumnSaved.getValue(),"");
								xCurrentValue = DBSObject.getNotNull(xQueryColumnValue,""); 
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
						return;
					}
				}
				if (wDAO != null){
					//Posiciona na primeira linha se não consegui encontrar o registro restaurado.
					wDAO.moveFirstRow();
				}			
			}
		}
	}
	
//	/**
//	 * Posiciona no mesmo registro que foi editado.<br/>
//	 * O reposicionamento é efetuado pesquisando-se dentro dos registros existentes, 
//	 * aquele que contém os dados salvos anteriormente no <b>pvCurrentRowSave</b>.
//	 * @throws DBSIOException
//	 */
//	private void pvCurrentRowRestore() throws DBSIOException{
//		boolean xOk;
//		Integer	xRowIndex;
//		Object xSavedValue = null;
//		Object xCurrentValue = null;
//		BigDecimal xSavedNumberValue = null;
//		BigDecimal xCurrentNumberValue = null;
//		boolean xEqual;
//		if (wDAO != null
//		 && wSavedCurrentColumns !=null
//		 && wSavedCurrentColumns.size() > 0
//		 && wDAO.getResultDataModel() != null){
//			//Recupera todas as linhas
//			Iterator<SortedMap<String, Object>> xIR = wDAO.getResultDataModel().iterator(); 
//			xRowIndex = -1;
//			//Loop por todas as linhas para procurar pela que é igual a linha salva
//			while (xIR.hasNext()){ 
//				xOk = true;
//				xRowIndex++; 
//				//Recupera todas as colunas da linha
//				SortedMap<String, Object> xColumns = xIR.next();
//				//Loop por todas as colunas da linha
//				for (Entry<String, Object> xC:xColumns.entrySet()){
//					Iterator<DBSColumn> xIS = wSavedCurrentColumns.iterator(); 
//					//Loop por todas as colunas salvas para pesquisar o conteúdo
//					//Procura pelo coluna que possua o mesmo nome
//					while (xIS.hasNext()){
//						DBSColumn xSC = xIS.next();
//						//Verifica se a coluna com o mesmo nome, possui o mesmo conteúdo.
//						if (xSC.getColumnName().equalsIgnoreCase(xC.getKey())){
//							//Verifica se valor é igual ao valor salvo 
//							xSavedValue = DBSObject.getNotNull(xSC.getValue(),"");
//							xCurrentValue = DBSObject.getNotNull(xC.getValue(),""); 
//							xEqual = false;
//							if (xCurrentValue == null
//							 && xSavedValue == null){
//								xEqual = true;
//							}else if (xCurrentValue instanceof Number){
//								xCurrentNumberValue = DBSNumber.toBigDecimal(xCurrentValue);
//								if (xSavedValue instanceof Number){
//									xSavedNumberValue = DBSNumber.toBigDecimal(xSavedValue);
//								}
//								if (xSavedNumberValue != null 
//								 && xCurrentNumberValue != null){
//									//Utiliza o compareTo para evitar diferença por quantidade de casas decimais
//									if (xCurrentNumberValue.compareTo(xSavedNumberValue) == 0){
//										xEqual = true;
//									}
//								}
//							}else{
//								xEqual = xSavedValue.equals(xCurrentValue);
//							}
//							if (!xEqual){
//								//Indica que este registro não é igual ao valor salvo
//								xOk = false;
//							}
//							break;
//						}
//					}
//					if (!xOk){
//						//Sai para procurar a próxima linha
//						break;
//					}
//				}
//				if (xOk){
//					wDAO.setCurrentRowIndex(xRowIndex);
//					return;
//				}
//			}
////			//Se for crud principal e não encontrou o registro anterior
////			if (wParentCrudBean == null){
////				addMessage(MESSAGE_TYPE.IMPORTANT, "Foi selecionado o primeiro registro, por não ter sido encontrato o registro anterior.");
////			}
//		}
//		if (wDAO != null){
//			//Posiciona na primeira linha se não consegui encontrar o registro restaurado.
//			wDAO.moveFirstRow();
//		}
//	}



	//Events -----------------------------------------------------
	private void pvFireEventInitialize(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.INITIALIZE, getEditingMode());
		try {
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventInitialize",e);
		}
	}
	
	private void pvFireEventFinalize(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.FINALIZE, getEditingMode());
		try {
			pvBroadcastEvent(xE, false, false, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventFinalize",e);
		}
	}

	private boolean pvFireEventBeforeClose(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_CLOSE, getEditingMode());
		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeClose",e);
		}
		return xE.isOk();
	}

	private boolean pvFireEventBeforeView(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_VIEW, getEditingMode());
		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeView",e);
		}
		return xE.isOk();
	}
	
	private void pvFireEventAfterView(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_VIEW, getEditingMode());
		//Marca que não houve edição nos campos, para que as configurações iniciais nos campos efetuadas no BeforeView sejam aceita sem ficarem caracterizadas como edição do usuário. 
		setValueChanged(false); 

		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterView",e);
		}
	}

	private boolean pvFireEventBeforeInsert() throws DBSIOException{
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_INSERT, getEditingMode());
		
		if (wDAO != null){
			openConnection();
			
			//Seta para posição inicial onde será efetuado o insert
			pvMoveBeforeFistRow();
			
			closeConnection();
		}

//		pvBeforeInsertResetValues(wCrudForm);

		try {
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeInsert",e);
		}
		
		setValueChanged(false); 

		return xE.isOk();
	}

	/**
	 * Disparado logo após o click do usuário no insert/delete/update/approve/reprove e antes de iniciar o respectivo insert/delete/update/approve/reprove.
	 * Podendo, portanto, cancelar o inicio da edição, informando setOk(False) no evento.<br/>
	 * Neste evento a conexão será aberta e posteriormente fechada no evento AfterEdit(ou em caso de erro ou o usário fechou dentro do evento)
	 * @return
	 */
	private boolean pvFireEventBeforeEdit(EditingMode pEditingMode){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_EDIT, pEditingMode);
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
	 * Disparado logo após a finalização da edição(insert/delete/update/approve/reprove), 
	 * independentemente da edição ter sido confirmada ou ignorada.
	 * Fecha a conexão com o banco.</br>
	 * @return
	 */
	private boolean pvFireEventAfterEdit(EditingMode pEditingMode){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_EDIT, pEditingMode);
		try{
			pvBroadcastEvent(xE, false, false, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterEdir",e);
		}
		return xE.isOk();
	}

	private boolean pvFireEventBeforeRefresh(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_REFRESH, getEditingMode());
		try{
			pvBroadcastEvent(xE, true, true, true);
		} catch (Exception e) { 
			xE.setOk(false);
			wLogger.error("EventBeforeRefresh",e);
		}
		return xE.isOk();
	}
	
	private void pvFireEventAfterRefresh(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_REFRESH, getEditingMode());
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
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_IGNORE, getEditingMode());
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
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_IGNORE, getEditingMode());
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
	private boolean pvFireEventBeforeValidate(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_VALIDATE, getEditingMode());
		try{
			/*Faz loop entre todos os registros selecionados quando for:
			 * .Aprovação e reprovação
			 * .Edição sem dialog
			*/
			if (getIsApprovingOrReproving() 
			|| wFormStyle == FormStyle.TABLE){
				if (getHasSelected()){
					wDAO.setCurrentRowIndex(-1);
					for (Integer xRowIndex : wSelectedRowsIndexes){
						wDAO.setCurrentRowIndex(xRowIndex);
						pvBroadcastEvent(xE, false, false, false);
					}
				}else{
					xE.setOk(false);
					addMessage("erroselecao", MESSAGE_TYPE.ERROR,  DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.notSelected"));
				}
			}else{
				pvBroadcastEvent(xE, false, false, false);
			}		
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeValidate",e);
		}
		return xE.isOk();
	}

	/**
	 * Ocorre após a indicação que se deseja salvar(commit) e antes de trocar para o estágio 
	 * de confirmação do commit
	 * @return
	 */
	private boolean pvFireEventValidate(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.VALIDATE, getEditingMode());
		try{
			/*Faz loop entre todos os registros selecionados quando for:
			 * .Aprovação e reprovação
			 * .Edição sem dialog
			*/
			if (getIsApprovingOrReproving() 
			|| wFormStyle == FormStyle.TABLE){
				if (getHasSelected()){
					wDAO.setCurrentRowIndex(-1);
					for (Integer xRowIndex : wSelectedRowsIndexes){
						wDAO.setCurrentRowIndex(xRowIndex);
						pvBroadcastEvent(xE, false, false, false);
						if (!xE.isOk()){
							if (getIsApprovingOrReproving()){
								addMessage("erroassinatura", MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.approvalAll"));
								break;
							}else{
								addMessage("erroselecao", MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.editAll"));
								break;
							}
						}
					}
				}else{
					xE.setOk(false);
					addMessage("erroselecao", MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.notSelected"));
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

	private boolean pvFireEventBeforeCommit(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_COMMIT, getEditingMode());
		String xErrorMsg = null;
		//Chame o metodo(evento) local para quando esta classe for extendida
		try {
			//Zera a quantidade de registros afetados
			xE.setCommittedRowCount(0);
			//Se não houver conexão(Pode ser for form view simples(sem crud))
			if (wConnection != null){
				//Certifica-se que a conexão do wDAO é a conexão do bean
				wDAO.setConnection(wConnection);

				//Se for o crud principal
				//Inicia transação
				if (wParentCrudBean == null){
					DBSIO.beginTrans(wConnection);
				}
			}
			
//Na aprovação ou reprovação, faz oop entre todos os registros selecionados
//			if (getIsApprovingOrReproving()){ Comentado em 10/04/2014 para possibilitar o loop em qualquer situação que haja mais de um registro selecionado

			/*Faz loop entre todos os registros selecionados quando for:
			 * .Aprovação e reprovação
			 * .Edição sem dialog
			*/
			if (getIsApprovingOrReproving() 
			|| wFormStyle == FormStyle.TABLE){
				if (getHasSelected()){
					int xCount = 0;
					wDAO.setCurrentRowIndex(-1);
					for (Integer xRowIndex : wSelectedRowsIndexes){
						wDAO.setCurrentRowIndex(xRowIndex);
						if (wFormStyle == FormStyle.TABLE){
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
						addMessage("erroassinatura", MESSAGE_TYPE.ERROR, DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.approvelAll"));
					}else{
						xE.setCommittedRowCount(xCount);
					}
				}
			}else{
				pvBroadcastEvent(xE, false, false, false);
			}


			//Exibe mensagem de erro padrão, caso nehum registro tenha sido afetado e já não houver mensagem a ser exibida.
			if (!wDialogMessages.hasMessages()
			 && (!xE.isOk() || (wConnection != null && xE.getCommittedRowCount().equals(0)))){
				xE.setOk(false);
				addMessage(wMessageNoRowComitted);
			}
			
			//Se for o crud principal
			//Da commit na transação
			//Se não houver conexão(Pode ser for form view simples(sem crud))
			if (wConnection != null){
				if (wParentCrudBean == null){
					DBSIO.endTrans(wConnection, xE.isOk());
				}
			}
		} catch (Exception e) {
			xE.setOk(false);
			try {
				//Se for o crud principal
				//da rollback na transação
				//Se não houver conexão(Pode ser for form view simples(sem crud))
				if (wConnection != null){
					if (wParentCrudBean == null){
						DBSIO.endTrans(wConnection, false);
					}
				}
				if (e instanceof DBSIOException){
					DBSIOException xDBException = (DBSIOException) e;
					xErrorMsg = e.getMessage(); 
					if (xDBException.isIntegrityConstraint()){
						clearMessages(); //Limpa mensagem padrão
//						addMessage("integridate", MESSAGE_TYPE.ERROR, xDBException.getLocalizedMessage());
					}else{
						wLogger.error("EventBeforeCommit", e);
					}
					wMessageError.setMessageText(xDBException.getLocalizedMessage());
					wMessageError.setMessageTooltip(xErrorMsg);
					addMessage(wMessageError);
				}else{
					wMessageError.setMessageText(DBSFaces.getBundlePropertyValue("dbsfaces", "crudbean.msg.support"));
					wMessageError.setMessageTooltip(e.getLocalizedMessage());
					addMessage(wMessageError);
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
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_COMMIT, getEditingMode());
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
	 * Disparado antes de selecionar um item no checkbox do datatable. Podendo, neste momento, inibir a seleção
	 * @return
	 */
	private boolean pvFireEventBeforeSelect(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_SELECT, getEditingMode());
		try{
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforeIgnore",e);
		}
		return xE.isOk();
	}
	
	private void pvFireEventAfterSelect(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_SELECT, getEditingMode());
		try{
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterIgnore",e);
		}
	}
	
	/**
	 * Disparado após o copy.
	 * @return
	 */
	private void pvFireEventAfterCopy(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.AFTER_COPY, getEditingMode());
		try{
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventAfterCopy",e);
		}
	}
	
	/**
	 * Disparado antes do paste.
	 * @return
	 */
	private boolean pvFireEventBeforePaste(){
		DBSCrudBeanEvent xE = new DBSCrudBeanEvent(this, CRUD_EVENT.BEFORE_PASTE, getEditingMode());
		try{
			pvBroadcastEvent(xE, false, true, true);
		} catch (Exception e) {
			xE.setOk(false);
			wLogger.error("EventBeforePaste",e);
		}
		return xE.isOk();
	}
	
	
	/**
	 * Disparado os eventos localmente, nos filhos e nos listerners que eventualmente possam existir.
	 * @param pEvent
	 * @param pInvokeChildren Se chama os filhos
	 * @param pOpenConnection Se abre a conexão antes de disparar os eventos
	 * @param pCloseConnection Se fecha a conexão após disparar os eventos
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
			case BEFORE_VALIDATE:
				beforeValidate(pEvent);
				break;
			case VALIDATE:
				validate(pEvent);
				break;
			case AFTER_COPY:
				afterCopy(pEvent);
				break;
			case BEFORE_PASTE:
				beforePaste(pEvent);
				break;
			default:
				break;
			}
		}		
	}

	/**
	 * Dispara o evento nos beans vinculados a este bean 
	 * @param pEvent
	 * @throws DBSIOException
	 */
	private void pvFireEventChildren(DBSCrudBeanEvent pEvent) throws DBSIOException{
		if (pEvent.isOk()){
			//Busca por beans vinculados e este bean
			for (DBSCrudBean xBean:wChildrenCrudBean){
				/* O refreshList dos CrudBean filhos serão disparados caso 
				 * o beforeView e before_insert deste crudBean seja disparado.<br>
				 * Permitindo que os cruds filhos atualizem seus dados em função da posição atual deste crud.
				 */
				if (pEvent.getEvent() == CRUD_EVENT.BEFORE_VIEW
				 || pEvent.getEvent() == CRUD_EVENT.BEFORE_INSERT){
					//Atualiza conteúdo do filho
					xBean.searchList();
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
				case BEFORE_VALIDATE:
					xBean.beforeValidate(pEvent);
					break;
				case VALIDATE:
					xBean.validate(pEvent);
					break;
				case AFTER_COPY:
					xBean.afterCopy(pEvent);
					break;
				case BEFORE_PASTE:
					xBean.beforePaste(pEvent);
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
				case BEFORE_VALIDATE:
					wEventListeners.get(xX).beforeValidate(pEvent);
					break;
				case VALIDATE:
					wEventListeners.get(xX).validate(pEvent);
					break;
				case AFTER_COPY:
					wEventListeners.get(xX).afterCopy(pEvent);
					break;
				case BEFORE_PASTE:
					wEventListeners.get(xX).beforePaste(pEvent);
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
