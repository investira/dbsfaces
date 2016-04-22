package br.com.dbsoft.ui.component.sidenav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.div.DBSDiv;
import br.com.dbsoft.ui.component.label.DBSLabel;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSSideNav.RENDERER_TYPE)
public class DBSSideNavRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
	}
	
	@Override //True=Informa que este componente chamará o render dos filhos
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSSideNav xSidenav = (DBSSideNav) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xSidenav.getClientId(pContext);
		String xClass = DBSFaces.CSS.SIDENAV.MAIN;
		String xIcon = xSidenav.getIconClass();
		List<UIComponent> xChildren = new ArrayList<UIComponent>();
		
		if (xSidenav.getStyleClass()!=null){
			xClass += DBSObject.getNotEmpty(xSidenav.getStyleClass(), "");
		}
		if (DBSObject.isEmpty(xSidenav.getIconClass())) {
			xIcon = "-i_equal"; //ÍCONE PADRÃO
		}
		
		//DIV PRINCIPAL
		xWriter.startElement("div", xSidenav);
		xWriter.writeAttribute("id", xClientId, "id");
		DBSFaces.setAttribute(xWriter, "class", xClass, null);
			//BOTAO PRINCIPAL
			DBSDiv xBotaoPrincipal = new DBSDiv();
			xBotaoPrincipal.setStyleClass(DBSFaces.CSS.MODIFIER.ICON +" "+ xIcon +" "+ DBSFaces.CSS.THEME.ACTION);
//			xBotaoPrincipal.setTooltip(xSidenav.getTooltip());
			xChildren.add(xBotaoPrincipal);
			
			//HIDDEN NAV
			DBSDiv xHiddenDiv = new DBSDiv();
			xHiddenDiv.setStyleClass(DBSFaces.CSS.SIDENAV.HIDDENNAV +" "+ DBSFaces.CSS.DIALOG.MAIN);
			xChildren.add(xHiddenDiv);
			
			//SIDEBAR
			DBSDiv xSideBar = new DBSDiv();
			xSideBar.setStyleClass(DBSFaces.CSS.SIDENAV.SIDEBAR +" "+ DBSFaces.CSS.BACK_TEXTURE_BLACK_GRADIENT + 
								   DBSFaces.CSS.SIDENAV.DEFAULTLOCATION + xSidenav.getDefaultLocation());
				DBSDiv xxSideBarHeader = new DBSDiv();
				xxSideBarHeader.setStyleClass(DBSFaces.CSS.SIDENAV.SIDEBARHEADER);
					//BOTAO FECHAR
					DBSDiv xBotaoFechar = new DBSDiv();
					xBotaoFechar.setStyleClass(DBSFaces.CSS.MODIFIER.ICON +" "+ xIcon +" "+ DBSFaces.CSS.THEME.ACTION);
					xxSideBarHeader.getChildren().add(xBotaoFechar);

					//ICONE CENTRAL
					DBSLabel xIconeCentral = new DBSLabel();
					xIconeCentral.setIconClass(xSidenav.getIconCaption());
					xIconeCentral.setStyleClass(DBSFaces.CSS.SIDENAV.ICONCAPTION);
					xxSideBarHeader.getChildren().add(xIconeCentral);
					
					//CAPTION CENTRAL
					DBSLabel xCaptionCentral = new DBSLabel();
					xCaptionCentral.setValue(xSidenav.getCaption());
					xCaptionCentral.setStyleClass(DBSFaces.CSS.SIDENAV.CAPTIONCENTRAL);
					xxSideBarHeader.getChildren().add(xCaptionCentral);
				xSideBar.getChildren().add(xxSideBarHeader);
				
				//CONTENT DOS FILHOS
				DBSDiv xSideBarContent = new DBSDiv();
				xSideBarContent.setStyleClass(DBSFaces.CSS.MODIFIER.CONTENT);
					//FILHOS
					xSideBarContent.getChildren().addAll(xSidenav.getChildren());
				xSideBar.getChildren().add(xSideBarContent);
			xChildren.add(xSideBar);
			
			//Limpa e adiciona os filhos
			xSidenav.getChildren().removeAll(xSidenav.getChildren());
			xSidenav.getChildren().addAll(xChildren);
			DBSFaces.renderChildren(pContext, xSidenav);
		pvEncodeJS(xClientId, xSidenav, xWriter);
		xWriter.endElement("div");
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent) throws IOException {
		super.encodeEnd(pContext, pComponent);
	}
	
	private void pvEncodeJS(String pClientId, DBSSideNav pSideNav, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
					 " var xSideNavId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
					 " dbs_sideNav(xSideNavId, "+ pSideNav.getDefaultLocation() +", '"+ pSideNav.getWidth() +"', '"+ pSideNav.getHeight() +"'); \n" +
		             "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
}
