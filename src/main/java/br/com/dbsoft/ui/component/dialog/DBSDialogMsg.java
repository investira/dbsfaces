package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.message.IDBSMessage.MESSAGE_TYPE;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSDialogMsg.COMPONENT_TYPE)
public class DBSDialogMsg extends DBSDialog{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOGMSG;

	@Override
	public String getType() {return TYPE.MSG.getName();}
	@Override
	public void setType(String pType) {}
	
	@Override
	public String getIconClass(){return null;}
	@Override
	public void setIconClass(String pIconClass) {}
	
	@Override
	public String getCloseTimeout() {
		return (String) getStateHelper().eval(PropertyKeys.closeTimeout, "a");
	}

	@Override
	public String getContentAlignment() {
		return (String) getStateHelper().eval(PropertyKeys.contentAlignment, CONTENT_ALIGNMENT.CENTER.getName());
	}
	
	@Override
	public String getContentPadding() {
		return (String) getStateHelper().eval(PropertyKeys.contentPadding, "0.6em");
	}

	@Override
	public String getMsgType() {
		return (String) getStateHelper().eval(PropertyKeys.msgType, MESSAGE_TYPE.INFORMATION.getName());

	}
	
	public String getCloseTimeout1() {
		return (String) getStateHelper().eval(PropertyKeys.closeTimeout, "a");
	}


}