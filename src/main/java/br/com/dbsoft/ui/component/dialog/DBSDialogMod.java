package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSDialogMod.COMPONENT_TYPE)
public class DBSDialogMod extends DBSDialog{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOGMOD;

	@Override
	public String getType() {return TYPE.MOD.getName();}
	@Override
	public void setType(String pType) {}
	
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
	public IDBSMessage getDBSMessage() {return null;}
	@Override
	public void setDBSMessage(IDBSMessage pDBSMessage) {}


}