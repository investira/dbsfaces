package br.com.dbsoft.ui.component.tab;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.tabpage.DBSTabPage;
import br.com.dbsoft.ui.core.DBSFaces;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSTab.RENDERER_TYPE)
public class DBSTabRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		if (!pComponent.isRendered()){return;}
		DBSTab xTab = (DBSTab) pComponent;
		//Configura página corrente após o submit
		if (xTab.getShowTabPageOnClick()){
			String xSelectedTabPage = pContext.getExternalContext().getRequestParameterMap().get(pvGetInputId(xTab, true));
			
			if (xSelectedTabPage!=null &&
				!xSelectedTabPage.equals("")){
				xTab.setSelectedTabPage(xSelectedTabPage);
			}
		}
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		//System.out.println("RENDER TAB #############################");
		if (!pComponent.isRendered()){return;}
		DBSTab xTab = (DBSTab) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xTab.getClientId(pContext);
		String xClass = DBSFaces.CSS.TAB.MAIN;
		//Recupera a tabpage corrente para evitar que mude quando ocorrer o post
		String xSelectedTabPage;
		if (xTab.getShowTabPageOnClick()){
			xSelectedTabPage = pContext.getExternalContext().getRequestParameterMap().get(pvGetInputId(xTab, true));
			if (xSelectedTabPage!=null &&
				!xSelectedTabPage.equals("")){
				xTab.setSelectedTabPage(xSelectedTabPage);
			}
		}
		
		xSelectedTabPage = xTab.getSelectedTabPage();
		
		if (xTab.getStyleClass()!=null){
			xClass = xClass + xTab.getStyleClass();
		}
		
		xWriter.startElement("div", xTab);
			xWriter.writeAttribute("id", xClientId, null);
			xWriter.writeAttribute("name", xClientId, null);
			xWriter.writeAttribute("class", xClass, null);
			if (!xTab.getShowTabPageOnClick()){
				xWriter.writeAttribute("showTabPageOnClick", "false", null);
			}
			DBSFaces.setAttribute(xWriter, "style", xTab.getStyle(), null);
			xWriter.startElement("ul", xTab);
				//Título
				for (int xI=xTab.getChildren().size()-1; xI >= 0; xI--){
					if (xTab.getChildren().get(xI) instanceof DBSTabPage){
						DBSTabPage xPage = (DBSTabPage) xTab.getChildren().get(xI);
						if (xPage.isRendered()){
							xWriter.startElement("li", xTab);  
								
								encodeClientBehaviors(pContext, xPage);
							
								String xPageId = xPage.getAttributes().get("id").toString();
								String xPageClientId = xClientId + DBSFaces.SEPARATOR + xPageId;
								DBSFaces.setAttribute(xWriter, "tabPage", xPageClientId, "tabPage");
								if (xI==0){
									if (xSelectedTabPage.equals("")){
										xSelectedTabPage = xPageClientId;
										xTab.setSelectedTabPage(xPageClientId);
									}
								}
								if (xPageClientId.equals(xSelectedTabPage)){
									xPage.setSelected(true);
									DBSFaces.setAttribute(xWriter, "class", DBSFaces.CSS.MODIFIER.SELECTED, "class");
								}
								
								xWriter.startElement("a", xTab);
//									xWriter.writeAttribute("ontouchstart", "javascript:void(0)", "ontouchstart"); //Para ipad ativar o css:ACTIVE
//									xWriter.writeAttribute("href", "#", "href"); //Para ipad ativar o css:ACTIVE
									xWriter.startElement("span", xTab);
										xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.ICON + " " + DBSFaces.CSS.NOT_SELECTABLE + " " + xPage.getIconClass(), "class");
									xWriter.endElement("span");
									xWriter.startElement("span", xTab);
										xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CAPTION + " " + DBSFaces.CSS.NOT_SELECTABLE, "class");
										xWriter.write(xPage.getCaption());
									xWriter.endElement("span");
								xWriter.endElement("a");
							xWriter.endElement("li");
						}
					}
				}
			xWriter.endElement("ul");
			
			/*Conteúdo da páginas*/
			xWriter.startElement("div", xTab);
				xWriter.startElement("div", xTab);
				xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), "class");
					
					renderChildren(pContext, xTab);

				xWriter.endElement("div");
			xWriter.endElement("div");
			//Input para controle do focus e caracteres digitados----
			xWriter.startElement("input", xTab);
				DBSFaces.setAttribute(xWriter, "id", pvGetInputId(xTab, true), null);
				DBSFaces.setAttribute(xWriter, "name", pvGetInputId(xTab, true), null);
				DBSFaces.setAttribute(xWriter, "type", "hidden", null);
				DBSFaces.setAttribute(xWriter, "value", xSelectedTabPage, null);
			xWriter.endElement("input");	
			
		xWriter.endElement("div");
		
		pvEncodeJS(xWriter, xClientId);
		
		
