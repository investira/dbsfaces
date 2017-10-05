package br.com.dbsoft.ui.component.componenttree;

import java.util.Iterator;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;

import com.sun.faces.facelets.compiler.UIInstructions;
import com.sun.faces.facelets.tag.ui.ComponentRef;

import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.component.DBSUIInput;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSString;

@FacesComponent(DBSComponentTree.COMPONENT_TYPE)
public class DBSComponentTree extends DBSUIInput implements NamingContainer {
	
	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.COMPONENTTREE;
	public final static String RENDERER_TYPE = COMPONENT_TYPE;
	public final static String NOT_SELECTABLE_PREFIX = "ct"; // prefixo de componentes que SEMPRE SERÃO EXIBIDOS, independentemente de a relação informada em wAllowedIdsPrefixes.
	public final static String FACET_EXTRAINFO = "extrainfo";
	private static String[] wAllowedIdsPrefixes;


	protected enum PropertyKeys {
		allowedIdsPrefixes,
		update,
		extraInfoOnLastChildOnly,
		rowIndex,
		rows,
		expandedIds;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}
	
    public DBSComponentTree(){
    	setRendererType(DBSComponentTree.RENDERER_TYPE);
		 FacesContext xContext = FacesContext.getCurrentInstance();
		 xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
    }
	
	@Override
	public void processEvent(SystemEvent pEvent) throws AbortProcessingException {
		UIComponent xComponent = (UIComponent) pEvent.getSource();
		//Exclui da view qualquer HtmlForm que seja filho do DBSComponentTree para evitar que os action não funcionem
		if (pvIsComponentTreeChild(xComponent)){
			xComponent.getParent().getChildren().addAll(xComponent.getChildren());
			xComponent.getParent().getChildren().remove(xComponent);
		}
		super.processEvent(pEvent);
	}

	@Override
	public boolean isListenerForSource(Object pSource) {
		//Força para que quando o clientId for utilizado, seja considerado o RowIndex no nome
		//Parace ser um bug, mas este artifício também é utilizado no UIData com o comentário // reset the client id (see spec 3.1.6)
		this.setId(this.getId());
		return (pSource instanceof HtmlForm) || super.isListenerForSource(pSource);
	} 
	
	private Boolean pvIsComponentTreeChild(UIComponent pComponent){
		if (pComponent.getParent()==null){
			return false;
		}else{
			if (pComponent.getParent() instanceof DBSComponentTree){
				return true;
			}else{
				return pvIsComponentTreeChild(pComponent.getParent());
			}
		}
	}
	

