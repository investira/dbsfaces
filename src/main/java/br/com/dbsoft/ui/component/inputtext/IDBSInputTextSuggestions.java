package br.com.dbsoft.ui.component.inputtext;


import br.com.dbsoft.error.DBSIOException;
import br.com.dbsoft.io.DBSResultDataModel;


public interface IDBSInputTextSuggestions {

	/**
	 * Retorna a lista contendo a relação de itens que inicial com o texto digitado no campo
	 * O primeiro campo do Entry é a chave e o segundo o conteúdo, podendo ser qualquer object
	 * @return
	 */
	public abstract DBSResultDataModel getList();

	/**
	 * Atualiza o conteúdo da lista a partir do valor digitado
	 * @param pString Valor digitado
	 */
	public abstract void refreshList(String pString) throws DBSIOException;
	
	/**
	 * Retorna o valor a ser exibido a partir do valor da chave informada 
	 * @param pKeyValue
	 * @return
	 * @throws DBSIOException 
	 */
	public abstract String getDisplayValue(String pKeyValue) throws DBSIOException;

}
