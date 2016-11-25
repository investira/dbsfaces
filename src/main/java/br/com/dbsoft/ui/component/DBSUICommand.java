package br.com.dbsoft.ui.component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UICommand;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderViewEvent;

import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import br.com.dbsoft.ui.component.command.DBSUICommandHasMessage;
import br.com.dbsoft.ui.core.DBSFaces;

@ResourceDependencies({
	// Estas libraries serão carregadas junto com o projeto
	@ResourceDependency(library = "css", name = "dbsfaces.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_a.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_theme.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_accordion.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_button.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_calendar.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_combobox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_componenttree.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudmodal.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_cruddialog.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudview.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudtable.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_datatable.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_group.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_dialog.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_modal.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_div.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_fileupload.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_floatbutton.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputtext.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputtextarea.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputphone.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputdate.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputnumber.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputmask.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_img.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_listbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_label.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_checkbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_messagelist.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_menu.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_nav.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_navmessage.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_progress.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_reportform.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_report.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_tab.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_tabpage.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_table.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_tooltip.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_radio.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_quickinfo.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_charts.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_chart.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_chartvalue.css", target = "head"),
	@ResourceDependency(library = "js", name = "jquery-3.1.1.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "jquery.actual.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "tinycolor.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsmask.js", target = "head"),
	@ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_accordion.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_button.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_calendar.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_checkbox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_componenttree.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_combobox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudmodal.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_cruddialog.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudview.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudtable.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_datatable.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_dialog.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_modal.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_fileupload.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_floatbutton.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_group.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputtext.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputtextarea.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputphone.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputdate.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputnumber.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputmask.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_listbox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_menu.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_messagelist.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_nav.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_navmessage.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_progress.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_push.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_plugin.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_radio.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_reportform.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_report.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tab.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tabpage.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tooltip.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_quickinfo.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_chartvalue.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_charts.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_chart.js", target = "head")
//	@ResourceDependency(library = "js", name = "eventsource.js", target = "head")
})
public abstract class DBSUICommand extends UICommand implements IDBSUIComponentBase, ClientBehaviorHolder, SystemEventListener{
	
	public final static String FACET_MESSAGE = "_message";
 
	protected enum PropertyKeys {
		styleClass,
		style,
		update,
		execute,
		onclick,
		readOnly,
		tooltip,
		closeDialog;
//		actionSourceClientId;

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
	
	
	public DBSUICommand() {
		 FacesContext xContext = FacesContext.getCurrentInstance();
		 xContext.getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class,this);
	}
	
	@Override
	public String getFamily() {
		return DBSFaces.FAMILY;
	}
	
	@Override
	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}
	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	
	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	
	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public void setUpdate(String pUpdate) {
		getStateHelper().put(PropertyKeys.update, pUpdate);
		handleAttribute("update", pUpdate);
	}
	
	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, null);
	}		

	public void setExecute(String pExecute) {
		getStateHelper().put(PropertyKeys.execute, pExecute);
		handleAttribute("execute", pExecute);
	}

	public String getExecute() {
		return (String) getStateHelper().eval(PropertyKeys.execute, null);
	}
	
	public void setonclick(String ponclick) {
		getStateHelper().put(PropertyKeys.onclick, ponclick);
		handleAttribute("onclick", ponclick);
	}
	
	public String getonclick() {
		return (String) getStateHelper().eval(PropertyKeys.onclick, null);
	}	

	
	public void setReadOnly(Boolean pReadOnly) {
		getStateHelper().put(PropertyKeys.readOnly, pReadOnly);
		handleAttribute("readOnly", pReadOnly);
	}
	
	public Boolean getReadOnly() {
		return (Boolean) getStateHelper().eval(PropertyKeys.readOnly, false);
	}	
	
	public void setCloseDialog(Boolean pCloseDialog) {
		getStateHelper().put(PropertyKeys.closeDialog, pCloseDialog);
		handleAttribute("closeDialog", pCloseDialog);
	}
	
	public Boolean getCloseDialog() {
		return (Boolean) getStateHelper().eval(PropertyKeys.closeDialog, false);
	}	

	public String getTooltip() {
		return (String) getStateHelper().eval(PropertyKeys.tooltip, null);
	}

	/**
//	 * ClientId do componente que originou a action que disparou o encode deste componente
//	 * @param pActionSourceClientId
//	 */
//	public void setActionSourceClientId(String pActionSourceClientId) {
//		getStateHelper().put(PropertyKeys.actionSourceClientId, pActionSourceClientId);
//		handleAttribute("actionSourceClientId", pActionSourceClientId);
//	}
//
//	/**
//	 * ClientId do componente que originou a action que disparou o encode deste componente
//	 * @return
//	 */
//	public String getActionSourceClientId() {
//		return (String) getStateHelper().eval(PropertyKeys.actionSourceClientId, null);
//	}

	public void setTooltip(String pTooltip) {
		getStateHelper().put(PropertyKeys.tooltip, pTooltip);
		handleAttribute("tooltip", pTooltip);
	}	

	@Override
	public String getDefaultEventName()
	{
	    return "action";
	}
	
	@Override
	public Collection<String> getEventNames() {
		return Arrays.asList("action","click", "blur", "change", "click", "dblclick", "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select", "valueChange"); 
	}
	
	@Override
	public void encodeBegin(FacesContext pContext) throws IOException {
		//Chama encode padrão
		super.encodeBegin(pContext);
		//Encode do indicador que há mensagem
		String 					xId = getId() + FACET_MESSAGE;
		DBSUICommandHasMessage 	xCmdMsg = (DBSUICommandHasMessage) getFacet(xId);
		if (xCmdMsg == null){return;}
		xCmdMsg.encodeAll(pContext);
	}
	
	@Override
	public void processEvent(SystemEvent pEvent) throws AbortProcessingException {
//		if (pEvent.getSource() instanceof UIComponent){
//			UIComponent xComponent = (UIComponent) pEvent.getSource();
//			System.out.println("DBSUICommand SystemEvent\t" + getClientId() + "\t#1 " + pEvent.getClass().getName() + "\t" + xComponent.getClass());
//		}else{
//			System.out.println("DBSUICommand SystemEvent\t" + getClientId() + "\t#1 " + pEvent.getClass().getName());
//		}
		if (getActionExpression() == null){return;}
		FacesContext 			xContext = FacesContext.getCurrentInstance();
		String 					xId = getId() + FACET_MESSAGE;
		//Recupera componente utilizado para indicar se existe mensagem
		DBSUICommandHasMessage 	xCmdMsg = (DBSUICommandHasMessage) getFacet(xId); 
//			System.out.println(getClientId() + "\t#1 Criou " + pEvent.getClass().getName());
		//Cria componente com JS que será utilizado para indicar se existe mensagem
		if (xCmdMsg == null){
			xCmdMsg = (DBSUICommandHasMessage) xContext.getApplication().createComponent(DBSUICommandHasMessage.COMPONENT_TYPE);
			xCmdMsg.setId(xId);
			getFacets().put(xId, xCmdMsg);
		}
	}
	
	@Override
	public boolean isListenerForSource(Object pSource) {
//		return pSource.equals(this) || pSource.getClass().isAssignableFrom(UIViewRoot.class);
//		System.out.println("isListenerForSource\t" + pSource.getClass());
//		return pSource.getClass().isAssignableFrom(UIViewRoot.class);
		//Como o evento capturado é PreRenderViewEvent, o source sempre será ViewRoot.
		return true;
	}


}
