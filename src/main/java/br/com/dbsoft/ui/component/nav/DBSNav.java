package br.com.dbsoft.ui.component.nav;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSNav.COMPONENT_TYPE)
public class DBSNav extends DBSUIComponentBase {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.NAV;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public final static String FACET_HEADER = "header";
	public final static String FACET_FOOTER = "footer";
	public final static String FACET_TOOLBAR = "toolbar";


	protected enum PropertyKeys {
		styleClass,
		style,
		iconClass,
		location,
		padding;

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
	
	public static enum LOCATION {
		TOP_LEFT_VERTICAL		("tlv",true,true,true),
		TOP_LEFT_HORIZONTAL 	("tlh",true,true,false),
		TOP_RIGHT_VERTICAL		("trv",true,false,true),
		TOP_RIGHT_HORIZONTAL 	("trh",true,false,false),
		BOTTOM_LEFT_VERTICAL	("blv",false,true,true),
		BOTTOM_LEFT_HORIZONTAL 	("blh",false,true,false),
		BOTTOM_RIGHT_VERTICAL	("brv",false,false,true),
		BOTTOM_RIGHT_HORIZONTAL ("brh",false,false,false);
		
		private String 	wCode;
		private Boolean	wIsTop;
		private Boolean	wIsLeft;
		private Boolean	wIsVertical;
		
		private LOCATION(String pCode, Boolean pIsTop, Boolean pIsLeft, Boolean pIsVertical) {
			wCode = pCode;
			wIsTop = pIsTop;
			wIsLeft = pIsLeft;
			wIsVertical = pIsVertical;
		}

		public String getCode() {
			return wCode;
		}
		public String getCSS() {
			return " -" + wCode;
		}
		public Boolean getIsTop(){
			return wIsTop;
		}
		public Boolean getIsLeft(){
			return wIsLeft;
		}
		public Boolean getIsVertical(){
			return wIsVertical;
		}
		
		public static LOCATION get(String pCode) {
			if (pCode == null){
				return TOP_LEFT_HORIZONTAL;
			}			
			pCode = pCode.trim().toLowerCase();
			switch (pCode) {
			case "tlh":
				return TOP_LEFT_HORIZONTAL;
			case "tlv":
				return TOP_LEFT_VERTICAL;
			case "trh":
				return TOP_RIGHT_HORIZONTAL;
			case "trv":
				return TOP_RIGHT_VERTICAL;
			case "blh":
				return BOTTOM_LEFT_HORIZONTAL;
			case "blv":
				return BOTTOM_LEFT_VERTICAL;
			case "brh":
				return BOTTOM_RIGHT_HORIZONTAL;
			case "brv":
				return BOTTOM_RIGHT_VERTICAL;
			default:
				return TOP_LEFT_HORIZONTAL;
			}
		}	
	}
    public DBSNav(){
		setRendererType(DBSNav.RENDERER_TYPE);
    }
	

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}
	
	public String getLocation() {
		return (String) getStateHelper().eval(PropertyKeys.location, LOCATION.TOP_LEFT_HORIZONTAL.getCode());
	}
	
	public void setLocation(String pLocation) {
		getStateHelper().put(PropertyKeys.location, pLocation);
		handleAttribute("location", pLocation);
	}

	public String getPadding() {
		return (String) getStateHelper().eval(PropertyKeys.padding, "0.4em");
	}
	public void setPadding(String pPadding) {
		getStateHelper().put(PropertyKeys.padding, pPadding);
		handleAttribute("padding", pPadding);
	}

	
	@Override
    public String getDefaultEventName()
    {
        return "click";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup"); 
	}
}
