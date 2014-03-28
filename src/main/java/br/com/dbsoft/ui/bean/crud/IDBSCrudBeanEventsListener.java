package br.com.dbsoft.ui.bean.crud;

import br.com.dbsoft.error.DBSIOException;

public interface IDBSCrudBeanEventsListener {
	/**
	 * Chamado antes de limpar os dados existentes e fazer uma nova pesquisa
	 * @param pEvent Informações do evento
	 */
	public abstract void initialize(DBSCrudBeanEvent pEvent) throws DBSIOException;
	/**
	 * Chamado depois após os dados limpos.
	 * A pesquisa principal dos dados deverá ser efetuado neste evento
	 * @param pEvent Informações do evento
	 */
	public abstract void finalize(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado antes de limpar os dados existentes e fazer uma nova pesquisa
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException;
	/**
	 * Chamado depois após os dados limpos.
	 * A pesquisa principal dos dados deverá ser efetuado neste evento
	 * @param pEvent Informações do evento
	 */
	public abstract void afterRefresh(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	/**
	 * Chamado antes de exibir os dados
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeView(DBSCrudBeanEvent pEvent) throws DBSIOException;
	/**
	 * Chamado depois depois de exibir os dados
	 * @param pEvent Informações do evento
	 */
	public abstract void afterView(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado antes de confirmar o comando de insert/delete/update
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeCommit(DBSCrudBeanEvent pEvent) throws DBSIOException;
	/**
	 * Chamado depois de confirmar o comando de insert/delete/update 
	 * @param pEvent Informações do evento
	 */
	public abstract void afterCommit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado antes de confirmar o comando de ignorar
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException;
	/**
	 * Chamado depois de confirmar o comando de ignorar
	 * @param pEvent Informações do evento
	 */
	public abstract void afterIgnore(DBSCrudBeanEvent pEvent) throws DBSIOException;
	

	/**
	 * Chamado depois depois de exibir os dados
	 * @param pEvent Informações do evento
	 */
	public abstract void validate(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado logo após o click do usuário no insert/delete/update e antes de iniciar o respectivo insert/delete/update
	 * Neste evento podesse ignorar o click do usuário, evitado que continue o comando
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeEdit(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado logo após a finalização da edição, independentemente da edição ter sido confirmada ou ignorada.
	 * @param pEvent Informações do evento
	 */
	public abstract void afterEdit(DBSCrudBeanEvent pEvent) throws DBSIOException;
	
	
	/**
	 * Chamado antes de efetuar a seleção da linha através do ckeckbox padrão do datatable. Podendo, neste momento, inibir a seleção retornando setOk(false) do evento 
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeSelect(DBSCrudBeanEvent pEvent) throws DBSIOException;

	/**
	 * Chamado depois da seleção de algum item
	 * @param pEvent Informações do evento
	 */
	public abstract void afterSelect(DBSCrudBeanEvent pEvent) throws DBSIOException;

	
	/**
	 * Chamado antes do form ser fechado
	 * @param pEvent Informações do evento
	 */
	public abstract void beforeClose(DBSCrudBeanEvent pEvent) throws DBSIOException;

//
//	/**
//	 * Chamado antes de exibir os dados
//	 * @param pEvent Informações do evento
//	 */
//	public abstract void beforeEdit(DBSCrudEvent pEvent);
//	/**
//	 * Chamado depois depois de exibir os dados
//	 * @param pEvent Informações do evento
//	 */
//	public abstract void afterEdit(DBSCrudEvent pEvent);
//
//	/**
//	 * Chamado antes de exibir os dados
//	 * @param pEvent Informações do evento
//	 */
//	public abstract void beforeDelete(DBSCrudEvent pEvent);
//	/**
//	 * Chamado depois depois de exibir os dados
//	 * @param pEvent Informações do evento
//	 */
//	public abstract void afterDelete(DBSCrudEvent pEvent);

}
