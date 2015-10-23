package br.com.dbsoft.ui.component.datatable;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import br.com.dbsoft.ui.component.DBSUIData;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSDataTable.COMPONENT_TYPE)
public class DBSDataTable extends DBSUIData implements ClientBehaviorHolder, SystemEventListener{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DATATABLE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public final static String FACET_PESQUISAR = "pesquisar";
	public final static String FACET_HEADER = "header";
	public final static String FACET_TOOLBAR = "toolbar";
	public final static String FACET_TOOLBAR_CONTROL = "toolbarcontrol";
	public final static String FACET_FILTER = "filter";
	public final static String FACET_INLINETOOLBAR = "inlineEditToolbar";

	protected enum PropertyKeys {
		style, 
		styleClass, 
		rowStyleClass, 
		caption, 
		multipleSelection, 
		searchAction, 
		viewOneAction, 
		selectAllAction,
		selected, 
		update,
		keyColumnName,
		currentRowIndex,
		iconClass;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {
		}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}

	public DBSDataTable() {
        super();
		setRendererType(DBSDataTable.RENDERER_TYPE);
//		this.setRowStatePreserved(true);
		// Código que força a criação de uma seção, pois na versão 2.1.7 do JSF
		// ocorre o error se não existir alguma seção criada
		if (FacesContext.getCurrentInstance().getExternalContext().getSession(false) == null) {
			FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		}

//		DBSFaces.subscribeToDynamicComponentEvent(this);

		 FacesContext xContext = FacesContext.getCurrentInstance();
		 xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderComponentEvent.class,this);
		// -------------------------------------------------------------------------------
//		 xContext.getViewRoot().subscribeToViewEvent(PostConstructViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostRestoreStateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreDestroyViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRemoveFromViewEvent.class,this);

	}
	
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
//		FacesContext xContext = FacesContext.getCurrentInstance();
//		 UIComponent xComponent = (UIComponent) event.getSource();
//		 System.out.print("=============================================================================");
//		 System.out.println(event.getClass().getName() +
//				 		  ":" + xComponent.getClass().getName() + "| UIComponentID:" + xComponent.getClientId());
//		 if (xComponent != null){
//			 System.out.println("| UIComponentID:" + xComponent.getClientId());
//			 if (xComponent.getChildren().size() > 0) {
//				 System.out.println("| Children     :" +
//				 xComponent.getChildren().size());
//			 }
//		 }
		// if (xContext.isPostback() && xComponent instanceof DBSDataTable){
		// UIComponent xC0 = DBSFaces.findComponent("C0",
		// xComponent.getChildren());
		// if (xC0 != null){
		// System.out.println("| FOUND COLUMN : <------!! 3");
		// }
		// }
		// UIComponent xC0 = DBSFaces.findComponent("C0");
		// if (xC0 != null){
		// System.out.println("| FOUND COLUMN : <------!!");
		// }
		// xC0 = DBSFaces.findComponent("dataTable:C0");
		// if (xC0 != null){
		// System.out.println("| FOUND COLUMN : <------!! 2");
		// }
		// }
		// if (xContext.getMaximumSeverity() != null ) {
		// System.out.println("| getMaximumSeverity ");
		// }
		//
		// if (xContext.isPostback()) {
		// System.out.println("| Postback");
		// }
//		if (!xContext.isPostback()) {
//			return;
//		}
			
//		//É necessário ser o próprio componente para poder recuperar os atributos com EL já resolvidos
//		//É necessário ser o evento PostAddToViewEvent para não ocorrer o erro de Id duplicado e funcionar os action dos botões dinamicamente incluidos
		if (event.getSource() instanceof DBSDataTable &&
			event instanceof PostAddToViewEvent) {
			DBSFaces.createDataTableBotaoPesquisar(this);
			DBSFaces.createDataTableSpecialColumns(this);
			DBSFaces.createDataTableToolbarFoo(this);
		}else if (event instanceof PreRenderComponentEvent && !(event.getSource() instanceof DBSDataTable)){
			//Redireciona para o encode do toolbar
			DBSFaces.encodeDataTableHeaderToolbar(this);
		}
	}

	@Override
	public boolean isListenerForSource(Object pSource) {
//		 String xStr = "";
//		 if (pSource instanceof UIComponent){
//		 xStr = ((UIComponent) pSource).getClientId();
//		 }
//		 xStr += "\t:" + pSource.getClass().getName();
//		 xStr += "\t:" + FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds();
//
// 		 System.out.println("isListenerForSource:" + xStr);

		UIComponent xSource =  (UIComponent) pSource;
		UIComponent xSourceParent = xSource.getParent();

		 //Verifica se source é o toolbar deste dataTable e se foi solicitado o update(render) ajax dele.
		if (xSourceParent != null
	 	 && xSourceParent.equals(this)){
			UIComponent xToolbarControlFacet = xSourceParent.getFacet(FACET_TOOLBAR_CONTROL);
			if (xToolbarControlFacet !=null 
			 && pSource.equals(xToolbarControlFacet)){
				PartialViewContext xPVC = FacesContext.getCurrentInstance().getPartialViewContext();
				//Se foi solicitado o update(render) ajax do toolbar
				if (xPVC !=null && xPVC.getRenderIds().size() > 0){
					if (xPVC.getRenderIds().contains(xToolbarControlFacet.getClientId())){
						return true;
					}
				}
			}
		}
		//Se é o dataTable
		if(pSource.equals(this)){
			return true;
		}
		
		return false;
	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, "");
	}

	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, "");
	}

	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}

	public String getRowStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.rowStyleClass, "-odd,-even");
	}

	public void setRowStyleClass(String pRowStyleClass) {
		getStateHelper().put(PropertyKeys.rowStyleClass, pRowStyleClass);
		handleAttribute("rowStyleClass", pRowStyleClass);
	}

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, "");
	}

	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}

	
