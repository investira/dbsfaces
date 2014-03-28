package br.com.dbsoft.ui.bean.report;

public interface IDBSReportBeanEventsListener {
	/**
	 * Chamado antes de limpar os dados existentes e fazer uma nova pesquisa
	 * @param pEvent Informações do evento
	 */
	public abstract void initialize(DBSReportBeanEvent pEvent);

	/**
	 * Chamado depois após os dados limpos.
	 * A pesquisa principal dos dados deverá ser efetuado neste evento
	 * @param pEvent Informações do evento
	 */
	public abstract void finalize(DBSReportBeanEvent pEvent);

	/**
	 * Chamado depois após os dados limpos.
	 * A pesquisa principal dos dados deverá ser efetuado neste evento
	 * @param pEvent Informações do evento
	 */
	public abstract void validate(DBSReportBeanEvent pEvent);

}
