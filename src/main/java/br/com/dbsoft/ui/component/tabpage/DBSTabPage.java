package br.com.dbsoft.ui.component.tabpage;

import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.behavior.ClientBehaviorHolder;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSTabPage.COMPONENT_TYPE)
public class DBSTabPage extends DBSUIComponentBase implements NamingContainer, ClientBehaviorHolder {

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.TABPAGE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	
	protected enum PropertyKeys {
		caption,
		style,
		styleClass,
		iconClass,
		selected,
		file,
		closeble,
		closeAction,
		selectedAction;

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
 
   public DBSTabPage(String pCaption, String pFile, boolean pCloseble, String pCloseAction, String pSelectedAction, String pStyle, String pStyleClass){
    	this.setCaption(pCaption);
    	this.setFile(pFile);
    	this.setCloseble(pCloseble);
    	this.setCloseAction(pCloseAction);
    	this.setSelectedAction(pSelectedAction);
    	this.setStyle(pStyle);
    	this.setStyleClass(pStyleClass);
    }
   
	public DBSTabPage(){
		setRendererType(DBSTabPage.RENDERER_TYPE);
    }
    
 	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, "");
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("styleClass", pCaption);
	}
	
	public Boolean getCloseble() {
		return (Boolean) getStateHelper().eval(PropertyKeys.closeble, false);
	}
	
	public void setCloseble(Boolean pCloseble) {
		getStateHelper().put(PropertyKeys.closeble, pCloseble);
		handleAttribute("closeble", pCloseble);
	}	

	public String getFile() {
		return (String) getStateHelper().eval(PropertyKeys.file, null);
	}
	
	public void setFile(String pFile) {
		getStateHelper().put(PropertyKeys.file, pFile);
		handleAttribute("file", pFile);
	}	

	public Boolean getSelected() {
		return (Boolean) getStateHelper().eval(PropertyKeys.selected, false);
	}
	
	public void setSelected(Boolean pSelected) {
		getStateHelper().put(PropertyKeys.selected, pSelected);
		handleAttribute("selected", pSelected);
	}	

	
	public String getIconClass() {
		return (String) getStateHelper().eval(PropertyKeys.iconClass, "");
	}
	
	public void setIconClass(String pIconClass) {
		getStateHelper().put(PropertyKeys.iconClass, pIconClass);
		handleAttribute("iconClass", pIconClass);
	}	


	//actions ====================================================================

	public String getSelectedAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.selectedAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.selectedAction, null);
		}
		return xStr;
	}

	public void setSelectedAction(String ppSelectedAction) {
    	getStateHelper().put(PropertyKeys.selectedAction, ppSelectedAction);
		handleAttribute("selectedAction", ppSelectedAction);
	}	
	
	public String getCloseAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.closeAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.closeAction, null);
		}
		return xStr;
	}
	public void setCloseAction(String pCloseAction) {
    	getStateHelper().put(PropertyKeys.closeAction, pCloseAction);
		handleAttribute("closeAction", pCloseAction);
	}

	
	@Override
    public String getDefaultEventName()
    {
        return "click";
    }
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("click", "dblclick", "blur", "focus", "select", "change"); 
	}	

}
