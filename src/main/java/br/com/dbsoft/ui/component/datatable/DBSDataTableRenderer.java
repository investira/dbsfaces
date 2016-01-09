package br.com.dbsoft.ui.component.datatable;


import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.sun.faces.facelets.compiler.UIInstructions;
import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.link.DBSLink;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSIO.SORT_DIRECTION;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSObject;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily = DBSFaces.FAMILY, rendererType = DBSDataTable.RENDERER_TYPE)
public class DBSDataTableRenderer extends DBSRenderer {
	
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSDataTable xDataTable = (DBSDataTable) pComponent;
		pvSetSubmittedInputs(pContext, pComponent, true);
		decodeBehaviors(pContext, pComponent); 
		if (RenderKitUtils.isPartialOrBehaviorAction(pContext, pvGetButtonSortId(pContext, xDataTable)) || /*Chamada Ajax*/
			pContext.getExternalContext().getRequestParameterMap().containsKey(pvGetButtonSortId(pContext, xDataTable))) { 	/*Chamada Sem Ajax*/
		} 
	}
	
	@Override
	public boolean getRendersChildren() {
		return true; // True=Chama o encodeChildren abaixo e interrompe a busca
						// por filho pela rotina renderChildren
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

		String xClass = DBSFaces.CSS.DATATABLE.MAIN + " " + xDataTable.getStyleClass();
		String xStyle = xDataTable.getStyle();
		
		if (xDataTable.isSelectable()){
			xClass += DBSFaces.CSS.MODIFIER.SELECTABLE;
		}
		//Cria botões no toolbar para edição diretamente na linha
//		DBSFaces.createDataTableInlineEditToolbar(xDataTable);
		
		pvSetCurrentRowIndex(pContext, pComponent, false);

		//Encode principal
		xWriter.startElement("div", xDataTable);
			xWriter.writeAttribute("id", xClientId, null);
			xWriter.writeAttribute("name", xClientId, null);
			DBSFaces.setAttribute(xWriter, "class", xClass.trim(), null);
			//Não exibe dataTable caso não exista cabeçalho principal e não possua dados a ser exibidos
//			if (!pvHasHeader(xDataTable) &&
//				!(xDataTable.getRowCount() > 0)){
//				xStyle = xStyle + " display:none;";
//			}
			if (!xStyle.trim().equals("")){
				DBSFaces.setAttribute(xWriter, "style", xStyle, null);
			}

			encodeClientBehaviors(pContext, xDataTable);
			//CSS
			pvEncodeCSS(xDataTable, xWriter, xClientId);
			xWriter.startElement("div", xDataTable);
				xWriter.writeAttribute("id", xClientId + ":container", null);
				xWriter.writeAttribute("name", xClientId + ":container", null);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER.trim(), null);
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
		String 			xClientId;
		
			xWriter.endElement("div");
		xWriter.endElement("div");

		xDataTable.setRowIndex(-1);
		
		xClientId = xDataTable.getClientId(pContext);
		//Scripts
		pvEncodeJS(xWriter, xClientId);
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
				DBSFaces.setAttribute(pWriter, "class",DBSFaces.CSS.MODIFIER.HEADER.trim(), null);
				// Caption -------------------------
				if (!DBSObject.isEmpty(pDataTable.getCaption())
				 || !DBSObject.isEmpty(pDataTable.getIconClass())) {
					pWriter.startElement("div", pDataTable);
						DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CAPTION + DBSFaces.CSS.NOT_SELECTABLE,null);
						if (!DBSObject.isEmpty(pDataTable.getIconClass())) {
							pWriter.startElement("span", pDataTable);
								pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.ICON + pDataTable.getIconClass(), null);
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
							DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.HORIZONTAL_LINE_WHITE.trim() + " -line1", null);
						pWriter.endElement("span");
					}
					pWriter.startElement("div", pDataTable);
						//Campos de seleção do filtro
						DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.FILTER.trim(),null); 
						
						pWriter.startElement("div", pDataTable);
							DBSFaces.setAttribute(pWriter, "class",DBSFaces.CSS.MODIFIER.INPUT.trim(), null);
							xFilter.encodeAll(pContext);
						pWriter.endElement("div");

						
						//Botão de "Pesquisar"
						pWriter.startElement("div", pDataTable);
							DBSFaces.setAttribute(pWriter, "class",DBSFaces.CSS.MODIFIER.BUTTON.trim(), null);
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
							DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.HORIZONTAL_LINE_WHITE.trim() + " -line2", null);
						pWriter.endElement("span");
					}
					pWriter.startElement("div", pDataTable);
						DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.TOOLBAR.trim(), null); 
							DBSFaces.encodeDataTableHeaderToolbar(pDataTable);
					pWriter.endElement("div");
				}
			pWriter.endElement("div");
		}
		
		//Encode do input de foco
		pvEncodeFocusInput(pContext, pDataTable, pWriter);
		
		//Encode dos input de controle do sort
		if (pDataTable.getSortAction() != null){
			pvEncodeSortInput(pContext, pDataTable);
		}


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
			DBSFaces.setAttribute(pWriter, "class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
	        pWriter.startElement("table", pDataTable);
				pWriter.writeAttribute("cellspacing", "0px", null);
				pWriter.writeAttribute("cellpadding", "0px", null);
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
		for (UIComponent xC : pDataTable.getChildren()){
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
					for (UIComponent xC : pDataTable.getChildren()){
						if (xC instanceof DBSDataTableColumn){
							DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
							if (xDTC.isRendered()){
								pvEncodeColumnHeader(xDTC, pContext, pDataTable, pWriter);
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
	private void pvEncodeFocusInput(FacesContext pContext, DBSDataTable pDataTable,ResponseWriter pWriter) throws IOException {
		//Input para controle do focus e caracteres digitados----
		pWriter.startElement("input", pDataTable);
			DBSFaces.setAttribute(pWriter, "id", pvGetInputFooId(pContext, pDataTable), null);
			DBSFaces.setAttribute(pWriter, "name", pvGetInputFooId(pContext, pDataTable), null);
			DBSFaces.setAttribute(pWriter, "type", "text", null);
			DBSFaces.setAttribute(pWriter, "class", "-foo", null);
			DBSFaces.setAttribute(pWriter, "autocomplete", "off", null);
			DBSFaces.setAttribute(pWriter, "readonly", "readonly", null);
			DBSFaces.setAttribute(pWriter, "value", pDataTable.getCurrentRowIndex(), null);
		pWriter.endElement("input");
	}
	
	/**
	 * Inputs que controtam os sort
	 * @param pContext
	 * @param pDataTable
	 * @throws IOException
	 */
	private void pvEncodeSortInput(FacesContext pContext, DBSDataTable pDataTable) throws IOException {
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
	private void pvEncodeDataTableBody(FacesContext pContext,DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException {
		pWriter.startElement("tbody", pDataTable);		
        	int 		xRowClassIndex = 0;
			String[]	xRowClasses;
	        int 		xRowCount = pDataTable.getRowCount(); 
			pDataTable.setRowIndex(-1);
			pDataTable.getFirst();
			pDataTable.getRows(); 
	        xRowClasses = DBSString.changeStr(pDataTable.getRowStyleClass(), ",", " ").split("\\s+");
	        for (int xRowIndex = 0; xRowIndex < xRowCount; xRowIndex++) {
	        	pDataTable.setRowIndex(xRowIndex);
	        	pWriter.startElement("tr", pDataTable);
//	        		pWriter.writeAttribute("id", pDataTable.getClientId(), null);
//	        		pWriter.writeAttribute("name", pDataTable.getClientId(), null);
	        		pWriter.writeAttribute("index", pDataTable.getRowIndex(), null);

	        		//Controle da class da linha
	        		if (xRowIndex == pDataTable.getCurrentRowIndex()){
						DBSFaces.setAttribute(pWriter, "class", xRowClasses[xRowClassIndex] + DBSFaces.CSS.MODIFIER.SELECTED, null);
	        		}else{

						DBSFaces.setAttribute(pWriter, "class", xRowClasses[xRowClassIndex], null);
	        		}
					
					//Encode das colunas----------------------------------
					pvConfigDataTableColumnsStyleClass(pContext, pDataTable);
					for (UIComponent xC : pDataTable.getChildren()){
						if (xC instanceof DBSDataTableColumn){
							DBSDataTableColumn xDTC = (DBSDataTableColumn) xC;
							if (xDTC.isRendered()){
								pvEncodeColumnBody(xDTC, pContext, pDataTable, pWriter);
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
	private void pvEncodeCSS(DBSDataTable pDataTable, ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeStyleTagStart(pWriter);
        	Integer xI = 0;
        	String	xCSS = "";
        	//Fixa o tamanho da cada coluna
			for (UIComponent xC : pDataTable.getChildren()){
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
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xDataTableId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
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
	private void pvEncodeColumnHeader(DBSDataTableColumn pColumn, FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException{
		UIComponent xHeader = pColumn.getFacet(DBSDataTable.FACET_HEADER);
		pvEncodeColumn(pColumn, pColumn.getStyleClass(), xHeader, pContext, pDataTable, pWriter);
	}

	/**
	 * Encode da coluna auxliar de controle.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeColumnBody(DBSDataTableColumn pColumn, FacesContext pContext, DBSDataTable pDataTable,ResponseWriter pWriter) throws IOException{
		pvEncodeColumn(null, pColumn.getStyleClass(), pColumn, pContext, pDataTable, pWriter);
	}
	
	/**
	 * Encode da coluna auxliar de controle.
	 * @param pContext
	 * @param pDataTable
	 * @param pWriter
	 * @throws IOException
	 */
	private void pvEncodeColumnAux(FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException{
		pvEncodeColumn(null, DBSFaces.getDataTableDataColumnStyleClass("X", ""), null, pContext, pDataTable, pWriter);
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
	private void pvEncodeColumn(DBSDataTableColumn pColumn, String pStyleClass, UIComponent pColumnContent, FacesContext pContext, DBSDataTable pDataTable, ResponseWriter pWriter) throws IOException{
		String xTag = (pColumn == null ? "td" : "th");
		pWriter.startElement(xTag, pDataTable);
			//Se for cabeçalho e coluna puder ser ordenada.
			if (pColumn != null
			&& pColumn.getSortable()){
				pStyleClass += " -sort ";
				//Marca como selecionado se for a colluna que está o sort
				if (pDataTable.getSortColumn().equals(pColumn.getId())){
					pStyleClass += DBSFaces.CSS.MODIFIER.SELECTED;
				}
				DBSFaces.setAttribute(pWriter, "sortColumn", pColumn.getId(), null);
			}
			//Encode do conteúdo da coluna
			DBSFaces.setAttribute(pWriter, "class", pStyleClass, null);
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
				if (pDataTable.getSortColumn().equals(pColumn.getId())){
					pStyleClass = SORT_DIRECTION.get(pDataTable.getSortDirection()).getIcon();
				}else{
					pStyleClass = SORT_DIRECTION.NONE.getIcon();
				}
				pWriter.startElement("span", pDataTable);
					DBSFaces.setAttribute(pWriter, "class", "-sort_icon " + pStyleClass, null);
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
		for (UIComponent xC : pDataTable.getChildren()){
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
		String xRowIndex = pContext.getExternalContext().getRequestParameterMap().get(pvGetInputFooId(pContext, xDataTable));
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
		Map<String, String> xRequestMap = pContext.getExternalContext().getRequestParameterMap();
		String xStr;
		//Seta coluna que será utilizada para o sort
		if (xRequestMap.containsKey(pvGetInputSortColumnId(pContext, xDataTable))){
			xStr = DBSObject.getNotEmpty(xRequestMap.get(pvGetInputSortColumnId(pContext, xDataTable)), "");
			//Somente seta valor se for diferente do já existente
			if (!xDataTable.getSortColumn().equals(xStr)){
				xDataTable.setSortColumn(xStr);
				//Ignora o set da ordem, pois já é resetado para "A" neste caso
				return;
			}
		}
		//Set direção do sort
		if (xRequestMap.containsKey(pvGetInputSortDirectionId(pContext, xDataTable))){
			SORT_DIRECTION xDirection = SORT_DIRECTION.get(xRequestMap.get(pvGetInputSortDirectionId(pContext, xDataTable)));
			//Somente seta valor se for diferente do já existente
			xDataTable.setSortDirection(xDirection.getCode());
		}
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
	private String pvGetButtonSortId(FacesContext pContext, DBSDataTable pDataTable){
		return pDataTable.getClientId(pContext) + ":" + DBSDataTable.BUTTON_SORT_ID;
	}
		
	
}
