package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.error.DBSIOException;

public interface IDBSCrudBeanEventsListener {

	/**
	 * Chamado quando a class é instanciada.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void initialize(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	/**
	 * Chamado antes da class ser finalizada.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void finalize(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado antes de limpar os dados existentes e fazer uma nova pesquisa.<br/>
	 * A pesquisa principal dos dados que utiliza o wDAO, deverá ser efetuada neste evento.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 * @throws DBSIOException 
	 */
	public abstract void beforeRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado após efetuada uma nova pesquisa.
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void afterRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado antes de iniciar um insert.<br/>
	 * Neste evento pode-se configura os valores default dos campos.<br/>
	 * Para ignorar a inclusão, deve-se setar setOk(False).<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeInsert(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado antes de exibir os dados em uma edição ou exclusão.<br/>
  	 * Durante este evento para saber o modo de edição, deve-se consultar o <b>pEvent.getEditingMode()</b>.<br/>
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeView(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado depois depois de exibir os dados.<br/>
	 * Procure utilizar o evento <b>beforeView</b><br/>
	 * Neste evento pode-se, também, configurar os valores default dos campos no caso de uma inclusão.
	 * Conexão com o banco encontra-se aberta.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void afterView(DBSCrudBeanEvent pEvent) throws DBSIOException;

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
	public abstract void beforeCommit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado depois de efetuado o CRUD com sucesso.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterCommit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado quando houve problema de validação ou não houver a confirmação do usuário para continuar.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado depois de ignorar o CRUD.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException;
	

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
	public abstract void validate(DBSCrudBeanEvent pEvent) throws DBSIOException;

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
	public abstract void beforeEdit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado logo após a finalização da edição(insert/delete/update), independentemente da edição ter sido confirmada ou ignorada.<br/>
	 * Conexão com o banco encontra-se aberta, porém será fechado logo após a finalização deste evento.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void afterEdit(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	
	/**
	 * Chamado antes de efetuar a seleção da linha através do ckeckbox padrão do datatable. Podendo, neste momento, inibir a seleção retornando setOk(false) do evento.<br/> 
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeSelect(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado depois da seleção de algum item.<br/>
	 * Conexão com o banco encontra-se aberta.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterSelect(DBSCrudBeanEvent pEvent) throws DBSIOException;

	
	/**
	 * Chamado antes do crudform ser fechado.<br/>
	 * Conexão com o banco já se encontra fechada.<br/>
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeClose(DBSCrudBeanEvent pEvent) throws DBSIOException;



}