//		UIViewRoot xViewRoot = pContext.getViewRoot();
//		for (UIComponent xResource : xViewRoot.getComponentResources(pContext, "head")) {
//			xResource.encodeAll(pContext);
//		}
//		for (UIComponent xResource : xViewRoot.getComponentResources(pContext, "body")) {
//			xResource.encodeAll(pContext);
//		}
					
	}
	
	/**Retorna a tabPage que contém a mesma chave da tagPage selecionada
	 * Se não houver nenhuma tabPage selecionada, retorna a primeira
	 * @param pTab
	 * @param pClientId
	 * @return
	 */
//	private String pvGetSelectedPage(DBSTab pTab, String pClientId){
//		String xSelectedPage = "";
//		if (pTab.getChildren().size()>0){
//			DBSTabPage xPage = null;
//			for (int xI=pTab.getChildren().size()-1; xI >= 0; xI--){
//				if (pTab.getChildren().get(xI) instanceof DBSTabPage){
//					if(pTab.getChildren().get(xI).isRendered()){
//						xPage = (DBSTabPage) pTab.getChildren().get(xI);
//						//Interrompe a busca, caso tenha encontrado a tabPage selecionada
//						if (pTab.getSelectedTabPage().toUpperCase().equals(xPage.getId().toUpperCase())){
//							break;
//						}
//					}
//				}
//			}
//			//Marca tabPage como selecionada e armazena o valor neste tab
//			xPage.setSelected(true);
//			xSelectedPage = pClientId + DBSFaces.SEPARATOR + xPage.getId();
//			pTab.setSelectedTabPage(xSelectedPage);
//		}
//		return xSelectedPage;
//	}
	
//	private void pvEncodeIcon(FacesContext pContext, DBSTabPage pTabPage, ResponseWriter pWriter) throws IOException{
//		String xClass = DBSFaces.CSS.NOT_SELECTABLE + " " + DBSFaces.CSS.MODIFIER.ICON + " " +  pTabPage.getIconClass();
//		pWriter.startElement("span", pTabPage);
//			pWriter.writeAttribute("class", DBSFaces.CSS.NOT_SELECTABLE + " " + DBSFaces.CSS.MODIFIER.ICON + " " +  pTabPage.getIconClass(), null);
//		pWriter.endElement("span");
//	}

	/**
	 * Encode do código JS necessário para o componente
	 * @param pWriter
	 * @param pClientId
	 * @throws IOException
	 */
	private void pvEncodeJS(ResponseWriter pWriter, String pClientId) throws IOException {
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xTabId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_tab(xTabId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
	
	private String pvGetInputId(DBSTab pTab, boolean pFullId){
		return pvGetId(pTab, DBSFaces.CSS.MODIFIER.INPUT.trim(), pFullId);
	}
	
	private String pvGetId(UIComponent pComponent, String pSufix, boolean pFullId){
		String xId;
		if (pFullId){
			xId = pComponent.getClientId() + pSufix;
		}else{
			xId = pComponent.getId() + pSufix;
		}
		return xId;
	}
	
//	/**
//	 * Retorna a clientId da página que será exibida inicialmente
//	 * @param pTab
//	 * @return
//	 */
//	private String pvGetFirstPageClientId(DBSTab pTab){
//		for (int xI=0; xI < pTab.getChildren().size(); xI++){
//			if (pTab.getChildren().get(xI) instanceof DBSTabPage){
//				DBSTabPage xPage = (DBSTabPage) pTab.getChildren().get(xI);
//				if (xPage.isRendered()){
//					return xPage.getClientId();
//				}
//			}
//		}
//		return "";
//	}
	
//	private String pvGetTabPageId(DBSTabPage pTabPage, String pClientId, int pIndex){
//		if (pTabPage.getAttributes().get("id")!=null){
//			return pTabPage.getAttributes().get("id").toString();//Usa Id do usuário
//		}else{
//			
//			if (pTabPage.getCaption()!=null){
//				return pClientId + "_" + DBSString.changeStr(pTabPage.getCaption(), " ", "_"); //Usa o título da página como complemento do id
//			}else{
//				return pClientId + "_tab" + pIndex; //Força um Id sequecial
//			}
//		}
//	}
}
