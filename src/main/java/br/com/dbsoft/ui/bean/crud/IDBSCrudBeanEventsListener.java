package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.error.DBSIOException;

public interface IDBSCrudBeanEventsListener {

	/**
	 * Disparado quando a class é instanciada ou quando é efetuado um <b>refreshList()</b>.<br/>
	 * O método <b>searchList()</b> efetua a mesma atualização do <b>refreshList()</b> sem disparar o <b>initialize</b>.<br/>
	 * Este evento deve ser utilizado para configurações iniciais que possam depender de alguma alteração de filtro, por exemplo.<br/>
	 * Para configurações que não serão modificadas durante o crud, como o <b>dialogCaption</b>, 
	 * deve-se sobreescrever o evento <b>initializeClass</b> chamando o <b>super</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */	
	public abstract void initialize(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	/**
	 * Disparado antes da class ser finalizada.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void finalize(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado antes do crudform ser fechado.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeClose(DBSCrudBeanEvent pEvent) throws DBSIOException;


	/**
	 * Disparado antes de limpar os dados existentes e fazer uma nova pesquisa.<br/>
	 * A pesquisa principal dos dados que utiliza o wDAO, deverá ser implementada neste evento.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	public abstract void beforeRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado após efetuada uma nova pesquisa.
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void afterRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado antes de iniciar um insert.<br/>
	 * Neste evento pode-se configurar os valores default dos campos.<br/>
	 * Para ignorar a inclusão, deve-se setar <b>setOk(False)</b>.<br/>
	 * Este evento ocorre antes do <b>beforeEdit</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeInsert(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado antes de exibir os dados em uma edição ou exclusão.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeView(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado depois de exibir os dados.<br/>
	 * Procure utilizar o evento <b>beforeView</b><br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void afterView(DBSCrudBeanEvent pEvent) throws DBSIOException;

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
	 * No caso do método ser sobreescrito, é necessário setar o atributo <b>pEvent.setCommittedRowCount</b> com
	 * a quantidade de registros afetados.<br>
	 * Deverá também, neste caso, verificar a necessidade de implementação do <i>copy</i> e <i>paste</i>
	 * atráves do <i>Override</i> dos eventos <b>afterCopy</b> e <b>beforePaste</b> ou 
	 * desabilitar esta funcionalidade através do atributo <b>allowCopy</b>.<br/>
	 * <b>É ACONSELHÁVEL QUE QUALQUER EDIÇÃO DE DADOS DO BANCO DEVERÁ SEJA IMPLEMENTADA NESTE EVENTO.</b><br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	public abstract void beforeCommit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado depois de efetuado o CRUD com sucesso.<br/>
	 * Ocorre antes de voltar ao modo sem edição(EditingMode.NONE).<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterCommit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado quando houver problema de validação ou não houver a confirmação do usuário para continuar.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado depois de ignorar o CRUD.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	/**
	 * Disparado antes do evento <b>validate</b>.
	 * Evento indicado para se preencher alguma informação de regra de negócio em função dos valores que o usuário informou.
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeValidate(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado após indicar que deseja salvar os dados.<br/>
	 * Caso o atributo <b>revalidateBeforeCommit</b> seja <b>true<b/> será disparado novamente após a confirmação da edição.
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
	public abstract void validate(DBSCrudBeanEvent pEvent) throws DBSIOException;

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
	public abstract void beforeEdit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado logo após a finalização da edição(insert/delete/update), independentemente da edição ter sido confirmada ou ignorada.<br/>
	 * Conexão com o banco encontra-se aberta, porém será fechado logo após a finalização deste evento.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void afterEdit(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	
	/**
	 * Disparado antes de efetuar a seleção da linha através do checkbox padrão do datatable. 
	 * Podendo, neste momento, inibir a seleção retornando <b>setOk(false)</b> do evento.<br/> 
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeSelect(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Disparado depois da seleção de algum item.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterSelect(DBSCrudBeanEvent pEvent) throws DBSIOException;

	
	/**
	 * Disparado depois de copiada a posição do registro atual.<br/>
	 * Normalmente nos casos que foi feito <i>Override</i> do evento <b>beforeCommit</b> para
	 * implementação de CRUD específico, deverá também ser implementado o <i>copy</i> e <i>paste</i>
	 * atráves do <i>Override</i> dos eventos <b>afterCopy</b> e <b>beforePaste</b> ou 
	 * desabilitar esta funcionalidade através do atributo <b>allowCopy</b>.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterCopy(DBSCrudBeanEvent pEvent) throws DBSIOException;

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
	public abstract void beforePaste(DBSCrudBeanEvent pEvent) throws DBSIOException;
}