	public String getAllowedIdsPrefixes() {
		return (String) getStateHelper().eval(PropertyKeys.allowedIdsPrefixes, "");
	}
	public void setAllowedIdsPrefixes(String pAllowedIdsPrefixes) {
		getStateHelper().put(PropertyKeys.allowedIdsPrefixes, pAllowedIdsPrefixes);
		handleAttribute("allowedIdsPrefixes", pAllowedIdsPrefixes);
		wAllowedIdsPrefixes = DBSString.changeStr(DBSComponentTree.NOT_SELECTABLE_PREFIX + " " + pAllowedIdsPrefixes.toLowerCase(), ",", " ").split("\\s+");
	}


	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, "");
	}

	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}

	public String getExpandedIds() {
		return (String) getStateHelper().eval(PropertyKeys.expandedIds, "");
	}

	public void setExpandedIds(String pExpandedIds) {
		getStateHelper().put(PropertyKeys.expandedIds, pExpandedIds);
		handleAttribute("expandedIds", pExpandedIds);
	}
	
	public Integer getRowIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.rowIndex, -1);
	}

	public void setRowIndex(Integer pRowIndex) {
		getStateHelper().put(PropertyKeys.rowIndex, pRowIndex);
		handleAttribute("rowIndex", pRowIndex);
		this.setId(this.getId()); //Necessário para obrigar o considerar o namingcontainer 
		this.resetChildrenId();
	}
	
	public Integer getRows() {
		return (Integer) getStateHelper().eval(PropertyKeys.rows, 62);
	}

	public void setRows(Integer pRows) {
		getStateHelper().put(PropertyKeys.rows, pRows);
		handleAttribute("rows", pRows);
	}
	
	public void setExtraInfoOnLastChildOnly(Boolean pExtraInfoOnLastChildOnly) {
		getStateHelper().put(PropertyKeys.extraInfoOnLastChildOnly, pExtraInfoOnLastChildOnly);
		handleAttribute("extraInfoOnLastChildOnly", pExtraInfoOnLastChildOnly);
	}
	public Boolean getExtraInfoOnLastChildOnly() {
		return (Boolean) getStateHelper().eval(PropertyKeys.extraInfoOnLastChildOnly, false);
	}


	@Override
	public String getClientId(FacesContext context) {
		if (context == null) {
			throw new NullPointerException();
	    }
	    if (getRowIndex() != -1){
	    	return super.getClientId(context) + DBSFaces.ID_SEPARATOR + getRowIndex();
	    }else{
	    	return super.getClientId(context);
	    }
	}
	
	@Override
	public void processDecodes(FacesContext pContext) {
		decode(pContext);
		UIComponent xExtraInfo = this.getFacet(DBSComponentTree.FACET_EXTRAINFO);
		pvProcessChildren(pContext, xExtraInfo.getFacetsAndChildren());
	}
	
	@Override
    public void processValidators(FacesContext pContext) {
		validate(pContext);
    }
	
	@Override
	public void processUpdates(FacesContext pContext) {
		updateModel(pContext);
	}
	
	
	private void pvProcessChildren(FacesContext pContext, Iterator<UIComponent> pChildren) {
        if (!isRendered()) {
            return;
        }
		while (pChildren.hasNext()){
			UIComponent xChild = pChildren.next();
			xChild.processDecodes(pContext);
        	if (xChild instanceof UIInput){
        		((UIInput) xChild).validate(pContext);
        		((UIInput) xChild).processUpdates(pContext);
        	}
			if (xChild.getChildCount()>0 || xChild.getFacetCount()>0){
				pvProcessChildren(pContext, xChild.getFacetsAndChildren());
			}
		}
	}
	
	
	public void resetChildrenId(){
		pvResetChildrenId(FacesContext.getCurrentInstance(), this.getFacetsAndChildren());
	}
	
	private void pvResetChildrenId(FacesContext facesContext, Iterator<UIComponent> childIterator){
		while (childIterator.hasNext()){
			UIComponent xChild = childIterator.next();
			//component.getTransientStateHelper(true);
			// reset the client id (see spec 3.1.6)
			xChild.setId(xChild.getId());
			if (!xChild.isTransient()){
				Iterator<UIComponent> childsIterator;
				childsIterator = xChild.getFacetsAndChildren();
				pvResetChildrenId(facesContext, childsIterator);
			}
		 }
	 }
	
	public String removeClientIdFromChildId(String pChildClientId){
//		String xComponentClientId = this.getClientId().toLowerCase().trim() + DBSFaces.SEPARATOR;
		String xComponentClientId = super.getClientId(getFacesContext()).toLowerCase().trim() + DBSFaces.ID_SEPARATOR; 
		pChildClientId = pChildClientId.trim();
		
		if (pChildClientId.toLowerCase().startsWith(xComponentClientId)){
			pChildClientId = DBSString.getSubString(pChildClientId, xComponentClientId.length()+1, pChildClientId.length());
		}
		return pChildClientId;
	}
	
	/**
	 * Verifica se o prefixo do id está dentro da lista válida de ids
	 * @param pId
	 * @return
	 */
	public boolean isValidIdPrefix(String pId){
		if (pId==null){
			return false;
		}
		if (wAllowedIdsPrefixes.length==0){
			return true;
		}
		for (int xX=0; xX<wAllowedIdsPrefixes.length; xX++){
			if (pId.startsWith(wAllowedIdsPrefixes[xX])){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verifica se é um componente de uma classe considerada válida para o DBSComponenttree
	 * @param pComponent
	 * @return
	 */
	public boolean isValidComponent(UIComponent pComponent){
		if (pComponent instanceof UIInstructions ||
			pComponent instanceof ComponentRef ||
			pComponent instanceof DBSUIComponentBase ||
			pComponent instanceof UINamingContainer ||
			pComponent instanceof HtmlForm ||
			pComponent.getClass().equals(UIOutput.class)){
			return false;
		}
		return true;
	}

	@Override
    public String getDefaultEventName(){
        return "click";
    }

}
