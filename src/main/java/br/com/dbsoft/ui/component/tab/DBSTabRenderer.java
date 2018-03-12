package br.com.dbsoft.ui.component.tab;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.tab.DBSTab.CAPTION_ALIGMENT;
import br.com.dbsoft.ui.component.tab.DBSTab.TYPE;
import br.com.dbsoft.ui.component.tabpage.DBSTabPage;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSTab.RENDERER_TYPE)
public class DBSTabRenderer extends DBSRenderer {

	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		if (!pComponent.isRendered()){return;}
		DBSTab xTab = (DBSTab) pComponent;
		//Configura página corrente após o submit
		if (xTab.getShowTabPageOnClick()){
			String xSelectedTabPage = pContext.getExternalContext().getRequestParameterMap().get(xTab.getInputId(true));
			if (xSelectedTabPage!=null &&
				!xSelectedTabPage.equals("")){
				xTab.setSelectedTabPage(xSelectedTabPage);
				xTab.setValue(xSelectedTabPage);
			}
		}
	}

	
	@Override
	public void encodeBegin(FacesContext pContext, UIComponent pComponent)
			throws IOException {
		//System.out.println("RENDER TAB #############################");
		if (!pComponent.isRendered()){return;}
		DBSTab xTab = (DBSTab) pComponent;
		ResponseWriter xWriter = pContext.getResponseWriter();
		String xClientId = xTab.getClientId(pContext);
		String xClass = CSS.TAB.MAIN + CAPTION_ALIGMENT.get(xTab.getCaptionAlignment()).getStyleClass();
		TYPE xType = TYPE.get(xTab.getType());
		//Recupera a tabpage corrente para evitar que mude quando ocorrer o post
		String xSelectedTabPage;
		if (xTab.getShowTabPageOnClick()){
			xSelectedTabPage = pContext.getExternalContext().getRequestParameterMap().get(xTab.getInputId(true));
			if (xSelectedTabPage!=null &&
				!xSelectedTabPage.equals("")){
				xTab.setSelectedTabPage(xSelectedTabPage);
			}
		}
		
		xSelectedTabPage = xTab.getSelectedTabPage();
		
		if (xTab.getStyleClass()!=null){
			xClass += " " + xTab.getStyleClass();
		}
		
		xWriter.startElement("div", xTab);
			DBSFaces.encodeAttribute(xWriter, "id", xClientId);
			DBSFaces.encodeAttribute(xWriter, "name", xClientId);
			DBSFaces.encodeAttribute(xWriter, "class", xClass);
			DBSFaces.encodeAttribute(xWriter, "type", xTab.getType());
			if (xTab.getShowTabPageOnClick()){
				DBSFaces.encodeAttribute(xWriter, "soc", true);
			}
			DBSFaces.encodeAttribute(xWriter, "style", xTab.getStyle());
			//Container
			xWriter.startElement("div", xTab);
				DBSFaces.encodeAttribute(xWriter, "class", CSS.MODIFIER.CONTAINER + CSS.THEME.FLEX + " -hide");
				//Abas com os título ========================================================================
				pvEncodeAba(pContext, xTab, xWriter, xSelectedTabPage, xType);
				
				//Conteúdo da páginas ======================================================================================
				pvEncodePage(pContext, xTab, xWriter, xSelectedTabPage);
			xWriter.endElement("div");
			pvEncodeJS(pComponent, xWriter);
		xWriter.endElement("div");

		//Chamada assincrona para carregar a página	
		for (int xI=0; xI <= xTab.getChildren().size()-1; xI++){
			if (xTab.getChildren().get(xI) instanceof DBSTabPage){
				DBSTabPage xPage = (DBSTabPage) xTab.getChildren().get(xI);
				if (xPage.isRendered()){
					if (xPage.getAjax()){
						DBSFaces.encodeJavaScriptTagStart(pComponent, xWriter);
						String xJS = "setTimeout(function(){" +
										"dbsfaces.ajax.request('" + xPage.getClientId() + "_aba" + "', '" + xTab.getInputId(true) + "', '" + xPage.getClientId() + "', dbsfaces.onajax, dbsfaces.onajaxerror);" +
										"}, 0);";
//						String xJS = "setTimeout(function(){" +
//												"jsf.ajax.request('" + xPage.getClientId() + "_aba" + "', 'update', {render:'" + xPage.getClientId() + "', execute:'" + xTab.getInputId(true) + "', onevent:dbsfaces.onajax, onerror:dbsfaces.onajaxerror});" +
//														   "}, 0);";
						xWriter.write(xJS);
						DBSFaces.encodeJavaScriptTagEnd(xWriter);	
					}
				}
			}
		}

	}
	
	//Abas com os título ========================================================================
	private void pvEncodeAba(FacesContext pContext, DBSTab pTab, ResponseWriter pWriter, String pSelectedTabPage, TYPE pType) throws IOException{
		String xClientId = pTab.getClientId(pContext);
		pWriter.startElement("div", pTab);
			DBSFaces.encodeAttribute(pWriter, "class", "-captions" + CSS.THEME.FLEX_COL);
			pWriter.startElement("div", pTab);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER + CSS.THEME.FLEX);
				UIComponent xParent = DBSFaces.getParentFirstChild(pTab, DBSTabPage.class);
				for (int xI=0; xI < xParent.getChildren().size(); xI++){	
					if (pTab.getChildren().get(xI) instanceof DBSTabPage){
						DBSTabPage xTabPage = (DBSTabPage) pTab.getChildren().get(xI);
						if (xTabPage.isRendered()){
							pWriter.startElement("div", pTab);  
								String xPageId = xTabPage.getAttributes().get("id").toString();
								String xPageClientId = xClientId + DBSFaces.ID_SEPARATOR + xPageId;
								String xClass = CSS.MODIFIER.CAPTION + CSS.NOT_SELECTABLE + CSS.THEME.FLEX_COL;
								if (pType == TYPE.TAB) {
									xClass += CSS.THEME.BC + CSS.THEME.FC + CSS.THEME.INVERT;
								}
								DBSFaces.encodeAttribute(pWriter, "id", xPageClientId + "_aba");
								DBSFaces.encodeAttribute(pWriter, "name", xPageClientId + "_aba");
								DBSFaces.encodeAttribute(pWriter, "class", xClass);	
								DBSFaces.encodeAttribute(pWriter, "tabPageid", xPageClientId);
								
								encodeClientBehaviors(pContext, xTabPage);
								
								//Marca a primiera aba como selecionada
								if (xI==0){
									if (pSelectedTabPage.equals("")){
										pSelectedTabPage = xPageClientId;
										pTab.setSelectedTabPage(xPageClientId);
									}
								}

								//							if (xPageClientId.equals(xSelectedTabPage)){
	//								xPage.setSelected(true);
	//								DBSFaces.setAttribute(pWriter, "class", CSS.MODIFIER.SELECTED, null);
	//							}
								
								pWriter.startElement("div", pTab);  
									xClass = CSS.TAB.CAPTION ;
									if (xTabPage.getAjax()){
										xClass += " -ajax";
									}
									DBSFaces.encodeAttribute(pWriter, "class", xClass);	
									pWriter.startElement("div", pTab);  
										DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER  + xTabPage.getCaptionStyleClass());
										pWriter.startElement("div", pTab);  
											DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTENT);
											UIComponent xCaption = xTabPage.getFacet("caption");
											if (xCaption!=null) {
												xCaption.encodeAll(pContext);
											}else {
												pWriter.startElement("div", pTab);
													DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CAPTION);
					//									pWriter.writeAttribute("ontouchstart", "javascript:void(0)", "ontouchstart"); //Para ipad ativar o css:ACTIVE
					//									pWriter.writeAttribute("href", "#", "href"); //Para ipad ativar o css:ACTIVE
													pWriter.startElement("span", pTab);
														DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.ICON + xTabPage.getCaptionIconClass());
													pWriter.endElement("span");
													pWriter.startElement("span", pTab);
														DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.VALUE);
														pWriter.write(xTabPage.getCaption());
													pWriter.endElement("span");
												pWriter.endElement("div");
												if (pType.equals(TYPE.ACCORDION)){
													UIComponent xCaptionObs = xTabPage.getFacet("caption_obs");
													if (xCaptionObs!=null || !DBSObject.isEmpty(xTabPage.getCaptionObs())) {
														pWriter.startElement("div", pTab);
															DBSFaces.encodeAttribute(pWriter, "class", "-obs");
															if (xCaptionObs!=null) {
																xCaptionObs.encodeAll(pContext);
															}else if (!DBSObject.isEmpty(xTabPage.getCaptionObs())){
																pWriter.write(xTabPage.getCaptionObs());
															}
														pWriter.endElement("div");
													}
												}
											}
											if (xTabPage.getAjax()){
												pWriter.startElement("span", pTab);
													DBSFaces.encodeAttribute(pWriter, "class", "loading_container");
													pWriter.startElement("span", pTab);
														DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.LOADING);
													pWriter.endElement("span");
												pWriter.endElement("span");
											}
										pWriter.endElement("div");
									pWriter.endElement("div");
								pWriter.endElement("div");
							pWriter.endElement("div");
						}
					}
				}
			pWriter.endElement("div");
		pWriter.endElement("div");
	}

	//Abas com os título ========================================================================
	private void pvEncodePage(FacesContext pContext, DBSTab pTab, ResponseWriter pWriter, String pSelectedTabPage) throws IOException{
		pWriter.startElement("div", pTab);
			DBSFaces.encodeAttribute(pWriter, "class", "-tabPages" + CSS.THEME.FLEX_COL);
			pWriter.startElement("div", pTab);
				DBSFaces.encodeAttribute(pWriter, "class", CSS.MODIFIER.CONTAINER);
	
				//Input para salvar a pagina selecionada ====================================================
				HtmlInputHidden xInput = (HtmlInputHidden) pTab.getFacet("input");
				if (xInput == null){
					xInput = (HtmlInputHidden) pContext.getApplication().createComponent(HtmlInputHidden.COMPONENT_TYPE);
					xInput.setId(pTab.getInputId(false));
					pTab.getFacets().put("input", xInput);
				}
				xInput.setValue(pSelectedTabPage);
				xInput.encodeAll(pContext);
	
				//Encode das páginas filhas, sem o conteúdo, pois será posteriormente chamado via atualização ajax
	//			for (int xI=pTab.getChildren().size()-1; xI >= 0; xI--){
				for (int xI=0; xI <= pTab.getChildren().size()-1; xI++){	
					if (pTab.getChildren().get(xI) instanceof DBSTabPage){
						DBSTabPage xPage = (DBSTabPage) pTab.getChildren().get(xI);
						if (xPage.isRendered()){
							xPage.encodeBegin(pContext);
							//Ignora o encode do conteúdo da página
							if (!xPage.getAjax()){
								xPage.encodeChildren(pContext);
							}
							xPage.encodeEnd(pContext);
						}
					}
				}
			
	//			renderChildren(pContext, pTab);
	
			pWriter.endElement("div");
		pWriter.endElement("div");
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
				     " var xTabId = dbsfaces.util.jsid('" + pComponent.getClientId() + "'); \n " + 
				     " dbs_tab(xTabId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);	
	}
	
//	private String getFlexBasis(DBSTab pTab) {
//		Integer xCount = 0;
//		for (int xI=0; xI <= pTab.getChildren().size()-1; xI++){	
//			if (pTab.getChildren().get(xI) instanceof DBSTabPage){
//				DBSTabPage xPage = (DBSTabPage) pTab.getChildren().get(xI);
//				if (xPage.isRendered()){
//					xCount++;
//				}
//			}
//		}
//		return DBSFaces.getCSSAllBrowser("flex-basis", (DBSNumber.divide(1, xCount).doubleValue() * 100D) + "%");
//	}
	

}
