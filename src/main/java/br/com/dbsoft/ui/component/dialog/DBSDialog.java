package br.com.dbsoft.ui.component.dialog;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;

import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;


@FacesComponent(DBSDialog.COMPONENT_TYPE)
public class DBSDialog extends DBSUIOutput implements NamingContainer{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOG;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	

	public final static String FACET_HEADER = "header";
	public final static String FACET_FOOTER = "footer";
	public final static String FACET_TOOLBAR = "toolbar";
	
	public final static String MSG_FOR_ALL = "@all";
	public final static String MSG_FOR_GLOBAL = "@global";

	protected enum PropertyKeys {
		type,
		caption,
		iconClass,
		position,
		contentStyleClass,
		contentAlignment,
		contentSize,
		contentPadding,
		closeTimeout,
		msgType,
		msgFor,
		open,
		//Atributos internos
		listfacesmessage,
		dbsmessages;

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
		NAV 			("nav"),
	    MOD 			("mod"),
		MSG 			("msg");	
		
		private String 	wName;
		
		private TYPE(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}
		

		public static TYPE get(String pCode) {
			if (pCode == null){
				return MSG;
			}			
			pCode = pCode.trim().toLowerCase();
	    	for (TYPE xT:TYPE.values()) {
	    		if (xT.getName().equals(pCode)){
	    			return xT;
	    		}
	    	}
	    	return null;
//			switch (pCode) {
//			case "msg":
//				return MSG;
//			case "nav":
//				return NAV;
//			case "mod":
//				return MOD;
//			default:
//				return MSG;
//			}
		}	
	}
	
	public static enum POSITION {
		TOP 			("t"),
	    BOTTOM 			("b"),
		LEFT 			("l"),
	    RIGHT 			("r"),
		CENTER 			("c");	
		
		private String 	wName;
		
		private POSITION(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}
		
		public static POSITION get(String pCode) {
			if (pCode == null){
				return CENTER;
			}			
			pCode = pCode.trim().toLowerCase();
	    	for (POSITION xP:POSITION.values()) {
	    		if (xP.getName().equals(pCode)){
	    			return xP;
	    		}
	    	}
	    	return null;
//			switch (pCode) {
//			case "t":
//				return TOP;
//			case "b":
//				return BOTTOM;
//			case "l":
//				return LEFT;
//			case "r":
//				return RIGHT;
//			case "c":
//				return CENTER;
//			default:
//				return CENTER;
//			}
		}	
	}

	public static enum CONTENT_SIZE {
		SCREEN 			("s"),
	    AUTO 			("a");
		
		private String 	wName;
		
		private CONTENT_SIZE(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}
		
		public String getStyleClass() {
			return " -cs_" + wName;
		}

		public static CONTENT_SIZE get(String pCode) {
			if (pCode == null){
				return AUTO;
			}			
			pCode = pCode.trim().toLowerCase();
	    	for (CONTENT_SIZE xCS:CONTENT_SIZE.values()) {
	    		if (xCS.getName().equals(pCode)){
	    			return xCS;
	    		}
	    	}
	    	return null;
//	    	
//	    	switch (pCode) {
//			case "s":
//				return SCREEN;
//			case "a":
//				return AUTO;
//			default:
//				return null;
//			}
		}	
	}
	
	public static enum CONTENT_ALIGNMENT {
		TOP 			("t"),
	    BOTTOM 			("b"),
		LEFT 			("l"),
	    RIGHT 			("r"),
		CENTER 			("c");	
		
		private String 	wName;
		
		private CONTENT_ALIGNMENT(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}

		public static CONTENT_ALIGNMENT get(String pCode) {
			if (pCode == null){
				return CENTER;
			}			
			pCode = pCode.trim().toLowerCase();
	    	for (CONTENT_ALIGNMENT xCA:CONTENT_ALIGNMENT.values()) {
	    		if (xCA.getName().equals(pCode)){
	    			return xCA;
	    		}
	    	}
	    	return null;
//	    	switch (pCode) {
//			case "t":
//				return TOP;
//			case "b":
//				return BOTTOM;
//			case "l":
//				return LEFT;
//			case "r":
//				return RIGHT;
//			case "c":
//				return CENTER;
//			default:
//				return null;
//			}
		}	
	}


    public DBSDialog(){
		setRendererType(DBSDialog.RENDERER_TYPE);
    }

    
	public String getType() {
		return (String) getStateHelper().eval(PropertyKeys.type, TYPE.NAV.getName());
	}
	
