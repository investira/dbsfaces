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
		tabPages;

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
