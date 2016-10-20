package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;

import br.com.dbsoft.message.IDBSMessage;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesComponent(DBSDialogNav.COMPONENT_TYPE)
public class DBSDialogNav extends DBSDialog{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.DIALOGNAV;
	
	@Override
	public String getType() {return TYPE.NAV.getName();}
	@Override
	public void setType(String pType) {}
	
	@Override
	public void setPosition(String pPosition) {
		POSITION xP = POSITION.get(pPosition);
		if (xP == null || xP == POSITION.CENTER){
			System.out.println("Position invalid\t:" + pPosition);
			return;
		}
		super.setPosition(pPosition);
	}

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
	public void setDBSMessage(IDBSMessage pListDBSMessage) {}


}