	public void setType(String pType) {
		TYPE xType = TYPE.get(pType);
		if (xType == null){
			System.out.println("Type invalid\t:" + pType);
			return;
		}
		getStateHelper().put(PropertyKeys.type, pType);
		handleAttribute("type", pType);
	}
	
	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}
	
	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, null);
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}
	
	public Boolean getOpen() {
		return (Boolean) getStateHelper().eval(PropertyKeys.open, false);
	}
	
	public void setOpen(Boolean pOpen) {
		getStateHelper().put(PropertyKeys.open, pOpen);
		handleAttribute("open", pOpen);
	}
	
	public String getPosition() {
		return (String) getStateHelper().eval(PropertyKeys.position, POSITION.LEFT.getName());
	}
	
	public void setPosition(String pPosition) {
		POSITION xP = POSITION.get(pPosition);
		if (xP == null){
			System.out.println("Position invalid\t:" + pPosition);
			return;
		}
		getStateHelper().put(PropertyKeys.position, pPosition);
		handleAttribute("position", pPosition);
	}

	public String getContentSize() {
		return (String) getStateHelper().eval(PropertyKeys.contentSize, CONTENT_SIZE.AUTO.getName());
	}
	
	public void setContentSize(String pContentSize) {
		CONTENT_SIZE xCS = CONTENT_SIZE.get(pContentSize);
		if (xCS == null){
			System.out.println("ContentSize invalid\t:" + pContentSize);
			return;
		}
		getStateHelper().put(PropertyKeys.contentSize, pContentSize);
		handleAttribute("contentSize", pContentSize);
	}

	public String getContentAlignment() {
		return (String) getStateHelper().eval(PropertyKeys.contentAlignment, CONTENT_ALIGNMENT.TOP.getName());
	}
	
	public void setContentAlignment(String pContentAlignment) {
		if (CONTENT_ALIGNMENT.get(pContentAlignment) == null){
			System.out.println("ContentAlignment invalid\t:" + pContentAlignment);
			return;
		}
		getStateHelper().put(PropertyKeys.contentAlignment, pContentAlignment);
		handleAttribute("contentAlignment", pContentAlignment);
	}

	public String getMsgType() {
		return (String) getStateHelper().eval(PropertyKeys.msgType, null);
	}
	
	public void setMsgType(String pMsgType) {
		getStateHelper().put(PropertyKeys.msgType, pMsgType);
		handleAttribute("msgType", pMsgType);
	}
	
	public String getMsgFor() {
		return (String) getStateHelper().eval(PropertyKeys.msgFor, null);
	}
	
	public void setMsgFor(String pMsgFor) {
		if (pMsgFor != null && pMsgFor.length() > 0){
			pMsgFor = pMsgFor.trim().toLowerCase();
		}
		getStateHelper().put(PropertyKeys.msgFor, pMsgFor);
		handleAttribute("msgFor", pMsgFor);
	}

	public String getCloseTimeout() {
		return (String) getStateHelper().eval(PropertyKeys.closeTimeout, "0");
	}
	
	public void setCloseTimeout(String pCloseTimeout) {
		String xCloseTimeout = "0";
		if (pCloseTimeout != null && pCloseTimeout.length() > 0){
			pCloseTimeout = pCloseTimeout.trim().toLowerCase();
			if (DBSNumber.isNumber(pCloseTimeout)
			|| pCloseTimeout.substring(0,1).equals("a")){
			   xCloseTimeout = pCloseTimeout;
			}
		}
		getStateHelper().put(PropertyKeys.closeTimeout, xCloseTimeout);
		handleAttribute("closeTimeout", xCloseTimeout);
	}

	public String getContentPadding() {
		return (String) getStateHelper().eval(PropertyKeys.contentPadding, "0.4em");
	}
	public void setContentPadding(String pContentPadding) {
		getStateHelper().put(PropertyKeys.contentPadding, pContentPadding);
		handleAttribute("contentPadding", pContentPadding);
	}

	public String getContentStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.contentStyleClass, "");
	}
	
	public void setContentStyleClass(String pContentStyleClass) {
		getStateHelper().put(PropertyKeys.contentStyleClass, pContentStyleClass);
		handleAttribute("contentStyleClass", pContentStyleClass);
	}
	
	@SuppressWarnings("unchecked")
	public List<FacesMessage> getListFacesMessage() {
		return (List<FacesMessage>) getStateHelper().eval(PropertyKeys.listfacesmessage, null);
	}
	
	public void setListFacesMessage(List<FacesMessage> pListFacesMessage) {
		getStateHelper().put(PropertyKeys.listfacesmessage, pListFacesMessage);
		handleAttribute("listfacesmessage", pListFacesMessage);
	}

	public IDBSMessages<?> getDBSMessages() {
		return (IDBSMessages<?>) getStateHelper().eval(PropertyKeys.dbsmessages, null);
	}
	
	public void setDBSMessages(IDBSMessages<?> pDBSMessages) {
		getStateHelper().put(PropertyKeys.dbsmessages, pDBSMessages);
		handleAttribute("dbsmessages", pDBSMessages);
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