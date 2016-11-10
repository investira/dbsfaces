package br.com.dbsoft.ui.component.messagelist;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.message.IDBSMessagesController;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;

@FacesComponent(DBSMessageList.COMPONENT_TYPE)
public class DBSMessageList extends DBSUIInput implements NamingContainer {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.MESSAGELIST;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;

	protected enum PropertyKeys {
		update,
		messageKey,
		deleting;

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

	public DBSMessageList() {
		setRendererType(DBSMessageList.RENDERER_TYPE);
	}

//
//	@Override
//	public String getStyle() {
//		return (String) getStateHelper().eval(PropertyKeys.style, "");
//	}
//
//	@Override
//	public void setStyle(String pStyle) {
//		getStateHelper().put(PropertyKeys.style, pStyle);
//		handleAttribute("style", pStyle);
//	}
//
//	@Override
//	public String getStyleClass() {
//		return (String) getStateHelper().eval(PropertyKeys.styleClass, "");
//	}
//
//	@Override
//	public void setStyleClass(String pStyleClass) {
//		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
//		handleAttribute("styleClass", pStyleClass);
//	}

	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, "");
	}
	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}

	public String getMessageKey() {
		return (String) getStateHelper().eval(PropertyKeys.messageKey, "");
	}

	public void setMessageKey(String pMessageKey) {
		getStateHelper().put(PropertyKeys.messageKey, pMessageKey);
		handleAttribute("messageKey", pMessageKey);
	}
	
	public void setValue(IDBSMessagesController pValue) {
		super.setValue(pValue);
	}

	@Override
	public IDBSMessages getValue() {
		return (IDBSMessages) super.getValue();
	}
	
	public void setDeleting(Boolean pDeleting) {
		getStateHelper().put(PropertyKeys.deleting, pDeleting);
		handleAttribute("deleting", pDeleting);
	}
	public Boolean getDeleting() {
		return (Boolean) getStateHelper().eval(PropertyKeys.deleting, false);
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