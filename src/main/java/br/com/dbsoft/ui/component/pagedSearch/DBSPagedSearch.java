package br.com.dbsoft.ui.component.pagedSearch;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

import br.com.dbsoft.pagedSearch.DBSPagedSearchController;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSPagedSearch.COMPONENT_TYPE)
public class DBSPagedSearch extends DBSUIComponentBase implements NamingContainer, ClientBehaviorHolder{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.PAGEDSEARCH;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
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
	public static final String ID_SELECT_KEY = "-selectKey";
	public static final String ID_SELECT_ROW = "selectRow";
	public static final String ID_BTSELECT = "selectItem";
	
	protected enum PropertyKeys {
		var,
		pagedSearch,
		suggestion,
		keyColumnName,
		displayValueColumnName,
		styleClass,
		style;

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

    public String getVar() {
		return (String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(String pVar) {
		getStateHelper().put(PropertyKeys.var, pVar);
		handleAttribute("var", pVar);
	}
	
    @SuppressWarnings("rawtypes")
	public DBSPagedSearchController getPageSearch() {
		return (DBSPagedSearchController) getStateHelper().eval(PropertyKeys.pagedSearch, null);
	}
    @SuppressWarnings("rawtypes")
	public void setPageSearch(DBSPagedSearchController pPageSearch) {
		getStateHelper().put(PropertyKeys.pagedSearch, pPageSearch);
		handleAttribute("pagedSearch", pPageSearch);
	}
	
    public Boolean getSuggestion() {
		return (Boolean) getStateHelper().eval(PropertyKeys.suggestion, null);
	}
	public void setSuggestion(Boolean pSuggestion) {
		getStateHelper().put(PropertyKeys.suggestion, pSuggestion);
		handleAttribute("suggestion", pSuggestion);
	}
	
	public String getKeyColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.keyColumnName, null);
	}
	public void setKeyColumnName(String pKeyColumnName) {
		getStateHelper().put(PropertyKeys.keyColumnName, pKeyColumnName);
		handleAttribute("keyColumnName", pKeyColumnName);
	}
	
	public String getDisplayValueColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.displayValueColumnName, null);
	}
	public void setDisplayValueColumnName(String pDisplayValueColumnName) {
		getStateHelper().put(PropertyKeys.displayValueColumnName, pDisplayValueColumnName);
		handleAttribute("displayValueColumnName", pDisplayValueColumnName);
	}
    
    public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

}
