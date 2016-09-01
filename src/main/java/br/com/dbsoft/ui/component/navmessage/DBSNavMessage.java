package br.com.dbsoft.ui.component.navmessage;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSNavMessage.COMPONENT_TYPE)
public class DBSNavMessage extends DBSUIComponentBase implements NamingContainer{
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.NAVMESSAGE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	public final static String FACET_HEADER = "header";
	public final static String FACET_FOOTER = "footer";

	protected enum PropertyKeys {
		styleClass,
		style,
		location,
		closeTimeout,
		contentPadding,
		contentStyleClass,
		contentVerticalAlign,
		contentHorizontalAlign,
		opened,
		message;

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
	
    public DBSNavMessage(){
		setRendererType(DBSNavMessage.RENDERER_TYPE);
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
	
	public Boolean getOpened() {
		return (Boolean) getStateHelper().eval(PropertyKeys.opened, false);
	}
	
	public void setOpened(Boolean pOpened) {
		getStateHelper().put(PropertyKeys.opened, pOpened);
		handleAttribute("opened", pOpened);
	}
	
	public String getLocation() {
		return (String) getStateHelper().eval(PropertyKeys.location, null);
	}
	
	public void setLocation(String pLocation) {
		getStateHelper().put(PropertyKeys.location, pLocation);
		handleAttribute("location", pLocation);
	}
	
	public String getCloseTimeout() {
		return (String) getStateHelper().eval(PropertyKeys.closeTimeout, "0");
	}
	
	public void setCloseTimeout(String pCloseTimeout) {
		getStateHelper().put(PropertyKeys.closeTimeout, pCloseTimeout);
		handleAttribute("closeTimeout", pCloseTimeout);
	}

	public String getContentPadding() {
		return (String) getStateHelper().eval(PropertyKeys.contentPadding, "0.4em");
	}
	public void setContentPadding(String pContentPadding) {
		getStateHelper().put(PropertyKeys.contentPadding, pContentPadding);
		handleAttribute("contentPadding", pContentPadding);
	}

	public String getContentStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.contentStyleClass, null);
	}
	
	public void setContentStyleClass(String pContentStyleClass) {
		getStateHelper().put(PropertyKeys.contentStyleClass, pContentStyleClass);
		handleAttribute("contentStyleClass", pContentStyleClass);
	}
	
	public String getContentVerticalAlign() {
		return (String) getStateHelper().eval(PropertyKeys.contentVerticalAlign, null);
	}
	
	public void setContentVerticalAlign(String pContentVerticalAlign) {
		getStateHelper().put(PropertyKeys.contentVerticalAlign, pContentVerticalAlign);
		handleAttribute("contentVerticalAlign", pContentVerticalAlign);
	}
	
	public String getContentHorizontalAlign() {
		return (String) getStateHelper().eval(PropertyKeys.contentHorizontalAlign, null);
	}
	
	public void setContentHorizontalAlign(String pContentHorizontalAlign) {
		getStateHelper().put(PropertyKeys.contentHorizontalAlign, pContentHorizontalAlign);
		handleAttribute("contentHorizontalAlign", pContentHorizontalAlign);
	}
	
	public String getMessage() {
		return (String) getStateHelper().eval(PropertyKeys.message, null);
	}
	
	public void setMessage(String pMessage) {
		getStateHelper().put(PropertyKeys.message, pMessage);
		handleAttribute("message", pMessage);
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
