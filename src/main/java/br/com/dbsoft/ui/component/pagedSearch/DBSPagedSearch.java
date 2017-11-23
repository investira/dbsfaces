package br.com.dbsoft.ui.component.pagedSearch;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

import br.com.dbsoft.pagedSearch.IDBSPagedSearchController;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSPagedSearch.COMPONENT_TYPE)
public class DBSPagedSearch extends DBSUIComponentBase implements NamingContainer, ClientBehaviorHolder{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.PAGEDSEARCH;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public static final String FACET_INPUTSEARCH = "facetInputSearch";
	public static final String FACET_SEARCHMORE = "facetSearchMore";
	public static final String FACET_DIVINVISIVEL = "facetDivInvisivel";
	
	public static final String ID_INPUTSEARCH = "inputSearch";
	public static final String ID_BTSEARCHMORE = "btSearchMore";
	public static final String ID_DIVRESULTS = "divResults";
	public static final String ID_VISIBLELISTCONTAINER = "visibleList";
	public static final String ID_DIVINVISIBLELIST = "invisibleListContainer";
	public static final String ID_INVISIBLELISTCONTAINER = "invisibleList";
	public static final String ID_PAGEDLIST = "pagedList";
	public static final String ID_ITEM = "pagedItem";
	
	protected enum PropertyKeys {
		pagedSearch,
		var,
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

    @SuppressWarnings("rawtypes")
	public IDBSPagedSearchController getPageSearch() {
		return (IDBSPagedSearchController) getStateHelper().eval(PropertyKeys.pagedSearch, null);
	}
    @SuppressWarnings("rawtypes")
	public void setPageSearch(IDBSPagedSearchController pPageSearch) {
		getStateHelper().put(PropertyKeys.pagedSearch, pPageSearch);
		handleAttribute("pagedSearch", pPageSearch);
	}

    public String getVar() {
		return (String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(String pVar) {
		getStateHelper().put(PropertyKeys.var, pVar);
		handleAttribute("var", pVar);
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
