package br.com.dbsoft.ui.component.navmessage;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.nav.DBSNavRenderer;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;

@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSNavMessage.RENDERER_TYPE)
public class DBSNavMessageRenderer extends DBSNavRenderer {
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		DBSNavMessage xNavMessage = (DBSNavMessage) pComponent;
		//VERIFICA SE FAZ O ENCODE
		//SE TEM FOR DEFINIDO, VERIFICA SE TEM MENSAGEM
		//SE NAO TEM FOR DEFINIDO, FAZ O ENCODE
		if (!DBSObject.isEmpty(xNavMessage.getFor())) {
			xNavMessage.setOpened(true);
			if (DBSObject.isEqual(xNavMessage.getFor().toUpperCase(), "@ALL")) {
				//Verifica se tem qualquer mensagem
				if (DBSObject.isEmpty(FacesContext.getCurrentInstance().getMessageList())) {
					xNavMessage.setRendered(false);
					return;
				}
			} else {
				//Verifica se tem mensagem para o componente do forParent
				if (DBSObject.isEmpty(FacesContext.getCurrentInstance().getMessageList(xNavMessage.getFor()))) {
					xNavMessage.setRendered(false);
					return;
				}
			}
		} else {
			if (xNavMessage.getChildCount() <= 0) {
				xNavMessage.setRendered(false);
				return;
			}
		}
		
		super.encodeBegin(pContext, xNavMessage); //JÁ TEM O RenderChildren
			pvEncodeMessageDefault(pContext, xNavMessage);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		super.encodeEnd(pContext, pComponent);
	}

	private void pvEncodeMessageDefault(FacesContext pContext, DBSNavMessage pNavMessage) throws IOException {
		if (DBSObject.isEmpty(pNavMessage.getFor())) {
			return;
		}
		
		FacesMessage 	xMessage;
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		xMessage = pvGetFacesMessage(pNavMessage);
		
		xWriter.startElement("div", pNavMessage);
			DBSFaces.encodeAttribute(xWriter, "class", "-divMessage");
			//Icone da Mensagem
			xWriter.startElement("div", pNavMessage);
				DBSFaces.encodeAttribute(xWriter, "class", "-iconMessage");
				pvEncodeIcon(pContext, pNavMessage);
			xWriter.endElement("div");
			//Mensagem
			xWriter.startElement("div", pNavMessage);
				DBSFaces.encodeAttribute(xWriter, "class", "-contentMessage");
				xWriter.startElement("span", pNavMessage);
					xWriter.write(xMessage.getSummary());
				xWriter.endElement("span");
			xWriter.endElement("div");
		xWriter.endElement("div");
		
	}
	
	private void pvEncodeIcon(FacesContext pContext, DBSNavMessage pNavMessage) throws IOException {
		if (DBSObject.isEmpty(pNavMessage.getFor())) {
			return;
		}
		FacesMessage 	xMessage = pvGetFacesMessage(pNavMessage);
		ResponseWriter 	xWriter = pContext.getResponseWriter();
		String 			xClass = "";
		
		if (xMessage.getSeverity() == FacesMessage.SEVERITY_INFO) {
			xClass = " -i_information ";
		} else if (xMessage.getSeverity() == FacesMessage.SEVERITY_WARN) {
			xClass = " -i_warning -yellow";
		} else if (xMessage.getSeverity() == FacesMessage.SEVERITY_ERROR) {
			xClass = " -i_error -red";
		} else { //ERROR
			xClass = " -i_error -red";
		}
		
		xWriter.startElement("div", pNavMessage);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
		xWriter.endElement("div");
	}

	private FacesMessage pvGetFacesMessage(DBSNavMessage pNavMessage) {
		FacesMessage xMessage = null;
		if (!DBSObject.isEmpty(pNavMessage.getFor())) {
			if (DBSObject.isEqual(pNavMessage.getFor().toUpperCase(), "@ALL")) {
				xMessage = FacesContext.getCurrentInstance().getMessageList().get(0);
			} else {
				xMessage = FacesContext.getCurrentInstance().getMessageList(pNavMessage.getFor()).get(0);
			}
		}
		return xMessage;
	}
	
}
