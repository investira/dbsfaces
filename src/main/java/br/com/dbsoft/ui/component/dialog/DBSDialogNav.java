package br.com.dbsoft.ui.component.dialog;

import java.util.List;

import javax.faces.application.FacesMessage;
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
	public List<FacesMessage> getListFacesMessage() {return null;}
	@Override
	public void setListFacesMessage(List<FacesMessage> pListFacesMessage) {}

	@Override
	public <T extends IDBSMessage> List<T> getListDBSMessage() {return null;}
	@Override
	public <T extends IDBSMessage> void setListDBSMessage(List<T> pListDBSMessage) {}


}