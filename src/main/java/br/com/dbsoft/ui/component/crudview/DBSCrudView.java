package br.com.dbsoft.ui.component.crudview;


import javax.el.MethodExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.validator.MethodExpressionValidator;

import com.sun.faces.facelets.compiler.UIInstructions;

import br.com.dbsoft.ui.bean.crud.DBSCrudOldBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSCrudView.COMPONENT_TYPE)
public class DBSCrudView extends DBSUIComponentBase implements NamingContainer, SystemEventListener{ 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CRUDVIEW;
	public final static String RENDERER_TYPE = "/resources/component/crudView.xhtml";
	
	protected enum PropertyKeys {
		crudBean;
		
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
	
	public DBSCrudView(){
		setRendererType(DBSCrudView.RENDERER_TYPE);
		 FacesContext xContext = FacesContext.getCurrentInstance();
		 xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
		 xContext.getViewRoot().subscribeToViewEvent(PreValidateEvent.class,this);

	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	public DBSCrudOldBean getCrudBean() {
		return (DBSCrudOldBean) getStateHelper().eval(PropertyKeys.crudBean, null);
	}


	public void setCrudBean(DBSCrudOldBean pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
	}

	
	@Override
	public void processEvent(SystemEvent pEvent) throws AbortProcessingException {
		FacesContext xContext = FacesContext.getCurrentInstance();
		UIComponent xComponent = (UIComponent) pEvent.getSource();

		if (xComponent.getFacets().size() > 0) {
			UIComponent xF = xComponent.getFacets().get(javax.faces.component.UIComponent.COMPOSITE_FACET_NAME);
			pvInvokeCrudBeanMethods(xContext, xF, pEvent);
		}
	}

	@Override
	public boolean isListenerForSource(Object source) {
		 return (source.equals(this));
	}

	private void pvInvokeCrudBeanMethods(FacesContext pContext, UIComponent pComponent, SystemEvent pEvent){
		if (pComponent==null){return;}
		for (UIComponent xC : pComponent.getChildren()){
			//Ignora componentes que não precisam de configuração visual. (UICommand e UIData foram incluidos na condição para posterior avaliação se serão mantidos)
			if (!(xC instanceof UIInstructions) &&
				!(xC instanceof UICommand) &&
				!(xC instanceof UIData)){
				//Recupera nome do bean
				String xELString = DBSFaces.getELString(this, PropertyKeys.crudBean.toString());
				MethodExpression xME = null;
				Object[] xParms = null;
				if (pEvent instanceof PostAddToViewEvent){
					if (xC instanceof UIInput){
						//Cria chamada ao método do crudBean para configura o campo
						xME = DBSFaces.createMethodExpression(pContext, xELString + ".crudFormBeforeShowComponent", null, new Class[]{UIComponent.class});
				    	xParms = new Object[1]; 
				    	xParms[0] = xC;
						xME.invoke(pContext.getELContext(), xParms);
					}else{
						//Chamada recursiva para pesquisar dentro do componente, até não haver mais filhos.
						pvInvokeCrudBeanMethods(pContext, xC, pEvent);
					}
				}else if (pEvent instanceof PreValidateEvent){
					if (xC instanceof UIInput){
						//Cria validator para que seja testado o valor em função do DAO no crudBean
						UIInput xInput = (UIInput) xC;
						xME = DBSFaces.createMethodExpression(pContext, xELString + ".crudFormValidateComponent", null, new Class[]{FacesContext.class, UIComponent.class, Object.class});
						MethodExpressionValidator xV = new MethodExpressionValidator(xME);
						xInput.addValidator(xV);					
					}else{
						//Chamada recursiva para pesquisar dentro do componente, até não haver mais filhos.
						pvInvokeCrudBeanMethods(pContext, xC, pEvent);
					}
				}
			}
		}
	}

}