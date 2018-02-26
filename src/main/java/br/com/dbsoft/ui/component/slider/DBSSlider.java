package br.com.dbsoft.ui.component.slider;

import java.util.Arrays;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSSlider.COMPONENT_TYPE)
public class DBSSlider extends DBSUIInput implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.SLIDER;
	public final static String RENDERER_TYPE = COMPONENT_TYPE; 
	
	protected enum PropertyKeys {
		type,
		orientation,
		maxValue,
		minValue,
		beginValue,
		endValue,
		decimalPlaces,
		animated,
		tooltip,
		valuesList,
		labelsList,
		invertValuesListPosition,
		obs,
		showValues;
		
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
	
	public static enum TYPE {
		VALUES 				("v"),
		STEPS 				("s"),	
		RANGE   			("r");
		
		private String 	wName;
		
		private TYPE(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}

		public String getStyleClass() {
			return " -" + wName + " ";
		}

		public static TYPE get(String pType) {
			if (pType == null){
				return VALUES;
			}			
			pType = pType.trim().toLowerCase();
	    	for (TYPE xP:TYPE.values()) {
	    		if (xP.getName().equals(pType)){
	    			return xP;
	    		}
	    	}
	    	return VALUES;
		}	
	}

	public static enum ORIENTATION {
		HORIZONTAL 			("h"),
		VERTICAL 			("v");
		
		private String 	wName;
		
		private ORIENTATION(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}

		public String getStyleClass() {
			return " -" + wName + " ";
		}

		public static ORIENTATION get(String pType) {
			if (pType == null){
				return HORIZONTAL;
			}			
			pType = pType.trim().toLowerCase();
	    	for (ORIENTATION xP:ORIENTATION.values()) {
	    		if (xP.getName().equals(pType)){
	    			return xP;
	    		}
	    	}
	    	return HORIZONTAL;
		}	
	
	}
	
	public DBSSlider(){
		setRendererType(DBSSlider.RENDERER_TYPE);
    }
	
	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, TYPE.VALUES.getName());
	}
	
	public void setType(String pType) {
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}

	public String getOrientation() {
		return (String) getStateHelper().eval(PropertyKeys.orientation, ORIENTATION.HORIZONTAL.getName());
	}
	
	public void setOrientation(String pOrientation) {
		getStateHelper().put(PropertyKeys.orientation, pOrientation);
		handleAttribute("orientation", pOrientation);
	}
	
	public Double getMaxValue() {
		return (Double) getStateHelper().eval(PropertyKeys.maxValue, null);
	}
	
	public void setMaxValue(Double pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}

	public Double getMinValue() {
		return (Double) getStateHelper().eval(PropertyKeys.minValue, null);
	}
	
	public void setMinValue(Double pMinValue) {
		getStateHelper().put(PropertyKeys.minValue, pMinValue);
		handleAttribute("minValue", pMinValue);
	}

	public Double getBeginValue() {
		return (Double) getStateHelper().eval(PropertyKeys.beginValue, 0D);
	}
	
	public void setBeginValue(Double pvBeginValue) {
		getStateHelper().put(PropertyKeys.beginValue, pvBeginValue);
		handleAttribute("beginValue", pvBeginValue);
	}

	public Double getEndValue() {
		return (Double) getStateHelper().eval(PropertyKeys.endValue, 100D);
	}
	
	public void setEndValue(Double pEndValue) {
		getStateHelper().put(PropertyKeys.endValue, pEndValue);
		handleAttribute("endValue", pEndValue);
	}

	public void setShowValues(Boolean pShowValues) {
		getStateHelper().put(PropertyKeys.showValues, pShowValues);
		handleAttribute("showValues", pShowValues);
	}
	
	public Boolean getShowValues() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showValues, true);
	}	

	public String getObs() {
		return (String) getStateHelper().eval(PropertyKeys.obs, null);
	}
	
	public void setObs(String pObs) {
		getStateHelper().put(PropertyKeys.obs, pObs);
		handleAttribute("obs", pObs);
	}

	public void setInvertValuesListPosition(Boolean pInvertValuesListPosition) {
		getStateHelper().put(PropertyKeys.invertValuesListPosition, pInvertValuesListPosition);
		handleAttribute("invertListValuesPosition", pInvertValuesListPosition);
	}
	
	public Boolean getInvertValuesListPosition() {
		return (Boolean) getStateHelper().eval(PropertyKeys.invertValuesListPosition, false);
	}	

	public Object getValuesList() {
		return getStateHelper().eval(PropertyKeys.valuesList, Arrays.asList());
	}
	
	@SuppressWarnings("unchecked")
	public void setValuesList(Object pValuesList) {
		if (pValuesList == null){return;}
		if (pValuesList instanceof List){
			pvSetValuesList((List<String>) pValuesList);
		}else{
			List<String> xValuesList = Arrays.asList(((String) pValuesList).split("\\s*;\\s*"));
			pvSetValuesList(xValuesList);
		}
	}

	public void pvSetValuesList(List<String> pValuesList) {
		getStateHelper().put(PropertyKeys.valuesList, pValuesList);
		handleAttribute("valuesList", pValuesList);
	}
	
	public Object getLabelsList() {
		return getStateHelper().eval(PropertyKeys.labelsList, Arrays.asList());
	}
	
	@SuppressWarnings("unchecked")
	public void setLabelsList(Object pLabelsList) {
		if (pLabelsList == null){return;}
		if (pLabelsList instanceof List){
			pvSetLabelsList((List<String>) pLabelsList);
		}else{
			List<String> xLabelsList = Arrays.asList(((String) pLabelsList).split("\\s*;\\s*"));
			pvSetLabelsList(xLabelsList);
		}
	}

	public void pvSetLabelsList(List<String> pLabelsList) {
		getStateHelper().put(PropertyKeys.labelsList, pLabelsList);
		handleAttribute("labelsList", pLabelsList);
	}
	
	public void setDecimalPlaces(Integer pDecimalPlaces) {
		getStateHelper().put(PropertyKeys.decimalPlaces, pDecimalPlaces);
		handleAttribute("decimalPlaces", pDecimalPlaces);
	}
	public Integer getDecimalPlaces() {
		return (Integer) getStateHelper().eval(PropertyKeys.decimalPlaces, 0);
	}	

	
}
