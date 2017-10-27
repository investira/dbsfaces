package br.com.dbsoft.ui.component;


import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

import br.com.dbsoft.ui.core.DBSFaces;
import br.com.dbsoft.util.DBSIO.SORT_DIRECTION;

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
	@ResourceDependency(library = "css", name = "dbsfaces_inputdate.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputphone.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputnumber.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputmask.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_img.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_listbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_link.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_label.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_checkbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_messagelist.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_menu.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_progress.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_push.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_reportform.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_report.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_slider.css", target = "head"),
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
	@ResourceDependency(library = "js", name = "dbsfaces_progress.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_push.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_plugin.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_radio.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_reportform.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_report.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_slider.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tab.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tabpage.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tooltip.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_quickinfo.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_chartvalue.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_charts.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_chart.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsinvest.js", target = "head")
//	@ResourceDependency(library = "js", name = "eventsource.js", target = "head")
})
public abstract class DBSUIData extends UIData implements IDBSUIComponentBase {
	
	protected enum PropertyKeys {
		style, 
		styleClass, 
		selected, 
		keyColumnName,
		currentRowIndex,
		sortColumn,
		sortDirection,
		sortAction;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {
		}

		@Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
		}
	}
	@Override
	public String getFamily() {
		return DBSFaces.FAMILY;
	}
	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, "");
	}

	public void setStyle(String pStyle) {
		getStateHelper().put(PropertyKeys.style, pStyle);
		handleAttribute("style", pStyle);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, "");
	}

	public void setStyleClass(String pStyleClass) {
		getStateHelper().put(PropertyKeys.styleClass, pStyleClass);
		handleAttribute("styleClass", pStyleClass);
	}
	
	public String getSelected() {
		// return (String) getStateHelper().eval(PropertyKeys.selected, false);
		return DBSFaces.getELString(this,PropertyKeys.selected.toString());
	}

	public void setSelected(String pSelected) {
		getStateHelper().put(PropertyKeys.selected, pSelected);
		handleAttribute("selected", pSelected);
	}

	public String getKeyColumnName() {
		return (String) getStateHelper().eval(PropertyKeys.keyColumnName, "");
	}
	public void setKeyColumnName(String pKeyColumnName) {
		getStateHelper().put(PropertyKeys.keyColumnName, pKeyColumnName);
		handleAttribute("keyColumnName", pKeyColumnName);
	}
	
	/**
	 * Linha corrente controlada pelo usuário.</br>
	 * <b>getRowIndex</b> é a linha correnta controlada pelo JSF.
	 * @param pCurrentRowIndex
	 */
	public Integer getCurrentRowIndex() {
		return (Integer) getStateHelper().eval(PropertyKeys.currentRowIndex, -1);
	}

	/**
	 * Linha corrente controlada pelo usuário
	 * <b>getRowIndex</b> é a linha correnta controlada pelo JSF.
	 * @param pCurrentRowIndex
	 */
	public void setCurrentRowIndex(Integer pCurrentRowIndex) {
		getStateHelper().put(PropertyKeys.currentRowIndex, pCurrentRowIndex);
		handleAttribute("currentRowIndex", pCurrentRowIndex);
	}

	public String getSortColumn() {
		return (String) getStateHelper().eval(PropertyKeys.sortColumn, "");
	}

	public void setSortColumn(String pSortColumn) {
		//Ignora o set caso não tenha havido alteração da coluna selecionada
		if (pSortColumn.equals(getSortColumn())){return;}
		
		String xELString = DBSFaces.getELString(this,PropertyKeys.sortColumn.toString());
		DBSFaces.setValueWithValueExpression(this.getFacesContext(), xELString, pSortColumn);

		getStateHelper().put(PropertyKeys.sortColumn, pSortColumn);
		handleAttribute("sortColumn", pSortColumn);
		//Seta direção inicial 
		setSortDirection(SORT_DIRECTION.ASCENDING.getCode());
	}
	
	public String getSortDirection() {
 		return (String) getStateHelper().eval(PropertyKeys.sortDirection, SORT_DIRECTION.NONE.getCode()); //A,D,empty
 	}

 	public void setSortDirection(String pSortDirection) {
 		//Uniformiza a partir do enum
 		pSortDirection = SORT_DIRECTION.get(pSortDirection).getCode();
 		//Ignora o set caso não tenha havido alteração da direção
 		if (pSortDirection.equals(getSortDirection())){return;}
 		
		String xELString = DBSFaces.getELString(this,PropertyKeys.sortDirection.toString());
		DBSFaces.setValueWithValueExpression(this.getFacesContext(), xELString, pSortDirection);

		getStateHelper().put(PropertyKeys.sortDirection, pSortDirection);
 		handleAttribute("sortDirection", pSortDirection);
 	}

	public String getSortAction() {
		String xStr = DBSFaces.getELString(this, PropertyKeys.sortAction.toString());
		return xStr;
	}

	public void setSortAction(String pSortAction) {
    	getStateHelper().put(PropertyKeys.sortAction, pSortAction);
		handleAttribute("sortAction", pSortAction);
	}	

	@Override
	public String getDefaultEventName() {
		return "select";
	}

	@Override
	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}

	//TODO VERIFICAR SE ESTE CÓDIGO PODE ESTAR INTERFERINDO COM OS ACTIONS DOS CHECKBOX E ETC. RICARDO 11/NOV/2014
	//TODO VERIFICAR SE ESTE CÓDIGO AINDA É NECESSÁRIO. RICARDO 07/FEV/2017
	@Override
	public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
		if (this.getVar() == null || this.getRowIndex() == -1) { //this.getRowIndex() == -1 Incluido para evitar erro //server Error: class java.lang.NumberFormatException Trying to extract rowIndex from clientId
			if (null == context || null == clientId || null == callback) {
				throw new NullPointerException();
			}

			boolean found = false;
			if (clientId.equals(this.getClientId(context))) {
				try {
					this.pushComponentToEL(context, this);
					callback.invokeContextCallback(context, this);
					return true;
				} catch (Exception e) {
					throw new FacesException(e);
				} finally {
					this.popComponentFromEL(context);
				}
			} else {
				Iterator<UIComponent> itr = this.getFacetsAndChildren();

				while (itr.hasNext() && !found) {
					found = itr.next().invokeOnComponent(context, clientId,
							callback);
				}
			}
			return found;
		} else {
			return super.invokeOnComponent(context, clientId, callback);
		}

	}
	
}
