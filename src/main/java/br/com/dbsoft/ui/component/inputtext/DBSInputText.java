package br.com.dbsoft.ui.component.inputtext;


import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInputText;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSInputText.COMPONENT_TYPE)
public class DBSInputText extends DBSUIInputText{

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.INPUTTEXT;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	MethodExpression wSuggestionRefreshAction;

	protected enum PropertyKeys {
		size,
		secret,
		letterCase,
		autocomplete,
		suggestionsBean,
		suggestionKeyColumnName,
		suggestionNullText,
		suggestionUpdate;

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
	
    public DBSInputText(){
		setRendererType(DBSInputText.RENDERER_TYPE);
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
	
//	public void setSuggestionList(LinkedHashMap<String, Object> pSuggestionList) {
//		getStateHelper().put(PropertyKeys.suggestionList, pSuggestionList);
//		handleAttribute("suggestionList", pSuggestionList);
//	}
//	@SuppressWarnings("unchecked")
//	public LinkedHashMap<String, Object> getSuggestionList() {
//		return (LinkedHashMap<String, Object>) getStateHelper().eval(PropertyKeys.suggestionList, null);
//	}
//	

	
	public void setLetterCase(String pLetterCase) {
		getStateHelper().put(PropertyKeys.letterCase, pLetterCase);
		handleAttribute("letterCase", pLetterCase);
	}
	public String getLetterCase() {
		String xValue = (String) getStateHelper().eval(PropertyKeys.letterCase, "upperfirst");
		return xValue.toLowerCase().trim();
	}	

	public boolean hasSuggestion(){
		if (DBSFaces.getELString(this, PropertyKeys.suggestionsBean.toString())!=null){
			return true;
		}
		return false;
	}

	public void setSuggestionKeyColumnName(String pSuggestionKeyColumnName) {
		getStateHelper().put(PropertyKeys.suggestionKeyColumnName, pSuggestionKeyColumnName);
		handleAttribute("suggestionKeyColumnName", pSuggestionKeyColumnName);
	}
	public String getSuggestionKeyColumnName() {
		String xValue = (String) getStateHelper().eval(PropertyKeys.suggestionKeyColumnName, "");
		return xValue;
	}
	
	public void setSuggestionUpdate(String pSuggestionUpdate) {
		getStateHelper().put(PropertyKeys.suggestionUpdate, pSuggestionUpdate);
		handleAttribute("suggestionUpdate", pSuggestionUpdate);
	}

	public String getSuggestionUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.suggestionUpdate, null);
	}

	public void setSuggestionNullText(String pSuggestionNullText) {
		getStateHelper().put(PropertyKeys.suggestionNullText, pSuggestionNullText);
		handleAttribute("suggestionNullText", pSuggestionNullText);
	}

	public String getSuggestionNullText() {
		return (String) getStateHelper().eval(PropertyKeys.suggestionNullText, null);
	}
}