//	public void setValue(LinkedHashMap<String, Object> pValue) {
//		List<Object> xList = new ArrayList<Object>(pValue.values());
//		this.setValue(xList);
//	}
	
	/**
	 * Retorna se está habilitada a opção de multiplos seleções de registros
	 * 
	 * @return
	 */
	public Boolean getMultipleSelection() {
		return (Boolean) getStateHelper().eval(PropertyKeys.multipleSelection, false);
	}

	/**
	 * Configura se está habilitada a opção de multiplos seleções de registros
	 * 
	 * @param pMultipleSelection
	 */
	public void setMultipleSelection(Boolean pMultipleSelection) {
		getStateHelper().put(PropertyKeys.multipleSelection, pMultipleSelection);
		handleAttribute("multipleSelection", pMultipleSelection);
	}

	/**
	 * Indica se item da lista pode ser selecionado.
	 * @return
	 */
	public Boolean isSelectable() {
		if (this.getHasViewOneAction() 
		 || this.getMultipleSelection() 
		 || this .getClientBehaviors().size() > 0 //Se houver algum <f:ajax
		 || !this.getKeyColumnName().equals("")){
			return true;
		}else{
			return false;
		}
	}

	public String getSearchAction() {
		return DBSFaces.getELString(this,PropertyKeys.searchAction.toString());
	}

	public void setSearchAction(String pSearchAction) {
		getStateHelper().put(PropertyKeys.searchAction, pSearchAction);
		handleAttribute("searchAction", pSearchAction);
	}

	public String getViewOneAction() {
		return DBSFaces.getELString(this,PropertyKeys.viewOneAction.toString());
	}

	public void setViewOneAction(String pViewOneAction) {
		getStateHelper().put(PropertyKeys.viewOneAction, pViewOneAction);
		handleAttribute("viewOneAction", pViewOneAction);
	}

	public String getSelectAllAction() {
		return DBSFaces.getELString(this,PropertyKeys.selectAllAction.toString());
	}

	public void setSelectAllAction(String pSelectAllAction) {
		getStateHelper().put(PropertyKeys.selectAllAction, pSelectAllAction);
		handleAttribute("selectAllAction", pSelectAllAction);
	}


	public String getSelected() {
		// return (String) getStateHelper().eval(PropertyKeys.selected, false);
		return DBSFaces.getELString(this,PropertyKeys.selected.toString());
	}

	public void setSelected(String pSelected) {
		getStateHelper().put(PropertyKeys.selected, pSelected);
		handleAttribute("selected", pSelected);
	}

	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, "");
	}
	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}

	public String getKeyColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.keyColumnName, "");
	}
	public void setKeyColumnName(String pKeyColumnName) {
		getStateHelper().put(PropertyKeys.keyColumnName, pKeyColumnName);
		handleAttribute("keyColumnName", pKeyColumnName);
	}
	
	public Integer getCurrentRowIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.currentRowIndex, -1);
	}

	public void setCurrentRowIndex(Integer pCurrentRowIndex) {
		getStateHelper().put(PropertyKeys.currentRowIndex, pCurrentRowIndex);
		handleAttribute("currentRowIndex", pCurrentRowIndex);
	}

	public boolean getHasViewOneAction() {
		return getValueExpression("viewOneAction") != null;
	}

 	public String getIconClass() {
 		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
 	}

 	public void setIconClass(String pIconClass) {
 		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
 		handleAttribute("iconClass", pIconClass);
 	}
	
	private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("change", "click", "blur", "dblclick","keydown", "keypress", "keyup", "mousedown", "mousemove","mouseout", "mouseover", "mouseup", "select"));

	@Override
	public Collection<String> getEventNames() {
		return EVENT_NAMES;
	}

	@Override
	public String getDefaultEventName() {
		return "select";
	}


}