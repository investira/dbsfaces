package br.com.dbsoft.ui.component.datatable;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.facelets.compiler.UIInstructions;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.link.DBSLink;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSIO.SORT_DIRECTION;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily = DBSFaces.FAMILY, rendererType = DBSDataTable.RENDERER_TYPE)
public class DBSDataTableRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
//		DBSDataTable xDataTable = (DBSDataTable) pComponent;
		pvSetSubmittedInputs(pContext, pComponent, true);
		decodeBehaviors(pContext, pComponent); 
 
	}
	
	@Override
	public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
		DBSDataTable 	xDataTable = (DBSDataTable) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		//Dados
		pvEncodeDataTable(pContext, xDataTable, xWriter);
	}


	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()) {
			return;
		}
		DBSDataTable 	xDataTable = (DBSDataTable) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClientId = xDataTable.getClientId(pContext);

		String xClass = CSS.DATATABLE.MAIN + xDataTable.getStyleClass();
		String xStyle = xDataTable.getStyle();
		
		if (xDataTable.isSelectable()){
			xClass += CSS.MODIFIER.SELECTABLE;
		}
		//Cria botões no toolbar para edição diretamente na linha
//		DBSFaces.createDataTableInlineEditToolbar(xDataTable);
		
		pvSetCurrentRowIndex(pContext, pComponent, false);

		//Encode principal
		xWriter.startElement("div", xDataTable);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style", xStyle);
			//Não exibe dataTable caso não exista cabeçalho principal e não possua dados a ser exibidos
//			if (!pvHasHeader(xDataTable) &&
//				!(xDataTable.getRowCount() > 0)){
//				xStyle = xStyle + " display:none;";
//			}

			encodeClientBehaviors(pContext, xDataTable);
			//CSS
			pvEncodeCSS(xDataTable, xWriter, xClientId);
			xWriter.startElement("div", xDataTable);
				DBSFaces.encodeAttribute(xWriter, "id", xClientId + ":container");
				DBSFaces.encodeAttribute(xWriter, "name", xClientId + ":container");
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER);
				//Cabeçalho externo
				pvEncodeHeader(pContext, xDataTable, xWriter);
	}

	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()) {
			return;
		}
		DBSDataTable 	xDataTable = (DBSDataTable) pComponent;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		xDataTable.setRowIndex(-1);

			xWriter.endElement("div");
			pvEncodeJS(xDataTable, xWriter);
		xWriter.endElement("div");

		
		//Scripts
	}


	/**
	 * Retorna se possui cabeçalho principal
	 * @param pDataTable
	 * @return
	 */
	private boolean pvHasHeader(DBSDataTable pDataTable){
		UIComponent xFilter = pDataTable.getFacet(DBSDataTable.FACET_FILTER);
		UIComponent xToolbar = pDataTable.getFacet(DBSDataTable.FACET_TOOLBAR);
		if (!DBSObject.isEmpty(pDataTable.getCaption())
		 || !DBSObject.isEmpty(pDataTable.getIconClass()) 
		 || xFilter != null
		 || xToolbar != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Encode do cabeçalho contendo os filtros e botões do toolbar definidos pelo usuário.
	 * O encode do header da tabela é efetuado em outra rotina.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeHeader(FacesContext pContext, DBSDataTable pDataTable,ResponseWriter pWriter) throws IOException {
		UIComponent xFilter = pDataTable.getFacet(DBSDataTable.FACET_FILTER);
		UIComponent xToolbar = pDataTable.getFacet(DBSDataTable.FACET_TOOLBAR);
		
		if (pvHasHeader(pDataTable)) { 
			pWriter.startElement("div", pDataTable);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.HEADER);
				// Caption -------------------------
				if (!DBSObject.isEmpty(pDataTable.getCaption())
				 || !DBSObject.isEmpty(pDataTable.getIconClass())) {
					pWriter.startElement("div", pDataTable);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION + CSS.NOT_SELECTABLE);
						if (!DBSObject.isEmpty(pDataTable.getIconClass())) {
							pWriter.startElement("span", pDataTable);
								DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON + pDataTable.getIconClass());
							pWriter.endElement("span");
						}
						if (!DBSObject.isEmpty(pDataTable.getCaption())){
							pWriter.write(pDataTable.getCaption());
						}
					pWriter.endElement("div");
				}

				// Filters -------------------------
				if (xFilter != null) {
					if (!pDataTable.getCaption().equals("")) {
						// Linha de separação
						pWriter.startElement("span", pDataTable);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.HORIZONTAL_LINE_WHITE + " -line1");
						pWriter.endElement("span");
					}
					pWriter.startElement("div", pDataTable);
						//Campos de seleção do filtro
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.FILTER); 
						
						pWriter.startElement("div", pDataTable);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.INPUT);
							xFilter.encodeAll(pContext);
						pWriter.endElement("div");

						
						//Botão de "Pesquisar"
						pWriter.startElement("div", pDataTable);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.BUTTON);
							UIComponent xFacetPesquisar = pDataTable.getFacet(DBSDataTable.FACET_PESQUISAR);
							if (xFacetPesquisar!=null){
								DBSButton xBtPesquisar = (DBSButton) xFacetPesquisar.findComponent("btPesquisar");
								if (xBtPesquisar!=null){
									//Cria lista com os ids componentes dentro do filtro para que façam parte do submit do botão pesquisar 
									String xExecute = "";
									//Se for UIPanel é pq existe mais de um input como filtro
									if (xFilter instanceof UIPanel){
										for (UIComponent xC:xFilter.getChildren()){
											if (!(xC instanceof UIInstructions)){
												xExecute += xC.getClientId() + " "; 
											}
										}
									//O facet é o próprio componente de input do filtro
									}else{
										 xExecute = xFilter.getClientId();
									}
									xBtPesquisar.setExecute(xExecute); 
									xBtPesquisar.encodeAll(pContext);
								}
							}
						pWriter.endElement("div");
					pWriter.endElement("div");
				}
		
				// Toolbar -------------------------
				if (xToolbar != null) {
//					xToolbar.setTransient(true);
					if (!pDataTable.getCaption().equals("") || 
						xFilter != null) {
						pWriter.startElement("span", pDataTable);
							DBSFaces.encodeAttribute(pWriter, "class", CSS.HORIZONTAL_LINE_WHITE + " -line2");
						pWriter.endElement("span");
					}
					pWriter.startElement("div", pDataTable);
						DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.TOOLBAR); 
							DBSFaces.encodeDataTableHeaderToolbar(pDataTable);
					pWriter.endElement("div");
				}
			pWriter.endElement("div");
		}
		
		//Encode do input de foco
		pvEncodeFocusInput(pContext, pDataTable, pWriter);
		
		//Encode dos input de controle do sort
		pvEncodeSortInput(pContext, pDataTable);


	}
	


