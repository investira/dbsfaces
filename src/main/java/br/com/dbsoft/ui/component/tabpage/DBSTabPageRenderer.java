package br.com.dbsoft.ui.component.tabpage;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.tab.DBSTab;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


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
    public void encodeChildren(FacesContext pContext, UIComponent pComponent) throws IOException {
    		if (!pComponent.isRendered()){return;}
	    	DBSTabPage xTabPage = (DBSTabPage) pComponent;
	    	DBSFaces.renderChildren(pContext, xTabPage);
    }
    

	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		//System.out.println("RENDER TAB #############################");
		if (!pComponent.isRendered()){return;}
		DBSTabPage xTabPage = (DBSTabPage) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xTabPage.getClientId(pContext);
		DBSTab xTab = DBSFaces.getFirstParent(xTabPage, DBSTab.class);
		if (xTab == null) {return;}
		
		String xClass = CSS.TABPAGE.MAIN + xTabPage.getStyleClass();
//		String xSelectedPage = DBSObject.getNotNull(pContext.getExternalContext().getRequestParameterMap().get(xTab.getInputId(true)), "").toString().toUpperCase();
		
		xWriter.startElement("div", xTabPage);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "style",xTabPage.getStyle());
			DBSFaces.encodeAttribute(xWriter, "type", xTab.getType());

//			encodeClientBehaviors(pContext, xTabPage);
	}
	
	@Override
	public void encodeEnd(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		if (!pComponent.isRendered()){return;}
		
			ResponseWriter xWriter = pContext.getResponseWriter();
			pvEncodeJS(pComponent, xWriter);
		xWriter.endElement("div"); //Final do Div com o id _content
	}
	
	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(UIComponent pComponent, ResponseWriter pWriter) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pComponent, pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xTabPageId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_tabPage(xTabPageId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}}
