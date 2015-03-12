package br.com.dbsoft.ui.component;


import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIOutput;

import br.com.dbsoft.ui.core.DBSFaces;

@ResourceDependencies({
	// Estas libraries serão carregadas junto com o projeto
	@ResourceDependency(library = "css", name = "dbsfaces.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_accordion.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_button.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_calendar.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_combobox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_componenttree.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudform.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_crudtable.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_datatable.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_group.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_dialog.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_fileupload.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputtext.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputtextarea.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputdate.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputnumber.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_inputmask.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_listbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_checkbox.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_messagelist.css", target = "head"),
	@ResourceDependency(library = "css", name = "dbsfaces_menu.css", target = "head"),
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
	@ResourceDependency(library = "js", name = "jquery-1.11.1.min.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsmask.js", target = "head"),
	@ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_accordion.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_button.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_calendar.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_checkbox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_componenttree.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_combobox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudform.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_crudtable.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_datatable.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_dialog.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_fileupload.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_group.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputtext.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputtextarea.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputdate.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputnumber.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_inputmask.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_listbox.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_menu.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_messagelist.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_progress.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_push.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_radio.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_reportform.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_report.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tab.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tabpage.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_tooltip.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_quickinfo.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_charts.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_chart.js", target = "head"),
	@ResourceDependency(library = "js", name = "dbsfaces_chartvalue.js", target = "head")
//	@ResourceDependency(library = "js", name = "eventsource.js", target = "head")
})

public abstract class DBSUIOutput extends UIOutput implements IDBSUIComponentBase{

	protected enum PropertyKeys {
		style,
		styleClass;

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

	@Override
	public String getFamily() {
		return DBSFaces.FAMILY;
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

	@Override
	public void handleAttribute(String name, Object value) {
		DBSFaces.handleAttribute(name, value, this);
	}

	public String getValueExpressionString(String pPropertyKeysName){
		return this.getValueExpression(pPropertyKeysName).getExpressionString();
	}
	
}