//	private String pvGetId(UIComponent pComponent, String pSufix, boolean pFullId){
//		String xId;
//		if (pFullId){
//			xId = pComponent.getClientId() + pSufix;
//		}else{
//			xId = pComponent.getId() + pSufix;
//		}
//		return xId;
//	}
	/**
	 * Encode integral da tabela
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeDataTable(FacesContext pContext,DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException {
		pDataTable.setRowIndex(-1);
		
		pWriter.startElement("div", pDataTable);
			DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
	        pWriter.startElement("table", pDataTable);
				DBSFaces.encodeAttribute(pWriter, "cellspacing", "0px");
				DBSFaces.encodeAttribute(pWriter, "cellpadding", "0px");
				//Cabeçalho da tabela
				pvEncodeDataTableHeader(pContext, pDataTable, pWriter);
				//Dados da tabela
				pvEncodeDataTableBody(pContext, pDataTable, pWriter);

			pWriter.endElement("table");
			
		pWriter.endElement("div");
	}

	/**
	 * Encode do header da tabela
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeDataTableHeader(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException {
		boolean xTemTitulo = false;
		//Verifica se possui alguma coluna com título
		UIComponent xParent = DBSFaces.getParentFirstChild(pDataTable, DBSDataTableColumn.class);
		if (xParent == null) {return;}

		for (UIComponent xC : xParent.getChildren()){
			if (xC instanceof DBSDataTableColumn){
				DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
				UIComponent xHeader = xDTC.getFacet(DBSDataTable.FACET_HEADER);
				if (xHeader !=null){
					xTemTitulo = true;
					break;
				}
			}
		}
		//Encode do cabeçalho com os títulos das colunas
		if (xTemTitulo){
			pWriter.startElement("thead", pDataTable);
				pWriter.startElement("tr", pDataTable);
					//Colunas do usuário
					pvConfigDataTableColumnsStyleClass(pContext, pDataTable);
					for (UIComponent xC : xParent.getChildren()){
						if (xC instanceof DBSDataTableColumn){
							DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
							if (xDTC.isRendered()){
								pvEncodeColumnHeader(pContext, pDataTable, pWriter, xDTC);
							}
						}else{
							xC.encodeAll(pContext);
						}
					}
					//Encode da coluna auxiliar
					pvEncodeColumnAux(pContext, pDataTable, pWriter);
				pWriter.endElement("tr");
			pWriter.endElement("thead");
		}
	}

	/**
	 * Encode do componente input para controle do focus, caracteres digitados e conterá o número da linha selecionada
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeFocusInput(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException {
		//Input para controle do focus e caracteres digitados----
		pWriter.startElement("input", pDataTable);
			DBSFaces.encodeAttribute(pWriter, "id", pvGetInputFooId(pContext, pDataTable));
			DBSFaces.encodeAttribute(pWriter, "name", pvGetInputFooId(pContext, pDataTable));
			DBSFaces.encodeAttribute(pWriter, "type", "text");
			DBSFaces.encodeAttribute(pWriter, "class", "-foo");
			DBSFaces.encodeAttribute(pWriter, "autocomplete", "off");
			DBSFaces.encodeAttribute(pWriter, "readonly", "readonly");
			DBSFaces.encodeAttribute(pWriter, "value", pDataTable.getCurrentRowIndex());
		pWriter.endElement("input");
	}
	
	/**
	 * Inputs que controtam os sort
	 * @param pContext
	 * @param pDataTable
	 * @throws IOException
	 */
	private void pvEncodeSortInput(FacesContext pContext, DBSDataTable pDataTable) throws IOException {
		if (!pvHasSortableColumns(pDataTable)){return;}
		
		HtmlInputHidden xInput;
		//Columa selecionada para sort
		xInput = (HtmlInputHidden) pDataTable.getFacet("sortColumn");
		if (xInput == null){
			xInput = (HtmlInputHidden) pContext.getApplication().createComponent(HtmlInputHidden.COMPONENT_TYPE);
			xInput.setId(DBSDataTable.INPUT_SORT_COLUMN_ID);
			xInput.setValue(pDataTable.getSortColumn());
			pDataTable.getFacets().put("sortColumn", xInput);
		}
		xInput.setValue(pDataTable.getSortColumn());
		xInput.encodeAll(pContext);
		//Ordem da coluna
		xInput = (HtmlInputHidden) pDataTable.getFacet("sortDirection");
		if (xInput == null){
			xInput = (HtmlInputHidden) pContext.getApplication().createComponent(HtmlInputHidden.COMPONENT_TYPE);
			xInput.setId(DBSDataTable.INPUT_SORT_DIRECTION_ID);
			xInput.setValue(pDataTable.getSortDirection());
			pDataTable.getFacets().put("sortDirection", xInput);
		}
		xInput.setValue(pDataTable.getSortDirection());
		xInput.encodeAll(pContext);
		//Encode do botão do sort
		String xClientIdButton = DBSDataTable.BUTTON_SORT_ID;
		DBSLink xBtn = (DBSLink) pDataTable.getFacets().get(xClientIdButton);
		if (xBtn == null){
			xBtn = (DBSLink) pContext.getApplication().createComponent(DBSLink.COMPONENT_TYPE);
			xBtn.setId(xClientIdButton);
			xBtn.setStyleClass("-sort"); 
			xBtn.setActionExpression(DBSFaces.createMethodExpression(pContext, pDataTable.getSortAction(), String.class, new Class[0]));
			xBtn.setUpdate(pDataTable.getClientId());
			pDataTable.getFacets().put(xClientIdButton, xBtn);
		}
		xBtn.encodeAll(pContext);

	}

	/**
	 * Encode do corpo da tabela contendo as linhas com os dados
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeDataTableBody(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException {
		pWriter.startElement("tbody", pDataTable);		
        	int 		xRowClassIndex = 0;
			String[]	xRowClasses;
	        int 		xRowCount = pDataTable.getRowCount(); 
			pDataTable.setRowIndex(-1);
//			pDataTable.getFirst();
//			pDataTable.getRows(); 
	        xRowClasses = DBSString.changeStr(pDataTable.getRowStyleClass(), ",", " ").split("\\s+");
	        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
	        	pDataTable.setRowIndex(xRowIndex);
	        	pWriter.startElement("tr", pDataTable);
//	        		pWriter.writeAttribute("id", pDataTable.getClientId(), null);
//	        		pWriter.writeAttribute("name", pDataTable.getClientId(), null);
	        		DBSFaces.encodeAttribute(pWriter, "index", pDataTable.getRowIndex());

	        		//Controle da class da linha
	        		if (xRowIndex == pDataTable.getCurrentRowIndex()){
						DBSFaces.encodeAttribute(pWriter, "class", xRowClasses[xRowClassIndex] + CSS.MODIFIER.SELECTED);
	        		}else{

						DBSFaces.encodeAttribute(pWriter, "class", xRowClasses[xRowClassIndex]);
	        		}
					
					//Encode das colunas----------------------------------
					pvConfigDataTableColumnsStyleClass(pContext, pDataTable);
					UIComponent xParent = DBSFaces.getParentFirstChild(pDataTable, DBSDataTableColumn.class);
					if (xParent != null) {
						for (UIComponent xC : xParent.getChildren()){
							if (xC instanceof DBSDataTableColumn){
								DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
								if (xDTC.isRendered()){
									pvEncodeColumnBody(pContext, pDataTable, pWriter, xDTC);
								}
							}
						}
					}
					//Encode da coluna auxiliar
					pvEncodeColumnAux(pContext, pDataTable, pWriter);
					xRowClassIndex++;
					if (xRowClassIndex == xRowClasses.length){
						xRowClassIndex = 0;
					}
				pWriter.endElement("tr");
	        }
		pWriter.endElement("tbody");
	}
	
	/**
	 * Encode do CSS necessário para fixar as dimensões das colunas
	 * @param pDataTable
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeCSS(UIComponent pComponent, ResponseWriter pWriter, String pClientId) throws IOException {
   		UIComponent xParent = DBSFaces.getParentFirstChild(pComponent, DBSDataTableColumn.class);
		if (xParent == null) {return;}
		DBSFaces.encodeStyleTagStart(pComponent, pWriter);
        	Integer xI = 0;
        	String	xCSS = "";
        	//Fixa o tamanho da cada coluna
    		for (UIComponent xC : xParent.getChildren()){
			if (xC instanceof DBSDataTableColumn){
				DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
				//Não cria css para a primeira e última coluna, pois foram criada automaticamente(não fazem parte das colunas criadas pelo usuário) para controle
				if (pvIsUserColumn(xDTC)){
					xI++;
					if (!DBSObject.isEmpty(xDTC.getWidth())){
						String xUserStyle = "";
						if (xDTC.getStyle()!= null){
							xUserStyle = xDTC.getStyle();
						}
						//Define a largura da coluna
						xCSS += "#" + DBSFaces.convertToCSSId(pClientId) + " > .-container > .-content > table > * > tr > ." + DBSFaces.getDataTableDataColumnStyleClass(xI.toString(), "") + "{"  +
								 "width:" + xDTC.getWidth() + ";"  +
								 "min-width:" + xDTC.getWidth() + ";"  +
								 "max-width:"  + xDTC.getWidth() + ";"  +
								 xUserStyle +
								"}\n";
					}
				}
			}
		}
		pWriter.write(xCSS);
		DBSFaces.encodeStyleTagEnd(pWriter);	
	}

	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xDataTableId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_dataTable(xDataTableId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
		
	}
	
	/**
	 * Encode da coluna auxliar de controle.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeColumnHeader(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter, DBSDataTableColumn pColumn) throws IOException{
		UIComponent xHeader = pColumn.getFacet(DBSDataTable.FACET_HEADER);
		if (xHeader == null){return;}
		pvEncodeColumn(pContext, pDataTable, pWriter, pColumn, pColumn.getStyleClass(), xHeader);
	}

	/**
	 * Encode da coluna auxliar de controle.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeColumnBody(FacesContext pContext, DBSDataTable pDataTable,ResponseWriter pWriter, DBSDataTableColumn pColumn) throws IOException{
		pvEncodeColumn(pContext, pDataTable, pWriter, null, pColumn.getStyleClass(), pColumn);
	}
	
	/**
	 * Encode da coluna auxliar de controle.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeColumnAux(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException{
		pvEncodeColumn(pContext, pDataTable, pWriter, null, DBSFaces.getDataTableDataColumnStyleClass("X", ""), null);
	}
	
	/**
	 * Encode padrão da coluna
	 * @param pHead
	 * @param pStyleClass
	 * @param pStyle
	 * @param pColumnContent
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeColumn(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter, DBSDataTableColumn pColumn, String pStyleClass, UIComponent pColumnContent) throws IOException{
		String xTag = (pColumn == null ? "td" : "th");
		pWriter.startElement(xTag, pDataTable);
			//Se for cabeçalho e coluna puder ser ordenada.
			if (pColumn != null
			&& pColumn.getSortable()){
				pStyleClass += " -sort ";
				//Marca como selecionado se for a colluna que está o sort
				if (pDataTable.getSortColumn().equals(pColumn.getId())){
					pStyleClass += CSS.MODIFIER.SELECTED;
				}
				DBSFaces.encodeAttribute(pWriter, "sortColumn", pColumn.getId());
			}
			//Encode do conteúdo da coluna
			DBSFaces.encodeAttribute(pWriter, "class", pStyleClass);
			if (pColumnContent != null){
				pColumnContent.encodeAll(pContext);
				if (pColumnContent instanceof ClientBehaviorHolder){
					encodeClientBehaviors(pContext, (ClientBehaviorHolder) pColumnContent);
				}
			}
			//Icone da direção do sort
			if (pColumn != null
			&& pColumn.getSortable()){
				//Indica a direção se é a coluna que que está o sort
				pWriter.startElement("span", pDataTable);
					DBSFaces.encodeAttribute(pWriter, "class", "-sort_icon");
					pWriter.startElement("span", pDataTable);
						if (pDataTable.getSortColumn().equals(pColumn.getId())){
							pStyleClass = SORT_DIRECTION.get(pDataTable.getSortDirection()).getIcon();
						}else{
							pStyleClass = SORT_DIRECTION.NONE.getIcon();
						}
						DBSFaces.encodeAttribute(pWriter, "class", pStyleClass);
					pWriter.endElement("span");
				pWriter.endElement("span");
			}
		pWriter.endElement(xTag);
	}

	/**
	 * Preenche o styleClass das colunas com o valor padrão
	 * @param pContext
	 * @param pDataTable
	 */
	private static void pvConfigDataTableColumnsStyleClass(FacesContext pContext, DBSDataTable pDataTable){
		Integer xI = 0;
		UIComponent xParent = DBSFaces.getParentFirstChild(pDataTable, DBSDataTableColumn.class);
		if (xParent == null) {return;}

		for (UIComponent xC : xParent.getChildren()){
			if (xC instanceof DBSDataTableColumn){
				DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
				if (pvIsUserColumn(xDTC)){
					xI++;
					xDTC.setStyleClass(DBSFaces.getDataTableDataColumnStyleClass(xI.toString(), xDTC.getStyleClass()));
				}
			}
		}
	}
	
	/**
	 * Retorna se a coluna foi criada pelo usuário
	 * @param pColumn
	 * @return true = usuário, false=criada dynamicamente dentro do DBSDataTable
	 */
	private static boolean pvIsUserColumn(DBSDataTableColumn pColumn){
		if (pColumn.getId() == null || 
		    (!pColumn.getId().equals("C0") &&
			 !pColumn.getId().equals("CX"))){
			return true;
		}
		return false;
	}

	
	private void pvSetSubmittedInputs(FacesContext pContext, UIComponent pComponent, boolean pDecode){
		pvSetCurrentRowIndex(pContext, pComponent, pDecode);
		pvSetSortInputs(pContext, pComponent);
	}
	/**Seta posição do registro a apartir da seleção do usuário.
	 * @param pContext
	 * @param pComponent
	 * @param pDecode
	 */
	private void pvSetCurrentRowIndex(FacesContext pContext, UIComponent pComponent, boolean pDecode){
		DBSDataTable 	xDataTable = (DBSDataTable) pComponent;
		String xRowIndex = (String) DBSFaces.getDecodedComponenteValue(pContext, pvGetInputFooId(pContext, xDataTable));
//		String xRowIndex = pContext.getExternalContext().getRequestParameterMap().get(pvGetInputFooId(pContext, xDataTable));
		if (!DBSObject.isEmpty(xRowIndex)){
			//No decode, set o rowIndex para que o valor selecionado pelo usuário, via submit, sensibilize efetivamente o valor corrente.
			if (pDecode){
				xDataTable.setRowIndex(DBSNumber.toInteger(xRowIndex)); 
			}
			//No encode, é ligo novamente o valor recebido para que o encode reflita a linha ainda marcada
			xDataTable.setCurrentRowIndex(DBSNumber.toInteger(xRowIndex)); 
		}else{
			xDataTable.setCurrentRowIndex(-1);
		}
	}
	/**Seta posição do registro a apartir da seleção do usuário.
	 * @param pContext
	 * @param pComponent
	 * @param pDecode
	 */
	private void pvSetSortInputs(FacesContext pContext, UIComponent pComponent){
		DBSDataTable 		xDataTable = (DBSDataTable) pComponent;
//		Map<String, String> xRequestMap = pContext.getExternalContext().getRequestParameterMap();
//		String xStr;
		//Seta coluna que será utilizada para o sort
		String xSortColumn = (String) DBSFaces.getDecodedComponenteValue(pContext, pvGetInputSortColumnId(pContext, xDataTable));
		if (xSortColumn != null){
			if (!xDataTable.getSortColumn().equals(xSortColumn)){
				xDataTable.setSortColumn(xSortColumn);
				//Ignora o set da ordem, pois já é resetado para "A" neste caso
				return;
			}
		}

//		if (xRequestMap.containsKey(pvGetInputSortColumnId(pContext, xDataTable))){
//			xStr = DBSObject.getNotEmpty(xRequestMap.get(pvGetInputSortColumnId(pContext, xDataTable)), "");
//			//Somente seta valor se for diferente do já existente
//			if (!xDataTable.getSortColumn().equals(xStr)){
//				xDataTable.setSortColumn(xStr);
//				//Ignora o set da ordem, pois já é resetado para "A" neste caso
//				return;
//			}
//		}
		//Set direção do sort
		String xSortDirection = (String) DBSFaces.getDecodedComponenteValue(pContext, pvGetInputSortDirectionId(pContext, xDataTable));
		if (xSortDirection != null){
			SORT_DIRECTION xDirection = SORT_DIRECTION.get(xSortDirection);
			//Somente seta valor se for diferente do já existente
			xDataTable.setSortDirection(xDirection.getCode());
		}
			
//		if (xRequestMap.containsKey(pvGetInputSortDirectionId(pContext, xDataTable))){
//			SORT_DIRECTION xDirection = SORT_DIRECTION.get(xRequestMap.get(pvGetInputSortDirectionId(pContext, xDataTable)));
//			//Somente seta valor se for diferente do já existente
//			xDataTable.setSortDirection(xDirection.getCode());
//		}
	}

	/**
	 * Retorna se exibe alguma coluna com sort
	 * @param pDataTable
	 * @return
	 */
	private boolean pvHasSortableColumns(DBSDataTable pDataTable){
		UIComponent xParent = DBSFaces.getParentFirstChild(pDataTable, DBSDataTableColumn.class);
		if (xParent != null) {
			for (UIComponent xC : xParent.getChildren()){
				if (xC instanceof DBSDataTableColumn){
					DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
					if (xDTC.getSortable()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private String pvGetInputFooId(FacesContext pContext, DBSDataTable pDataTable){
		return pDataTable.getClientId(pContext) + ":" + DBSDataTable.INPUT_FOO_ID;
	}
	private String pvGetInputSortColumnId(FacesContext pContext, DBSDataTable pDataTable){
		return pDataTable.getClientId(pContext) + ":" + DBSDataTable.INPUT_SORT_COLUMN_ID;
	}
	private String pvGetInputSortDirectionId(FacesContext pContext, DBSDataTable pDataTable){
		return pDataTable.getClientId(pContext) + ":" + DBSDataTable.INPUT_SORT_DIRECTION_ID;
	}
//	private String pvGetButtonSortId(FacesContext pContext, DBSDataTable pDataTable){
//		return pDataTable.getClientId(pContext) + ":" + DBSDataTable.BUTTON_SORT_ID;
//	}
		
	
}
