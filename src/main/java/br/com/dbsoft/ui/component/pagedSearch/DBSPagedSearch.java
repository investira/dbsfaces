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
	public static final String FACET_NEWSEARCH = "facetNewSearch";
	public static final String FACET_SEARCHMORE = "facetSearchMore";
	public static final String FACET_DIVINVISIVEL = "facetDivInvisivel";
	public static final String FACET_SELECTBUTTON = "facetSelectButton";
	
	public static final String ID_INPUTSEARCH = "inputSearch";
	public static final String ID_INPUTSUGGESTION = "inputSuggestion";
	public static final String ID_BTNEWSEARCH = "btNewSearch";
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
		size,
		secret,
		letterCase,
		autocomplete,
		valor,
		itemName,
		controller,
		openWhenSearch,
		resultListMaxHeight,
		contentStyleClass,
		keyValueColumnName,
		displayValueColumnName,
		
		itemObjectSelected;

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
	
	public Integer getSize() {
		return (Integer) getStateHelper().eval(PropertyKeys.size, 0);
	}
	
	public void setSize(Integer pSize) {
		getStateHelper().put(PropertyKeys.size, pSize);
		handleAttribute("size", pSize);
	}

	public void setSecret(Boolean pSecret) {
		getStateHelper().put(PropertyKeys.secret, pSecret);
		handleAttribute("secret", pSecret);
	}
	
	public Boolean getSecret() {
		return (Boolean) getStateHelper().eval(PropertyKeys.secret, false);
	}	

	public void setAutocomplete(String pAutocomplete) {
		getStateHelper().put(PropertyKeys.autocomplete, pAutocomplete);
		handleAttribute("autocomplete", pAutocomplete);
	}
	public String getAutocomplete() {
		return (String) getStateHelper().eval(PropertyKeys.autocomplete, "on");
	}	
	
	public void setLetterCase(String pLetterCase) {
		getStateHelper().put(PropertyKeys.letterCase, pLetterCase);
		handleAttribute("letterCase", pLetterCase);
	}
	public String getLetterCase() {
		String xValue;
		if (getSecret()) {
			xValue = (String) getStateHelper().eval(PropertyKeys.letterCase, "none");
		} else {
			xValue = (String) getStateHelper().eval(PropertyKeys.letterCase, "upperfirst");
		}
		return xValue.toLowerCase().trim();
	}	

	
	public String getValor() {
		return (String) getStateHelper().eval(PropertyKeys.valor, null);
	}
	public void setValor(String pValor) {
		getStateHelper().put(PropertyKeys.valor, pValor);
		handleAttribute("valor", pValor);
	}
	
    public String getItemName() {
		return (String) getStateHelper().eval(PropertyKeys.itemName, null);
	}
	public void setItemName(String pItemName) {
		getStateHelper().put(PropertyKeys.itemName, pItemName);
		handleAttribute("itemName", pItemName);
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
    
    public Boolean getOpenWhenSearch() {
    	return (Boolean) getStateHelper().eval(PropertyKeys.openWhenSearch, false);
    }	
    public void setOpenWhenSearch(Boolean pOpenWhenSearch) {
		getStateHelper().put(PropertyKeys.openWhenSearch, pOpenWhenSearch);
		handleAttribute("openWhenSearch", pOpenWhenSearch);
	}
    
    public String getResultListMaxHeight() {
    	return (String) getStateHelper().eval(PropertyKeys.resultListMaxHeight, null);
    }
    public void setResultListMaxHeight(String pResultListMaxHeight) {
    	getStateHelper().put(PropertyKeys.resultListMaxHeight, pResultListMaxHeight);
    	handleAttribute("resultListMaxHeight", pResultListMaxHeight);
    }
    
    public String getContentStyleClass() {
    	return (String) getStateHelper().eval(PropertyKeys.contentStyleClass, null);
    }
    public void setContentStyleClass(String pContentStyleClass) {
    	getStateHelper().put(PropertyKeys.contentStyleClass, pContentStyleClass);
    	handleAttribute("contentStyleClass", pContentStyleClass);
    }
	
    public String getKeyValueColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.keyValueColumnName, null);
	}
	public void setKeyValueColumnName(String pKeyValueColumnName) {
		getStateHelper().put(PropertyKeys.keyValueColumnName, pKeyValueColumnName);
		handleAttribute("keyValueColumnName", pKeyValueColumnName);
	}
    
	public String getDisplayValueColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.displayValueColumnName, null);
	}
	public void setDisplayValueColumnName(String pDisplayValueColumnName) {
		getStateHelper().put(PropertyKeys.displayValueColumnName, pDisplayValueColumnName);
		handleAttribute("displayValueColumnName", pDisplayValueColumnName);
	}
	
	public Object getItemObjectSelected() {
		return getStateHelper().eval(PropertyKeys.itemObjectSelected, null);
	}
	public void setItemObjectSelected(Object pItemObjectSelected) {
		getStateHelper().put(PropertyKeys.itemObjectSelected, pItemObjectSelected);
		handleAttribute("itemObjectSelected", pItemObjectSelected);
	}
}
