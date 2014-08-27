package br.com.dbsoft.ui.component.inputtext;


import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.io.DBSResultDataModel;


public interface IDBSInputTextSuggestions {

	/**
	 * Retorna a lista contendo a relação de itens que inicia com o texto digitado no campo.
	 * @return
	 */
	public abstract DBSResultDataModel getList();

	/**
	 * Atualiza o conteúdo da lista a partir do valor digitado
	 * @param pString Valor digitado
	 */
	public abstract void refreshList(String pString) throws DBSIOException;
	
	/**
	 * Retorna o valor a ser exibido a partir do valor da chave informada.<br/>
	 * O valor retornador deverá originar da mesma coluna informada em suggestionDisplayColumnName.
	 * @param pKeyValue
	 * @return
	 * @throws DBSIOException 
	 */
	public abstract String setDisplayValue(String pKeyValue) throws DBSIOException;

	/**
	 * Retorna o valor a ser exibido a partir do valor da chave informada.<br/>
	 * O valor retornador deverá originar da mesma coluna informada em suggestionDisplayColumnName.
	 * @param pKeyValue
	 * @return
	 * @throws DBSIOException 
	 */
	public abstract String getDisplayValue() throws DBSIOException;
}
