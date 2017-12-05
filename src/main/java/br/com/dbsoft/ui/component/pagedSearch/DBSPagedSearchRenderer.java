package br.com.dbsoft.ui.component.pagedSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.div.DBSDiv;
import br.com.dbsoft.ui.component.inputtext.DBSInputText;
import br.com.dbsoft.ui.core.DBSAjaxBehaviorListener;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSPagedSearch.RENDERER_TYPE)
public class DBSPagedSearchRenderer extends DBSRenderer {
	
	//MÉTODOS OVERRIDE ===========================================================================
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		
		DBSPagedSearch xPagedSearch = (DBSPagedSearch) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xPagedSearch.getClientId(pContext);
		String xClass = CSS.PAGEDSEARCH.MAIN;
		if (xPagedSearch.getStyleClass()!=null){
			xClass += xPagedSearch.getStyleClass();
		}
		
		//Verifica se tem o mínimo pra cada tipo
		if (DBSObject.isEqual(xPagedSearch.getType(), "select") 
		 || DBSObject.isEqual(xPagedSearch.getType(), "suggestion")) {
			if (DBSObject.isEmpty(xPagedSearch.getController())
			 || DBSObject.isEmpty(xPagedSearch.getVar())
			 || DBSObject.isEmpty(xPagedSearch.getDisplayValueColumnName())){
				return;
			}
		} else if (DBSObject.isEqual(xPagedSearch.getType(), "search")) {
			if (DBSObject.isEmpty(xPagedSearch.getController())
			 || DBSObject.isEmpty(xPagedSearch.getVar())){
				return;
			}
		} else if (DBSObject.isEqual(xPagedSearch.getType(), "normal")) {
			pvEncodePesquisa(pContext, xPagedSearch, xWriter);
			return;
		}
		
