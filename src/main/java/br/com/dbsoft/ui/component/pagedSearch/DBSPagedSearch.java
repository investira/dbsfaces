package br.com.dbsoft.ui.component.pagedSearch;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;

import br.com.dbsoft.pagedSearch.DBSPagedSearchController;
import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSPagedSearch.COMPONENT_TYPE)
public class DBSPagedSearch extends DBSUIInputText implements NamingContainer{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.PAGEDSEARCH;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public static final String FACET_INPUT_VALOR = "facetInputValor";
	public static final String FACET_INPUTSEARCH = "facetInputSearch";
	public static final String FACET_INPUTSUGGESTION = "facetInputSuggestion";
	public static final String FACET_SEARCHMORE = "facetSearchMore";
	public static final String FACET_DIVINVISIVEL = "facetDivInvisivel";
	public static final String FACET_SELECTBUTTON = "facetSelectButton";
	
	public static final String ID_INPUTSEARCH = "inputSearch";
	public static final String ID_INPUTSUGGESTION = "inputSuggestion";
	public static final String ID_BTSEARCHMORE = "btSearchMore";
	public static final String ID_DIVRESULTS = "divResults";
	public static final String ID_VISIBLELISTCONTAINER = "visibleList";
	public static final String ID_DIVINVISIBLELIST = "invisibleListContainer";
	public static final String ID_INVISIBLELISTCONTAINER = "invisibleList";
	public static final String ID_PAGEDLIST = "pagedList";
	public static final String ID_ITEM = "pagedItem";
	public static final String ID_ITEMKEY = "itemKey";
	public static final String ID_ITEMDISPLAY = "itemDisplayValue";
	public static final String ID_SELECT_ROW = "selectRow";
	public static final String ID_BTSELECT = "selectItem";
	
	protected enum PropertyKeys {
		type,
		valor,
		var,
		controller,
		displayValueColumnName,
		contentStyleClass;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}
    public DBSPagedSearch(){
		setRendererType(DBSPagedSearch.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

    public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, "normal");
	}
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}
	
	public Object getValor() {
		return getStateHelper().eval(PropertyKeys.valor, null);
	}
	public void setValor(Object pValor) {
		getStateHelper().put(PropertyKeys.valor, pValor);
		handleAttribute("valor", pValor);
	}
	
    public String getVar() {
		return (String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(String pVar) {
		getStateHelper().put(PropertyKeys.var, pVar);
		handleAttribute("var", pVar);
	}
	
	@SuppressWarnings("rawtypes")
	public DBSPagedSearchController getController() {
		return (DBSPagedSearchController) getStateHelper().eval(PropertyKeys.controller, null);
	}
    @SuppressWarnings("rawtypes")
	public void setController(DBSPagedSearchController pController) {
		getStateHelper().put(PropertyKeys.controller, pController);
		handleAttribute("controller", pController);
	}
	
	public String getDisplayValueColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.displayValueColumnName, null);
	}
	public void setDisplayValueColumnName(String pDisplayValueColumnName) {
		getStateHelper().put(PropertyKeys.displayValueColumnName, pDisplayValueColumnName);
		handleAttribute("displayValueColumnName", pDisplayValueColumnName);
	}
    
    public String getContentStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.contentStyleClass, null);
	}
	public void setContentStyleClass(String pContentStyleClass) {
		getStateHelper().put(PropertyKeys.contentStyleClass, pContentStyleClass);
		handleAttribute("contentStyleClass", pContentStyleClass);
	}
	
}
