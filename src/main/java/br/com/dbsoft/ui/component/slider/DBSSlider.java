package br.com.dbsoft.ui.component.slider;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIInput;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;

@FacesComponent(DBSSlider.COMPONENT_TYPE)
public class DBSSlider extends DBSUIInput {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.SLIDER;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		type,
		orientation,
		maxValue,
		minValue,
		animated,
		tooltip;
		
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
	    OPTION 				("o");
		
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
		return (Double) getStateHelper().eval(PropertyKeys.maxValue, 100D);
	}
	
	public void setMaxValue(Double pMaxValue) {
		getStateHelper().put(PropertyKeys.maxValue, pMaxValue);
		handleAttribute("maxValue", pMaxValue);
	}

	public Double getMinValue() {
		return (Double) getStateHelper().eval(PropertyKeys.minValue, 0D);
	}
	
	public void setMinValue(Double pMinValue) {
		getStateHelper().put(PropertyKeys.minValue, pMinValue);
		handleAttribute("minValue", pMinValue);
	}

		
//	public Double getWidth() {
//		return (Double) getStateHelper().eval(PropertyKeys.width, 16D);
//	}
//	public void setWidth(Double pWidth) {
//		getStateHelper().put(PropertyKeys.width, pWidth);
//		handleAttribute("width", pWidth);
//	}


	@Override
	public Double getValue() {
		return DBSNumber.toDouble(super.getValue());
	}
	
	public void setAnimated(Boolean pAnimated) {
		getStateHelper().put(PropertyKeys.animated, pAnimated);
		handleAttribute("animated", pAnimated);
	}
	
	public Boolean getAnimated() {
		return (Boolean) getStateHelper().eval(PropertyKeys.animated, false);
	}	

}