		//DIV PRINCIPAL
		xWriter.startElement("div", xPagedSearch);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xPagedSearch.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xPagedSearch.getType());
			//Container
			xWriter.startElement("div", xPagedSearch);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.THEME.FLEX + CSS.MODIFIER.CENTER);

				//INPUT COM O INDEX DO ITEM SELECIONADO
				if (DBSObject.isEqual(xPagedSearch.getType(), "select")) {
					pvEncodeInputSelectedRow(pContext, xPagedSearch);
					pvEncodeSelectButton(pContext, xPagedSearch, xWriter);
				}
				
				//DIV DE PESQUISA
				pvEncodePesquisa(pContext, xPagedSearch, xWriter);
				
				if (DBSObject.isEqual(xPagedSearch.getType(), "search")
				 || DBSObject.isEqual(xPagedSearch.getType(), "suggestion")
				 || DBSObject.isEqual(xPagedSearch.getType(), "select")) {
					//DIV DE RESULTADOS
					pvEncodeResultados(pContext, xPagedSearch, xWriter);
				}
			xWriter.endElement("div");
			pvEncodeJS(xPagedSearch, xWriter);
		xWriter.endElement("div");
	}
	
	//MÉTODOS PRIVADOS ===========================================================================
	/**
	 * Cria o Input escondido com o index do item escolhido na lista. 
	 * @param pPagedSearch
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeInputSelectedRow(FacesContext pContext, DBSPagedSearch pPagedSearch) throws IOException {
		DBSInputText xInputSelectedRow = (DBSInputText) pPagedSearch.getFacet(DBSPagedSearch.ID_SELECT_ROW);
		if (DBSObject.isNull(xInputSelectedRow)) {
			xInputSelectedRow = (DBSInputText) pContext.getApplication().createComponent(DBSInputText.COMPONENT_TYPE);
			xInputSelectedRow.setId(DBSPagedSearch.ID_SELECT_ROW);
			xInputSelectedRow.setType("hidden");
			xInputSelectedRow.setStyleClass(CSS.PAGEDSEARCH.SELECT_ROW);
			pPagedSearch.getFacets().put(DBSPagedSearch.ID_SELECT_ROW, xInputSelectedRow);
		}
		xInputSelectedRow.setValue(pPagedSearch.getController().getSelectedRow());
		xInputSelectedRow.setValueExpression("value", DBSFaces.createValueExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.controller.name()).getExpressionString() +".selectedRow", String.class));
		xInputSelectedRow.encodeAll(pContext);
	}
	
	/**
	 * Cria o Botão que configura o objeto selecionado (setSelectedItem) no DBSPagedSearchController.
	 * @param pContext 
	 * @param pPagedSearch
	 * @param pWriter
	 * @throws IOException 
	 */
	private void pvEncodeSelectButton(FacesContext pContext, DBSPagedSearch pPagedSearch, ResponseWriter pWriter) throws IOException {
		DBSButton xSelect = (DBSButton) pPagedSearch.getFacet(DBSPagedSearch.FACET_SELECTBUTTON);
		if (DBSObject.isNull(xSelect)) {
			xSelect = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xSelect.setId(DBSPagedSearch.ID_BTSELECT);
			xSelect.setStyleClass(CSS.PAGEDSEARCH.BT_SELECT);
			pPagedSearch.getFacets().put(DBSPagedSearch.FACET_SELECTBUTTON, xSelect);
		}
		
		//AJAX DO BUTTON
		AjaxBehavior xAjaxBehavior = (AjaxBehavior) xSelect.getClientBehaviors().get(AjaxBehavior.BEHAVIOR_ID);
		if (DBSObject.isNull(xAjaxBehavior)) {
			xAjaxBehavior = (AjaxBehavior) pContext.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
			Collection<String> xExecute = new ArrayList<String>();
			xExecute.add(DBSPagedSearch.ID_SELECT_ROW);
			xAjaxBehavior.setExecute(xExecute);
			xSelect.addClientBehavior("click", xAjaxBehavior);
		}
		MethodExpression xMethod = DBSFaces.createMethodExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.controller.name()).getExpressionString() +".selectItem()", null, null);
		xAjaxBehavior.addAjaxBehaviorListener(new DBSAjaxBehaviorListener(xMethod));
		xSelect.encodeAll(pContext);
	}
	
	/**
	 * Efetua o encode dos elementos de pesquisa
	 * @param pContext
	 * @param pPagedSearch
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodePesquisa(FacesContext pContext, DBSPagedSearch pPagedSearch, ResponseWriter pWriter) throws IOException {
		pWriter.startElement("div", pPagedSearch);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.FLEX_COL + CSS.PAGEDSEARCH.R1);
			//INPUT SEARCHPARAM
			pvEncodeInputSearch(pContext, pPagedSearch, pWriter);
			//INPUT VALOR
			pvEncodeInputValor(pContext, pPagedSearch);
			//INPUT SUGGESTION
			if (DBSObject.isEqual(pPagedSearch.getType(), "suggestion")
			 || DBSObject.isEqual(pPagedSearch.getType(), "select")) {
				pvEncodeInputSuggestion(pContext, pPagedSearch);
			}
			//COMMANDBUTTON ESCONDIDO
			pvEncodeSearchMore(pContext, pPagedSearch, pWriter);
		pWriter.endElement("div");
	}

	/**
	 * Encode do Input de pesquisa.
	 * @param pContext
	 * @param pPagedSearch
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeInputSearch(FacesContext pContext, final DBSPagedSearch pPagedSearch, ResponseWriter pWriter) throws IOException {
		DBSInputText xInputSearch = (DBSInputText) pPagedSearch.getFacet(DBSPagedSearch.FACET_INPUTSEARCH);
		if (DBSObject.isNull(xInputSearch)) {
			xInputSearch = (DBSInputText) pContext.getApplication().createComponent(DBSInputText.COMPONENT_TYPE);
			xInputSearch.setId(DBSPagedSearch.ID_INPUTSEARCH);
			xInputSearch.setStyleClass(CSS.PAGEDSEARCH.INPUTSEARCH);
			xInputSearch.setPlaceHolder(pPagedSearch.getPlaceHolder());
			xInputSearch.setLabel(pPagedSearch.getLabel());
			xInputSearch.setLabelWidth(pPagedSearch.getLabelWidth());
			xInputSearch.setMaxLength(pPagedSearch.getMaxLength());
			xInputSearch.setTooltip(pPagedSearch.getTooltip());
			xInputSearch.setRightLabel(pPagedSearch.getRightLabel());
			xInputSearch.setLetterCase("upper");
			xInputSearch.setReadOnly(pPagedSearch.getReadOnly());
			pPagedSearch.getFacets().put(DBSPagedSearch.FACET_INPUTSEARCH, xInputSearch);
		}
		//AJAX DO INPUT
		AjaxBehavior xAjaxBehavior = (AjaxBehavior) xInputSearch.getClientBehaviors().get(AjaxBehavior.BEHAVIOR_ID);
		if (!DBSObject.isEqual(pPagedSearch.getType(), "normal") && DBSObject.isNull(xAjaxBehavior)) {
			xAjaxBehavior = (AjaxBehavior) pContext.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
			xAjaxBehavior.setDelay("500");
			Collection<String> xExecute = new ArrayList<String>();
			xExecute.add("@this");
			xAjaxBehavior.setExecute(xExecute);
			Collection<String> xRender = new ArrayList<String>();
			xRender.add("@none");
			xAjaxBehavior.setRender(xRender);
			MethodExpression xMethod = DBSFaces.createMethodExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.controller.name()).getExpressionString() +".newSearch()", null, null);
			xAjaxBehavior.addAjaxBehaviorListener(new DBSAjaxBehaviorListener(xMethod));
			xAjaxBehavior.setOnevent("dbsfaces.pagedSearch.newSearch");
			xInputSearch.addClientBehavior("keyup", xAjaxBehavior);
		}
		xInputSearch.setValue(pPagedSearch.getController().getSearchParam());
		xInputSearch.setValueExpression("value", DBSFaces.createValueExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.controller.name()).getExpressionString() +".searchParam", String.class));
		xInputSearch.encodeAll(pContext);
		
		//Icon que indica para indicar que é possível efetuar pesquisa a partir do texto digitado
		if (!DBSObject.isEqual(pPagedSearch.getType(), "normal")) {
			pWriter.startElement("div", pPagedSearch);
				DBSFaces.encodeAttribute(pWriter, "class", "-small -i_find");
			pWriter.endElement("div");
		}
	}
	
	private void pvEncodeInputValor(FacesContext pContext, DBSPagedSearch pPagedSearch) throws IOException {
		DBSInputText xInputValor = (DBSInputText) pPagedSearch.getFacet(DBSPagedSearch.FACET_INPUT_VALOR);
		if (DBSObject.isNull(xInputValor)) {
			xInputValor = (DBSInputText) pContext.getApplication().createComponent(DBSInputText.COMPONENT_TYPE);
			xInputValor.setId(DBSPagedSearch.FACET_INPUT_VALOR);
			xInputValor.setType("hidden");
			xInputValor.setStyleClass(CSS.PAGEDSEARCH.INPUTVALOR);
			pPagedSearch.getFacets().put(DBSPagedSearch.FACET_INPUT_VALOR, xInputValor);
		}
		xInputValor.setValue(pPagedSearch.getValor());
		xInputValor.setValueExpression("value", DBSFaces.createValueExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.valor.name()).getExpressionString(), String.class));
		xInputValor.encodeAll(pContext);
	}
	
	private void pvEncodeInputSuggestion(FacesContext pContext, DBSPagedSearch pPagedSearch) throws IOException {
		DBSInputText xInputSuggestion = (DBSInputText) pPagedSearch.getFacet(DBSPagedSearch.FACET_INPUTSUGGESTION);
		if (DBSObject.isNull(xInputSuggestion)) {
			xInputSuggestion = (DBSInputText) pContext.getApplication().createComponent(DBSInputText.COMPONENT_TYPE);
			xInputSuggestion.setId(DBSPagedSearch.ID_INPUTSUGGESTION);
			xInputSuggestion.setStyleClass(CSS.PAGEDSEARCH.INPUTSUGGESTION);
			pPagedSearch.getFacets().put(DBSPagedSearch.FACET_INPUTSUGGESTION, xInputSuggestion);
		}
		xInputSuggestion.encodeAll(pContext);
	}
	
	/**
	 * Encode do Botão invisível de SearchMore.
	 * @param pContext
	 * @param pPagedSearch
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeSearchMore(FacesContext pContext, DBSPagedSearch pPagedSearch, ResponseWriter pWriter) throws IOException {
		DBSButton xSearchMore = (DBSButton) pPagedSearch.getFacet(DBSPagedSearch.FACET_SEARCHMORE);
		if (DBSObject.isNull(xSearchMore)) {
			xSearchMore = (DBSButton) pContext.getApplication().createComponent(DBSButton.COMPONENT_TYPE);
			xSearchMore.setId(DBSPagedSearch.ID_BTSEARCHMORE);
			xSearchMore.setStyleClass(CSS.PAGEDSEARCH.BT_SEARCH_MORE);
			pPagedSearch.getFacets().put(DBSPagedSearch.FACET_SEARCHMORE, xSearchMore);
		}
		
		//AJAX DO BUTTON
		AjaxBehavior xAjaxBehavior = (AjaxBehavior) xSearchMore.getClientBehaviors().get(AjaxBehavior.BEHAVIOR_ID);
		if (DBSObject.isNull(xAjaxBehavior)) {
			xAjaxBehavior = (AjaxBehavior) pContext.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
			Collection<String> xRender = new ArrayList<String>();
			xRender.add("@none");
			xAjaxBehavior.setRender(xRender);
			xAjaxBehavior.setOnevent("dbsfaces.pagedSearch.setSearching");
			xSearchMore.addClientBehavior("click", xAjaxBehavior);
		}
		xSearchMore.setValue(pPagedSearch.getController().getSearchParam());
		
		MethodExpression xMethod = DBSFaces.createMethodExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.controller.name()).getExpressionString() +".searchMore()", null, null);
		xAjaxBehavior.addAjaxBehaviorListener(new DBSAjaxBehaviorListener(xMethod));
		xSearchMore.encodeAll(pContext);
	}
	
	/**
	 * Faz o Encode da lista de resultados.
	 * @param pContext
	 * @param pPagedSearch
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeResultados(FacesContext pContext, DBSPagedSearch pPagedSearch, ResponseWriter pWriter) throws IOException {
		String xClientId = pPagedSearch.getClientId(pContext);
		String xDivResultsClientId = xClientId +":"+ DBSPagedSearch.ID_DIVRESULTS;
		String xVisibleListContainerClientId = xClientId +":"+ DBSPagedSearch.ID_VISIBLELISTCONTAINER;
		pWriter.startElement("div", pPagedSearch);
			DBSFaces.encodeAttribute(pWriter, "id", xDivResultsClientId);
			DBSFaces.encodeAttribute(pWriter, "name", xDivResultsClientId);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.THEME.FLEX_COL + CSS.PAGEDSEARCH.R2);
			//LISTA VISÍVEL
			pWriter.startElement("div", pPagedSearch);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.PAGEDSEARCH.VISIBLE_CONTAINER);
				//CONTAINER
				pWriter.startElement("div", pPagedSearch);
					DBSFaces.encodeAttribute(pWriter, "id", xVisibleListContainerClientId);
					DBSFaces.encodeAttribute(pWriter, "name", xVisibleListContainerClientId);
					DBSFaces.encodeAttribute(pWriter, "class", CSS.PAGEDSEARCH.VISIBLE_LIST);
				pWriter.endElement("div");
			pWriter.endElement("div");
			
			//LISTA INVISÍVEL
			pvEncodeDivInvisibleList(pContext, pPagedSearch);
		pWriter.endElement("div");
	}
	
	/**
	 * Encode da Lista Invisível
	 * @param pContext
	 * @param pPagedSearch
	 * @throws IOException
	 */
	private void pvEncodeDivInvisibleList(FacesContext pContext, DBSPagedSearch pPagedSearch) throws IOException {
		DBSDiv 		xContainerInvisivel = (DBSDiv) pPagedSearch.getFacet(DBSPagedSearch.FACET_DIVINVISIVEL);
		
		if (DBSObject.isNull(xContainerInvisivel)) {
			xContainerInvisivel = (DBSDiv) pContext.getApplication().createComponent(DBSDiv.COMPONENT_TYPE);
			xContainerInvisivel.setId(DBSPagedSearch.ID_DIVINVISIBLELIST);
			xContainerInvisivel.setStyleClass(CSS.PAGEDSEARCH.INVISIBLE_CONTAINER);

				//LISTA
				DBSDiv xListaInvisivel = (DBSDiv) pContext.getApplication().createComponent(DBSDiv.COMPONENT_TYPE);
				xListaInvisivel.setId(DBSPagedSearch.ID_INVISIBLELISTCONTAINER);
				xListaInvisivel.setStyleClass(CSS.PAGEDSEARCH.INVISIBLE_LIST);
				
					//Cria dataTable
					HtmlDataTable xDT = (HtmlDataTable) pContext.getApplication().createComponent(HtmlDataTable.COMPONENT_TYPE);
						xDT.setId(DBSPagedSearch.ID_PAGEDLIST);
						xDT.setValueExpression("value", DBSFaces.createValueExpression(pContext, pPagedSearch.getValueExpression(DBSPagedSearch.PropertyKeys.controller.name()).getExpressionString() + ".getList()", ArrayList.class)); 
						xDT.setVar(pPagedSearch.getVar());
						
						//COLUNA UNICA
						HtmlColumn xDTC = (HtmlColumn) pContext.getApplication().createComponent(HtmlColumn.COMPONENT_TYPE);
							//Div para agrupar os filhos
							DBSDiv xItem = (DBSDiv) pContext.getApplication().createComponent(DBSDiv.COMPONENT_TYPE);
								xItem.setId(DBSPagedSearch.ID_ITEM);
								xItem.setStyleClass(CSS.PAGEDSEARCH.PAGED_ITEM);
								//Adiciona a chave do item se foi configurada
								if (!DBSObject.isEmpty(pPagedSearch.getDisplayValueColumnName())) {
									//Item DisplayValue
									DBSInputText xInputDisplayValue = (DBSInputText) pContext.getApplication().createComponent(DBSInputText.COMPONENT_TYPE);
										xInputDisplayValue.setId(DBSPagedSearch.ID_ITEMDISPLAY);
										xInputDisplayValue.setStyleClass(CSS.PAGEDSEARCH.PAGED_ITEM_DISPLAY);
										xInputDisplayValue.setType("hidden");
										xInputDisplayValue.setValueExpression("value", DBSFaces.createValueExpression(pContext, pPagedSearch.getVar()+"."+pPagedSearch.getDisplayValueColumnName(), String.class));
									xItem.getChildren().add(xInputDisplayValue);
								}
							xItem.getChildren().addAll(pPagedSearch.getChildren());
						xDTC.getChildren().add(xItem);
					xDT.getChildren().add(xDTC);
				xListaInvisivel.getChildren().add(xDT);
			xContainerInvisivel.getChildren().add(xListaInvisivel);
			pPagedSearch.getFacets().put(DBSPagedSearch.FACET_DIVINVISIVEL, xContainerInvisivel);
		}
		xContainerInvisivel.encodeAll(pContext);
	}
	
	/**
	 * Encode do JavaScript do componente
	 * @param pComponent
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = 
			"$(document).ready(function() { \n" +
				" var xPagedSearchId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				" dbs_pagedSearch(xPagedSearchId); \n" +
			"}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
}