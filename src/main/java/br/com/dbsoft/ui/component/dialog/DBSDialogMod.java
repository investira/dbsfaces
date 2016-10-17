package br.com.dbsoft.ui.component.dialog;

import java.util.List;

import javax.faces.application.FacesMessage;
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
	public List<FacesMessage> getListFacesMessage() {return null;}
	@Override
	public void setListFacesMessage(List<FacesMessage> pListFacesMessage) {}

	@Override
	public <T extends IDBSMessage> List<T> getListDBSMessage() {return null;}
	@Override
	public <T extends IDBSMessage> void setListDBSMessage(List<T> pListDBSMessage) {}


}