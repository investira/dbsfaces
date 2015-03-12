package br.com.dbsoft.ui.component.tabpage;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
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
    	if (!pComponent.isRendered()){return;}
    	DBSTabPage xTabPage = (DBSTabPage) pComponent;
		renderChildren(pContext, xTabPage);
    }
    

	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		//System.out.println("RENDER TAB #############################");
		if (!pComponent.isRendered()){return;}
		DBSTabPage xTabPage = (DBSTabPage) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xTabPage.getClientId(pContext);
//		DBSTab xTab = (DBSTab) xTabPage.getParent();

		String xClass = DBSFaces.CSS.TABPAGE.MAIN + " " + xTabPage.getStyleClass();
//		String xSelectedPage = DBSObject.getNotNull(pContext.getExternalContext().getRequestParameterMap().get(xTab.getInputId(true)), "").toString().toUpperCase();
		
		xWriter.startElement("div", xTabPage);
			DBSFaces.setAttribute(xWriter, "id", xClientId, "id");
			DBSFaces.setAttribute(xWriter, "name", xClientId, "name");
//			if (xClientId.toUpperCase().equals(xTab.getSelectedTabPage().toUpperCase())
//			 || xClientId.toUpperCase().equals(xSelectedPage)){
//				xClass = xClass + " " + DBSFaces.CSS.MODIFIER.SELECTED;
//			}
			xWriter.writeAttribute("class", xClass.trim(), "class");
			DBSFaces.setAttribute(xWriter, "style",xTabPage.getStyle(), null);
//			encodeClientBehaviors(pContext, xTabPage);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		
		ResponseWriter xWriter = pContext.getResponseWriter();
		xWriter.endElement("div"); //Final do Div com o id _content
		super.encodeEnd(pContext, pComponent);
		
		pvEncodeJS(xWriter, pComponent.getClientId());
		
	}
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xTabPageId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_tabPage(xTabPageId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}}
