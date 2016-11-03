package br.com.dbsoft.ui.component;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import br.com.dbsoft.ui.component.DBSUIOutput;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSMessagesFacesContext;

/**
 * Componente com JS que seta atributo hasmessage se for o caso
 * @author ricardo.villar
 * 
 */
@FacesComponent(DBSUICommandHasMessage.COMPONENT_TYPE)
public class DBSUICommandHasMessage extends DBSUIOutput{  
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.COMMANDHASMESSAGE;
	
	@Override
	public void encodeBegin(FacesContext pContext) throws IOException {
//		System.out.println("DBSUICommandHasMessage encodeBegin \t " + getClientId());
		DBSUICommand 		xUICommand = (DBSUICommand) getParent();
		ResponseWriter 		xWriter = pContext.getResponseWriter();
		if (!xUICommand.isRendered()){return;}

		DBSFaces.encodeJavaScriptTagStart(xUICommand, xWriter, "id=" + getClientId());
		if (DBSMessagesFacesContext.getMessage(DBSMessagesFacesContext.ALL) != null){
			String xJS = "dbsfaces.component.setHasMessage(dbsfaces.util.jsid('" + xUICommand.getClientId() + "'));";
			xWriter.write(xJS);
		}
		DBSFaces.encodeJavaScriptTagEnd(xWriter);
//		System.out.println("DBSUICommandHasMessage encodeBegin END:\t"+ pContext.getViewRoot().getViewId());
	}
	
	
}