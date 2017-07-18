package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.message.IDBSMessages;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSDialogBtn.COMPONENT_TYPE)
public class DBSDialogBtn extends DBSDialog{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOGBTN;
	
	@Override
	public String getType() {return TYPE.BTN.getName();}
	@Override
	public void setType(String pType) {}
	
	@Override
	public void setPosition(String pPosition) {}
	@Override
	public String getPosition() {return super.getPosition();}
	
	@Override
	public String getContentAlignment() {return super.getContentAlignment();}
	@Override
	public void setContentAlignment(String pContentAlignment) {}

	@Override
	public String getMsgType() {return null;}
	@Override
	public void setMsgType(String pMsgType) {}
	
	@Override
	public String getMsgFor() {return null;}
	@Override
	public void setMsgFor(String pMsgFor) {}

	@Override
	public String getCloseTimeout() {return "0";}
	@Override
	public void setCloseTimeout(String pCloseTimeout) {}

	@Override
	public IDBSMessages getDBSMessages() {return null;}

	@Override
	public void setDBSMessages(IDBSMessages pDBSMessages) {}


}