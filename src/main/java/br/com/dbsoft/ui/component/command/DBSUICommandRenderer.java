package br.com.dbsoft.ui.component.command;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.ui.core.DBSFaces.FACESCONTEXT_ATTRIBUTE;


public class DBSUICommandRenderer extends DBSRenderer implements ActionListener {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
        DBSUICommand xCommand = (DBSUICommand) pComponent;
//        System.out.println("DBSUICommandRenderer decode--\t" + xCommand.getClientId());
		String 		xClientId = xCommand.getClientId(); //xButton.getClientId(pContext);
        if(xCommand.getReadOnly()) {return;}

        decodeBehaviors(pContext, xCommand);
        
        //Dispara chamada do action
		if (wasClicked(pContext, xCommand, xClientId)) { 
			//Salva qual é a página atual antes de efetur o action
			pContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.PREVIOUS_VIEW, DBSFaces.getViewId());
			//Salva qual é a página atual antes de efetur o action
			pContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE, xCommand);
			//Inclui action na fila para ser executado logo em seguida
			xCommand.queueEvent(new ActionEvent(xCommand));
			xCommand.addActionListener(this);
		}
    }

	@Override
	public void processAction(ActionEvent pEvent) throws AbortProcessingException {
//		System.out.println("DBSUICommandRenderer processAction\t" + pEvent.getPhaseId());
		//Cria evento para ser disparado após a execução do action
		DBSUICommandAfterActionEvent xAAE = new DBSUICommandAfterActionEvent(pEvent);
		xAAE.queue();
	}

	/**
	 * Retorna styleClass básica para UICommand
	 * @param pCommand
	 * @return
	 */
	protected String getBasicStyleClass(DBSUICommand pCommand){
		StringBuilder xClass = new StringBuilder();
		xClass.append(CSS.NOT_SELECTABLE);
		xClass.append(" ");
		if (pCommand.getStyleClass()!=null){
			xClass.append(pCommand.getStyleClass());
			xClass.append(" ");
		}
		if (pCommand.getCloseDialog()){
			xClass.append("-closeDialog");
		}
		xClass.append(" ");
		return xClass.toString();
	}
}


