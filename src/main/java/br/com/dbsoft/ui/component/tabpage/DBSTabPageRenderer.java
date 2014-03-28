package br.com.dbsoft.ui.component.tabpage;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.tab.DBSTab;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSTabPage.RENDERER_TYPE)
public class DBSTabPageRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		if (!pComponent.isRendered()){return;}
		decodeBehaviors(pContext, pComponent);
//		DBSTabPage xTab = (DBSTabPage) pComponent;
		/*recupera o nome do objeto que fez o submit*/
		//DBSFaces.showMapContent(pContext.getExternalContext().getRequestParameterMap(), 0);
//		String xObjName  = pContext.getExternalContext().getRequestParameterMap().get(DBSFaces.PARTIAL_SOURCE_PARAM);
//		if (xObjName!=null){
//			if (xTab.getTabPages()!=null){//Se houver controle dinamico
//				xTab.getTabPages().removeTabPageUsingClientId(xObjName); //Remove a página se encontrar alguma com o nome informado
//			}
//		}
		
	}

	@Override 
	public boolean getRendersChildren() {
		return true; //True=Chama o encodeChildren abaixo e interrompe a busca por filho pela rotina renderChildren
	}
	
    @Override
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
    	//É necessário manter está função para evitar que faça o render dos childrens
    	//O Render dos childrens é feita do encode
    }
    

	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		//System.out.println("RENDER TAB #############################");
		if (!pComponent.isRendered()){return;}
		DBSTabPage xTabPage = (DBSTabPage) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xTabPage.getClientId(pContext);
		DBSTab xTab = (DBSTab) xTabPage.getParent();

		String xClass = DBSFaces.CSS.TABPAGE.MAIN + " " + xTabPage.getStyleClass();
		
		xWriter.startElement("div", xTabPage);
			DBSFaces.setAttribute(xWriter, "id", xClientId, "id");
			DBSFaces.setAttribute(xWriter, "name", xClientId, "name");
			if (xTab.getSelectedTabPage().toUpperCase().equals(xClientId.toUpperCase())){
				xClass = xClass + " " + DBSFaces.CSS.MODIFIER.SELECTED;
			}
			xWriter.writeAttribute("class", xClass.trim(), "class");
			DBSFaces.setAttribute(xWriter, "style",xTabPage.getStyle(), null);
//			encodeClientBehaviors(pContext, xTabPage);
			renderChildren(pContext, xTabPage);
		xWriter.endElement("div"); //Final do Div com o id _content
			
	}
}
