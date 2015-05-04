package br.com.dbsoft.ui.component.cruddialog;


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

import br.com.dbsoft.ui.bean.crud.DBSCrudBean;
import br.com.dbsoft.ui.component.DBSUIComponentBase;
import br.com.dbsoft.ui.core.DBSFaces;

/**
 * @author ricardovillar
 *
 */
@FacesComponent(DBSCrudDialog.COMPONENT_TYPE)
public class DBSCrudDialog extends DBSUIComponentBase implements NamingContainer, SystemEventListener { 

	public final static String COMPONENT_TYPE = DBSFaces.DOMAIN_UI_COMPONENT + "." + DBSFaces.ID.CRUDDIALOG;
	public final static String RENDERER_TYPE = "/resources/component/crudDialog.xhtml";
	
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
	
	public DBSCrudDialog(){
		setRendererType(DBSCrudDialog.RENDERER_TYPE);
		 FacesContext xContext = FacesContext.getCurrentInstance();

		 xContext.getViewRoot().subscribeToViewEvent(PostAddToViewEvent.class, this);
		 xContext.getViewRoot().subscribeToViewEvent(PreValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostValidateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRenderComponentEvent.class,this);
//		 //-------------------------------------------------------------------------------
//		 xContext.getViewRoot().subscribeToViewEvent(PostConstructViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PostRestoreStateEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreDestroyViewMapEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PreRemoveFromViewEvent.class,this);
//		 
//		 xContext.getViewRoot().subscribeToViewEvent(ComponentSystemEvent.class,this);
//		 xContext.getViewRoot().subscribeToViewEvent(PhaseEvent.class,this);
//		 xContext.getViewRoot().subscribeToEvent(PhaseEvent.class, this);
	}

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY;
    }


	public DBSCrudBean getCrudBean() {
		return (DBSCrudBean) getStateHelper().eval(PropertyKeys.crudBean, null);
	}


	public void setCrudBean(DBSCrudBean pCrudBean) {
		getStateHelper().put(PropertyKeys.crudBean, pCrudBean);
	}

	
	@Override
	public void processEvent(SystemEvent pEvent) throws AbortProcessingException {
		FacesContext xContext = FacesContext.getCurrentInstance();
		UIComponent xComponent = (UIComponent) pEvent.getSource();
//		 System.out.println("=============================================================================");
//		 System.out.println(pEvent.getClass().getName() + "\t" + 
//				 		    xComponent.getClass().getName() + "\t" +
//				 		    xContext.getCurrentPhaseId()  + "\t" +
//				 		    xContext.isProcessingEvents()  + "\t" +
//				 		    "UIComponentID:" + xComponent.getClientId());
//		if (pEvent instanceof PostAddToViewEvent){
//			//Informa ao crudBean qual a qual crudform ele pertence
//			if (xComponent instanceof DBSCrudDialog
//			 && xContext.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE){
//				String xELString = DBSFaces.getELString(this, PropertyKeys.crudBean.toString());
//				MethodExpression xME = null;
//				Object[] xParms = null;
//				//Cria chamada ao método do crudBean para configura o campo
//				xME = DBSFaces.createMethodExpression(xContext, xELString + ".setCrudForm", null, new Class[]{UIComponent.class});
//		    	xParms = new Object[1]; 
//		    	xParms[0] = xComponent;
//				xME.invoke(xContext.getELContext(), xParms);
//			}
//		}

		if (xComponent.getFacets().size() > 0) {
			UIComponent xF = xComponent.getFacets().get(javax.faces.component.UIComponent.COMPOSITE_FACET_NAME);
			pvInvokeCrudBeanMethods(xContext, xF, pEvent);
		}
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

	@Override
	public boolean isListenerForSource(Object source) {
		 return (source.equals(this));
	}
}