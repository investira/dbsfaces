package br.com.dbsoft.ui.component.dialog;

import javax.faces.component.FacesComponent;

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

//	@Override
//	public Boolean getOpen() {return false;}
//	@Override
//	public void setOpen(Boolean pOpen) {}
	

}