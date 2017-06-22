package br.com.dbsoft.ui.component.command;


import javax.el.MethodExpression;
import javax.faces.component.ActionSource2;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSMessagesFacesContext;
import br.com.dbsoft.ui.core.DBSFaces.FACESCONTEXT_ATTRIBUTE;


public class DBSUICommandAfterActionEvent extends FacesEvent {

	private static final long serialVersionUID = 654791198375318147L;
	
	ActionEvent wActionEvent;
	
	public DBSUICommandAfterActionEvent(ActionEvent pEvent) {
		super(pEvent.getComponent());
		wActionEvent = pEvent;
	}

	@Override
	public boolean isAppropriateListener(FacesListener pListener) {
		return true;
	}

	@Override
	public void processListener(FacesListener pListener) {
//		System.out.println("DBSUICommandAfterActionEvent processListener");
		FacesContext xContext = FacesContext.getCurrentInstance();
		//Se não houver qualquer mensagem
		if (DBSMessagesFacesContext.getMessage(DBSMessagesFacesContext.ALL) == null){
			//Remove ACTION ORIGINAL salvo anteriormente
			xContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE, null);
			return;
		}
//		System.out.println("DBSUICommandAfterActionEvent processListener TEM MENSAGEM");
		//Força a atualização do componente que indica se há mensagem que fica dentro do componente que originou o action(button menuitem, etc), para setar nele, via JS, se há mensagem.
		xContext.getPartialViewContext().getRenderIds().add(getComponent().getClientId() + DBSUICommand.FACET_MESSAGE);
		//Força que se mantenha na mesma página(inibe qualquer redirect)
		ActionSource2 xAS = (ActionSource2) wActionEvent.getSource();
		MethodExpression xME = xAS.getActionExpression();
		if (xME !=null){
//			xContext.getApplication().getNavigationHandler().handleNavigation(xContext, xME.getExpressionString(), xContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.PREVIOUS_VIEW).toString());
			xContext.getPartialViewContext().setRenderAll(false);
			if (xContext.getAttributes().get(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE) == null){
				//Salva qual ACTION ORIGINAL
				xContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE, wActionEvent.getComponent());
			}
		}
	}

}
