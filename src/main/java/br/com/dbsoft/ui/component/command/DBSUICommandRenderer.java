package br.com.dbsoft.ui.component.command;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;


/**
 * Controla se botão foi selecionado
 * Adiciona evento para ser chamado após a execução do action
 * 
 * No DBSUICommandAfterActionEvent
 * 		Se após execução do action houver mensagem, não efetua o outcome
 * 			Forma o update do componente que gerou o action para informar que existe mensagem
 * 			Salva action original para ser chamado após a exibição da últgima mensagem que não seja de erro
 */
public class DBSUICommandRenderer extends DBSRenderer implements ActionListener {
	
    @Override
	public void decode(FacesContext pContext, UIComponent pComponent) {
        DBSUICommand xCommand = (DBSUICommand) pComponent;
        if(xCommand.getReadOnly()) {return;}

        decodeBehaviors(pContext, xCommand);

        if (!(pComponent instanceof UIInput)) {
	        //Dispara chamada do action
			if (DBSFaces.wasClicked(pContext, xCommand) && !DBSFaces.isReset(xCommand)) { 
//				//Salva qual é a página atual antes de efetur o action
//				pContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.PREVIOUS_VIEW, DBSFaces.getViewId());
//				//Salva qual ACTION ORIGINAL
//				pContext.getAttributes().put(FACESCONTEXT_ATTRIBUTE.ACTION_SOURCE, xCommand);
				ActionEvent xAE = new ActionEvent(xCommand);
				xCommand.queueEvent(xAE);
				//Adiciona listener que será executado antes do action.
				//Este listener irá adicionar um novo evento que será executado após do action.
				//Este novo evento irá verificar se após o action, foi adicionado mensagem.
				//Caso positivo, impede o outcome
				
				//Exclui listener anterior
				for (ActionListener xAL:xCommand.getActionListeners()){
					if (xAL.getClass().isAssignableFrom(this.getClass())){
						xCommand.removeActionListener(xAL);
					}
				}
				xCommand.addActionListener(this); 
			}
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


