package br.com.dbsoft.ui.component.tab;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;



@FacesComponent(DBSTab.COMPONENT_TYPE)
public class DBSTab extends DBSUIOutput implements NamingContainer, ClientBehaviorHolder {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.TAB;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		selectedTabPage,
		showTabPageOnClick,
		tabPages,
		captionAlignment;

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
	
	public static enum CAPTION_ALIGMENT {
		LEFT			("l"),
	    RIGHT 			("r"),
		CENTER 			("c"),
		EVEN 			("e");	
		
		private String 	wName;
		
		private CAPTION_ALIGMENT(String pName) {
			this.wName = pName;
		}

		public String getName() {
			return wName;
		}
		
		public String getStyleClass() {
			return " -" + wName;
		}

		public static CAPTION_ALIGMENT get(String pCode) {
			if (pCode == null){
				return LEFT;
			}			
			pCode = pCode.trim().toLowerCase();
	    	for (CAPTION_ALIGMENT xT:CAPTION_ALIGMENT.values()) {
	    		if (xT.getName().equals(pCode)){
	    			return xT;
	    		}
	    	}
	    	return null;
		}	
	}
	
	
    public DBSTab(){
		setRendererType(DBSTab.RENDERER_TYPE);
		//FacesContext xContext = FacesContext.getCurrentInstance();
	    //UIViewRoot xRoot = xContext.getViewRoot();
	    //xRoot.subscribeToViewEvent(PreRenderViewEvent.class, this );
    }
	
	public String getSelectedTabPage() {
		return (String) getStateHelper().eval(PropertyKeys.selectedTabPage, "");
	}
	
	public void setSelectedTabPage(String pSelectedTabPage) {
		getStateHelper().put(PropertyKeys.selectedTabPage, pSelectedTabPage);
		handleAttribute("selectedTabPage", pSelectedTabPage);
	}


	public Boolean getShowTabPageOnClick() {
		return (Boolean) getStateHelper().eval(PropertyKeys.showTabPageOnClick, true);
	}

	public void setShowTabPageOnClick(Boolean pShowTabPageOnClick) {
		getStateHelper().put(PropertyKeys.showTabPageOnClick, pShowTabPageOnClick);
		handleAttribute("showTabPageOnClick", pShowTabPageOnClick);
	}

	public DBSTabPages getTabPages() {
		return (DBSTabPages) getStateHelper().eval(PropertyKeys.tabPages, null);
	}
	
	public void setTabPages(DBSTabPages pTabPages) {
		getStateHelper().put(PropertyKeys.tabPages, pTabPages);
		handleAttribute("tabPages", pTabPages);
	}

	public String getCaptionAlignment() {
		return (String) getStateHelper().eval(PropertyKeys.captionAlignment, CAPTION_ALIGMENT.LEFT.getName());
	}
	
	public void setCaptionAlignment(String pCaptionAlignment) {
		CAPTION_ALIGMENT xCaptionAlignment = CAPTION_ALIGMENT.get(pCaptionAlignment);
		if (xCaptionAlignment == null){
			return;
		}
		getStateHelper().put(PropertyKeys.captionAlignment, pCaptionAlignment);
		handleAttribute("captionAlignment", pCaptionAlignment);
	}
	

	@Override
    public String getDefaultEventName()
    {
        return "select";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "dblclick", "blur", "focus", "select", "change"); 
	}	
	
	public String getInputId(boolean pFullId){
		String xId;
		if (pFullId){
			xId = getClientId() + ":input";
		}else{
			xId = "input";
		}
		return xId;
	}

}
