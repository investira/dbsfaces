package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;

import br.com.dbsoft.ui.component.DBSUIOutput;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSDialog.COMPONENT_TYPE)
public class DBSDialog extends DBSUIOutput implements NamingContainer{  

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOG;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;


	protected enum PropertyKeys {
		caption,
		width,
		height,
		resizable,
		closeable,
		okAction,
		yesAction,
		noAction,
		update,
		execute,
		messageType,
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
	

	
    public DBSDialog(){
		setRendererType(DBSDialog.RENDERER_TYPE);
    }
	
    @Override
    public void decode(FacesContext pContext) {
        super.decode(pContext);
    }

	public String getCaption() {
		return (String) getStateHelper().eval(PropertyKeys.caption, null);
	}
	
	public void setCaption(String pCaption) {
		getStateHelper().put(PropertyKeys.caption, pCaption);
		handleAttribute("caption", pCaption);
	}
	

	public Boolean isResizable() {
		return (Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
	}
	
	public void setResizable(Boolean pResizable) {
		getStateHelper().put(PropertyKeys.resizable, pResizable);
		handleAttribute("resizable", pResizable);
	}
	

	public void setCloseble(Boolean pCloseble) {
		getStateHelper().put(PropertyKeys.closeable, pCloseble);
		handleAttribute("closeable", pCloseble);
	}
	
	public Boolean getCloseble() {
		return (Boolean) getStateHelper().eval(PropertyKeys.closeable, true);
	}

	public void setWidth(Integer pWidth) {
		getStateHelper().put(PropertyKeys.width, pWidth);
		handleAttribute("width", pWidth);
	}
	
	public Integer getWidth() {
		return (Integer) getStateHelper().eval(PropertyKeys.width, null);
	}

	public void setHeight(Integer pHeight) {
		getStateHelper().put(PropertyKeys.height, pHeight);
		handleAttribute("height", pHeight);
	}
	
	public Integer getHeight() {
		return (Integer) getStateHelper().eval(PropertyKeys.height, null);
	}
	
	public String getMessageType() {
		return (String) getStateHelper().eval(PropertyKeys.messageType, null);
	}
	
	public void setMessageType(String pMessageType) {
		getStateHelper().put(PropertyKeys.messageType, pMessageType);
		handleAttribute("messageType", pMessageType);
	}

	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, "");
	}
	
	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}

	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, "");
	}
	
	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}
// ################################################################

	public String getYesAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.yesAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.yesAction, null);
		}
		return xStr;

	}

	public void setYesAction(String pYesAction) {
    	getStateHelper().put(PropertyKeys.yesAction, pYesAction);
		handleAttribute("yesAction", pYesAction);
	}	
	
	public String getNoAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.noAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.noAction, null);
		}
		return xStr;
	}

	public void setNoAction(String pNoAction) {
    	getStateHelper().put(PropertyKeys.noAction, pNoAction);
		handleAttribute("noAction", pNoAction);
	}	

	public String getOkAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.okAction.toString());
		if (xStr==null){
			//Considera que action por ser um redirect para outra página
			xStr = (String) getStateHelper().eval(PropertyKeys.okAction, null);
		}
		return xStr;
	}

	public void setOkAction(String pOkAction) {
    	getStateHelper().put(PropertyKeys.okAction, pOkAction);
		handleAttribute("okAction", pOkAction);
	}	
	
	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, "");
	}
	
	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}


}