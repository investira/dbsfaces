package br.com.dbsoft.ui.component.componenttree;

import java.io.IOException;
import java.util.Iterator;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.component.menu.DBSMenu;
import br.com.dbsoft.ui.component.button.DBSButton;
import br.com.dbsoft.ui.component.menuitem.DBSMenuitem;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSNumber;
import br.com.dbsoft.util.DBSString;


@FacesRenderer(componentFamily=DBSFaces.FAMILY, rendererType=DBSComponentTree.RENDERER_TYPE)
public class DBSComponentTreeRenderer extends DBSRenderer {

	

	/* (non-Javadoc)
	 * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
	 */
	@Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
		DBSComponentTree xComponenttree = (DBSComponentTree) pComponent;
		decodeBehaviors(pContext, pComponent);
		String xClientId;
		
		xClientId = pvGetFxExpandedIds(xComponenttree);
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)){
			String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientId);
	        if(xSubmittedValue != null) {
	        	xComponenttree.setExpandedIds(xSubmittedValue);
	        }
		}
    	
    	if (xComponenttree.getReadOnly()){return;}

		
		xClientId = pvGetFxSelectedClientId(xComponenttree);
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xClientId)){
			String xSubmittedValue = pContext.getExternalContext().getRequestParameterMap().get(xClientId);
	        if (xSubmittedValue != null) {
	        	//Força a atualização do valor com o item selecionado para que ele esteja disponível antes do processamento dos componentes filhos
	        	//Desta forma, os componentes filhos saberão qual o item foi selecionado
	        	//TODO Mesmo funcionando, me parece haver uma forma mais limpa de se implementar está questão
	        	
	        	Integer xIndex = pvGetRowIndexFromSelected(xSubmittedValue);
	        	if (xIndex != -1){
	        		xComponenttree.setRowIndex(xIndex);
	        	}
	        	xSubmittedValue = pvGetKeyFromSelected(xSubmittedValue);

	        	ValueExpression xVE = pComponent.getValueExpression("value");
	        	if (xVE!=null){
	        		xVE.setValue(pContext.getELContext(), xSubmittedValue);
		        	//Seta o valor como nulo deforma a inibir uma nova chamada ao set value, já que foi forçado na chamada acima
		            xComponenttree.setSubmittedValue(null); 
	        	}
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
	public void encodeBegin(FacesContext pContext, UIComponent pComponent) throws IOException {
		if (!pComponent.isRendered()){return;}
		DBSComponentTree	xComponenttree = (DBSComponentTree) pComponent;
		ResponseWriter 		xWriter = pContext.getResponseWriter();
    	xComponenttree.setRowIndex(-1);
		String 				xClientId = xComponenttree.getClientId();
		String				xClass = DBSFaces.CSS.COMPONENTTREE.MAIN + " " + DBSFaces.getInputDataClass(pComponent);
		if (xComponenttree.getStyleClass()!=null){
			xClass += " " + xComponenttree.getStyleClass();
		}

		String xExpandedIdsClientId = pvGetFxExpandedIds(xComponenttree);
		if (pContext.getExternalContext().getRequestParameterMap().containsKey(xExpandedIdsClientId)){
			String xValue = pContext.getExternalContext().getRequestParameterMap().get(xExpandedIdsClientId);
	        if(xValue != null) {
	        	xComponenttree.setExpandedIds(xValue);
	        }
		}	
		
		if (xComponenttree.getReadOnly()){		
			xClass = xClass + DBSFaces.CSS.MODIFIER.READONLY;
		}
		
		//Se houver filhos ou facets como filhos
		if (xComponenttree.getChildCount() > 0 ||
			xComponenttree.getFacetCount() > 0){
			xWriter.startElement("div", xComponenttree);
				xWriter.writeAttribute("id", xClientId, null);
				xWriter.writeAttribute("name", xClientId, null);
				xWriter.writeAttribute("class", xClass, null);
				xWriter.writeAttribute("style", xComponenttree.getStyle(), null);

				encodeClientBehaviors(pContext, xComponenttree);

				xWriter.startElement("div", xComponenttree);
					xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTAINER.trim(), null);

					//Encode das informações adicionais dos componentes filhos
					xWriter.startElement("ul", pComponent);
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
						xComponenttree.setRowIndex(0);
						pvEncodeExtraInfo(pContext, xWriter, xComponenttree, xComponenttree.getFacetsAndChildren());
					xWriter.endElement("ul");
					
					//Encode da lista dos componentes filhos
					xWriter.startElement("ul", pComponent);
						xWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CAPTION.trim(), null);
						xComponenttree.setRowIndex(0);
						pvEncodeNodes(pContext, xWriter, xComponenttree, xComponenttree.getFacetsAndChildren());
						xComponenttree.setRowIndex(-1);
					xWriter.endElement("ul");
					
				xWriter.endElement("div");
				
				//Encode dos componentes para controle da seleção do item da lista
				pvEncodeSelection(pContext, xWriter, xComponenttree);
				
				
			xWriter.endElement("div");
		}
		
		pvEncodeJS(xClientId, xWriter);
	}


	/**
	 * Encode das informações adicioais dos componentes filhos
	 * @param pContext
	 * @param pWriter
	 * @param pComponenttree
	 * @param pParentComponents
	 * @throws IOException
	 */
	private void pvEncodeExtraInfo(FacesContext pContext, ResponseWriter pWriter, DBSComponentTree pComponenttree, Iterator<UIComponent> pParentComponents) throws IOException{
		UIComponent xExtraInfo = pComponenttree.getFacet(DBSComponentTree.FACET_EXTRAINFO);
		while (pParentComponents.hasNext()){
			UIComponent xChild = pParentComponents.next();
			if (!xChild.equals(xExtraInfo)){
				//Se não for componente que seja controlado
				if (!pComponenttree.isValidComponent(xChild) ||
					!pComponenttree.isValidIdPrefix(xChild.getId())){
					//Chamada recursiva para buscar componentes filhos que sejam controlados
					pvEncodeExtraInfo(pContext, pWriter, pComponenttree, xChild.getFacetsAndChildren());
				//Se for componente controlado
				}else{
					pComponenttree.setRowIndex(pComponenttree.getRowIndex() + 1);
					pComponenttree.setRows(pComponenttree.getRowIndex());
//					String xKey = xChild.getClientId(); //pvRemoveClientIdFromChildId(pComponenttree.getClientId(), xChild.getClientId());
					String xKey = pComponenttree.removeClientIdFromChildId(xChild.getClientId());
					pWriter.startElement("li", pComponenttree);
						//Encode do face 'extrainfo' 
						pWriter.startElement("span", pComponenttree);
							pWriter.writeAttribute("id", xChild.getClientId() + DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
							pWriter.writeAttribute("name", xChild.getClientId() + DBSFaces.CSS.MODIFIER.EXTRAINFO.trim(), null);
							pWriter.writeAttribute("key", xKey, null);
							pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.LABEL.trim(), null);
							//Encode do face 'extrainfo' SOMENTE se for o último item filho ou
							//Não, dependento do parametro getExtraInfoOnLastChildOnly() 
							if ((pComponenttree.getExtraInfoOnLastChildOnly() && xChild.getChildCount() == 0)
							  || !pComponenttree.getExtraInfoOnLastChildOnly()){
								if (xExtraInfo!=null){
									pWriter.startElement("div", pComponenttree);
										
										//Chama bean para setar o valor da corrente
										pComponenttree.getValueExpression("value").setValue(pContext.getELContext(), pvGetKeyFromSelected(xKey));
										
										//Encode extrainfo
										xExtraInfo.encodeAll(pContext);

									pWriter.endElement("div");
								}
							}
						pWriter.endElement("span");
						//Encode dos filhos 
						pWriter.startElement("div", pComponenttree);
							pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
							if (xChild.getChildCount()>0){
								pWriter.startElement("div", pComponenttree);
									if (!pvExpand(pComponenttree, xKey)){
										pWriter.writeAttribute("style", "display:none;", null);
									}
									pWriter.startElement("ul", pComponenttree);
										pvEncodeExtraInfo(pContext, pWriter, pComponenttree, xChild.getFacetsAndChildren());
									pWriter.endElement("ul");
								pWriter.endElement("div");
							}
						pWriter.endElement("div");
				    pWriter.endElement("li");
					//TODO
				}
			}
		}
	}
	
	private boolean pvExpand(DBSComponentTree pComponenttree, String pKey){
		pKey = pKey.trim();
		if (pKey.equals("")){
			return false;
		}
		String xIds = " " + pComponenttree.getExpandedIds() + " ";
		if (xIds.indexOf(pKey) > -1){
			return true;
		}
		return false;
		
	}
	/**
	 * Encode da lista dos componentes filhos
	 * @param pContext
	 * @param pWriter
	 * @param pComponenttree
	 * @param pParentComponents
	 * @throws IOException
	 */
	private void pvEncodeNodes(FacesContext pContext, ResponseWriter pWriter, DBSComponentTree pComponenttree, Iterator<UIComponent> pParentComponents) throws IOException{
		UIComponent xExtraInfo = pComponenttree.getFacet(DBSComponentTree.FACET_EXTRAINFO);
		while (pParentComponents.hasNext()){
			UIComponent xChild = pParentComponents.next();
			if (!xChild.equals(xExtraInfo)){
				if (!pComponenttree.isValidComponent(xChild) ||
					!pComponenttree.isValidIdPrefix(xChild.getId())){
					pvEncodeNodes(pContext, pWriter, pComponenttree, xChild.getFacetsAndChildren());
				}else{
					String xValue = null;
					String xIconClass = "";
					pComponenttree.setRowIndex(pComponenttree.getRowIndex() + 1);
//					pComponenttree.resetChildrenId();
					String xKey = pComponenttree.removeClientIdFromChildId(xChild.getClientId());
//					String xKey = pvGetKeyFromClientId(pComponenttree, xChild.getClientId());
//					String xKey = xChild.getClientId(); //pvRemoveClientIdFromChildId(pComponenttree.getClientId(), xChild.getClientId());
					//Recupera o texto e o icone se componente for de uma das classe abaixo
					if (xChild instanceof DBSButton){
						xValue = ((DBSButton) xChild).getLabel();
						xIconClass = ((DBSButton) xChild).getIconClass();
					}else if (xChild instanceof DBSMenu){
						xValue = ((DBSMenu) xChild).getLabel();
						xIconClass = ((DBSMenu) xChild).getIconClass();
					}else if (xChild instanceof DBSMenuitem){
						xValue = ((DBSMenuitem) xChild).getLabel();
						xIconClass = ((DBSMenuitem) xChild).getIconClass();
					}else if (xChild instanceof DBSUIInput){
						xValue = ((DBSUIInput) xChild).getLabel();
					}
					//Defini texto padrão para identificar o componente, se não tiver sido encontrada informação na condição acima
					if (xValue==null){
						xValue = "[" + xChild.getClientId() + "][" + xChild.getClass().getName() + "]";
					}
					
					pWriter.startElement("li", pComponenttree);
						//Encode da lista dos componentes
						pWriter.startElement("span", pComponenttree);
							String xClass = DBSFaces.CSS.MODIFIER.LABEL;
							if (xChild.getChildCount() == 0){
								xClass = xClass + DBSFaces.CSS.MODIFIER.LAST.trim();
							}
							pWriter.writeAttribute("id", xChild.getClientId(), null);
							pWriter.writeAttribute("name", xChild.getClientId(), null);
							pWriter.writeAttribute("class", xClass.trim(), null);
							pWriter.writeAttribute("key", xKey, null);

							pWriter.startElement("div", pComponenttree);
								pWriter.startElement("span", pComponenttree);
								if (xChild.getChildCount()>0){
									if (pvExpand(pComponenttree, xKey)){
										pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CLOSABLE.trim() + " -i_subtract -small ", null);
									}else{
										pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CLOSABLE.trim() + " -i_add -small ", null);
									}
								}
								pWriter.endElement("span");
								pWriter.startElement("span", pComponenttree);
									pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.ICON + xIconClass, null);
//									pWriter.startElement("span", pComponenttree);
//										pWriter.writeAttribute("class", xIconClass, null);
//									pWriter.endElement("span");
								pWriter.endElement("span");
								pWriter.startElement("span", pComponenttree);
									pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.VALUE.trim(), null);
									pWriter.write(xValue);
								pWriter.endElement("span");
							pWriter.endElement("div");
						pWriter.endElement("span");	
						//Encode dos filhos 
						pWriter.startElement("div", pComponenttree);
							pWriter.writeAttribute("class", DBSFaces.CSS.MODIFIER.CONTENT.trim(), null);
							if (xChild.getChildCount()>0){
								pWriter.startElement("div", pComponenttree);
									if (!pvExpand(pComponenttree, xKey)){
										pWriter.writeAttribute("style", "display:none;", null);
									}
									pWriter.startElement("ul", pComponenttree);
							    		pvEncodeNodes(pContext, pWriter, pComponenttree, xChild.getFacetsAndChildren());
									pWriter.endElement("ul");
								pWriter.endElement("div");
							}
						pWriter.endElement("div");
				    pWriter.endElement("li");
				}
			}
		}
	}
	
	/**
	 * Encode dos componentes para controle da seleção do item da lista
	 * @param pContext
	 * @param pWriter
	 * @param pComponenttree
	 * @throws IOException
	 */
	private void pvEncodeSelection(FacesContext pContext, ResponseWriter pWriter, DBSComponentTree pComponenttree) throws IOException  {
		pWriter.startElement("input", pComponenttree);
			DBSFaces.setAttribute(pWriter, "id", pvGetFxExpandedIds(pComponenttree), null);
			DBSFaces.setAttribute(pWriter, "name", pvGetFxExpandedIds(pComponenttree), null);
			DBSFaces.setAttribute(pWriter, "type", "hidden", null);
			DBSFaces.setAttribute(pWriter, "value", pComponenttree.getExpandedIds(), null);
		pWriter.endElement("input");	
		
		UIComponent xExtraInfo = pComponenttree.getFacet(DBSComponentTree.FACET_EXTRAINFO);
		
		if (!pComponenttree.getReadOnly() &&
			!(xExtraInfo==null)){
			//Campo que conterá o item selectionado
			pWriter.startElement("input", pComponenttree);
				DBSFaces.setAttribute(pWriter, "id", pvGetFxSelectedClientId(pComponenttree), null);
				DBSFaces.setAttribute(pWriter, "name", pvGetFxSelectedClientId(pComponenttree), null);
				DBSFaces.setAttribute(pWriter, "type", "hidden", null);
				DBSFaces.setAttribute(pWriter, "value", pComponenttree.getValue(), null);
			pWriter.endElement("input");						
		}
	}
	
	private String pvGetFxSelectedClientId(UIComponent pInputText){
		return pInputText.getClientId() + DBSFaces.ID_SEPARATOR + "selection" + DBSFaces.CSS.MODIFIER.INPUT.trim();
	}
	
	private String pvGetFxExpandedIds(UIComponent pInputText){
		return pInputText.getClientId() + DBSFaces.ID_SEPARATOR + DBSComponentTree.PropertyKeys.expandedIds.toString();
	}

	private int pvGetRowIndexFromSelected(String pKey){
    	int xI = pKey.indexOf(DBSFaces.ID_SEPARATOR);
    	String xIndex = pKey.substring(0, xI);
    	if (DBSNumber.isNumber(xIndex)){
    		return DBSNumber.toInteger(xIndex);
    	}else{
    		return -1;
    	}
	}

	private String pvGetKeyFromSelected(String pKey){
    	int xI = pKey.indexOf(DBSFaces.ID_SEPARATOR);
    	return DBSString.getSubString(pKey, xI+2, pKey.length());

	}
	

	private void pvEncodeJS(String pClientId, ResponseWriter pWriter) throws IOException{
		DBSFaces.encodeJavaScriptTagStart(pWriter);
		String xJS = "$(document).ready(function() { \n" +
				     " var xComponenttreeId = '#' + dbsfaces.util.jsid('" + pClientId + "'); \n " + 
				     " dbs_componenttree(xComponenttreeId); \n" +
                     "}); \n"; 
		pWriter.write(xJS);
		DBSFaces.encodeJavaScriptTagEnd(pWriter);		
	}
	
	
}