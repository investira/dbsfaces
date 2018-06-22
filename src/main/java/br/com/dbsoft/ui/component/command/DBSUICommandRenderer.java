package br.com.dbsoft.ui.component.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.sun.faces.renderkit.RenderKitUtils;

import br.com.dbsoft.ui.component.DBSRenderer;
import br.com.dbsoft.ui.component.DBSUICommand;
import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.ui.core.DBSFaces.CSS;
import br.com.dbsoft.util.DBSObject;


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
	
	/**
	 * Enconde do onclick
	 * @param pContext
	 * @param pComponent
	 * @param pWriter
	 * @throws IOException
	 */
	protected void encodeOnClick(FacesContext pContext, DBSUICommand pComponent, ResponseWriter pWriter) throws IOException  {
		if (!pComponent.getReadOnly()){
			if (pComponent.getActionExpression() != null){
				DBSFaces.encodeAttribute(pWriter, "type", "submit");
				//Cria behavior ajax padrão
				if (!DBSObject.isEmpty(pComponent.getUpdate())
				 || !DBSObject.isEmpty(pComponent.getExecute())) {
					AjaxBehavior xAjaxBehavior = new AjaxBehavior();
					if (DBSObject.isEmpty(pComponent.getonclick())){
						xAjaxBehavior.setOnevent("dbsfaces.onajax");
						xAjaxBehavior.setOnerror("dbsfaces.onajaxerror");
					}else{
						//Remove return, parenteses e respectivo conteúdo
						String xOnClick = DBSObject.getNotNull(pComponent.getonclick(), "").replaceAll("\\(.*?\\)(.*)", "").replaceAll("^(return )","").trim();
//						String xOnClick = DBSObject.getNotNull(pComponent.getonclick(), "").replace(/ *\([^)]*\) */g, "").replace("/^(return )/","");
//						String xOnClick = DBSObject.getNotNull(pComponent.getonclick(), "").replaceAll("(?<=\\().*?(?=\\))", "").replaceAll("^(return )","").trim();
						xAjaxBehavior.setOnevent(xOnClick);
						xAjaxBehavior.setOnerror(xOnClick);
						xOnClick = "return " + xOnClick+ "({type:'event', status:'validate'});";
						pComponent.setonclick(xOnClick);
					}
					if (!DBSObject.isEmpty(pComponent.getExecute())){
						xAjaxBehavior.setExecute(Collections.unmodifiableList(Arrays.asList(pComponent.getExecute().split("[,\\s]+"))));
					}else {
						xAjaxBehavior.setExecute(Collections.unmodifiableList(Arrays.asList(RenderKitUtils.getFormClientId(pComponent, pContext))));
					}
					if (!DBSObject.isEmpty(pComponent.getUpdate())){
						xAjaxBehavior.setRender(Collections.unmodifiableList(Arrays.asList(pComponent.getUpdate().split("[,\\s]+"))));
					}
					pComponent.addClientBehavior("action", xAjaxBehavior);
				}
				Collection<ClientBehaviorContext.Parameter> params = getBehaviorParameters(pComponent);
				RenderKitUtils.renderOnclick(pContext, pComponent, params, null, false);
			}else{
				DBSFaces.encodeAttribute(pWriter, "type", "button");
				RenderKitUtils.renderOnclick(pContext, pComponent, null, null, false);
			}
		}
	}

}


